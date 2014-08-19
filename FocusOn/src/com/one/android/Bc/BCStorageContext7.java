/**
 * Copyright (c) 2011 Michael A. MacDonald
 */
package com.one.android.Bc;

import java.io.File;

import android.content.Context;

import android.os.Environment;

/**
 * @author Michael A. MacDonald
 *
 */
public class BCStorageContext7 implements IBCStorageContext {

	/* (non-Javadoc)
	 * @see com.antlersoft.android.bc.IBCStorageContext#getExternalStorageDir(android.content.Context, java.lang.String)
	 */
	@Override
	public File getExternalStorageDir(Context context, String type) {
		File f = Environment.getExternalStorageDirectory();
		f = new File(f, "Android/data/com.one.dontmind.Multivnc/files");
		if (type != null)
			f=new File(f, type);
		f.mkdirs();
		return f;
	}

}
