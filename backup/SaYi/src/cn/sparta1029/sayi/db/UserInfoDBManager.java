package cn.sparta1029.sayi.db;


import java.util.Hashtable;

import cn.sparta1029.sayi.utils.CipherUtil;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserInfoDBManager {
	String sql;
	public void userInfoInsert(SQLiteDatabase db,UserInfoEntity userInfo)
	{
		try {
			userInfo.account=CipherUtil.encrypt(userInfo.account);
			userInfo.password=CipherUtil.encrypt(userInfo.password);
		} catch (Exception e1) {
			 
			e1.printStackTrace();
		}
		  db.beginTransaction();  //手动设置开始事务
		  try{
		            //批量处理操作
		        	sql="insert into userinfo(account, pwd) values ('"+userInfo.account+"','"+userInfo.password+"')";
		    		db.execSQL(sql);
		            db.setTransactionSuccessful(); //设置事务处理成功，不设置会自动回滚不提交
		           }catch(Exception e){
                     Log.i("insert:",e.toString());
		           }finally{
		               db.endTransaction(); //处理完成
		           }
	}
	
	public void userInfoDelete(SQLiteDatabase db,String account)
	{
		try {
			account=CipherUtil.encrypt(account);
		} catch (Exception e1) {
			 
			e1.printStackTrace();
		}
		 db.beginTransaction();  //手动设置开始事务
		  try{
		            //批量处理操作
			  sql = "delete from userinfo where account='"+account+"'";
				db.execSQL(sql);
		            db.setTransactionSuccessful(); //设置事务处理成功，不设置会自动回滚不提交
		           }catch(Exception e){
                    Log.i("delete:",e.toString());
		           }finally{
		               db.endTransaction(); //处理完成
		           }
	}
	
	public void userInfoUpdate(SQLiteDatabase db,UserInfoEntity userInfo)
	{
		try {
			userInfo.account=CipherUtil.encrypt(userInfo.account);
			userInfo.password=CipherUtil.encrypt(userInfo.password);
		} catch (Exception e1) {
			 
			e1.printStackTrace();
		}
		 db.beginTransaction();  //手动设置开始事务
		  try{
		            //批量处理操作
			  sql = "update userinfo set pwd = '"+userInfo.password+"' where account='"+userInfo.account+"'";
				db.execSQL(sql);
		            db.setTransactionSuccessful(); //设置事务处理成功，不设置会自动回滚不提交
		           }catch(Exception e){
                   Log.i("update:",e.toString());
		           }finally{
		               db.endTransaction(); //处理完成
		           }
	}
    
	public String userInfoPasswordQuery(SQLiteDatabase db,String account)
	{
		try {
			account=CipherUtil.encrypt(account);
		} catch (Exception e1) {
			 
			e1.printStackTrace();
		}
		String password = null;
		Cursor cursor =db.rawQuery("select pwd from userinfo where account=?",new String[]{account});
		if (cursor.moveToFirst())
		{
			password=cursor.getString(cursor.getColumnIndex("pwd"));
		}
		cursor.close();
		try {
			password=CipherUtil.decrypt(password);
		} catch (Exception e1) {
			 
			e1.printStackTrace();
		}
		return password;
	}
	
	public String userInfoAccountQuery(SQLiteDatabase db,String account)
	{
		try {
			account=CipherUtil.encrypt(account);
		} catch (Exception e1) {
			 
			e1.printStackTrace();
		}
		String password = null;
		Cursor cursor =db.rawQuery("select pwd from userinfo where account=?",new String[]{account});
		if (cursor.moveToFirst())
		{
			password=cursor.getString(cursor.getColumnIndex("pwd"));
		}
		cursor.close();
		if(password==null)
			return password;
		else
		{
		try {
			password=CipherUtil.decrypt(password);
		} catch (Exception e1) {
			 
			e1.printStackTrace();
		}
		return password;
		}
	}
	
	
	public Hashtable<String, String> userInfoQueryAll(SQLiteDatabase db)
	{
		Hashtable<String,String> result=new Hashtable<String,String>();
		Cursor  cursor = db.query("userinfo", new String[]{"account","pwd"},null, null, null, null, null);
		while(cursor.moveToNext()){
			String tempAccount= cursor.getString(cursor.getColumnIndex("account"));
			String tempPassword= cursor.getString(cursor.getColumnIndex("pwd"));
			try {
				tempAccount=CipherUtil.decrypt(tempAccount);
				tempPassword=CipherUtil.decrypt(tempPassword);
			} catch (Exception e1) {
				 
				e1.printStackTrace();
			}
			result.put(tempAccount, tempPassword);
		}
		if (!cursor.isClosed()) 
		cursor.close();
		return result;
	}

}