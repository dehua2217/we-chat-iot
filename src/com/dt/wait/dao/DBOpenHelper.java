package com.dt.wait.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
	private static final int VERSION = 1;// 定义数据库版本号
	private static final String DBNAME = "wait.db";// 定义数据库名

	public DBOpenHelper(Context context){// 定义构造函数
	
		super(context, DBNAME, null, VERSION);// 重写基类的构造函数
	}

	@Override
	public void onCreate(SQLiteDatabase db){// 创建数据库
	
		db.execSQL("create table tb_waitset(_id integer primary key,"
				+ "ip varchar(20),inOrOut varchar(20),dtComNo varchar(20),kzComNo varchar(20),outIfCheck varchar(20),csIfOut varchar(20),leftOrRight varchar(20))");// 创建配置信息表
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)// 覆写基类的onUpgrade方法，以便数据库版本更新
	{
	}
}
