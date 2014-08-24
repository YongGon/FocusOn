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
		Log.e("1", "onCreateView ȣ���");

		mView = inflater.inflate(R.layout.fragment_messagelist, container, false);
		mListView = (ListView)mView.findViewById(R.id.messageListView);
		
		

		return mView;
	}
	

	@Override
	public void onResume() {
		super.onResume();
		
		Log.e("onResume", "onResume ȣ���");
		

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
			
			// cursor ��ü ����
			cursor.close();
			// dbmgr ��ü ����
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
				
				alert.setTitle("Ȯ��â");
				alert.setIcon(R.drawable.message);
				alert.setMessage("���� : " + mArrayList.get(position).sendTitle +
						"\n���۳�¥ : "+ mArrayList.get(position).sendDate +"\n����� �����Ͻðڽ��ϱ�?" );
				alert.setCancelable(true);
				alert.setPositiveButton("����", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						DbManager dbmgr = new DbManager(getActivity(), "messageListDB", null, 1);
						try{
							// DB ����
							SQLiteDatabase sdb = dbmgr.getReadableDatabase();
							// SQL�� ���� ����� cursor ��ü�� ���� 
							String delete = " delete from message where sendTitle = '"+title+
									"' and sendValue = '"+ value +"' and sendDate = '"+ date +"' and sendDate2 = '" + date2 + "'";
							sdb.execSQL(delete);

							mArrayList.clear();
							mMsgAdapter.notifyDataSetChanged();

							// SQL�� ���� ����� cursor ��ü�� ���� 
							Cursor cursor = sdb.rawQuery("select * from message", null);

							// cursor ��ü�� �Ҵ�� customers ���̺� �����͸� �� �྿ �̵��ϸ鼭 �����
							while(cursor.moveToNext()) {
								// ���� ù ��° ��(0), �� ��° ��(1), �� ��° ��(2)�� ���� ������  
								String sendTitle = cursor.getString(0);
								String sendValue = cursor.getString(1);
								String sendDate = cursor.getString(2);
								String sendDate2 = cursor.getString(3);

								MessageList_Item  msgList_item;
								msgList_item = new MessageList_Item(sendTitle, sendValue, sendDate, sendDate2);
								mArrayList.add(msgList_item);
							}

							cursor.close();
							// dbmgr ��ü ����
							dbmgr.close();

						} catch (SQLiteException e) {

						}
					}
				});
				
				alert.setNegativeButton("���", new DialogInterface.OnClickListener() {
					
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
		
		Log.e("1", "onAttach ȣ���");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.e("1", "onCreate ȣ���");
		
		// �׼ǹ� icon �����ϱ����� �޼��� ȣ��
		setHasOptionsMenu(true);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("1", "onDestroy ȣ���");
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		Log.e("1", "onDestroyView ȣ���");
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.e("1", "onPause ȣ���");
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.e("1", "onStart ȣ���");
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.e("1", "onStop ȣ���");

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub


		return super.onOptionsItemSelected(item);

	}
}
