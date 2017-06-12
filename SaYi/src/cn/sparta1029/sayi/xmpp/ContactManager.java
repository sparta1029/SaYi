package cn.sparta1029.sayi.xmpp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import android.util.Log;

public class ContactManager {
	public static boolean addFriend(String friendName, XMPPConnection connect) {
		try {
			Roster roster;
			roster = connect.getRoster();
			roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
			// 无分组
			roster.createEntry(
					friendName.trim() + "@" + connect.getServiceName(), null,
					null);
			// 有分组
			// roster.createEntry(friendName.trim()+"@"+connect.getServiceName(),
			// friendName, new String[]{"contact"});
			Log.i("contacttest", "成功");
			return true;
		} catch (XMPPException e) {
			e.printStackTrace();
			Log.i("contacttest", e.toString());
			return false;
		}
	}

	public static boolean removeFriend(String friendName, XMPPConnection connect) {
		try {

			Roster roster;
			roster = connect.getRoster();
			roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
			roster.removeEntry(roster.getEntry(friendName.trim() + "@"
					+ connect.getServiceName()));
			return true;
		} catch (XMPPException e) {
			e.printStackTrace();
			Log.i("contacttest", e.toString());
			return false;
		}
	}

	public static ArrayList<RosterEntry> getFriend(XMPPConnection connect) {
		Roster roster;
		roster = connect.getRoster();
		roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
		roster.reload();;
		Collection<RosterEntry> contactAll = roster.getEntries();
		  Log.i("contactListtest", "contactAll:"+contactAll);
     	 
		ArrayList<RosterEntry> contactList = new ArrayList<RosterEntry>();
		Iterator<RosterEntry> contactIterator = contactAll.iterator();
		while (contactIterator.hasNext())
			contactList.add(contactIterator.next());
		return contactList;
	}

//	public static void addListerner (XMPPConnection connect){
//		PacketFilter filter =new PacketTypeFilter(Presence.class);  
//		        //packet监听器  
//		        PacketListener listener = new PacketListener() {  	              
//		            @Override  
//		            public void processPacket(Packet packet) {  
//		                Log.i("addListernertest","PresenceService-"+packet.toXML());  
//		                if(packet instanceof Presence){  
//		                    Presence presence = (Presence)packet;  
//		                     String from = presence.getFrom();//发送方    
//		                     String to = presence.getTo();//接收方    
//		                     if (presence.getType().equals(Presence.Type.subscribe)) {    
//		                         Log.i("addListernertest","收到添加请求！");  
//		                        } else if (presence.getType().equals(    
//		                             Presence.Type.subscribed)) {  
//		                        Log.i("addListernertest","恭喜，对方同意添加好友！");  
//		                     } else if (presence.getType().equals(    
//		                             Presence.Type.unsubscribe)) {  
//		                        Log.i("addListernertest","抱歉，对方拒绝添加好友，将你从好友列表移除");  
//		                     } else if (presence.getType().equals(    
//		                             Presence.Type.unsubscribed)){  
//		                    	 Log.i("addListernertest","抱歉，将你从好友列表移除");  
//			                     
//		                     } 
//		                }  
//		            }  
//		        };  
//		        connect.addPacketListener(listener, filter); 
//	}
	
}
