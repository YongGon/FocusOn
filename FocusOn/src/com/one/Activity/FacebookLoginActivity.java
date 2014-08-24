package com.one.Activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.one.Entities.FaceBookUser;
import com.one.util.PreferenceUtil;
import com.one.util.SharedpreferencesUtil;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

public class FacebookLoginActivity extends Activity{

//	private static final String WEB_SERVER 				= "http://192.168.0.98:8080/login.jsp";
//	private static final String KEY_SERVER 				= "http://192.168.0.98:8080/focus_on/key_receive.jsp";
	
	
	private static final String ACCESS_TOKEN 			= "ACCESS_TOKEN";
	private static final String FOCUS_TOKEN   			= "FOCUS_TOKEN";
	private static final String ERROR 					= "error";
	private static final String TRUE 					= "true";
	private static final String FACEBOOK_ID  			= "FACEBOOK_ID";
	private static final String FACEBOOK_BASE_URL  		= "http://graph.facebook.com/";
	private static final String PICTURE_TYPE			= "/picture?type=large";

	private Session.StatusCallback 		statusCallback = new SessionStatusCallback();
	private SharedpreferencesUtil 		mSharedpreferencesUtil;
	
	private String WEB_SERVER;
	private String KEY_SERVER;
	
	
	private Button 						buttonLoginLogout, mNewMember, mLogin;
	private FloatLabeledEditText 	 	mId, mPasswd;
	private RequestQueue 				requestQueue;
	private Session 					mSession;
	private String 						ok;
	private ImageView 					mProfile_Img;
	private TextView 					mWelcome_text;
	private DisplayImageOptions 		options;
	private Animation 					verticalShake;
	private FaceBookUser 				faceBookUser;
	private ImageLoader 				imageLoader;
	private MenuItem 					mNext;
	//APA91bFEesU8XALCPCAdyi4bx9JyKUutlt9ljt5o9J1UwitH9VWVG2yEblKklljCawnxEAfUApE83iNKyivlpg-M6BYqz-qvFcUIL3vmrSUs2bO9rmfBq_I9D0nA4cQrTSNyow82Qpry_Jn7qfm9po2sClwIqfv6qw
	private String sender_id = "23652975835";
	private GoogleCloudMessaging gcm;
	private String regId = "";	




	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facebooklogin);


		registerInBackground();


		init();
		dataInit();
		facebookInit(savedInstanceState);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub


		getMenuInflater().inflate(R.menu.login, menu);

		if(mSharedpreferencesUtil.getValue("user_id", "").equals("")){
			mNext = menu.findItem(R.id.actionbar_next);
			mNext.setVisible(false);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		saveLoginInfo();

		return super.onOptionsItemSelected(item);
	}

	public void saveLoginInfo(){

		mSharedpreferencesUtil.put(ACCESS_TOKEN, mSession.getAccessToken());
		mSharedpreferencesUtil.put(FACEBOOK_ID, faceBookUser.getUserId());
		mSharedpreferencesUtil.put("push", "on");

		if(mSharedpreferencesUtil.getValue("KEY_CHECK", "").equals("")){
			
			keySend_Check();
			mSharedpreferencesUtil.put("KEY_CHECK", "true");
		}

		Intent intent = new Intent(FacebookLoginActivity.this, FocusActivity.class);
		startActivity(intent);
		finish();

	}

	private void init() {
		buttonLoginLogout = (Button)findViewById(R.id.loginlogout_btn);
		mId = (FloatLabeledEditText)findViewById(R.id.login_id_edittext);
		mPasswd = (FloatLabeledEditText)findViewById(R.id.login_passwd_edittext);
		mProfile_Img = (ImageView)findViewById(R.id.profile_imgView);
		mWelcome_text = (TextView)findViewById(R.id.welcome_textView);

		mLogin = (Button)findViewById(R.id.login_btn);
		mLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub

				if(mId.getText().toString().equals("")){
					Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
				}else if(mPasswd.getText().toString().equals("")){
					Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
				}else{

					if(mSharedpreferencesUtil.getValue("KEY_CHECK", "").equals("")){
						
						keySend_Check();
						mSharedpreferencesUtil.put("KEY_CHECK", "true");
					}
					mSharedpreferencesUtil.put("push", "on");
					onfocuslogin();
				}
			}
		});

		mNewMember = (Button)findViewById(R.id.New_Member);
		mNewMember.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(FacebookLoginActivity.this, NewMemberActivity.class);
				startActivity(intent);
			}
		});
	}

	private void dataInit() {
		//ActionBar Init
		getActionBar().setDisplayShowHomeEnabled(true);
		mSharedpreferencesUtil = new SharedpreferencesUtil(FacebookLoginActivity.this);
		faceBookUser = new FaceBookUser();
		WEB_SERVER = mSharedpreferencesUtil.getValue("WEB", "") + "focus_on/phonelogin.jsp";
		KEY_SERVER = mSharedpreferencesUtil.getValue("WEB", "") + "focus_on/key_receive.jsp";
		imageLoderInit();
	}

	private void imageLoderInit() {
		options = new DisplayImageOptions.Builder()
		.showImageOnFail(Color.TRANSPARENT) 
		.displayer(new RoundedBitmapDisplayer(1000))
		.cacheOnDisc(true)
		.considerExifParams(true)
		.build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
		.threadPriority(Thread.NORM_PRIORITY - 2)
		.denyCacheImageMultipleSizesInMemory()
		.discCacheFileNameGenerator(new Md5FileNameGenerator())
		.tasksProcessingOrder(QueueProcessingType.LIFO)
		.writeDebugLogs()
		.build();
		ImageLoader.getInstance().init(config);
		imageLoader = ImageLoader.getInstance();
	}

	private void facebookInit(Bundle savedInstanceState) {
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

		Session session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
			}
			if (session == null) {
				session = new Session(this);
			}
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
			}
		}

		updateView();
	}

	@Override
	public void onStart() {
		super.onStart();
		Session.getActiveSession().addCallback(statusCallback);
	}

	@Override
	public void onStop() {
		super.onStop();
		Session.getActiveSession().removeCallback(statusCallback);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	private void updateView() {

		buttonLoginLogout.setOnClickListener(new OnClickListener() {
			public void onClick(View view) { onClickLogin();  }

		});
	}


	private void onClickLogin() {
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
		} else {
			Session.openActiveSession(this, true, statusCallback);
		}
	}

	public void onClickLogout() {
		Session session = Session.getActiveSession();
		if (!session.isClosed()) {
			session.closeAndClearTokenInformation();
		}
	}

	public void onfocuslogin(){
		
		InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);   
		  
		//키보드를 없앤다.   
		imm.hideSoftInputFromWindow(mId.getWindowToken(),0);

		requestQueue = Volley.newRequestQueue(FacebookLoginActivity.this);

		requestQueue.add(new StringRequest(Request.Method.POST, WEB_SERVER, new Response.Listener<String>() {  


			@Override  
			public void onResponse(String response) {
				JSONObject JsonObject;
				ok = null;

				int position = response.lastIndexOf('{');

				String json = "";
				for(int i = position ; i < response.length() - 1 ; i++ ){

					json += response.charAt(i);
				}

				try {
					Log.e("response", json);
					JsonObject = new JSONObject(json);
					ok = JsonObject.getString("TRUE");
					if(ok.equals(TRUE)){
						String name = JsonObject.getString("name");
						mSharedpreferencesUtil.put(FOCUS_TOKEN, "FOCUS");
						mSharedpreferencesUtil.put("focus_name", name);
						Intent intent = new Intent(FacebookLoginActivity.this, FocusActivity.class);
						startActivity(intent);
						finish();
					}

				} catch (JSONException e) {
					dialogInit();
					e.printStackTrace();
				}

			}  
		}, new Response.ErrorListener() {  
			@Override  
			public void onErrorResponse(VolleyError error) {  
				VolleyLog.d(ERROR, error.getMessage());  
			}  
		}){

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {

				Map<String, String> params = new HashMap<String, String>();
				params.put("id", mId.getText().toString());
				params.put("passwd", mPasswd.getText().toString());


				return params;
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {

				Map<String, String> params = new HashMap<String, String>();
				params.put("Content-Type","application/x-www-form-urlencoded");

				return params;
			}
		});

	}

	public void dialogInit(){


		AlertDialog.Builder dialog = new AlertDialog.Builder(FacebookLoginActivity.this);
		dialog.setTitle("로그인 실패");
		dialog.setMessage("아이디 와 비밀번호를 확인해 주세요.");
		dialog.setIcon(R.drawable.focuson);
		dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				dialog.dismiss();

			}
		});

		AlertDialog alert = dialog.create();
		alert.show();

		//		}
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
				int start = msg.lastIndexOf('=') + 1;
				String appkey = msg.substring(start, msg.length());
				mSharedpreferencesUtil.put("KEY", appkey.toString());
				Log.e("te", appkey);

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
	
	public void keySend_Check(){
		
		requestQueue = Volley.newRequestQueue(FacebookLoginActivity.this);

		requestQueue.add(new StringRequest(Request.Method.POST, KEY_SERVER, new Response.Listener<String>() {  


			@Override  
			public void onResponse(String response) {

			}  
		}, new Response.ErrorListener() {  
			@Override  
			public void onErrorResponse(VolleyError error) {  
				VolleyLog.d(ERROR, error.getMessage());  
			}  
		}){

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {

				Map<String, String> params = new HashMap<String, String>();
				params.put("key1", mSharedpreferencesUtil.getValue("KEY", ""));

				return params;
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {

				Map<String, String> params = new HashMap<String, String>();
				params.put("Content-Type","application/x-www-form-urlencoded");

				return params;
			}
		});
		
	}



	public class SessionStatusCallback implements Session.StatusCallback {


		@Override
		public void call(Session session, SessionState state, Exception exception) {

			mSession = session;


			getFaceBookMe(session);

			if (session.isOpened()) {
				final Animation alphaFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_fade_out);
				mId.setVisibility(View.GONE);
				mPasswd.setVisibility(View.GONE);
				mLogin.setVisibility(View.GONE);
				mNewMember.setVisibility(View.GONE);
				buttonLoginLogout.setVisibility(View.GONE);


				mId.startAnimation(alphaFadeOut);
				mPasswd.startAnimation(alphaFadeOut);
				mLogin.startAnimation(alphaFadeOut);
				mNewMember.startAnimation(alphaFadeOut);
				buttonLoginLogout.startAnimation(alphaFadeOut);
				alphaFadeOut.setAnimationListener(alphaFadeOutAnimationListener);
			}

			updateView();  
		}
	}

	public void getFaceBookMe(Session session){


		if(session.isOpened()){
			com.facebook.Request.newMeRequest(session, new com.facebook.Request.GraphUserCallback() {

				@Override
				public void onCompleted(GraphUser user, com.facebook.Response response) {
					response.getError();

					mWelcome_text.setText(user.getName() + "님 환영합니다.");
					String url = FACEBOOK_BASE_URL+ user.getId()+PICTURE_TYPE;
					imageLoader.displayImage(url, mProfile_Img, options, mImageLoadingListener);
					faceBookUser.setUserId(user.getId());
					mSharedpreferencesUtil.put("user_id", user.getId());
					mSharedpreferencesUtil.put("user_name", user.getName());

					System.err.println(" getId  :  " + user.getId());
					System.err.println(" getFirstName  :  " + user.getFirstName());
					System.err.println(" getLastName  :  " + user.getLastName());
					System.err.println(" getMiddleName  :  " + user.getMiddleName());
					System.err.println(" getBirthday  :  " + user.getBirthday());
					System.err.println(" getLink  :  " + user.getLink());
					System.err.println(" getName  :  " + user.getName());
					System.err.println(" getUsername :  " + user.getUsername());
					System.err.println(" getLocation :  " + user.getLocation());
					System.err.println("getRawResponse  :  " + response.getRawResponse());
				}
			}).executeAsync();
		}
	}

	ImageLoadingListener mImageLoadingListener = new ImageLoadingListener(){
		@Override
		public void onLoadingStarted(String imageUri, View view) {
		}
		@Override
		public void onLoadingCancelled(String imageUri, View view) {}
		@Override
		public void onLoadingComplete(String arg0, View arg1,Bitmap arg2) {
		}
		@Override
		public void onLoadingFailed(String arg0, View arg1,FailReason arg2) {}
	};

	AnimationListener alphaFadeInAnimationListener = new AnimationListener(){

		@Override
		public void onAnimationStart(Animation animation) {}
		@Override
		public void onAnimationRepeat(Animation animation) {}
		@Override
		public void onAnimationEnd(Animation animation) {
			verticalShake.setRepeatCount(Animation.INFINITE);
			mProfile_Img.startAnimation(verticalShake);
		}
	};

	AnimationListener alphaFadeOutAnimationListener = new AnimationListener(){

		@Override
		public void onAnimationStart(Animation animation) {}
		@Override
		public void onAnimationRepeat(Animation animation) {}
		@Override
		public void onAnimationEnd(Animation animation) {
			verticalShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.vertical_shake);
			verticalShake.setRepeatCount(Animation.INFINITE);
			Animation alphaFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_fade_in);
			mProfile_Img.setVisibility(View.VISIBLE);
			mWelcome_text.setVisibility(View.VISIBLE);
			invalidateOptionsMenu();

			mProfile_Img.startAnimation(alphaFadeIn);
			mWelcome_text.startAnimation(alphaFadeIn);
			alphaFadeIn.setAnimationListener(alphaFadeInAnimationListener);
		}

	};

}
