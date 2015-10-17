package com.dt.wait.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.dt.wait.model.TbWaitSet;

public class WaitSetDao {
	private DBOpenHelper helper;// ����DBOpenHelper����
	private SQLiteDatabase db;// ����SQLiteDatabase����

	public WaitSetDao(Context context)// ���幹�캯��
	{
		helper = new DBOpenHelper(context);// ��ʼ��DBOpenHelper����
	}

	/**
	 * ��ӱ�ǩ��Ϣ
	 * 
	 * @param tb_flag
	 */
	public void add(TbWaitSet tb_waitset) {
		try {
		db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
		} catch(SQLiteException e){
			
		db = helper.getReadableDatabase();	
		}
		db.execSQL("insert into tb_waitset (_id,ip,inOrOut,dtComNo,kzComNo,outIfCheck,csIfOut,leftOrRight) values (?,?,?,?,?,?,?,?)", new Object[] {
				tb_waitset.get_id(), tb_waitset.getIp(),tb_waitset.getInOrOut(), tb_waitset.getDtComNo(),tb_waitset.getKzComNo(),tb_waitset.getOutIfChecked(),tb_waitset.getCsIfOut(),tb_waitset.getLeftOrRight()});// ִ����ӱ�ǩ��Ϣ����
	}

	/*
	 * ���±�ǩ��Ϣ
	 * 
	 * @param tb_flag
	 */
	public void update(TbWaitSet tb_waitset) {
		try {
			db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
			} catch(SQLiteException e){
				
			db = helper.getReadableDatabase();	
			}
		db.execSQL("update tb_waitset set ip = ?,inOrOut=?,dtComNo=?,kzComNo=?,outIfCheck=?,csIfOut=?,leftOrRight=? where _id = ?", new Object[] {
				tb_waitset.getIp(),tb_waitset.getInOrOut(),tb_waitset.getDtComNo(),tb_waitset.getKzComNo(), tb_waitset.getOutIfChecked(),tb_waitset.getCsIfOut(),tb_waitset.getLeftOrRight(),tb_waitset.get_id() });// ִ���޸ı�ǩ��Ϣ����
	}

	/**
	 * ���ұ�ǩ��Ϣ
	 * 
	 * @param id
	 * @return
	 */
	public TbWaitSet find(int id) {
		try {
			db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
			} catch(SQLiteException e){
				
			db = helper.getReadableDatabase();	
			}
		Cursor cursor = db.rawQuery(
				"select * from tb_waitset where _id = ?",
				new String[] { String.valueOf(id) });// ���ݱ�Ų��ұ�ǩ��Ϣ�����洢��Cursor����
		if (cursor.moveToNext())// �������ҵ��ı�ǩ��Ϣ
		{
			// ���������ı�ǩ��Ϣ�洢��Tb_flag����
			return new TbWaitSet(cursor.getInt(cursor.getColumnIndex("_id")),
					cursor.getString(cursor.getColumnIndex("ip")),cursor.getString(cursor.getColumnIndex("inOrOut")),cursor.getString(cursor.getColumnIndex("dtComNo")),cursor.getString(cursor.getColumnIndex("kzComNo")),cursor.getString(cursor.getColumnIndex("outIfCheck")),cursor.getString(cursor.getColumnIndex("csIfOut")),cursor.getString(cursor.getColumnIndex("leftOrRight")));
		}
	//	cursor.close();  //�رշ�ֹ�ڴ�й¶,����ͨ���м������ֵת������ǰ�رգ�
		return null;// ���û����Ϣ���򷵻�null
	}

	/**
	 * �h����ǩ��Ϣ
	 * 
	 * @param ids
	 */
	public void detele(Integer... ids) {
		if (ids.length > 0)// �ж��Ƿ����Ҫɾ����id
		{
			StringBuffer sb = new StringBuffer();// ����StringBuffer����
			for (int i = 0; i < ids.length; i++)// ����Ҫɾ����id����
			{
				sb.append('?').append(',');// ��ɾ��������ӵ�StringBuffer������
			}
			sb.deleteCharAt(sb.length() - 1);// ȥ�����һ����,���ַ�
			db = helper.getWritableDatabase();// ����SQLiteDatabase����
			// ִ��ɾ����ǩ��Ϣ����
			db.execSQL("delete from tb_waitset where _id in (" + sb + ")",
					(Object[]) ids);
		}
	}

	/**
	 * ��ȡ��ǩ��Ϣ
	 * 
	 * @param start
	 *            ��ʼλ��
	 * @param count
	 *            ÿҳ��ʾ����
	 * @return
	 */
	public List<TbWaitSet> getScrollData(int start, int count) {
		List<TbWaitSet> lisTb_waitset = new ArrayList<TbWaitSet>();// �������϶���
		try {
			db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
			} catch(SQLiteException e){
				
			db = helper.getReadableDatabase();	
			}
		// ��ȡ���б�ǩ��Ϣ
		Cursor cursor = db.rawQuery("select * from tb_waitset limit ?,?",
				new String[] { String.valueOf(start), String.valueOf(count) });
		while (cursor.moveToNext())// �������еı�ǩ��Ϣ
		{
			// ���������ı�ǩ��Ϣ��ӵ�������
			lisTb_waitset.add(new TbWaitSet(cursor.getInt(cursor.getColumnIndex("_id")),
					cursor.getString(cursor.getColumnIndex("ip")),cursor.getString(cursor.getColumnIndex("inOrOut")),cursor.getString(cursor.getColumnIndex("dtComNo")),cursor.getString(cursor.getColumnIndex("kzComNo")),cursor.getString(cursor.getColumnIndex("outIfCheck")),cursor.getString(cursor.getColumnIndex("csIfOut")),cursor.getString(cursor.getColumnIndex("leftOrRight"))));
		}
		cursor.close();
		return lisTb_waitset;// ���ؼ���
	}

	/**
	 * ��ȡ�ܼ�¼��
	 * 
	 * @return
	 */
	public long getCount() {
		try {
			db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
			} catch(SQLiteException e){
				
			db = helper.getReadableDatabase();	
			}
		Cursor cursor = db.rawQuery("select count(_id) from tb_waitset", null);// ��ȡ��ǩ��Ϣ�ļ�¼��
		if (cursor.moveToNext())// �ж�Cursor���Ƿ�������
		{
			return cursor.getLong(0);// �����ܼ�¼��
		}
		//cursor.close();
		return 0;// ���û�����ݣ��򷵻�0
	}

	/**
	 * ��ȡ��ǩ�����
	 * 
	 * @return
	 */
	public int getMaxId() {
		try {
			db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
			} catch(SQLiteException e){
				
			db = helper.getReadableDatabase();	
			}
		Cursor cursor = db.rawQuery("select max(_id) from tb_waitset", null);// ��ȡ��Ϣ���е������
		while (cursor.moveToLast()) {// ����Cursor�е����һ������
			return cursor.getInt(0);// ��ȡ���ʵ������ݣ��������
		}
		//cursor.close();
		return 0;// ���û�����ݣ��򷵻�0
	}
}
