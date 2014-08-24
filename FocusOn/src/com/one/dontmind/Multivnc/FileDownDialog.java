package com.one.dontmind.Multivnc;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.one.Activity.R;
import com.one.Adapter.DownList_Adapter;
import com.one.Entities.DownList_Item;
import com.one.util.SharedpreferencesUtil;

public class FileDownDialog  extends Dialog{


	PullToRefreshListView downList;
	ArrayList<DownList_Item> downNames;
	DownList_Adapter fileAdapter;
	String Save_Path;
	String Save_folder = "/FocusOnDownload";
	//	String fileURL = "http://192.168.0.98:8080/focus_on/filesave";
	String fileURL;
	private Context mContext;

	private long latestId = -1;
	SharedpreferencesUtil mSharedpreferencesUtil;

	private DownloadManager downloadManager;
	private DownloadManager.Request request;
	private Uri urlToDownload;

	private BroadcastReceiver completeReceiver = new BroadcastReceiver(){


		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Toast.makeText(context, "�ٿ�ε尡 �Ϸ�Ǿ����ϴ�.",Toast.LENGTH_SHORT).show();
		}

	};


	public FileDownDialog(Context context) {
		super(context);

		mContext = context;
		mSharedpreferencesUtil = new SharedpreferencesUtil(context);
		fileURL = mSharedpreferencesUtil.getValue("WEB", "") + "focus_on/filesave";

		// TODO Auto-generated constructor stub
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_refresh);

		setTitle("File DownList");

		downList = (PullToRefreshListView)findViewById(R.id.refresh_ListView);

		downloadManager = (DownloadManager)mContext.getSystemService(Context.DOWNLOAD_SERVICE);

		String ext = Environment.getExternalStorageState();  
		if (ext.equals(Environment.MEDIA_MOUNTED)) {  
			Save_Path = Environment.getExternalStorageDirectory().getAbsolutePath() + Save_folder;  


			downNames = new ArrayList<DownList_Item>();
			

			fileAdapter = new DownList_Adapter(mContext, R.layout.activity_filedown_item, downNames);

			new loadJSP().execute();

			IntentFilter completeFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
			
			mContext.registerReceiver(completeReceiver, completeFilter); 
			
			downList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					try{
						File dir = new File(Save_Path);
						Log.e("filename", fileURL + "/"+ downNames.get(position-1).text1);
						// ������ �������� ���� ��� ������ ����
						if (!dir.exists()) {
							dir.mkdir();
						}
						if (new File(Save_Path + "/" + downNames.get(position-1).text1).exists() == false) {
							//loadingBar.setVisibility(View.VISIBLE); 

							urlToDownload = Uri.parse(fileURL + "/"+ downNames.get(position-1).text1);
							Log.e("url",fileURL + "/"+ downNames.get(position-1).text1);
							List<String> pathSegments = urlToDownload.getPathSegments();
							request = new DownloadManager.Request(urlToDownload);
							request.setTitle("���� �ٿ�ε�");
							request.setDescription("���� �̸� : "+downNames.get(position-1).text1);
							request.setDestinationInExternalPublicDir(Save_folder, pathSegments.get(pathSegments.size()-1));
							Environment.getExternalStoragePublicDirectory(Save_folder).mkdirs();
							latestId = downloadManager.enqueue(request);

						} else {

							showDownloadFile(downNames.get(position-1).text1,downNames.get(position-1).text2);
						}

					}catch(NullPointerException e){
						Log.e("nullex", e.toString());
						e.printStackTrace();
					}
				}

			});
			
			
			downList.setOnRefreshListener(new OnRefreshListener<ListView>() {

				@Override
				public void onRefresh(PullToRefreshBase<ListView> refreshView) {
					String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(),
							DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

					// Update the LastUpdatedLabel
					refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

					// Do work to refresh the list here.
					new RefreshTask().execute();
				}
			});
			downList.getRefreshableView();

		}
	}

	public void onBackPressed() {
		dismiss();
		mContext.unregisterReceiver(completeReceiver);
	}

	public void showDownloadFile(String File_Name, String File_extend) {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		File file = new File(Save_Path + "/" + File_Name);

		// ���� Ȯ���� ���� mime type ������ �ش�.
		if (File_extend.equals("mp3")) {
			intent.setDataAndType(Uri.fromFile(file), "audio/*");	
			//		else if (File_extend.equals("wmv")) {
			//			intent.setDataAndType(Uri.fromFile(file), "video/x-ms-wmv");
			//		}
		} else if (File_extend.equals("avi") || File_extend.equals("mp4")) {
			intent.setDataAndType(Uri.fromFile(file), "video/*");
		} else if (File_extend.equals("jpg") || File_extend.equals("jpeg")
				|| File_extend.equals("JPG") || File_extend.equals("gif")
				|| File_extend.equals("png") || File_extend.equals("bmp")) {
			intent.setDataAndType(Uri.fromFile(file), "image/*");
		} else if (File_extend.equals("txt")) {
			intent.setDataAndType(Uri.fromFile(file), "text/plain");
		} else if (File_extend.equals("doc") || File_extend.equals("docx")) {
			intent.setDataAndType(Uri.fromFile(file), "application/msword");
		} else if (File_extend.equals("xls") || File_extend.equals("xlsx")) {
			intent.setDataAndType(Uri.fromFile(file),
					"application/vnd.ms-excel");
		} else if (File_extend.equals("ppt") || File_extend.equals("pptx")) {
			intent.setDataAndType(Uri.fromFile(file),
					"application/vnd.ms-powerpoint");
		} else if (File_extend.equals("pdf")) {
			intent.setDataAndType(Uri.fromFile(file), "application/pdf");
		} else if (File_extend.equals("hwp")) {
			intent.setDataAndType(Uri.fromFile(file), "application/x-hwp");
		} 
		//		else if (File_extend.equals("zip")) {
		//			intent.setDataAndType(Uri.fromFile(file), "application/zip");
		//		}
	}

	public void dialogInit(){


		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle("�������� ����");
		dialog.setMessage("���� ������ ��Ȱ���� �ʽ��ϴ�. ��� �� �ٽ� �õ��� �ּ���.");
		dialog.setIcon(R.drawable.file);
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



	class loadJSP extends AsyncTask<Void, Void, ArrayList> {



		@Override
		protected ArrayList doInBackground(Void... params) {

			URL url = null;
			HttpURLConnection urlConnection = null;
			BufferedInputStream buf = null;


			try {
				// /// URL ������ ����
				// ������ URL ����
				url = new URL( mSharedpreferencesUtil.getValue("WEB", "") + "focus_on/jsonchange/filejson.jsp");
				// URL ����
				urlConnection = (HttpURLConnection) url.openConnection();
				Log.e("urlcon", "url ����");

				// /// ������ �ҽ��� ���ۿ� ����
				// ������ �ٿ�ε�
				buf = new BufferedInputStream(urlConnection.getInputStream());
				// �����͸� ���ۿ� ���
				BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "euc-kr"));

				String line = null;
				String page = "";


				// ������ ������ �ҽ��� �� ������ �о�(line), page�� ������
				while ((line = bufreader.readLine()) != null) {
					page += line;

				}
				Log.e("page", page.toString());

				// �о���� JSON ������ ���̾�Ʋ JSON ��ü�� ��ȯ
				JSONObject json = new JSONObject(page);
				// customers�� �ش��ϴ� �迭�� �Ҵ�
				JSONArray jArr = json.getJSONArray("file");

				downNames.clear();
				
				// �迭�� ũ�⸸ŭ �ݺ��ϸ鼭, name�� address�� ���� ������
				for (int i = jArr.length() - 1; i >= 0; i--) {

					// i��° �迭 �Ҵ�
					json = jArr.getJSONObject(i);

					// name�� address, news �� ���� ������
					String name = json.getString("name");
					String ext = json.getString("type");
					String writer = json.getString("writer");
					String date = json.getString("date");

					Log.e("json1", name.toString());
					Log.e("json2", ext.toString());
					Log.e("json3", writer.toString());

					// name�� address�� ���� �����

					DownList_Item fileItem;
					fileItem = new DownList_Item(name, ext, writer, date);
					downNames.add(fileItem);



				}


			}catch (NullPointerException nullerror){

				dialogInit();

			} catch (Exception e) {
				// �����޽��� ���
			} finally {
				// URL ���� ����
				urlConnection.disconnect();
			}
			return downNames;
		}



		protected void onPostExecute(ArrayList result) {
			
			downList.setAdapter(fileAdapter);

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
				Thread.sleep(1350);

				// /// URL ������ ����
				// ������ URL ����
				url = new URL(mSharedpreferencesUtil.getValue("WEB", "") + "focus_on/jsonchange/filejson.jsp");
				// URL ����
				urlConnection = (HttpURLConnection) url.openConnection();
				Log.e("urlcon", "url ����");

				// /// ������ �ҽ��� ���ۿ� ����
				// ������ �ٿ�ε�
				buf = new BufferedInputStream(urlConnection.getInputStream());
				// �����͸� ���ۿ� ���
				BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "euc-kr"));
				String line = null;
				String page = "";


				// ������ ������ �ҽ��� �� ������ �о�(line), page�� ������
				while ((line = bufreader.readLine()) != null) {
					page += line;

				}
				Log.e("page", page.toString());

				// �о���� JSON ������ ���̾�Ʋ JSON ��ü�� ��ȯ
				JSONObject json = new JSONObject(page);
				// customers�� �ش��ϴ� �迭�� �Ҵ�
				JSONArray jArr = json.getJSONArray("file");
				
				downNames.clear();

				// �迭�� ũ�⸸ŭ �ݺ��ϸ鼭, name�� address�� ���� ������
				for (int i = jArr.length() - 1; i >= 0; i--) {

					// i��° �迭 �Ҵ�
					json = jArr.getJSONObject(i);

					// name�� address, news �� ���� ������
					String name = json.getString("name");
					String ext = json.getString("type");
					String writer = json.getString("writer");
					String date = json.getString("date");

					Log.e("json1", name.toString());
					Log.e("json2", ext.toString());
					Log.e("json3", writer.toString());

					// name�� address�� ���� �����

					DownList_Item fileItem;
					fileItem = new DownList_Item(name, ext, writer, date);
					downNames.add(fileItem);



				}

			} catch (NullPointerException nullerr){

				dialogInit();

			} catch (Exception e) {
				// �����޽��� ���
			} finally {
				// URL ���� ����
				urlConnection.disconnect();
			}
			return downNames;
		}

		@Override
		protected void onPostExecute(ArrayList result) {

			fileAdapter.notifyDataSetChanged();
			downList.onRefreshComplete();
			super.onPostExecute(result);
		}
	}


}
