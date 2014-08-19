package com.one.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

public class NewsActivity extends Activity {


	TextView mTextView, mTextView2, mTextView3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newsactivity);
		
		setTitle("알림내용");

		Intent intent = getIntent();
		String position_title = intent.getStringExtra("title");
		String position_writer = intent.getStringExtra("writer");
		String position_content = intent.getStringExtra("content");


		mTextView = (TextView)findViewById(R.id.title_TextView);
		mTextView.setText(position_title);
		mTextView = (TextView)findViewById(R.id.writer_TextView);
		mTextView.setText(position_writer);
		mTextView = (TextView)findViewById(R.id.content_TextView);
		mTextView.setText(position_content);


	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if(event.getAction() == KeyEvent.ACTION_DOWN){
			switch(keyCode){
				
			case KeyEvent.KEYCODE_BACK:
				finish();
				overridePendingTransition(R.anim.activity_in2, R.anim.activity_out);
				return true;
			}
		}
		return false;

	}


}
