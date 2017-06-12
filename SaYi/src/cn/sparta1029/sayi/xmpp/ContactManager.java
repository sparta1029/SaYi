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
			// �޷���
			roster.createEntry(
					friendName.trim() + "@" + connect.getServiceName(), null,
					null);
			// �з���
			// roster.createEntry(friendName.trim()+"@"+connect.getServiceName(),
			// friendName, new String[]{"contact"});
			Log.i("contacttest", "�ɹ�");
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
//		        //packet������  
//		        PacketListener listener = new PacketListener() {  	              
//		            @Override  
//		            public void processPacket(Packet packet) {  
//		                Log.i("addListernertest","PresenceService-"+packet.toXML());  
//		                if(packet instanceof Presence){  
//		                    Presence presence = (Presence)packet;  
//		                     String from = presence.getFrom();//���ͷ�    
//		                     String to = presence.getTo();//���շ�    
//		                     if (presence.getType().equals(Presence.Type.subscribe)) {    
//		                         Log.i("addListernertest","�յ��������");  
//		                        } else if (presence.getType().equals(    
//		                             Presence.Type.subscribed)) {  
//		                        Log.i("addListernertest","��ϲ���Է�ͬ����Ӻ��ѣ�");  
//		                     } else if (presence.getType().equals(    
//		                             Presence.Type.unsubscribe)) {  
//		                        Log.i("addListernertest","��Ǹ���Է��ܾ���Ӻ��ѣ�����Ӻ����б��Ƴ�");  
//		                     } else if (presence.getType().equals(    
//		                             Presence.Type.unsubscribed)){  
//		                    	 Log.i("addListernertest","��Ǹ������Ӻ����б��Ƴ�");  
//			                     
//		                     } 
//		                }  
//		            }  
//		        };  
//		        connect.addPacketListener(listener, filter); 
//	}
	
}
