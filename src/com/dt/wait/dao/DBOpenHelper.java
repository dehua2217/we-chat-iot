package com.dt.wait.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
	private static final int VERSION = 1;// �������ݿ�汾��
	private static final String DBNAME = "wait.db";// �������ݿ���

	public DBOpenHelper(Context context){// ���幹�캯��
	
		super(context, DBNAME, null, VERSION);// ��д����Ĺ��캯��
	}

	@Override
	public void onCreate(SQLiteDatabase db){// �������ݿ�
	
		db.execSQL("create table tb_waitset(_id integer primary key,"
				+ "ip varchar(20),inOrOut varchar(20),dtComNo varchar(20),kzComNo varchar(20),outIfCheck varchar(20),csIfOut varchar(20),leftOrRight varchar(20))");// ����������Ϣ��
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)// ��д�����onUpgrade�������Ա����ݿ�汾����
	{
	}
}
