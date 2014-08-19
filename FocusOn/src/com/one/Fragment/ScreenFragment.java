package com.one.Fragment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.one.Activity.R;
import com.one.Adapter.ScreenList_Adapter;
import com.one.Entities.ScreenList_Item;
import com.one.dontmind.Multivnc.ConnectionBean;
import com.one.dontmind.Multivnc.Constants;
import com.one.dontmind.Multivnc.MostRecentBean;
import com.one.dontmind.Multivnc.VncDatabase;
import com.one.util.SharedpreferencesUtil;


public class ScreenFragment extends Fragment{

	private static final String TAG = "ScreenFragment";

	PullToRefreshListView mScreenList;
	ArrayList<ScreenList_Item> mScreenArray;
	ScreenList_Adapter mScreenAdapter;
	private VncDatabase database;
	SharedpreferencesUtil mSharedpreferencesUtil;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View v = inflater.inflate(R.layout.fragment_refresh, container, false);

		mScreenList = (PullToRefreshListView)v.findViewById(R.id.refresh_ListView);
		database = new VncDatabase(getActivity());

		return v;
	}

	@Override
	public void onResume() 
	{
		super.onResume();


		mSharedpreferencesUtil = new SharedpreferencesUtil(getActivity());

		mScreenArray = new ArrayList<ScreenList_Item>();
		
		mScreenAdapter = new ScreenList_Adapter(getActivity(), R.layout.fragment_screenlist_item, mScreenArray);

		new GetDataTask().execute();

		mScreenList.setOnItemClickListener(new OnItemClickListener() 
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{

				//				Intent intent = new Intent(getActivity(), MainMenuActivity.class);
				//				intent.putExtra("ip", mScreenArray.get(position).ip);
				//				startActivity(intent);

				ConnectionBean conn = makeNewConnFromView(position);
				if(conn == null)
					return;
				writeRecent(conn);
				Log.d(TAG, "Starting NEW connection " + conn.toString());
				Intent intent = new Intent(getActivity(), com.one.dontmind.Multivnc.VncCanvasActivity.class);
				intent.putExtra(Constants.CONNECTION,conn.Gen_getValues());
				startActivity(intent);


			}

		});


		mScreenList.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				new RefreshTask().execute();
			}
		});
		mScreenList.getRefreshableView();

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		database.close();

	}



	static MostRecentBean getMostRecent(SQLiteDatabase db)
	{
		ArrayList<MostRecentBean> recents = new ArrayList<MostRecentBean>(1);
		MostRecentBean.getAll(db, MostRecentBean.GEN_TABLE_NAME, recents, MostRecentBean.GEN_NEW);
		if (recents.size() == 0)
			return null;
		return recents.get(0);
	}

	VncDatabase getDatabaseHelper()
	{
		return database;
	}



	private void writeRecent(ConnectionBean conn)
	{
		SQLiteDatabase db = database.getWritableDatabase();
		db.beginTransaction();
		try
		{
			MostRecentBean mostRecent = getMostRecent(db);
			if (mostRecent == null)
			{
				mostRecent = new MostRecentBean();
				mostRecent.setConnectionId(conn.get_Id());
				mostRecent.Gen_insert(db);
			}
			else
			{
				mostRecent.setConnectionId(conn.get_Id());
				mostRecent.Gen_update(db);
			}
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
		}
	}



	private ConnectionBean makeNewConnFromView(int position) {

		ConnectionBean conn = new ConnectionBean();

		conn.setAddress(mScreenArray.get(position-1).ip);

		if(conn.getAddress().length() == 0)
			return null;

		conn.set_Id(0); // is new!!

		try {
			conn.setPort(Integer.parseInt("5900"));
		}
		catch (NumberFormatException nfe) {
		}
		conn.setPassword("123");
		conn.setUseLocalCursor(true); // always enable
		conn.setColorModel("24-bit color (4 bpp)");

		return conn;
	}

	public void dialogInit(){


		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle("서버접속 오류");
		dialog.setMessage("서버 접속이 원활하지 않습니다. 잠시 후 다시 시도해 주세요.");
		dialog.setIcon(R.drawable.screen);
		dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				dialog.dismiss();

			}
		});

		AlertDialog alert = dialog.create();
		alert.show();

	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		
	}


	private class GetDataTask extends AsyncTask<Void, Void, ArrayList> {


		@Override
		protected ArrayList doInBackground(Void... params) {
			// 고객정보 출력 영역 인식

			URL url = null;
			HttpURLConnection urlConnection = null;
			BufferedInputStream buf = null;

			try {    
				///// URL 지정과 접속 
				// 웹서버 URL 지정 
				String addr = mSharedpreferencesUtil.getValue("WEB", "") + "focus_on/jsonchange/roomjson.jsp";
				//				url = new URL( mSharedpreferencesUtil.getValue("WEB", "") + "focus_on/jsonchange/roomjson.jsp");
				url = new URL(addr);
				// URL 접속
				urlConnection = (HttpURLConnection) url.openConnection(); 

				///// 웹문서 소스를 버퍼에 저장  
				// 데이터 다운로드  			
				buf  = new BufferedInputStream(urlConnection.getInputStream());    
				// 데이터를 버퍼에 기록 
				BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "euc-kr"));

				String line  = null;
				String page  = "";

				// 버퍼의 웹문서 소스를 줄 단위로 읽어(line), page에 저장함 
				while ((line = bufreader.readLine()) != null) {
					page += line;
				}

				// 읽어들인 JSON 포맷의 데이어틀 JSON 객체로 변환
				JSONObject json = new JSONObject(page);
				// customers에 해당하는 배열을 할당  
				JSONArray  jArr = json.getJSONArray("room");

				// 배열의 크기만큼 반복하면서, name과 address의 값을 추출함 
				for (int i=jArr.length() - 1 ; i>=0; i--) {

					// i번째 배열 할당  
					json = jArr.getJSONObject(i);

					// name과 address, news 의 값을 추출함
					String name    = json.getString("roomname");
					String ip = json.getString("ip");

					Log.e("json1", name.toString());
					Log.e("json2", ip.toString());

					// name과 address의 값을 출력함

					ScreenList_Item screenItem;
					screenItem = new ScreenList_Item(name, ip);
					mScreenArray.add(screenItem);
				}
				
			} catch (IOException IO){
				
				dialogInit();

			} catch (Exception e){
				// 에러메시지 출력 
			} finally {   
				// URL 연결 해제
				urlConnection.disconnect();  
			}	
			return mScreenArray;
		}

		@Override
		protected void onPostExecute(ArrayList result) {
			
			mScreenList.setAdapter(mScreenAdapter);

			super.onPostExecute(result);
		}
	}
	
	
	private class RefreshTask extends AsyncTask<Void, Void, ArrayList> {


		@Override
		protected ArrayList doInBackground(Void... params) {
			URL url = null;
			HttpURLConnection urlConnection = null;
			BufferedInputStream buf = null;

			try {
				Thread.sleep(2000);

				///// URL 지정과 접속 
				// 웹서버 URL 지정 
				String addr = mSharedpreferencesUtil.getValue("WEB", "") + "focus_on/jsonchange/roomjson.jsp";
				//				url = new URL( mSharedpreferencesUtil.getValue("WEB", "") + "focus_on/jsonchange/roomjson.jsp");
				url = new URL( addr);
				// URL 접속
				urlConnection = (HttpURLConnection) url.openConnection(); 

				///// 웹문서 소스를 버퍼에 저장  
				// 데이터 다운로드  			
				buf  = new BufferedInputStream(urlConnection.getInputStream());    
				// 데이터를 버퍼에 기록 
				BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "euc-kr"));

				String line  = null;
				String page  = "";

				// 버퍼의 웹문서 소스를 줄 단위로 읽어(line), page에 저장함 
				while ((line = bufreader.readLine()) != null) {
					page += line;
				}

				// 읽어들인 JSON 포맷의 데이어틀 JSON 객체로 변환
				JSONObject json = new JSONObject(page);
				// customers에 해당하는 배열을 할당  
				JSONArray  jArr = json.getJSONArray("room");

				mScreenArray.clear();
				// 배열의 크기만큼 반복하면서, name과 address의 값을 추출함 
				for (int i=0; i<jArr.length(); i++) {

					// i번째 배열 할당  
					json = jArr.getJSONObject(i);

					// name과 address, news 의 값을 추출함
					String name    = json.getString("roomname");
					String ip = json.getString("ip");

					// name과 address의 값을 출력함

					ScreenList_Item screenItem;
					screenItem = new ScreenList_Item(name, ip);
					mScreenArray.add(screenItem);
				}

			}catch (InterruptedException e) {

			} catch (Exception e){
				// 에러메시지 출력 
			} finally {   
				// URL 연결 해제
				urlConnection.disconnect();  
			}	
			return mScreenArray;
		}

		@Override
		protected void onPostExecute(ArrayList result) {

			mScreenAdapter.notifyDataSetChanged();
			mScreenList.onRefreshComplete();
			super.onPostExecute(result);
		}
	}

}
