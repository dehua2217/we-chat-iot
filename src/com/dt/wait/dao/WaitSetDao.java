package com.dt.wait.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.dt.wait.model.TbWaitSet;

public class WaitSetDao {
	private DBOpenHelper helper;// 创建DBOpenHelper对象
	private SQLiteDatabase db;// 创建SQLiteDatabase对象

	public WaitSetDao(Context context)// 定义构造函数
	{
		helper = new DBOpenHelper(context);// 初始化DBOpenHelper对象
	}

	/**
	 * 添加便签信息
	 * 
	 * @param tb_flag
	 */
	public void add(TbWaitSet tb_waitset) {
		try {
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
		} catch(SQLiteException e){
			
		db = helper.getReadableDatabase();	
		}
		db.execSQL("insert into tb_waitset (_id,ip,inOrOut,dtComNo,kzComNo,outIfCheck,csIfOut,leftOrRight) values (?,?,?,?,?,?,?,?)", new Object[] {
				tb_waitset.get_id(), tb_waitset.getIp(),tb_waitset.getInOrOut(), tb_waitset.getDtComNo(),tb_waitset.getKzComNo(),tb_waitset.getOutIfChecked(),tb_waitset.getCsIfOut(),tb_waitset.getLeftOrRight()});// 执行添加便签信息操作
	}

	/*
	 * 更新便签信息
	 * 
	 * @param tb_flag
	 */
	public void update(TbWaitSet tb_waitset) {
		try {
			db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
			} catch(SQLiteException e){
				
			db = helper.getReadableDatabase();	
			}
		db.execSQL("update tb_waitset set ip = ?,inOrOut=?,dtComNo=?,kzComNo=?,outIfCheck=?,csIfOut=?,leftOrRight=? where _id = ?", new Object[] {
				tb_waitset.getIp(),tb_waitset.getInOrOut(),tb_waitset.getDtComNo(),tb_waitset.getKzComNo(), tb_waitset.getOutIfChecked(),tb_waitset.getCsIfOut(),tb_waitset.getLeftOrRight(),tb_waitset.get_id() });// 执行修改便签信息操作
	}

	/**
	 * 查找便签信息
	 * 
	 * @param id
	 * @return
	 */
	public TbWaitSet find(int id) {
		try {
			db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
			} catch(SQLiteException e){
				
			db = helper.getReadableDatabase();	
			}
		Cursor cursor = db.rawQuery(
				"select * from tb_waitset where _id = ?",
				new String[] { String.valueOf(id) });// 根据编号查找便签信息，并存储到Cursor类中
		if (cursor.moveToNext())// 遍历查找到的便签信息
		{
			// 将遍历到的便签信息存储到Tb_flag类中
			return new TbWaitSet(cursor.getInt(cursor.getColumnIndex("_id")),
					cursor.getString(cursor.getColumnIndex("ip")),cursor.getString(cursor.getColumnIndex("inOrOut")),cursor.getString(cursor.getColumnIndex("dtComNo")),cursor.getString(cursor.getColumnIndex("kzComNo")),cursor.getString(cursor.getColumnIndex("outIfCheck")),cursor.getString(cursor.getColumnIndex("csIfOut")),cursor.getString(cursor.getColumnIndex("leftOrRight")));
		}
	//	cursor.close();  //关闭防止内存泄露,可以通过中间变量赋值转换，提前关闭，
		return null;// 如果没有信息，则返回null
	}

	/**
	 * 刪除便签信息
	 * 
	 * @param ids
	 */
	public void detele(Integer... ids) {
		if (ids.length > 0)// 判断是否存在要删除的id
		{
			StringBuffer sb = new StringBuffer();// 创建StringBuffer对象
			for (int i = 0; i < ids.length; i++)// 遍历要删除的id集合
			{
				sb.append('?').append(',');// 将删除条件添加到StringBuffer对象中
			}
			sb.deleteCharAt(sb.length() - 1);// 去掉最后一个“,“字符
			db = helper.getWritableDatabase();// 创建SQLiteDatabase对象
			// 执行删除便签信息操作
			db.execSQL("delete from tb_waitset where _id in (" + sb + ")",
					(Object[]) ids);
		}
	}

	/**
	 * 获取便签信息
	 * 
	 * @param start
	 *            起始位置
	 * @param count
	 *            每页显示数量
	 * @return
	 */
	public List<TbWaitSet> getScrollData(int start, int count) {
		List<TbWaitSet> lisTb_waitset = new ArrayList<TbWaitSet>();// 创建集合对象
		try {
			db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
			} catch(SQLiteException e){
				
			db = helper.getReadableDatabase();	
			}
		// 获取所有便签信息
		Cursor cursor = db.rawQuery("select * from tb_waitset limit ?,?",
				new String[] { String.valueOf(start), String.valueOf(count) });
		while (cursor.moveToNext())// 遍历所有的便签信息
		{
			// 将遍历到的便签信息添加到集合中
			lisTb_waitset.add(new TbWaitSet(cursor.getInt(cursor.getColumnIndex("_id")),
					cursor.getString(cursor.getColumnIndex("ip")),cursor.getString(cursor.getColumnIndex("inOrOut")),cursor.getString(cursor.getColumnIndex("dtComNo")),cursor.getString(cursor.getColumnIndex("kzComNo")),cursor.getString(cursor.getColumnIndex("outIfCheck")),cursor.getString(cursor.getColumnIndex("csIfOut")),cursor.getString(cursor.getColumnIndex("leftOrRight"))));
		}
		cursor.close();
		return lisTb_waitset;// 返回集合
	}

	/**
	 * 获取总记录数
	 * 
	 * @return
	 */
	public long getCount() {
		try {
			db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
			} catch(SQLiteException e){
				
			db = helper.getReadableDatabase();	
			}
		Cursor cursor = db.rawQuery("select count(_id) from tb_waitset", null);// 获取便签信息的记录数
		if (cursor.moveToNext())// 判断Cursor中是否有数据
		{
			return cursor.getLong(0);// 返回总记录数
		}
		//cursor.close();
		return 0;// 如果没有数据，则返回0
	}

	/**
	 * 获取便签最大编号
	 * 
	 * @return
	 */
	public int getMaxId() {
		try {
			db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
			} catch(SQLiteException e){
				
			db = helper.getReadableDatabase();	
			}
		Cursor cursor = db.rawQuery("select max(_id) from tb_waitset", null);// 获取信息表中的最大编号
		while (cursor.moveToLast()) {// 访问Cursor中的最后一条数据
			return cursor.getInt(0);// 获取访问到的数据，即最大编号
		}
		//cursor.close();
		return 0;// 如果没有数据，则返回0
	}
}
