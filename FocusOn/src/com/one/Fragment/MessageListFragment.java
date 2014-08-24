package com.one.Fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.one.Activity.FocusActivity;
import com.one.Activity.MessageContentActivity;
import com.one.Activity.R;
import com.one.Adapter.MessageList_Adapter;
import com.one.Entities.MessageList_Item;
import com.one.SQlite.DbManager;

public class MessageListFragment extends Fragment {

	View mView;
	ListView mListView;
	ArrayList<MessageList_Item> mArrayList;
	MessageList_Adapter mMsgAdapter;



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e("1", "onCreateView 호출됨");

		mView = inflater.inflate(R.layout.fragment_messagelist, container, false);
		mListView = (ListView)mView.findViewById(R.id.messageListView);
		
		

		return mView;
	}
	

	@Override
	public void onResume() {
		super.onResume();
		
		Log.e("onResume", "onResume 호출됨");
		

		mArrayList = new ArrayList<MessageList_Item>();
		mMsgAdapter = new MessageList_Adapter(getActivity(), R.layout.fragment_messagelist_item, mArrayList);
		mListView.setAdapter(mMsgAdapter);
		
		AnimationSet animSet = new AnimationSet(true);
		Animation anim_RighttoLeft = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, 
				             Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		
		Animation anim_Alpha = new AlphaAnimation(0.0f, 1.0f);
		
		
		anim_RighttoLeft.setDuration(500);
		animSet.addAnimation(anim_RighttoLeft);
		anim_Alpha.setDuration(500);
		animSet.addAnimation(anim_Alpha);
		
		LayoutAnimationController layout_anim_controller = new LayoutAnimationController(animSet, 0.25f);
		mListView.setLayoutAnimation(layout_anim_controller);
		
		try{
			DbManager dbmgr = new DbManager(getActivity(), "messageListDB", null, 1 );
			SQLiteDatabase sql = dbmgr.getReadableDatabase();
			String select = "select * from message order by num desc";
			Cursor cursor = sql.rawQuery(select, null);

			while(cursor.moveToNext()){
				String sendTitle = cursor.getString(0);
				String sendValue = cursor.getString(1);
				String sendDate = cursor.getString(2);
				String sendDate2 = cursor.getString(3);

				
				MessageList_Item msgList_item;
				msgList_item = new MessageList_Item(sendTitle, sendValue, sendDate, sendDate2);
				mArrayList.add(msgList_item);

			}
			
			// cursor 객체 닫음
			cursor.close();
			// dbmgr 객체 닫음
			dbmgr.close();

		} catch(SQLiteException e){
			Log.e("messageListDB", e.toString());
		}
		
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				String position_title = mArrayList.get(position).sendTitle;
				String Position_value = mArrayList.get(position).sendValue;
				
				Intent intent = new Intent(getActivity(), MessageContentActivity.class);
				intent.putExtra("Title", position_title);
				intent.putExtra("Value", Position_value);
				FocusActivity.OnOffFlug = false;
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.activity_in, R.anim.splash_out);

			}
		
		});
		
		
		
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				
				final String title = mArrayList.get(position).sendTitle;
				final String value = mArrayList.get(position).sendValue;
				final String date = mArrayList.get(position).sendDate;
				final String date2 = mArrayList.get(position).sendDate2;
				
				AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
				
//				CustomeDialog alert = new CustomeDialog(getActivity());
				
				alert.setTitle("확인창");
				alert.setIcon(R.drawable.message);
				alert.setMessage("제목 : " + mArrayList.get(position).sendTitle +
						"\n전송날짜 : "+ mArrayList.get(position).sendDate +"\n기록을 삭제하시겠습니까?" );
				alert.setCancelable(true);
				alert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						DbManager dbmgr = new DbManager(getActivity(), "messageListDB", null, 1);
						try{
							// DB 연결
							SQLiteDatabase sdb = dbmgr.getReadableDatabase();
							// SQL문 실행 결과를 cursor 객체로 받음 
							String delete = " delete from message where sendTitle = '"+title+
									"' and sendValue = '"+ value +"' and sendDate = '"+ date +"' and sendDate2 = '" + date2 + "'";
							sdb.execSQL(delete);

							mArrayList.clear();
							mMsgAdapter.notifyDataSetChanged();

							// SQL문 실행 결과를 cursor 객체로 받음 
							Cursor cursor = sdb.rawQuery("select * from message", null);

							// cursor 객체로 할당된 customers 테이블 데이터를 한 행씩 이동하면서 출력함
							while(cursor.moveToNext()) {
								// 행의 첫 번째 열(0), 두 번째 열(1), 세 번째 열(2)을 각각 추출함  
								String sendTitle = cursor.getString(0);
								String sendValue = cursor.getString(1);
								String sendDate = cursor.getString(2);
								String sendDate2 = cursor.getString(3);

								MessageList_Item  msgList_item;
								msgList_item = new MessageList_Item(sendTitle, sendValue, sendDate, sendDate2);
								mArrayList.add(msgList_item);
							}

							cursor.close();
							// dbmgr 객체 닫음
							dbmgr.close();

						} catch (SQLiteException e) {

						}
					}
				});
				
				alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						dialog.cancel();
					}
				});
				
				AlertDialog alertdialog = alert.create();
				alertdialog.show();
				
				return true;
			}
			
			
		});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		
		
		super.onCreateOptionsMenu(menu, inflater);

	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		Log.e("1", "onAttach 호출됨");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.e("1", "onCreate 호출됨");
		
		// 액션바 icon 생성하기위한 메서드 호출
		setHasOptionsMenu(true);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("1", "onDestroy 호출됨");
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		Log.e("1", "onDestroyView 호출됨");
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.e("1", "onPause 호출됨");
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.e("1", "onStart 호출됨");
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.e("1", "onStop 호출됨");

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub


		return super.onOptionsItemSelected(item);

	}
}
