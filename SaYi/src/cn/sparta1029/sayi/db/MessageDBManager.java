package cn.sparta1029.sayi.db;


import java.util.ArrayList;
import java.util.HashMap;

import cn.sparta1029.sayi.utils.CipherUtil;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

//��Ϣ���ݿ������Ҫʵ�����ݲ���ͱ��ɾ�����Լ������Ѷ�״̬
public class MessageDBManager {
	String sql;
	
	//������Ϣ
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
		  db.beginTransaction();  //�ֶ����ÿ�ʼ����
		  try{
		            //�����������
			  
		        	sql="insert into chatlog(sender,receiver,message,readed,time) values ('"+message.messageSender+"','"+message.messageReceiver+"','"+message.messageContent+"','"+message.messageIsReaded+"','"+message.messageTime+"')";
		    		db.execSQL(sql);
		            db.setTransactionSuccessful(); //����������ɹ��������û��Զ��ع����ύ
		           }catch(Exception e){
                     Log.i("messageinserttest:",e.toString());
		           }finally{
		               db.endTransaction(); //�������
		           }
	}
	
	//ɾ����
	public void messageDelete(SQLiteDatabase db)
	{
		 db.beginTransaction();  //�ֶ����ÿ�ʼ����
		  try{
		            //�����������
			  sql = "drop table chatlog";
				db.execSQL(sql);
		            db.setTransactionSuccessful(); //����������ɹ��������û��Զ��ع����ύ
		           }catch(Exception e){
                    Log.i("deletetest:",e.toString());
		           }finally{
		               db.endTransaction(); //�������
		           }
	}

	//������Ϣ��������Ϣ��״̬����unreaded����Ϊreaded��
	public void messageUpdate(SQLiteDatabase db,String sender)
	{
		String state = new String();
		try {
			sender=CipherUtil.encrypt(sender);
			state = CipherUtil.encrypt("readed");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		 db.beginTransaction();  //�ֶ����ÿ�ʼ����
		  try{
		            //�����������
			  sql = "update chatlog set readed = '"+state+"' where sender='"+sender+"'";
				db.execSQL(sql);
		            db.setTransactionSuccessful(); //����������ɹ��������û��Զ��ع����ύ
		           }catch(Exception e){
                   Log.i("updatetest:",e.toString());
		           }finally{
		               db.endTransaction(); //�������
		           }
	}
	
    
	
	//��ȡ������Ϣ�ķ����ߣ���mainactivity��friendҳ����ʾ��Ϣ������,����δ������Ϣ���Ѷ�����Ϣ
	public ArrayList<String>  messageAllSenderQuery(SQLiteDatabase db,String user)
	{
		
		ArrayList<String>  senderList = new ArrayList<String>();
		Cursor cursor =db.rawQuery("select DISTINCT sender from chatlog",null);
		while (cursor.moveToNext())
		{
			String sender=cursor.getString(cursor.getColumnIndex("sender"));
			try {
				sender=CipherUtil.decrypt(sender);
				//�ų�������з�����Ϊ�û��Ľ��
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
	
	//��ȡĳ�û�������Ϣ��״̬�������Ѷ���δ������Ϣ��������Ϣ�Լ���Ϣ���,���������ʼ��ʱ��ʾ
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

	//��ȡ�û������������߷��͵���Ϣ��������Ϣ�Լ���Ϣ�����
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
	
	
	
	

	


//��ȡ����������Ϣ����
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