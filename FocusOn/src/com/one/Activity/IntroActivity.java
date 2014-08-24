package com.one.Activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.one.util.SharedpreferencesUtil;


public class IntroActivity extends Activity{

	private static final int SLEEP_TIME = 2000;

	private SharedpreferencesUtil sharedpreferencesUtil;
	private Handler mhd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);


		dataInit();

		mhd = new Handler(); // 핸들러 객체 생성
		mhd.postDelayed(mRunnuable, SLEEP_TIME); // postDelayed 메서드 호출 (Runnable 객책, 지연시간) 인자값


	}

	private void dataInit() {
		sharedpreferencesUtil = new SharedpreferencesUtil(getApplicationContext());
		sharedpreferencesUtil.put("WEB", "http://192.168.0.2:8080/");
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stubㅊ
		super.onBackPressed();

		mhd.removeCallbacks(mRunnuable); // back버튼 누르면 핸들러 종료
	}

	

	Runnable mRunnuable	= new Runnable() { // 로딩화면  Runnable 객채 생성

		@Override
		public void run() {

			if(sharedpreferencesUtil.getValue("ACCESS_TOKEN", "").equals("") && sharedpreferencesUtil.getValue("FOCUS_TOKEN", "").equals("")){
				Intent i = new Intent(IntroActivity.this, FacebookLoginActivity.class);
				startActivity(i);
			}else{
				Intent i = new Intent(IntroActivity.this, FocusActivity.class);
				startActivity(i);
			}

			finish();

			// fade in , fade out 애니메이션 추가
			overridePendingTransition(R.anim.splash_in, R.anim.splash_out);

		}
	};

}


