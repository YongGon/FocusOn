package com.one.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BasePreferenceUtil {
	private SharedPreferences _sharedPreferences;
	protected BasePreferenceUtil(Context $context) {
		super();
		_sharedPreferences = PreferenceManager.getDefaultSharedPreferences($context);
	}

	protected void put(String $key, String $value) {
		SharedPreferences.Editor editor = _sharedPreferences.edit();
		editor.putString($key, $value);
		editor.commit();
	}
 
	protected String get(String $key) {
		return _sharedPreferences.getString($key, null);
	}

	protected void put(String $key, boolean $value) {
		SharedPreferences.Editor editor = _sharedPreferences.edit();
		editor.putBoolean($key, $value);
		editor.commit();
	}

	protected boolean get(String $key, boolean $default) {
		return _sharedPreferences.getBoolean($key, $default);
	}

	protected void put(String $key, int $value) {
		SharedPreferences.Editor editor = _sharedPreferences.edit();
		editor.putInt($key, $value);
		editor.commit();
	}

	protected int get(String $key, int $default) {
		return _sharedPreferences.getInt($key, $default);
	}
	
}