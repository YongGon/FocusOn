package com.one.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.one.Activity.R;
import com.one.util.SharedpreferencesUtil;

public class OptionFragment extends Fragment {

	
	ToggleButton mPushSwitch;
	SharedpreferencesUtil mSharedpreferencesUtil;
	View v;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		v = inflater.inflate(R.layout.fragment_option, container, false);

		Init();
		
		return v;

	}
	
	
	public void Init() {

		mPushSwitch = (ToggleButton)v.findViewById(R.id.on_off);
		mSharedpreferencesUtil = new SharedpreferencesUtil(getActivity());
		
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(mSharedpreferencesUtil.getValue("push", "").equals("")){
			
			mPushSwitch.setChecked(false);
			
		}else{
			mPushSwitch.setChecked(true);
		}
	
		mPushSwitch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				
				if(mPushSwitch.isChecked()){
					
					mSharedpreferencesUtil.put("push", "on");
					
				}else{
					
					mSharedpreferencesUtil.removePreferences("push");
				}
				
			}
		});
	
	}
}
