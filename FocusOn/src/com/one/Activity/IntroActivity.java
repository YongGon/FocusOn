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

		mhd = new Handler(); // �ڵ鷯 ��ü ����
		mhd.postDelayed(mRunnuable, SLEEP_TIME); // postDelayed �޼��� ȣ�� (Runnable ��å, �����ð�) ���ڰ�


	}

	private void dataInit() {
		sharedpreferencesUtil = new SharedpreferencesUtil(getApplicationContext());
		sharedpreferencesUtil.put("WEB", "http://192.168.0.2:8080/");
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub��
		super.onBackPressed();

		mhd.removeCallbacks(mRunnuable); // back��ư ������ �ڵ鷯 ����
	}

	

	Runnable mRunnuable	= new Runnable() { // �ε�ȭ��  Runnable ��ä ����

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

			// fade in , fade out �ִϸ��̼� �߰�
			overridePendingTransition(R.anim.splash_in, R.anim.splash_out);

		}
	};

}


