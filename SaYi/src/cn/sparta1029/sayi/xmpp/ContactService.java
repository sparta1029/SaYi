package cn.sparta1029.sayi.xmpp;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import cn.saprta1029.sayi.activity.MainActivity;
import cn.sparta1029.sayi.db.ContactRequestDBManager;
import cn.sparta1029.sayi.db.ContactRequestDBOpenHelper;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class ContactService extends Service {
	static XMPPConnection connect;
@Override
public void onCreate() {
	super.onCreate();
}

@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	String serverAddress; 
	if(intent!=null)
	{
	serverAddress=intent.getStringExtra("address");
	connect=XMPPConnectionUtil.getInstanceNotPresence().getConnection(serverAddress);
	}
	addListerner (connect);
		return super.onStartCommand(intent, flags, startId);
	}


public void addListerner (XMPPConnection connect){
	Log.i("servicetest", connect.toString());
	PacketFilter filter =new PacketTypeFilter(Presence.class);  
	        //packet监听器  
	        PacketListener listener = new PacketListener() {  
	              //发送好友请求后,如果对方同意,先接收来自对方的好友请求,然后接收subscribed
	            @Override  
	            public void processPacket(Packet packet) {  
	            	ContactRequestDBOpenHelper DBHelper = new ContactRequestDBOpenHelper(
	            			ContactService.this, "sayi", null, 1);
	        		SQLiteDatabase db = DBHelper.getWritableDatabase();
	        		ContactRequestDBManager contactDBManager = new ContactRequestDBManager();
	                Log.i("addListernertest","PresenceService-"+packet.toXML());  
	                if(packet instanceof Presence){  
	                    Presence presence = (Presence)packet;  
	                     String from = presence.getFrom().split("@")[0];//发送方    
	                     String to = presence.getTo().split("@")[0];//接收方  
	                     if (presence.getType().equals(Presence.Type.subscribe)) { 
	                    	 
	                    	 if(contactDBManager.contactRequestExist(db, to, from))
	                    	 {	
	                    		 contactDBManager.contactRequestUpdate(db, to,from, ContactRequestDBManager.ADD);
	                    	 }
	                    	 else
	                        		contactDBManager.contactRequestInsert(db, to,from, ContactRequestDBManager.ADD);
	  		                Log.i("addListernertest","收到添加请求！");  
	                        } else if (presence.getType().equals(    
	                             Presence.Type.subscribed)) {  
	                        	if(contactDBManager.contactRequestExist(db, to, from))
	                        		contactDBManager.contactRequestUpdate(db, to,from, ContactRequestDBManager.APPROVED);
	                        		else
	                        		contactDBManager.contactRequestInsert(db, to,from, ContactRequestDBManager.APPROVED);
	 	                     Log.i("addListernertest","恭喜，对方同意添加好友！");  
	                     } else if (presence.getType().equals(    
	                             Presence.Type.unsubscribe)) {  
	                    	 if(contactDBManager.contactRequestExist(db, to, from))
	                        		contactDBManager.contactRequestUpdate(db, to,from, ContactRequestDBManager.REFUSE);
	                        		else
	                        		contactDBManager.contactRequestInsert(db, to,from, ContactRequestDBManager.REFUSE);
	                    	 Log.i("addListernertest","抱歉，对方拒绝添加好友，将你从好友列表移除");  
	                     } else if (presence.getType().equals(    
	                             Presence.Type.unsubscribed)){  
	                    	 if(contactDBManager.contactRequestExist(db, to, from))
	                        		contactDBManager.contactRequestUpdate(db, to,from, ContactRequestDBManager.DELETE);
	                        		else
	                        		contactDBManager.contactRequestInsert(db, to,from, ContactRequestDBManager.DELETE);
	                    	 Log.i("addListernertest","抱歉，将你从好友列表移除");  
		                     
	                     } 
	               final Intent intentBC = new Intent();  
	           	   intentBC.setAction(MainActivity.ACTION_UPDATE); 
	           	   intentBC.putExtra("update", true);  
	               sendBroadcast(intentBC);                      
//	                     else if (presence.getType().equals(    
//	                             Presence.Type.unavailable)) {  
//	                         Log.i("好友下线！");  
//	                     } else {    
//	                         Log.i("好友上线！");  
//	                     }    
	                }  
	            }  
	        };  
	        connect.addPacketListener(listener, filter); 
}


	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
	
}
