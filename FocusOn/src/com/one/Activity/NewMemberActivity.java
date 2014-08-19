package com.one.Activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.one.util.SharedpreferencesUtil;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

public class NewMemberActivity extends Activity{

	private final static String ERROR = "Error";
	private String ID_CHECK; 
	private String WEB_SERVER;
	private String 	ok;
	
	private SharedpreferencesUtil mSharedpreferencesUtil;
	private FloatLabeledEditText mID, mPasswd, mName, mAge, mGroup, mEmail, mPhone;

	private Button mCheckID;
	int count = 0;
	String idCheck;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newmember);

		Init();

		mCheckID.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {



				RequestQueue request = Volley.newRequestQueue(getApplicationContext());  

				request.add(new StringRequest(Request.Method.POST, ID_CHECK, new Response.Listener<String>() {  
					@Override  
					public void onResponse(String response) {


						JSONObject JsonObject;
						ok = "";

						int position = response.lastIndexOf('{');

						String json = "";
						//	for(int i = position+2 ; i < response.length() - 4 ; i++ ){
						for(int i = position; i < response.length()-1; i++){
							json += response.charAt(i);
						}


						try {
							Log.e("response", json);
							JsonObject = new JSONObject(json);
							ok = JsonObject.getString("TRUE");
							Log.e("ok",ok);


							if(ok.equals("true")){
								AlertDialog.Builder dialog1 = new AlertDialog.Builder(NewMemberActivity.this);
								dialog1.setTitle("아이디 중복확인");
								dialog1.setMessage("이미 가입된 아이디입니다.");
								dialog1.setIcon(R.drawable.focuson);
								dialog1.setPositiveButton("확인", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub

										dialog.dismiss();

									}
								});

								AlertDialog alert1 = dialog1.create();
								alert1.show();
							}

							else if(mID.getText().toString().equals("")){
								AlertDialog.Builder dialog1 = new AlertDialog.Builder(NewMemberActivity.this);
								dialog1.setTitle("아이디 중복확인");
								dialog1.setMessage("아이디를 입력해주세요.");
								dialog1.setIcon(R.drawable.focuson);
								dialog1.setPositiveButton("확인", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub

										dialog.dismiss();

									}
								});

								AlertDialog alert1 = dialog1.create();
								alert1.show();
							}



							else if(ok.equals("false")){
								AlertDialog.Builder dialog2 = new AlertDialog.Builder(NewMemberActivity.this);
								dialog2.setTitle("아이디 중복확인");
								dialog2.setMessage("사용할 수 있는 아이디입니다.");
								dialog2.setIcon(R.drawable.focuson);
								dialog2.setPositiveButton("확인", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub

										dialog.dismiss();

									}
								});
								count = 1;
								idCheck = mID.getText().toString();
								AlertDialog alert2 = dialog2.create();
								alert2.show();
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}

					}  
				}, new Response.ErrorListener() {  
					@Override  
					public void onErrorResponse(VolleyError error) {  
						VolleyLog.d(ERROR, error.getMessage());  
					}  
				}){
					@Override
					protected Map<String, String> getParams() throws AuthFailureError {

						Map<String, String> params = new HashMap<String, String>();
						params.put("id_check", mID.getText().toString());



						return params;
					}

					@Override
					public Map<String, String> getHeaders() throws AuthFailureError {

						Map<String, String> params = new HashMap<String, String>();
						params.put("Content-Type","application/x-www-form-urlencoded");

						return params;
					}
				});	




			}
		});

	}

	private void Init(){

		mID = (FloatLabeledEditText)findViewById(R.id.member_id);
		mCheckID = (Button)findViewById(R.id.member_id_check);
		mPasswd = (FloatLabeledEditText)findViewById(R.id.member_passwd);
		mName = (FloatLabeledEditText)findViewById(R.id.member_name);
		mAge = (FloatLabeledEditText)findViewById(R.id.member_age);
		mGroup = (FloatLabeledEditText)findViewById(R.id.member_group);
		mEmail = (FloatLabeledEditText)findViewById(R.id.member_mail);
		mPhone = (FloatLabeledEditText)findViewById(R.id.member_phone);
		mSharedpreferencesUtil = new SharedpreferencesUtil(getApplicationContext());
		WEB_SERVER = mSharedpreferencesUtil.getValue("WEB", "") + "focus_on/receive.jsp";
		ID_CHECK = mSharedpreferencesUtil.getValue("WEB", "") + "focus_on/checkId.jsp";
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		getMenuInflater().inflate(R.menu.messagesend, menu);
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case R.id.send_bt:
			if(count == 0){
				AlertDialog.Builder dialog1 = new AlertDialog.Builder(NewMemberActivity.this);
				dialog1.setTitle("회원가입");
				dialog1.setMessage("아이디 중복확인을 해주세요.");
				dialog1.setIcon(R.drawable.focuson);
				dialog1.setPositiveButton("확인", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						dialog.dismiss();

					}
				});
				AlertDialog alert1 = dialog1.create();
				alert1.show();
				
				break;
				
			}
			
			else if(!mID.getText().toString().equals(idCheck)){
				AlertDialog.Builder dialog1 = new AlertDialog.Builder(NewMemberActivity.this);
				dialog1.setTitle("회원가입");
				dialog1.setMessage("아이디 중복확인을 해주세요.");
				dialog1.setIcon(R.drawable.focuson);
				dialog1.setPositiveButton("확인", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						dialog.dismiss();

					}
				});
				AlertDialog alert1 = dialog1.create();
				alert1.show();
				
				break;
				
			}
			
			else if(mPasswd.getText().toString().equals("")){
				AlertDialog.Builder dialog1 = new AlertDialog.Builder(NewMemberActivity.this);
				dialog1.setTitle("회원가입");
				dialog1.setMessage("비밀번호를 입력해주세요.");
				dialog1.setIcon(R.drawable.focuson);
				dialog1.setPositiveButton("확인", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						dialog.dismiss();

					}
				});
				AlertDialog alert1 = dialog1.create();
				alert1.show();
				
				break;
				
			}
			else if(mName.getText().toString().equals("")){
				AlertDialog.Builder dialog1 = new AlertDialog.Builder(NewMemberActivity.this);
				dialog1.setTitle("회원가입");
				dialog1.setMessage("이름을 입력해주세요.");
				dialog1.setIcon(R.drawable.focuson);
				dialog1.setPositiveButton("확인", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						dialog.dismiss();

					}
				});
				AlertDialog alert1 = dialog1.create();
				alert1.show();
				
				break;
				
			}
			else if(mAge.getText().toString().equals("")){
				AlertDialog.Builder dialog1 = new AlertDialog.Builder(NewMemberActivity.this);
				dialog1.setTitle("회원가입");
				dialog1.setMessage("나이를 입력해주세요.");
				dialog1.setIcon(R.drawable.focuson);
				dialog1.setPositiveButton("확인", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						dialog.dismiss();

					}
				});
				AlertDialog alert1 = dialog1.create();
				alert1.show();
				
				break;
				
			}
			else if(mGroup.getText().toString().equals("")){
				AlertDialog.Builder dialog1 = new AlertDialog.Builder(NewMemberActivity.this);
				dialog1.setTitle("회원가입");
				dialog1.setMessage("소속을 입력해주세요.");
				dialog1.setIcon(R.drawable.focuson);
				dialog1.setPositiveButton("확인", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						dialog.dismiss();

					}
				});
				AlertDialog alert1 = dialog1.create();
				alert1.show();
				
				break;
				
			}
			
			else if(mEmail.getText().toString().equals("")){
				AlertDialog.Builder dialog1 = new AlertDialog.Builder(NewMemberActivity.this);
				dialog1.setTitle("회원가입");
				dialog1.setMessage("이메일을 입력해주세요.");
				dialog1.setIcon(R.drawable.focuson);
				dialog1.setPositiveButton("확인", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						dialog.dismiss();

					}
				});
				AlertDialog alert1 = dialog1.create();
				alert1.show();
				
				break;
				
			}
			
			else if(mPhone.getText().toString().equals("")){
				AlertDialog.Builder dialog1 = new AlertDialog.Builder(NewMemberActivity.this);
				dialog1.setTitle("회원가입");
				dialog1.setMessage("핸드폰 번호를 입력해주세요.");
				dialog1.setIcon(R.drawable.focuson);
				dialog1.setPositiveButton("확인", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						dialog.dismiss();

					}
				});
				AlertDialog alert1 = dialog1.create();
				alert1.show();
				
				break;
				
			}
			
			
			
			
			RequestQueue request = Volley.newRequestQueue(getApplicationContext());  

			request.add(new StringRequest(Request.Method.POST, WEB_SERVER, new Response.Listener<String>() {  
				@Override  
				public void onResponse(String response) {

					System.err.println("   response   :  " + response);
					finish();
				}  
			}, new Response.ErrorListener() {  
				@Override  
				public void onErrorResponse(VolleyError error) {  
					VolleyLog.d(ERROR, error.getMessage());  
				}  
			}){
				@Override
				protected Map<String, String> getParams() throws AuthFailureError {

					Map<String, String> params = new HashMap<String, String>();
					params.put("id", mID.getText().toString());
					params.put("passwd", mPasswd.getText().toString());
					params.put("name", mName.getText().toString());
					params.put("age",  mAge.getText().toString());
					params.put("group",  mGroup.getText().toString());
					params.put("email",  mEmail.getText().toString());
					params.put("phone",  mPhone.getText().toString());

					return params;
				}

				@Override
				public Map<String, String> getHeaders() throws AuthFailureError {

					Map<String, String> params = new HashMap<String, String>();
					params.put("Content-Type","application/x-www-form-urlencoded");

					return params;
				}
			});




		}


		return super.onOptionsItemSelected(item);
	}

	private String getSubUrl(){

		String sub_url = "?id="+mID.getText().toString()+"&passwd="+mPasswd.getText().toString() +
				"&name="+mName.getText().toString();

		return sub_url;
	}

}
