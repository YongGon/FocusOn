package com.one.Entities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.view.Window;

public class CustomeDialog extends AlertDialog{

	public CustomeDialog(Context context) {
		super(context);
		
	}
	
	@Override
	public Window getWindow() {
		// TODO Auto-generated method stub
		 super.getWindow().setBackgroundDrawableResource(Color.TRANSPARENT);
		 
		 return null;
	}

	public void setNegativeButton(String string, OnClickListener onClickListener) {
		// TODO Auto-generated method stub
		
	}

	public void setPositiveButton(String string, OnClickListener onClickListener) {
		// TODO Auto-generated method stub
		
	}

	public AlertDialog create() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
