package com.one.Activity;

import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;
import com.one.Activity.R;
import com.one.Activity.R.drawable;
import com.one.Activity.R.id;
import com.one.Activity.R.layout;
import com.one.Entities.Push_Thread;
import com.one.util.SharedpreferencesUtil;

public class GCMIntentService extends GCMBaseIntentService {

	NotificationCompat.Builder mBuilder;
	private View obj;
	private TextView push_title, push_content;
	private SharedpreferencesUtil mSharedpreferencesUtil;

	public GCMIntentService() {
		super("PROJECT ID");
	}

	@Override
	protected void onError(Context arg0, String arg1) {
		Log.i("onError Call", "onError Call");
	}



	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i("onMessage Call", "onMessage Call");

		mSharedpreferencesUtil = new SharedpreferencesUtil(getApplicationContext());


		String title = intent.getExtras().getString("title");
		String contents = intent.getExtras().getString("contents");



		NotificationManager mNotificationManager = (NotificationManager)
				this.getSystemService(Context.NOTIFICATION_SERVICE);
		// 메시지를 클릭시 MainActivity가 실행
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, IntroActivity.class), 0);


		Calendar c = Calendar.getInstance();
		long today = c.getTimeInMillis();

		long[] pattern = {1000,300,1000,1200};

		Uri ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(),RingtoneManager.TYPE_NOTIFICATION);
		AudioManager audioManager =  (AudioManager)getSystemService(Context.AUDIO_SERVICE);


		if(audioManager.getRingerMode()== AudioManager.RINGER_MODE_NORMAL){
			mBuilder = new NotificationCompat.Builder(this)
			// 메시지 좌측의 아이콘 
			.setSmallIcon(R.drawable.focuson)
			.setContentTitle(title)
			.setStyle(new NotificationCompat.BigTextStyle()
			.bigText(contents))
			.setWhen(today)
			.setTicker(contents)
			.setSound(ringtoneUri)
			.setAutoCancel(true)
//			.setDefaults(Notification.DEFAULT_SOUND)
			.setContentText(contents);

		}else{
			mBuilder = new NotificationCompat.Builder(this)
			// 메시지 좌측의 아이콘 
			.setSmallIcon(R.drawable.focuson)
			.setContentTitle(title)
			.setStyle(new NotificationCompat.BigTextStyle()
			.bigText(contents))
			.setWhen(today)
			.setTicker(contents)
			.setVibrate(pattern)
			.setAutoCancel(true)
			.setContentText(contents);
		}



		mBuilder.setContentIntent(contentIntent);

		if(!mSharedpreferencesUtil.getValue("push", "").equals("")){
			mNotificationManager.notify(0, mBuilder.build());
			Push_Thread p = new Push_Thread(mHandler, title ,contents);
			p.start();
		}else{
			
		}

	}



	@Override
	protected void onRegistered(Context arg0, String regId) {
		Log.i("onRegistered Call", "onRegistered Call");
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		Log.i("onUnregistered Call", "onUnregistered Call");
	}


	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			String push = (String) msg.obj;

			String title = push.substring(0, push.lastIndexOf('|'));

			String content = push.substring(push.lastIndexOf('|') + 1, push.length());


			LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
			obj = inflater.inflate(R.layout.toast_gcm, null, false);

			push_title=(TextView)obj.findViewById(R.id.Toast_title);
			push_title.setText(title);

			push_content=(TextView)obj.findViewById(R.id.Toast_content);
			push_content.setText(content);

			Toast toast=new Toast(getApplicationContext());
			toast.setGravity(Gravity.TOP, 0, 130);
			toast.setDuration(Toast.LENGTH_LONG);
			toast.setView(obj);
			toast.show();

		}

	};




}
