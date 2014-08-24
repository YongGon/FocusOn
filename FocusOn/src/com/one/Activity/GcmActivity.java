package com.one.Activity;

import java.io.IOException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.one.util.PreferenceUtil;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

public class GcmActivity extends Activity{
//APA91bFEesU8XALCPCAdyi4bx9JyKUutlt9ljt5o9J1UwitH9VWVG2yEblKklljCawnxEAfUApE83iNKyivlpg-M6BYqz-qvFcUIL3vmrSUs2bO9rmfBq_I9D0nA4cQrTSNyow82Qpry_Jn7qfm9po2sClwIqfv6qw
	private String sender_id = "23652975835";
	private GoogleCloudMessaging gcm;
	private String regId = null;	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gcm);


		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regId = getRegistrationId();
			Log.e("resID", regId);

			TextView textView = (TextView)findViewById(R.id.stone);
			textView.setText(regId);

			if (TextUtils.isEmpty(regId))
				registerInBackground();
		} else {
			Log.i("STONE", "No valid Google Play Services APK found.|");
		}
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, 9000).show();
			} else {
				Log.i("STONE", "|This device is not supported.|");
				finish();
			}
			return false;
		}
		return true;
	}

	private String getRegistrationId() {
		String registrationId = PreferenceUtil.instance(getApplicationContext()).regId();
		if (TextUtils.isEmpty(registrationId)) {
			Log.i("STONE", "|Registration not found.|");
			return "";
		}
		int registeredVersion = PreferenceUtil.instance(getApplicationContext()).appVersion();
		int currentVersion = getAppVersion();
		if (registeredVersion != currentVersion) {
			Log.i("STONE", "|App version changed.|");
			return "";
		}
		return registrationId;
	}

	private int getAppVersion() {
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
					}
					regId = gcm.register(sender_id);
					msg = "Device registered, registration ID=" + regId;
					storeRegistrationId(regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {		
				Log.i("MainActivity.java | onPostExecute", "|" + msg + "|");
			}
		}.execute(null, null, null);
	}

	private void storeRegistrationId(String regId) {
		int appVersion = getAppVersion();
		Log.i("STONE", "|" + "Saving regId on app version " + appVersion + "|");
		PreferenceUtil.instance(getApplicationContext()).putRedId(regId);
		PreferenceUtil.instance(getApplicationContext()).putAppVersion(appVersion);
	}
}
