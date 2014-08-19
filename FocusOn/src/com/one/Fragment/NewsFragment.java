package com.one.Fragment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.one.Activity.NewsActivity;
import com.one.Activity.R;
import com.one.Adapter.NewsList_Adapter;
import com.one.Entities.NewsList_Item;
import com.one.util.SharedpreferencesUtil;

public class NewsFragment extends Fragment {

	PullToRefreshListView mNewsList;
	ArrayList<NewsList_Item> mNewsArray;
	NewsList_Adapter mNewsAdapter;
	SharedpreferencesUtil mSharedpreferencesUtil;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View v = inflater.inflate(R.layout.fragment_refresh, container, false);

		mNewsList = (PullToRefreshListView)v.findViewById(R.id.refresh_ListView);

		return v;

	}

	@Override
	public void onResume() 
	{
		super.onResume();

		mSharedpreferencesUtil = new SharedpreferencesUtil(getActivity());
		mNewsArray = new ArrayList<NewsList_Item>();
		mNewsAdapter = new NewsList_Adapter(getActivity(), R.layout.fragment_news_list_item, mNewsArray);

		new GetDataTask().execute();

		mNewsList.setOnItemClickListener(new OnItemClickListener() 
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{

				Intent intent = new Intent(getActivity(), NewsActivity.class);
				intent.putExtra("title", mNewsArray.get(position-1).text1);
				intent.putExtra("writer", mNewsArray.get(position-1).text2);
				intent.putExtra("content", mNewsArray.get(position-1).text3);


				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.activity_in, R.anim.splash_out);

			}

		});


		mNewsList.setOnRefreshListener(new OnRefreshListener<ListView>() {

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
		mNewsList.getRefreshableView();
	}

	public void dialogInit(){


		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle("서버접속 오류");
		dialog.setMessage("서버 접속이 원활하지 않습니다. 잠시 후 다시 시도해 주세요.");
		dialog.setIcon(R.drawable.news);
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
				String addr = mSharedpreferencesUtil.getValue("WEB", "") + "focus_on/jsonchange/alarmjson.jsp";
				url = new URL(addr); 
				// URL 접속
				if(url != null){



					urlConnection = (HttpURLConnection) url.openConnection(); 
					Log.e("urlcon", "url 연결");

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
					Log.e("page", page.toString());

					// 읽어들인 JSON 포맷의 데이어틀 JSON 객체로 변환
					JSONObject json = new JSONObject(page);
					// customers에 해당하는 배열을 할당  
					JSONArray  jArr = json.getJSONArray("alarm");

					// 배열의 크기만큼 반복하면서, name과 address의 값을 추출함 
					for (int i= jArr.length() - 1 ; i>=0; i--) {

						// i번째 배열 할당  
						json = jArr.getJSONObject(i);

						String name    = json.getString("name");
						String writer = json.getString("writer");
						String content    = json.getString("content");
						String date    = json.getString("date");

						Log.e("json1", name.toString());
						Log.e("json2", writer.toString());
						Log.e("json3", content.toString());
						Log.e("json4", date.toString());


						NewsList_Item newsItem;
						newsItem = new NewsList_Item(name, writer, content, date);
						mNewsArray.add(newsItem);
					}
				}
			}catch(NullPointerException nullerror){

				dialogInit();

			} catch (Exception e){
				// 에러메시지 출력 
			} finally {   
				// URL 연결 해제
				urlConnection.disconnect();  
			}	
			return mNewsArray;
		}

		@Override
		protected void onPostExecute(ArrayList result) {

			mNewsList.setAdapter(mNewsAdapter);
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
				String addr = mSharedpreferencesUtil.getValue("WEB", "") + "focus_on/jsonchange/alarmjson.jsp";
				url = new URL(addr); 
				// URL 접속
				if(url != null){

					urlConnection = (HttpURLConnection) url.openConnection(); 
					Log.e("urlcon", "url 연결");

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
					Log.e("page", page.toString());

					// 읽어들인 JSON 포맷의 데이어틀 JSON 객체로 변환
					JSONObject json = new JSONObject(page);
					// customers에 해당하는 배열을 할당  
					JSONArray  jArr = json.getJSONArray("alarm");

					mNewsArray.clear();

					// 배열의 크기만큼 반복하면서, name과 address의 값을 추출함 
					for (int i= jArr.length() - 1 ; i>=0; i--) {

						// i번째 배열 할당  
						json = jArr.getJSONObject(i);

						String name    = json.getString("name");
						String writer = json.getString("writer");
						String content    = json.getString("content");
						String date    = json.getString("date");

						Log.e("json1", name.toString());
						Log.e("json2", writer.toString());
						Log.e("json3", content.toString());
						Log.e("json4", date.toString());


						NewsList_Item newsItem;
						newsItem = new NewsList_Item(name, writer, content, date);
						mNewsArray.add(newsItem);
					}
				}

			}catch (InterruptedException e) {

			}catch(NullPointerException nullerror){

				dialogInit();

			} catch (Exception e){
				// 에러메시지 출력 
			} finally {   
				// URL 연결 해제
				urlConnection.disconnect();  
			}	
			return mNewsArray;
		}

		@Override
		protected void onPostExecute(ArrayList result) {

			mNewsAdapter.notifyDataSetChanged();
			mNewsList.onRefreshComplete();
			super.onPostExecute(result);
		}
	}

}

