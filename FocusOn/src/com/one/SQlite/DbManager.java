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
		// ���̺��� ������(�̹� ������ ���� �������� ����)
		db.execSQL("create table message(sendTitle, sendValue, sendDate, sendDate2, num INTEGER PRIMARY KEY AUTOINCREMENT);");
	}
	
	// �����ϴ� DB�� ������ �ٸ� ���
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}