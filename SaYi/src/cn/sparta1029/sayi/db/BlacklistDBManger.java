package cn.sparta1029.sayi.db;


import java.util.ArrayList;

import cn.sparta1029.sayi.utils.CipherUtil;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

//��Ϣ���ݿ������Ҫʵ�����ݲ���ͱ��ɾ�����Լ������Ѷ�״̬
public class BlacklistDBManger {
	String sql;
	
	//������Ϣ
	public void blacklistInsert(SQLiteDatabase db,String account,String user)
	{
		try {
			account=CipherUtil.encrypt(account);
			user=CipherUtil.encrypt(user);
		} catch (Exception e1) {
			 
			e1.printStackTrace();
		}
		  db.beginTransaction();  //�ֶ����ÿ�ʼ����
		  try{
		        	sql="insert into blacklist(account,user) values ('"+account+"','"+user+"')";
		    		db.execSQL(sql);
		            db.setTransactionSuccessful(); //����������ɹ��������û��Զ��ع����ύ
		           }catch(Exception e){
                     Log.i("blacklistinserttest:",e.toString());
		           }finally{
		               db.endTransaction(); //�������
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
				//����
				account=CipherUtil.decrypt(account);
				//�ų�������з�����Ϊ�û��Ľ��
					accountList.add(account);
			} catch (Exception e1) {
				Log.i("mytest", "error "+e1.toString());
				e1.printStackTrace();
			}
		}
		cursor.close();
		return accountList;
	}	
	
	//ɾ����
	public void blacklistTableDelete(SQLiteDatabase db,String user)
	{
		 db.beginTransaction();  //�ֶ����ÿ�ʼ����
		  try{
		            //�����������
			  try {
				  user=CipherUtil.encrypt(user);
				} catch (Exception e1) {
					 
					e1.printStackTrace();
				}
				db.execSQL("delete from blacklist where user='"+user+"'");
		            db.setTransactionSuccessful(); //����������ɹ��������û��Զ��ع����ύ
		           }catch(Exception e){
                  Log.i("deleteblacklisttest:",e.toString());
		           }finally{
		               db.endTransaction(); //�������
		           }
	}

	
	//��ȡĳ�û�������Ϣ��״̬�������Ѷ���δ������Ϣ��������Ϣ�Լ���Ϣ���
	public void  blacklistDelete(SQLiteDatabase db,String account,String user)
	{
		
		 db.beginTransaction();  //�ֶ����ÿ�ʼ����
		  try{
		            //�����������
			  try {
				  user=CipherUtil.encrypt(user);
					account=CipherUtil.encrypt(account);
				} catch (Exception e1) {
					 
					e1.printStackTrace();
				}
				db.execSQL("delete from blacklist where account='"+account+"' and user='"+user+"'");
		            db.setTransactionSuccessful(); //����������ɹ��������û��Զ��ع����ύ
		           }catch(Exception e){
                   Log.i("deleteblacklisttest:",e.toString());
		           }finally{
		               db.endTransaction(); //�������
		           }
}
}