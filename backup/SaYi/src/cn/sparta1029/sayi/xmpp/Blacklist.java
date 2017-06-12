package cn.sparta1029.sayi.xmpp;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.PrivacyList;
import org.jivesoftware.smack.PrivacyListManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.PrivacyItem;
import android.util.Log;

public class Blacklist {
	static final String Black_List = "Blacklist";
	public static boolean addToPrivacyList(String account,XMPPConnection connect) { // 添加到黑名单  
		    
		    try {  
		    	
		        PrivacyListManager privacyManager = PrivacyListManager  
		                .getInstanceFor(connect);  
		        if (privacyManager == null) {  
		            return false;  
		        }  
		  
		        PrivacyList[] plists = privacyManager.getPrivacyLists();  
		        if (plists.length == 0) {// 没有黑名单或是名单中没有列，直接getPrivacyList会出错  
		            List<PrivacyItem> items = new ArrayList<PrivacyItem>();  
		            Log.i("", "addToPrivacyList plists.length==0");  
		            PrivacyItem newitem = new PrivacyItem("jid", false, 100);  
		            newitem.setValue("BLACKNAME" + "@"  
		                    + connect.getServiceName());  
		            items.add(newitem);  
		  
		            privacyManager.updatePrivacyList(Black_List, items);  
		            privacyManager.setActiveListName(Black_List);  
		            return true;  
		        }  
		  
		        PrivacyList plist = privacyManager.getPrivacyList(Black_List);  
		        if (plist != null) {  
		            String ser = "@" + connect.getServiceName();  
		  
		            List<PrivacyItem> items = plist.getItems();  
		            for (PrivacyItem item : items) {  
		                String from = item.getValue().substring(0,  
		                        item.getValue().indexOf(ser));  
		                Log.i("",  
		                        "addToPrivacyList item.getValue=" + item.getValue());  
		                if (from.equalsIgnoreCase(account)) {  
		                    items.remove(item);  
		                    break;  
		                }  
		            }  
		  
		            PrivacyItem newitem = new PrivacyItem("jid", false, 100);  
		            newitem.setValue(account + "@"  
		                    + connect.getServiceName());  
		            items.add(newitem);  
		            Log.i("", "addToPrivacyList item.getValue=" + newitem.toXML());  
		            Log.i("", "deleteFromPrivacyList items size=" + items.size());  
		            privacyManager.updatePrivacyList(Black_List, items);  
		            privacyManager.setActiveListName(Black_List);  
		  
		        }  
		        return true;  
		    } catch (XMPPException ex) {  
		        // Logger.getLogger(XMPP.class.getName()).log(Level.SEVERE, null,  
		        // ex);  
		    }  
		    return false;  
		}  
		  
	
		public static boolean deleteFromPrivacyList(String account,XMPPConnection connect) {// 删除黑名单  
		  
		    try {  
		        PrivacyListManager privacyManager = PrivacyListManager  
		                .getInstanceFor(connect);  
		        if (privacyManager == null) {  
		            return false;  
		        }  
		        PrivacyList plist = privacyManager.getPrivacyList(Black_List);  
		        if (plist != null) {  
		            String ser = "@" + connect.getServiceName();  
		  
		            List<PrivacyItem> items = plist.getItems();  
		            for (PrivacyItem item : items) {  
		                String from = item.getValue().substring(0,  
		                        item.getValue().indexOf(ser));  
		                Log.i("",  
		                        "deleteFromPrivacyList item.getValue="  
		                                + item.getValue());  
		                if (from.equalsIgnoreCase(account)) {  
		                    Log.i("", "deleteFromPrivacyList find object");  
		                    items.remove(item);  
		                    break;  
		                }  
		            }  
		            Log.i("", "deleteFromPrivacyList items size=" + items.size());  
		            privacyManager.updatePrivacyList(Black_List, items);  
		        }  
		    } catch (XMPPException ex) {  
		    }  
		    return true;  
		}  
		  
		public static List<String> getPrivacyList(XMPPConnection connect) { // 获取所有黑名单  
		    List<String> privacyList = new ArrayList<String>();  
		    try {  
		        PrivacyListManager privacyManager = PrivacyListManager  
		                .getInstanceFor(connect);  
		        if (privacyManager == null) {  
		            return privacyList;  
		        }  
		        String ser = "@" + connect.getServiceName();  
		        PrivacyList plist = privacyManager.getPrivacyList(Black_List);  
		        if (plist == null) {// 没有黑名单或是名单中没有列，直接getPrivacyList会出错  
		            List<PrivacyItem> items = plist.getItems();  
		            for (PrivacyItem item : items) {  
		                String from = item.getValue().substring(0,  
		                        item.getValue().indexOf(ser));  
		                privacyList.add(from);  
		            }  
		        } else {  
		            return privacyList;  
		        }  
		    } catch (XMPPException ex) {  
		    }  
		    return privacyList;  
		}  
}
