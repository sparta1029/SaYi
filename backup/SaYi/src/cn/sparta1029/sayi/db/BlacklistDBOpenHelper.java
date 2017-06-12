package cn.sparta1029.sayi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BlacklistDBOpenHelper extends SQLiteOpenHelper{

	
	public BlacklistDBOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql="create table if not exists blacklist(account varchar,user varchar,PRIMARY KEY(account,user))";
		db.execSQL(sql);
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		String sql="create table if not exists blacklist(account varchar,user varchar,PRIMARY KEY(account,user))";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
