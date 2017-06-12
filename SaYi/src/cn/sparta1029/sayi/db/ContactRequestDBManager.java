package cn.sparta1029.sayi.db;

import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import cn.sparta1029.sayi.utils.CipherUtil;

public class ContactRequestDBManager {
	String sql;
	public static final String ADD="ADD";//�Է��������
	public static final String REFUSE="REFUSE";//���󱻶Է��ܾ�
	public static final String APPROVED="APPROVED";//���󱻶Է�ͬ��
	public static final String DELETE="DELETE";//�Է�ɾ������
	public void contactRequestInsert(SQLiteDatabase db,String user,String contact,String request )
	{
		try {
			contact=CipherUtil.encrypt(contact);
			user=CipherUtil.encrypt(user);
			request=CipherUtil.encrypt(request);
		} catch (Exception e1) {
			 
			e1.printStackTrace();
		}
		  db.beginTransaction();  //�ֶ����ÿ�ʼ����
		  try{
		       	sql="insert into contact_request(username,contactname,request) values ('"+user+"','"+contact+"','"+request+"')";
		    		db.execSQL(sql);
		            db.setTransactionSuccessful(); //����������ɹ��������û��Զ��ع����ύ
		           }catch(Exception e){
                     Log.i("inserttest",e.toString());
		           }finally{
		               db.endTransaction(); //�������
		           }
	}
	
	public void contactRequestUpdate(SQLiteDatabase db,String user,String contact,String request)
	{
		try {
			Log.i("updatetest", "request:"+request);
			contact=CipherUtil.encrypt(contact);
			user=CipherUtil.encrypt(user);
			request=CipherUtil.encrypt(request);
			Log.i("updatetest", "request:"+request);
		} catch (Exception e1) {
			 
			e1.printStackTrace();
		}
		 db.beginTransaction();  //�ֶ����ÿ�ʼ����
		  try{
		            //�����������
			  sql = "update contact_request set request = '"+request+"' where username='"+user+"' and contactname='"+contact+"'";

				Log.i("updatetest", "sql:"+sql);
			  db.execSQL(sql);
		            db.setTransactionSuccessful(); //����������ɹ��������û��Զ��ع����ύ
		           }catch(Exception e){
		        	   Log.i("updatetest", e.toString());
		           }finally{
		               db.endTransaction(); //�������
		           }
	}
	
	public void  contactRequestDelete(SQLiteDatabase db,String username,String contactname)
	{
		
		 db.beginTransaction();  //�ֶ����ÿ�ʼ����
		  try{
		            //�����������
			  try {
				  username=CipherUtil.encrypt(username);
				  contactname=CipherUtil.encrypt(contactname);
				} catch (Exception e1) {
					 
					e1.printStackTrace();
				}
				db.execSQL("delete from contact_request where username='"+username+"' and contactname='"+contactname+"'");
		            db.setTransactionSuccessful(); //����������ɹ��������û��Զ��ع����ύ
		           }catch(Exception e){
                   Log.i("deleteblacklisttest:",e.toString());
		           }finally{
		               db.endTransaction(); //�������
		           }
}
	
	
	public ArrayList<HashMap<String,String>>  contactRequestQuery(SQLiteDatabase db,String account)
	{
		ArrayList<HashMap<String,String>>  accountList =new  ArrayList<HashMap<String,String>>();
		try {
			account=CipherUtil.encrypt(account);
		} catch (Exception e1) {
			 
			e1.printStackTrace();
		}
		Cursor cursor =db.rawQuery("select contactname,request from contact_request where username='"+account+"'",null);
		while (cursor.moveToNext())
		{
			String contactname=cursor.getString(cursor.getColumnIndex("contactname"));
			String request=cursor.getString(cursor.getColumnIndex("request"));
			try {
				//����
				HashMap<String,String> map= new HashMap<String,String>();
				contactname=CipherUtil.decrypt(contactname);
				request=CipherUtil.decrypt(request);
				map.put("contactname", contactname);
				if(request.equals(ContactRequestDBManager.ADD))
				{
				map.put("request", "�Է�������������");
				}else if(request.equals(ContactRequestDBManager.APPROVED))
				{
					map.put("request", "�Է�ͨ����������");
				}else if(request.equals(ContactRequestDBManager.DELETE))
				{
				map.put("request", "�Է�ɾ����� ����");
				}else if(request.equals(ContactRequestDBManager.REFUSE))
				{
				map.put("request", "�Է��ܾ���������");
				}
				accountList.add(map);
			} catch (Exception e1) {
				Log.i("mytest", "error "+e1.toString());
				e1.printStackTrace();
			}
		}
		cursor.close();
		return accountList;
	}	
	
	public boolean  contactRequestExist(SQLiteDatabase db,String account,String contact)
	{
		
		try {
			account=CipherUtil.encrypt(account);
			contact=CipherUtil.encrypt(contact);
		} catch (Exception e1) {
			 
			e1.printStackTrace();
		}
		Cursor cursor =db.rawQuery("select request from contact_request where username='"+account+"' and contactname='"+contact+"'",null);
		if(cursor.moveToFirst())
		{
			cursor.close();
			return true;
		}
		else{

			cursor.close();
			return false;
		}
	}	
}
