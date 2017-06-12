package cn.sparta1029.sayi.db;


import java.util.ArrayList;
import java.util.HashMap;

import cn.sparta1029.sayi.utils.CipherUtil;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

//信息数据库管理，主要实现数据插入和表的删除，以及更改已读状态
public class MessageDBManager {
	String sql;
	
	//插入消息
	public void messageInsert(SQLiteDatabase db,MessageEntity message)
	{
		try {
			message.messageSender=CipherUtil.encrypt(message.messageSender);
			message.messageReceiver=CipherUtil.encrypt(message.messageReceiver);
			message.messageContent=CipherUtil.encrypt(message.messageContent);
			message.messageIsReaded=CipherUtil.encrypt(message.messageIsReaded);
			message.messageTime=CipherUtil.encrypt(message.messageTime);
		} catch (Exception e1) {
			 
			e1.printStackTrace();
		}
		  db.beginTransaction();  //手动设置开始事务
		  try{
		            //批量处理操作
			  
		        	sql="insert into chatlog(sender,receiver,message,readed,time) values ('"+message.messageSender+"','"+message.messageReceiver+"','"+message.messageContent+"','"+message.messageIsReaded+"','"+message.messageTime+"')";
		    		db.execSQL(sql);
		            db.setTransactionSuccessful(); //设置事务处理成功，不设置会自动回滚不提交
		           }catch(Exception e){
                     Log.i("messageinserttest:",e.toString());
		           }finally{
		               db.endTransaction(); //处理完成
		           }
	}
	
	//删除表
	public void messageDelete(SQLiteDatabase db)
	{
		 db.beginTransaction();  //手动设置开始事务
		  try{
		            //批量处理操作
			  sql = "drop table chatlog";
				db.execSQL(sql);
		            db.setTransactionSuccessful(); //设置事务处理成功，不设置会自动回滚不提交
		           }catch(Exception e){
                    Log.i("deletetest:",e.toString());
		           }finally{
		               db.endTransaction(); //处理完成
		           }
	}

	//更新消息，更新消息的状态（从unreaded更新为readed）
	public void messageUpdate(SQLiteDatabase db,String sender)
	{
		String state = new String();
		try {
			sender=CipherUtil.encrypt(sender);
			state = CipherUtil.encrypt("readed");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		 db.beginTransaction();  //手动设置开始事务
		  try{
		            //批量处理操作
			  sql = "update chatlog set readed = '"+state+"' where sender='"+sender+"'";
				db.execSQL(sql);
		            db.setTransactionSuccessful(); //设置事务处理成功，不设置会自动回滚不提交
		           }catch(Exception e){
                   Log.i("updatetest:",e.toString());
		           }finally{
		               db.endTransaction(); //处理完成
		           }
	}
	
    
	
	//获取所有消息的发送者，在mainactivity中friend页面显示信息发送者,包含未读的消息和已读的消息
	public ArrayList<String>  messageAllSenderQuery(SQLiteDatabase db,String user)
	{
		
		ArrayList<String>  senderList = new ArrayList<String>();
		Cursor cursor =db.rawQuery("select DISTINCT sender from chatlog",null);
		while (cursor.moveToNext())
		{
			String sender=cursor.getString(cursor.getColumnIndex("sender"));
			try {
				sender=CipherUtil.decrypt(sender);
				//排除结果集中发送者为用户的结果
				if(!user.equals(sender.trim()))
				senderList.add(sender);
			} catch (Exception e1) {
				Log.i("mytest", "error "+e1.toString());
				e1.printStackTrace();
			}
		}
		cursor.close();
		return senderList;
	}
	
	//获取某用户所有消息和状态，包括已读和未读的消息，返回信息以及信息序号,进入聊天初始化时显示
	public ArrayList<HashMap<String,String>>  messageQueryOfSender(SQLiteDatabase db,String sender)
	{
		try {
			sender=CipherUtil.encrypt(sender);
		} catch (Exception e1) {
			 
			e1.printStackTrace();
		}
		ArrayList<HashMap<String,String>> messageList = new ArrayList<HashMap<String,String>>();
		Cursor cursor =db.rawQuery("select number,message,time,readed from chatlog where sender=?",new String[]{sender});
		while (cursor.moveToNext())
		{
			String message=cursor.getString(cursor.getColumnIndex("message"));
			String number=cursor.getString(cursor.getColumnIndex("number"));
			String time=cursor.getString(cursor.getColumnIndex("time"));
			String state=cursor.getString(cursor.getColumnIndex("readed"));
			try {
				HashMap<String,String> map= new HashMap<String,String>();
				message=CipherUtil.decrypt(message);
				time=CipherUtil.decrypt(time);
				state=CipherUtil.decrypt(state);
				map.put("number", number);
				map.put("message", message);
				map.put("time", time);
				map.put("state", state);
				messageList.add(map);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		cursor.close();
		return messageList;
	}

	//获取用户给输入聊天者发送的消息，返回信息以及信息的序号
	public ArrayList<HashMap<String,String>>  messageQueryOfUser(SQLiteDatabase db,String receiver)
	{
		try {
			receiver=CipherUtil.encrypt(receiver);
		} catch (Exception e1) {
			 
			e1.printStackTrace();
		}
		ArrayList<HashMap<String,String>> messageList = new ArrayList<HashMap<String,String>>();
		Cursor cursor =db.rawQuery("select number,message,time from chatlog where receiver=?",new String[]{receiver});
		while (cursor.moveToNext())
		{
			String message=cursor.getString(cursor.getColumnIndex("message"));
			String number=cursor.getString(cursor.getColumnIndex("number"));
			String time=cursor.getString(cursor.getColumnIndex("time"));
			try {
				HashMap<String,String> map= new HashMap<String,String>();
				message=CipherUtil.decrypt(message);
				time=CipherUtil.decrypt(time);
				map.put("number", number);
				map.put("message", message);
				map.put("time", time);
				messageList.add(map);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		cursor.close();
		return messageList;
	}
	
	
	
	

	


//获取表中所有消息总数
public int  messageAllCountQuery(SQLiteDatabase db)
{
	ArrayList<String> messageList =new ArrayList<String>();
	Cursor cursor =db.rawQuery("select message from chatlog",null);
	while (cursor.moveToNext())
	{
		String message=cursor.getString(cursor.getColumnIndex("message"));
		try {
			message=CipherUtil.decrypt(message);
			messageList.add(message);
		}catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	cursor.close();
	return messageList.size();
}

}