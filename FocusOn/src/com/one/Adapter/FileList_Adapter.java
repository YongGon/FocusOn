package com.one.Adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.one.Activity.R;
import com.one.Entities.FileList_Item;

public class FileList_Adapter extends BaseAdapter{

	Context context;
	int layout;
	ArrayList<FileList_Item> mArrList;
	LayoutInflater inflater;

	public FileList_Adapter(Context context, int layout, ArrayList<FileList_Item> arrayList) {
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

	public View getView(final int position, View view, ViewGroup viewgroup) {
		// TODO Auto-generated method stub

		try{
			if(view == null){
				view = inflater.inflate(layout, viewgroup, false);
			}

			TextView name = (TextView)view.findViewById(R.id.file_name);
			name.setText(mArrList.get(position).title);

			TextView date = (TextView)view.findViewById(R.id.file_date);
			date.setText(mArrList.get(position).date);

			String type = (String)name.getText();
			String ext=type.substring(type.lastIndexOf(".")+1, type.length()).toLowerCase();

			ImageView img = (ImageView)view.findViewById(R.id.type_img);




			Log.e("ext",ext);


			if (ext.equals("jpg") || ext.equals("jpeg")) {
				img.setImageResource(R.drawable.jpg);
			} else if (ext.equals("txt")) {
				img.setImageResource(R.drawable.txt);
			}else if (ext.equals("gif")) {
				img.setImageResource(R.drawable.gif);
			}else if (ext.equals("zip")) {
				img.setImageResource(R.drawable.zip);
			}else if (ext.equals("wmv")) {
				img.setImageResource(R.drawable.wmv);
			}else if (ext.equals("png")) {
				img.setImageResource(R.drawable.png);
			}else if (ext.equals("bmp")) {
				img.setImageResource(R.drawable.bmp);
			}else if (ext.equals("wav")) {
				img.setImageResource(R.drawable.wav);
			} else if (ext.equals("doc") || ext.equals("docx")) {
				img.setImageResource(R.drawable.doc);
			} else if (ext.equals("xls") || ext.equals("xlsx")) {
				img.setImageResource(R.drawable.xls);
			} else if (ext.equals("ppt") || ext.equals("pptx")) {
				img.setImageResource(R.drawable.pptx);
			} else if (ext.equals("pdf")) {
				img.setImageResource(R.drawable.pdf);
			} else if (ext.equals("hwp")) {
				img.setImageResource(R.drawable.hwp);
			} else if (ext.equals("mp3")) {
				img.setImageResource(R.drawable.mp3);
			} else if (ext.equals("mp4") || ext.equals("avi")) {
				img.setImageResource(R.drawable.mp4);
			} else {
				img.setImageResource(R.drawable.focuson);
			}


		}catch(Exception e){
			Log.e("FileAdapter", "FileList_Adapter getView����");
		}

		view.setBackgroundResource(R.drawable.background_card);

		return view;
	}



}
