package com.one.Adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.one.Activity.R;
import com.one.Entities.MessageList_Item;

public class MessageList_Adapter extends BaseAdapter{


	Context context;
	int layout;
	ArrayList<MessageList_Item> mArrList;
	LayoutInflater inflater;

	public MessageList_Adapter(Context context, int layout, ArrayList<MessageList_Item> arrayList) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.layout = layout;
		this.mArrList = arrayList;
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount(){
		return mArrList.size();
	}

	public Object getItem(int position){
		return  mArrList.get(position);
	}

	public long getItemId(int position){
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup viewgroup) {
		// TODO Auto-generated method stub

		try{
			if(view == null){
				view = inflater.inflate(layout, viewgroup, false);
			}

			TextView title = (TextView)view.findViewById(R.id.messageList_item_title);
			title.setText(mArrList.get(position).sendTitle);

			TextView date = (TextView)view.findViewById(R.id.messageList_item_sendDate);
			date.setText(mArrList.get(position).sendDate);
			
			TextView date2 = (TextView)view.findViewById(R.id.messageList_item_sendDate2);
			date2.setText(mArrList.get(position).sendDate2);
			
		}catch(Exception e){
			Log.e("messageAdapter", "MessageList_Adapter getView¿¡·¯");
		}

		view.setBackgroundResource(R.drawable.background_card);
		
		return view;
	}

}
