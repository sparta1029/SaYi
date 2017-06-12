package cn.sparta1029.sayi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MessageDBOpenHelper extends SQLiteOpenHelper{

	
	public MessageDBOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql="create table if not exists chatlog(number INTEGER PRIMARY KEY,sender  varchar,message Text,readed varchar)";
		db.execSQL(sql);
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		String sql="create table if not exists chatlog(number INTEGER PRIMARY KEY,sender  varchar,receiver varchar,message Text,readed varchar,time varchar)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
