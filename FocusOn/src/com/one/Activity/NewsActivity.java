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
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

public class NewsActivity extends Activity {


	protected static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	TextView mTextView, mTextView2, mTextView3;
	private SharedpreferencesUtil mSharedpreferencesUtil;
	private boolean OnOffFlug = true;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newsactivity);

		mSharedpreferencesUtil = new SharedpreferencesUtil(getApplicationContext());
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
