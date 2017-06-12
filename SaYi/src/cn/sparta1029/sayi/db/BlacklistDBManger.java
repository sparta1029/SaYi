package cn.sparta1029.sayi.db;


import java.util.ArrayList;

import cn.sparta1029.sayi.utils.CipherUtil;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

//信息数据库管理，主要实现数据插入和表的删除，以及更改已读状态
public class BlacklistDBManger {
	String sql;
	
	//插入消息
	public void blacklistInsert(SQLiteDatabase db,String account,String user)
	{
		try {
			account=CipherUtil.encrypt(account);
			user=CipherUtil.encrypt(user);
		} catch (Exception e1) {
			 
			e1.printStackTrace();
		}
		  db.beginTransaction();  //手动设置开始事务
		  try{
		        	sql="insert into blacklist(account,user) values ('"+account+"','"+user+"')";
		    		db.execSQL(sql);
		            db.setTransactionSuccessful(); //设置事务处理成功，不设置会自动回滚不提交
		           }catch(Exception e){
                     Log.i("blacklistinserttest:",e.toString());
		           }finally{
		               db.endTransaction(); //处理完成
		           }
	}
	
	
	public ArrayList<String>  blacklistAllAccountQuery(SQLiteDatabase db,String user)
	{
		
		ArrayList<String>  accountList = new ArrayList<String>();
		try {
			user=CipherUtil.encrypt(user);
		} catch (Exception e1) {
			 
			e1.printStackTrace();
		}
		Cursor cursor =db.rawQuery("select DISTINCT account from blacklist where user='"+user+"'",null);
		while (cursor.moveToNext())
		{
			String account=cursor.getString(cursor.getColumnIndex("account"));
			try {
				//解密
				account=CipherUtil.decrypt(account);
				//排除结果集中发送者为用户的结果
					accountList.add(account);
			} catch (Exception e1) {
				Log.i("mytest", "error "+e1.toString());
				e1.printStackTrace();
			}
		}
		cursor.close();
		return accountList;
	}	
	
	//删除表
	public void blacklistTableDelete(SQLiteDatabase db,String user)
	{
		 db.beginTransaction();  //手动设置开始事务
		  try{
		            //批量处理操作
			  try {
				  user=CipherUtil.encrypt(user);
				} catch (Exception e1) {
					 
					e1.printStackTrace();
				}
				db.execSQL("delete from blacklist where user='"+user+"'");
		            db.setTransactionSuccessful(); //设置事务处理成功，不设置会自动回滚不提交
		           }catch(Exception e){
                  Log.i("deleteblacklisttest:",e.toString());
		           }finally{
		               db.endTransaction(); //处理完成
		           }
	}

	
	//获取某用户所有消息和状态，包括已读和未读的消息，返回信息以及信息序号
	public void  blacklistDelete(SQLiteDatabase db,String account,String user)
	{
		
		 db.beginTransaction();  //手动设置开始事务
		  try{
		            //批量处理操作
			  try {
				  user=CipherUtil.encrypt(user);
					account=CipherUtil.encrypt(account);
				} catch (Exception e1) {
					 
					e1.printStackTrace();
				}
				db.execSQL("delete from blacklist where account='"+account+"' and user='"+user+"'");
		            db.setTransactionSuccessful(); //设置事务处理成功，不设置会自动回滚不提交
		           }catch(Exception e){
                   Log.i("deleteblacklisttest:",e.toString());
		           }finally{
		               db.endTransaction(); //处理完成
		           }
}
}