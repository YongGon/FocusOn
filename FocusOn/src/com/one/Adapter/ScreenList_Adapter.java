package com.one.Adapter;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import com.one.Activity.R;
import com.one.Entities.ScreenList_Item;

public class ScreenList_Adapter extends BaseAdapter{

	ArrayList<ScreenList_Item> mArrayList;
	Context context;
	int layout ;
	LayoutInflater inflater;


	public ScreenList_Adapter(Context context, int layout, ArrayList<ScreenList_Item> arraylist) {
		// TODO Auto-generated constructor stub
		try{

			this.context = context;
			this.layout = layout;
			this.mArrayList = arraylist;
			this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}catch(Exception e){
			e.printStackTrace();
		}

	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void dialogInit(){


		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
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
	public View getView(int position, View view, ViewGroup container) {
		// TODO Auto-generated method stub

		try{
			if(view == null){
				view = inflater.inflate(layout, container, false);
			}

			TextView name = (TextView)view.findViewById(R.id.screen_TextView);
			name.setText(mArrayList.get(position).name);
			
			Log.e("name", mArrayList.get(position).name);

			
		}catch(Exception e){
			Log.e("ScreenList_Adapter", "ScreenList_Adapter getView에러");
		}


		view.setBackgroundResource(R.drawable.background_card);

		return view;

	}




}
