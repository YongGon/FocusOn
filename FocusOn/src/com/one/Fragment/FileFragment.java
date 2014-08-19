package com.one.Fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.one.Activity.FileDownActivity;
import com.one.Activity.R;
import com.one.Adapter.FileList_Adapter;
import com.one.Entities.FileList_Item;

public class FileFragment extends Fragment {

	ListView mFileList;
	ArrayList<FileList_Item> fileNames = new ArrayList<FileList_Item>();
	FileList_Adapter downAdapter;
//	private List<String> mFileNames = new ArrayList<String>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View v = inflater.inflate(R.layout.fragment_file, container, false);

		mFileList = (ListView)v.findViewById(R.id.file_ListView);
		
		
		return v;

	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		final String DirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/FocusOnDownload";
		
		
		mFileList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				
				AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
				
				alert.setTitle("확인창");
				alert.setIcon(R.drawable.file);
				alert.setMessage("제목 : " + fileNames.get(position).title + "을 삭제하시겠습니까?" );
				alert.setCancelable(true);
				alert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
					
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						File file = new File(DirPath+"/"+fileNames.get(position).title);
						file.delete();
						
						fileNames.clear();
						downAdapter.notifyDataSetChanged();
						updateFileList();
						
					}
					
				}).setNegativeButton("취소", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
					
				});
				
				
				alert.show();
				return false;
				
			}
			
			
			});
	
		
		mFileList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String FileName = fileNames.get(position).title;
				int num = FileName.lastIndexOf('.');
				String File_extend="";
				for(int i=num+1;i<FileName.length();i++){
					File_extend+=FileName.charAt(i);
				}
				Log.e("type",File_extend);
	//			final String excelFileName = "text.xls";= =
				File file = new File(DirPath , FileName);
				Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);  

				String extension = MimeTypeMap.getFileExtensionFromUrl(FileName);
				String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
    //   		Intent intent = new Intent(Intent.ACTION_VIEW);
	//			intent.setDataAndType(Uri.fromFile(excelFile), mimeType);
		        if (File_extend.equals("mp3")) {  
		            intent.setDataAndType(Uri.fromFile(file), "audio/*");  
		        } else if (File_extend.equals("avi") || File_extend.equals("mp4")) {
					intent.setDataAndType(Uri.fromFile(file), "video/x-msvideo");
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
		        }  
				startActivity(intent);

				
			}
		});
		
		updateFileList();

	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		
		inflater.inflate(R.menu.messagelist, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		Intent intent = new Intent(getActivity(), FileDownActivity.class);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.activity_in, R.anim.splash_out);
		
		return super.onOptionsItemSelected(item);
	}

	public void updateFileList()
	{
		String ext = Environment.getExternalStorageState();
		String path = null;
		if(ext.equals(Environment.MEDIA_MOUNTED)) {
			path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FocusOnDownload/";
		}
		else
		{
			path = Environment.MEDIA_UNMOUNTED;
		}

		File files = new File(path);
		//            File files = new File(FILE_PATH);
//		String name = files.getName();
		downAdapter = new FileList_Adapter(getActivity(), R.layout.fragment_file_list, fileNames);
//		FileList_Item listitem = new FileList_Item(name);

		if(files.listFiles() != null)
		{
			fileNames.clear();
			for(File file : files.listFiles())
			{
				
				long lastday = file.lastModified();
				Date d = new Date();
				d.setTime(lastday);
				SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy/MM/dd", Locale.KOREA );
				String date = formatter.format( d );
				FileList_Item fi = new FileList_Item(file.getName(),date);
				fileNames.add(fi);
			}
		}
		mFileList.setAdapter(downAdapter);
	}
}