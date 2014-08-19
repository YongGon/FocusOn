package com.one.Activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MessageContentActivity extends Activity{

	EditText mTitle_EditText, mValue_EditText;
	Intent mintent; 
	String position_title, position_value;
	Button mLeftbtn, mRightbtn;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_messagesend);
		
		mTitle_EditText = (EditText)findViewById(R.id.send_Title_EditText);
		mValue_EditText = (EditText)findViewById(R.id.send_Value_EditText);
		mLeftbtn = (Button)findViewById(R.id.Left_btn);
		mRightbtn = (Button)findViewById(R.id.right_btn);
		
		mLeftbtn.setVisibility(View.GONE);
		mRightbtn.setVisibility(View.GONE);
		

		
		mintent = getIntent();
		position_title = mintent.getStringExtra("Title");
		position_value = mintent.getStringExtra("Value");

		if(position_title != null && position_value != null){

			mTitle_EditText.setText(position_title);
			mTitle_EditText.setFocusable(false);
			mTitle_EditText.setClickable(false);

			mValue_EditText.setText(position_value);
			mValue_EditText.setFocusable(false);
			mValue_EditText.setClickable(false);
		}

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

