package com.one.dontmind.Multivnc;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.one.Activity.R;

public class HelpActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		WebView wv = (WebView) findViewById(R.id.helpwebView);
		wv.loadUrl("file:///android_asset/help.html");
		wv.getSettings().setBuiltInZoomControls(true);
	
	}
	
}
