package com.one.Adapter;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.one.Activity.R;
import com.one.Entities.NewsList_Item;

public class NewsList_Adapter extends BaseAdapter{


	Context context;
	int layout;
	ArrayList<NewsList_Item> mArrList;
	LayoutInflater inflater;

	public NewsList_Adapter(Context context, int layout, ArrayList<NewsList_Item> arrayList) {
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

			TextView title = (TextView)view.findViewById(R.id.news_textView1);
			title.setText(mArrList.get(position).text1);

			TextView writer = (TextView)view.findViewById(R.id.news_textView2);
			writer.setText(mArrList.get(position).text2);
			
			TextView date = (TextView)view.findViewById(R.id.news_textView3);
			date.setText(mArrList.get(position).date);
			
			
		}catch (NullPointerException nullerror){

			nullerror.printStackTrace();

		}catch(Exception e){
			Log.e("NewsAdapter", "NewsList_Adapter getView¿¡·¯");
		}

		view.setBackgroundResource(R.drawable.background_card);

		return view;
	}
}
