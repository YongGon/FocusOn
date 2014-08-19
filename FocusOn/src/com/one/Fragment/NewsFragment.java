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
		dialog.setTitle("�������� ����");
		dialog.setMessage("���� ������ ��Ȱ���� �ʽ��ϴ�. ��� �� �ٽ� �õ��� �ּ���.");
		dialog.setIcon(R.drawable.news);
		dialog.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {

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
			// ������ ��� ���� �ν�

			URL url = null;
			HttpURLConnection urlConnection = null;
			BufferedInputStream buf = null;

			try {    
				///// URL ������ ���� 
				// ������ URL ���� 
				String addr = mSharedpreferencesUtil.getValue("WEB", "") + "focus_on/jsonchange/alarmjson.jsp";
				url = new URL(addr); 
				// URL ����
				if(url != null){



					urlConnection = (HttpURLConnection) url.openConnection(); 
					Log.e("urlcon", "url ����");

					///// ������ �ҽ��� ���ۿ� ����  
					// ������ �ٿ�ε�  			
					buf  = new BufferedInputStream(urlConnection.getInputStream());    
					// �����͸� ���ۿ� ��� 
					BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "euc-kr"));

					String line  = null;
					String page  = "";

					// ������ ������ �ҽ��� �� ������ �о�(line), page�� ������ 
					while ((line = bufreader.readLine()) != null) {
						page += line;
					}
					Log.e("page", page.toString());

					// �о���� JSON ������ ���̾�Ʋ JSON ��ü�� ��ȯ
					JSONObject json = new JSONObject(page);
					// customers�� �ش��ϴ� �迭�� �Ҵ�  
					JSONArray  jArr = json.getJSONArray("alarm");

					// �迭�� ũ�⸸ŭ �ݺ��ϸ鼭, name�� address�� ���� ������ 
					for (int i= jArr.length() - 1 ; i>=0; i--) {

						// i��° �迭 �Ҵ�  
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
				// �����޽��� ��� 
			} finally {   
				// URL ���� ����
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

				///// URL ������ ���� 
				// ������ URL ���� 
				String addr = mSharedpreferencesUtil.getValue("WEB", "") + "focus_on/jsonchange/alarmjson.jsp";
				url = new URL(addr); 
				// URL ����
				if(url != null){

					urlConnection = (HttpURLConnection) url.openConnection(); 
					Log.e("urlcon", "url ����");

					///// ������ �ҽ��� ���ۿ� ����  
					// ������ �ٿ�ε�  			
					buf  = new BufferedInputStream(urlConnection.getInputStream());    
					// �����͸� ���ۿ� ��� 
					BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "euc-kr"));

					String line  = null;
					String page  = "";

					// ������ ������ �ҽ��� �� ������ �о�(line), page�� ������ 
					while ((line = bufreader.readLine()) != null) {
						page += line;
					}
					Log.e("page", page.toString());

					// �о���� JSON ������ ���̾�Ʋ JSON ��ü�� ��ȯ
					JSONObject json = new JSONObject(page);
					// customers�� �ش��ϴ� �迭�� �Ҵ�  
					JSONArray  jArr = json.getJSONArray("alarm");

					mNewsArray.clear();

					// �迭�� ũ�⸸ŭ �ݺ��ϸ鼭, name�� address�� ���� ������ 
					for (int i= jArr.length() - 1 ; i>=0; i--) {

						// i��° �迭 �Ҵ�  
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
				// �����޽��� ��� 
			} finally {   
				// URL ���� ����
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

