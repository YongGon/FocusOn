package com.one.Activity;


import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.one.util.SharedpreferencesUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MessageContentActivity extends Activity{

	protected static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	EditText mTitle_EditText, mValue_EditText;
	Intent mintent; 
	String position_title, position_value;
	Button mLeftbtn, mRightbtn;
	private SharedpreferencesUtil mSharedpreferencesUtil;
	private boolean OnOffFlug = true;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_messagesend);

		mSharedpreferencesUtil = new SharedpreferencesUtil(getApplicationContext());

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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();


		UesrOn(FocusActivity.name);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		if(OnOffFlug){
			UesrOff(FocusActivity.name);
		}

	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if(event.getAction() == KeyEvent.ACTION_DOWN){
			switch(keyCode){

			case KeyEvent.KEYCODE_BACK:
				OnOffFlug = false;
				finish();
				overridePendingTransition(R.anim.activity_in2, R.anim.activity_out);
				return true;
			}
		}
		return false;

	}


	public void UesrOn(String name){

		RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

		StringRequest request = new StringRequest(Request.Method.POST, FocusActivity.webServer, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub

				System.err.println(response);

			}


		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub

				VolleyLog.d("e", error.getMessage());

			}

		}){

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// TODO Auto-generated method stub


				Map<String, String> params = new Hashtable<String, String>();

				if(!mSharedpreferencesUtil.getValue(ACCESS_TOKEN, "").equals("")){
					params.put("name", mSharedpreferencesUtil.getValue("user_name", ""));
					params.put("log", "on");
				}else{
					params.put("name", mSharedpreferencesUtil.getValue("focus_name", ""));
					params.put("log", "on");
				}

				return params;
			}


			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				// TODO Auto-generated method stub

				Map<String, String> params = new HashMap<String, String>();
				params.put("Content-Type","application/x-www-form-urlencoded");

				return params;

			}

		};

		queue.add(request);

	}

	public void UesrOff(String name){

		RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

		StringRequest request = new StringRequest(Request.Method.POST, FocusActivity.webServer, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub

				System.err.println(response);

			}


		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub

				VolleyLog.d("e", error.getMessage());

			}

		}){

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// TODO Auto-generated method stub


				Map<String, String> params = new Hashtable<String, String>();

				if(!mSharedpreferencesUtil.getValue(ACCESS_TOKEN, "").equals("")){
					params.put("name", mSharedpreferencesUtil.getValue("user_name", ""));
					params.put("log", "off");
				}else{
					params.put("name", mSharedpreferencesUtil.getValue("focus_name", ""));
					params.put("log", "off");
				}

				return params;
			}


			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				// TODO Auto-generated method stub

				Map<String, String> params = new HashMap<String, String>();
				params.put("Content-Type","application/x-www-form-urlencoded");

				return params;

			}

		};

		queue.add(request);

	}
}

