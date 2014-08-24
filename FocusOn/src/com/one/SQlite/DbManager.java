package com.one.SQlite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbManager extends SQLiteOpenHelper{
	

	public DbManager(Context context,String name, CursorFactory factory, int version) {
		// TODO Auto-generated constructor stub
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 테이블을 생성함(이미 생성된 경우는 생성되지 않음)
		db.execSQL("create table message(sendTitle, sendValue, sendDate, sendDate2, num INTEGER PRIMARY KEY AUTOINCREMENT);");
	}
	
	// 존재하는 DB와 버전이 다른 경우
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}