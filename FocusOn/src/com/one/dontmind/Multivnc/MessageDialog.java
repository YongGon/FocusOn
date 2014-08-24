package com.one.dontmind.Multivnc;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.one.Activity.R;
import com.one.SQlite.DbManager;
import com.one.util.SharedpreferencesUtil;

public class MessageDialog extends Dialog {

	private static final String ERROR = "error";
	//	private String mWeb_Server = "http://192.168.0.98:8080/focus_on/mail_receive.jsp";

	private VncCanvasActivity _canvasActivity;
	private EditText mTitle_EditText;
	private EditText mContent_EditText;
	private Button mLeftbtn, mRightbtn;
	private RequestQueue mRequestQueue;
	private StringRequest mStringRequest;
	private Context mContext;
	private SharedpreferencesUtil mSharedpreferencesUtil;
	//	private String mWeb_Server = mSharedpreferencesUtil.getValue("WEB", "") + "focus_on/mail_receive.jsp";
	private String mWeb_Server;
	private GetTesk getTesk;


	public MessageDialog(Context context) {
		super(context);

		mContext = context;
		setOwnerActivity((Activity)context);
		_canvasActivity = (VncCanvasActivity)context;
		mRequestQueue = Volley.newRequestQueue(context);
		mSharedpreferencesUtil = new SharedpreferencesUtil(context);
		mWeb_Server = mSharedpreferencesUtil.getValue("WEB", "") + "focus_on/mail_receive.jsp";

	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_messagesend);
		setTitle("쪽지 전송");

		getTesk = new GetTesk();
		mSharedpreferencesUtil = new SharedpreferencesUtil(mContext);
		mTitle_EditText = (EditText)findViewById(R.id.send_Title_EditText);
		mContent_EditText = (EditText)findViewById(R.id.send_Value_EditText);
		mLeftbtn = (Button)findViewById(R.id.Left_btn);
		mRightbtn = (Button)findViewById(R.id.right_btn);


		mLeftbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if(mTitle_EditText.getText().toString().equals("") || mContent_EditText.getText().toString().equals("")){
					
					Toast.makeText(mContext, "쪽지 내용을 입력하세요.", Toast.LENGTH_SHORT).show();

				}else{

					Log.e("Exception" , mTitle_EditText.getText().toString());
					Log.e("Exception" , mContent_EditText.getText().toString());
					Log.e("Exception" , mSharedpreferencesUtil.getValue("user_name", ""));
					try{

						getTesk.execute();
						//mRequestQueue.add(MessageSend());

						String sendTitle, sendValue, sendDate, sendDate2;
						SimpleDateFormat formatter = new SimpleDateFormat ( "yy년MM월dd일", Locale.KOREA );
						SimpleDateFormat formatter2 = new SimpleDateFormat ( "HH시 mm분", Locale.KOREA );
						Date currentTime = new Date ();

						sendTitle = mTitle_EditText.getText().toString();
						sendValue = mContent_EditText.getText().toString();
						sendDate = formatter.format( currentTime );
						sendDate2 = formatter2.format( currentTime );

						// DB객체 생성(DB가 존재하지 않으면 생성함)
						DbManager dbmgr = new DbManager(mContext, "messageListDB", null, 1);

						// DB연결
						SQLiteDatabase sdb = dbmgr.getWritableDatabase();

						// message 테이블에 추출정보 추가
						sdb.execSQL("insert into message values('" + sendTitle + "', '" + sendValue 
								+"', '" + sendDate + "', '" + sendDate2 + "', " + null + ");");

						// DB닫음
						dbmgr.close();

						dismiss();
					} catch (SQLiteException e) {
						Log.e("SQLiteException", e.toString());
					} catch (Exception ee){
						AlertDialog.Builder alert = new AlertDialog.Builder(getOwnerActivity());

						alert.setTitle("알림창");
						alert.setMessage("에러 발생");
						alert.setIcon(R.drawable.message);
						alert.setCancelable(false);
						alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						}); 

						AlertDialog alertdialog = alert.create();
						alertdialog.show();

						Log.e("Exception" , ee.toString());
						ee.printStackTrace();
					}

				}
			}
		});

		mRightbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				dismiss();
			}
		});

	}


	public StringRequest MessageSend(){


		mStringRequest = new StringRequest(Request.Method.POST, mWeb_Server, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub

				System.err.println("response   :  " + response);

			}


		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError e) {
				// TODO Auto-generated method stub
				VolleyLog.d(ERROR, e.getMessage());
			}


		}){

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// TODO Auto-generated method stub

				Map<String, String> pa = new HashMap<String, String>();

				pa.put("title", mTitle_EditText.getText().toString());
				pa.put("content", mContent_EditText.getText().toString());
				pa.put("name", mSharedpreferencesUtil.getValue("user_name", ""));

				return pa;

			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				// TODO Auto-generated method stub

				Map<String, String> pa = new HashMap<String, String>();
				pa.put("Content-Type","application/x-www-form-urlencoded");

				return pa;
			}
		};

		return mStringRequest;
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		_canvasActivity.getMenuInflater().inflate(R.menu.messagesend, menu);

		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)  {

		return super.onOptionsItemSelected(item);
	}

	public void dialogInit(){


		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle("서버접속 오류");
		dialog.setMessage("서버 접속이 원활하지 않습니다. 잠시 후 다시 시도해 주세요.");
		dialog.setIcon(R.drawable.message);
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



	class GetTesk extends AsyncTask<Void,Void,Void>{

		protected Void doInBackground(Void... param) {

			Calendar date = Calendar.getInstance();
			String today = date.get(date.YEAR) + "-" + (date.get(date.MONTH) + 1) + "-"
					+ date.get(date.DATE);


			try{

				HttpClient client = new DefaultHttpClient();

				HttpPost post = new HttpPost(mWeb_Server);

				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

				params.add(new BasicNameValuePair("title",mTitle_EditText.getText().toString()));
				params.add(new BasicNameValuePair("content",mContent_EditText.getText().toString()));
				if(!mSharedpreferencesUtil.getValue("user_name", "").equals("")){
					params.add(new BasicNameValuePair("writer",mSharedpreferencesUtil.getValue("user_name", "")));
				}else{
					params.add(new BasicNameValuePair("writer",mSharedpreferencesUtil.getValue("focus_name", "")));
				}
				params.add(new BasicNameValuePair("date", today));
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);

				post.setEntity(ent);

				HttpResponse responsePOST = client.execute(post);

				HttpEntity resEntity = responsePOST.getEntity();   

				if (resEntity != null) {      

					Log.i("RESPONSE", EntityUtils.toString(resEntity));  

				}
			}catch (NullPointerException nullerror){

				dialogInit();

			}catch(IOException e){
				e.printStackTrace();
			}

			return null;   

		}

	}
}
