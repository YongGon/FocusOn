
package com.one.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.simonvt.menudrawer.MenuDrawer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.Session;
import com.one.Adapter.MenuAdapter;
import com.one.Entities.Category;
import com.one.Entities.Item;
import com.one.Fragment.FileFragment;
import com.one.Fragment.MainFragment;
import com.one.Fragment.MessageListFragment;
import com.one.Fragment.NewsFragment;
import com.one.Fragment.OptionFragment;
import com.one.Fragment.ScreenFragment;
import com.one.util.SharedpreferencesUtil;

public class FocusActivity extends FragmentActivity {

	private static final String ACCESS_TOKEN  	 	=  "ACCESS_TOKEN";
	private static final String FOCUS_TOKEN    		=  "FOCUS_TOKEN";
	private static final String TAG 				=  "FocusActivity";
	private static final String ERROR       		=  "error";

	private MenuDrawer mDrawer;
	private MenuAdapter mAdapter;
	private ListView mList;
	private String name, webServer;
	private int mCurrentFragmentIndex, mFragmentCount = 0;
	private SharedpreferencesUtil mSharedpreferencesUtil;
	private Handler mHandler;
	private boolean mBackbtnFlug = false;
	private boolean mLogout = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mHandler = new Handler(){  // Back 버튼 두번 클릭 확인을 위한 핸들러 객채 
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				if(msg.what == 0){
					mBackbtnFlug = false;
				}

			}
		};

		mSharedpreferencesUtil = new SharedpreferencesUtil(getApplicationContext());
		webServer = mSharedpreferencesUtil.getValue("WEB", "") + "focus_on/check.jsp";


		if(mSharedpreferencesUtil.getValue(ACCESS_TOKEN, "").equals("")){
			name = mSharedpreferencesUtil.getValue("focus_name", "");
		}else{
			name = mSharedpreferencesUtil.getValue("user_name", "");
		}

		mDrawer = MenuDrawer.attach(this, MenuDrawer.Type.OVERLAY);
		mDrawer.setContentView(R.layout.activity_focus_main);

		List<Object> items = new ArrayList<Object>();
		items.add(new Category("프로필"));
		items.add(new Item(name,  R.drawable.profle));
		items.add(new Category("focus"));
		items.add(new Item("실시간 화면", R.drawable.screen));
		items.add(new Item("알림방", R.drawable.news));
		items.add(new Item("질문함", R.drawable.message));
		items.add(new Item("파일 보관함", R.drawable.file));
		items.add(new Item("환경설정", R.drawable.option));

		mList = new ListView(this);
		mAdapter = new MenuAdapter(this, items);
		mList.setAdapter(mAdapter);
		mDrawer.setMenuView(mList);

		mDrawer.setSlideDrawable(R.drawable.ic_drawer);
		mDrawer.setDrawerIndicatorEnabled(true);
		mDrawer.setBackgroundColor(Color.argb(255, 255, 255, 255));

		mCurrentFragmentIndex = 8;
		fragmentReplace(mCurrentFragmentIndex);

		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				switch(position){

				case 3:
					fragmentReplace(position);
					mDrawer.toggleMenu();
					break;
				case 4:
					fragmentReplace(position);
					mDrawer.toggleMenu();
					break;
				case 5:
					fragmentReplace(position);
					mDrawer.toggleMenu();
					break;
				case 6:
					fragmentReplace(position);
					mDrawer.toggleMenu();
					break;
				case 7:
					fragmentReplace(position);
					mDrawer.toggleMenu();
					break;
				}

				if(mFragmentCount == 0){
					mSharedpreferencesUtil.put(String.format("%d", mFragmentCount), 8);
					Log.e("fragmentclick", String.format("%s", mSharedpreferencesUtil.getValue(String.format("%d", mFragmentCount), 0)));
					mFragmentCount++;
					mCurrentFragmentIndex = position;
				}else{
					mSharedpreferencesUtil.put(String.format("%d", mFragmentCount), mCurrentFragmentIndex);
					Log.e("fragmentclick", String.format("%s", mSharedpreferencesUtil.getValue(String.format("%d", mFragmentCount), 0)));
					mFragmentCount++;
					Log.e("fragmentclick", String.format("%s" , mFragmentCount));
					mCurrentFragmentIndex = position;
				}
			}
		});


		UesrOn(name);

	}

	public void fragmentReplace(int reqNewFragmentIndex) {

		Fragment newFragment = null;

		Log.d(TAG, "fragmentReplace " + reqNewFragmentIndex);

		newFragment = getFragment(reqNewFragmentIndex);

		// replace fragment
		final FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();

		transaction.replace(R.id.fragment_Change_Layout, newFragment);

		// Commit the transaction
		transaction.commit();

	}

	private Fragment getFragment(int idx) {
		Fragment newFragment = null;


		switch (idx) {
		case 3:
			newFragment = new ScreenFragment();
			setTitle("실시간 화면");
			break;
		case 4:
			newFragment = new NewsFragment();
			setTitle("알림방");
			break;
		case 5:
			newFragment = new MessageListFragment();
			setTitle("질문함");
			break;
		case 6:
			newFragment = new FileFragment();
			setTitle("파일 보관함");
			break;
		case 7:
			newFragment = new OptionFragment();
			setTitle("환경설정");
			break;	
		case 8:
			newFragment = new MainFragment();
			setTitle("FocusOn");
			break;
		}

		return newFragment;
	}


	public static void callFacebookLogout(Context context) {
		Session session = Session.getActiveSession();
		if (session != null) {

			if (!session.isClosed()) {
				session.closeAndClearTokenInformation();
				//clear your preferences if saved
			}
		} else {

			session = new Session(context);
			Session.setActiveSession(session);

			session.closeAndClearTokenInformation();
			//clear your preferences if saved

		}

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.logout, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			mDrawer.toggleMenu();
			return true;

		case R.id.actionbar_logout:
			
			UserOff(name);
			Log.e("name", name.toString());
			
			mLogout = true;
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			try{
				if(mSharedpreferencesUtil.getValue(ACCESS_TOKEN, "").equals("")){
					mSharedpreferencesUtil.removePreferences(FOCUS_TOKEN);
					mSharedpreferencesUtil.removePreferences("focus_name");
				}else{
					mSharedpreferencesUtil.removePreferences(ACCESS_TOKEN);
					mSharedpreferencesUtil.removePreferences("user_id");
//					mSharedpreferencesUtil.removePreferences("user_name");
					callFacebookLogout(getApplicationContext());
				}

				Intent intent = new Intent(FocusActivity.this, FacebookLoginActivity.class);
				startActivity(intent);
				finish();
				return true;

			}catch(NullPointerException e){
				e.printStackTrace();
			}

		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if(!mLogout){
			UserOff(name);
		}
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) { // Back 버튼 두번 입력시 종료
		super.onKeyDown(keyCode, event);

		if(event.getAction() == KeyEvent.ACTION_DOWN){
			switch(keyCode){

			case KeyEvent.KEYCODE_BACK:
				
				if(mDrawer.isMenuVisible()){
					
					mDrawer.toggleMenu();
				}

				else if(mFragmentCount > 0){

					fragmentReplace(mSharedpreferencesUtil.getValue(String.format("%d", mFragmentCount-1), 0));
					mFragmentCount--;

				}else if(mFragmentCount == 0){
					if(!mBackbtnFlug){
						Toast.makeText(FocusActivity.this, "'뒤로' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
						mBackbtnFlug = true;
						mHandler.sendEmptyMessageDelayed(0, 2000);
						return false;
					}else{
						finish();
						return true;
					}
				}

			}
		}
		return false;
	}


	public void UesrOn(String name){

		RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

		StringRequest request = new StringRequest(Request.Method.POST, webServer, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub

				System.err.println(response);

			}


		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub

				VolleyLog.d(ERROR, error.getMessage());

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

	public void UserOff(String name){

		RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

		StringRequest request = new StringRequest(Request.Method.POST, webServer, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub

				System.err.println(response);

			}


		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub

				VolleyLog.d(ERROR, error.getMessage());

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
