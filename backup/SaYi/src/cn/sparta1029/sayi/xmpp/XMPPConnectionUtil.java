package cn.sparta1029.sayi.xmpp;

import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.sasl.SASLPlainMechanism;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;

public class XMPPConnectionUtil {
	XMPPConnection connection;
	private XMPPConnectionUtil() {
	}
	private static class XMPPNestClass{    
		private static final XMPPConnectionUtil instance = new XMPPConnectionUtil();
		 }
	public static XMPPConnectionUtil getInstanceNotPresence() {    
		  return XMPPNestClass.instance;    
		 } 
	public XMPPConnection getConnection(String serverAddress) {  
				if (connection == null) {  
					connection=ConnectServerNotPresence(serverAddress);  
		        }  
		        return connection;  
		    }
	public XMPPConnection ConnectServerNotPresence(String serverAddress) {
		serverAddress=serverAddress.trim();
		//TODO “sparta1029”改为openfire所设置的域名
		ConnectionConfiguration connConfig=new 
				ConnectionConfiguration(serverAddress,5222);
		connConfig.setReconnectionAllowed(true);
		connConfig.setSendPresence(false);
		connConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
		SASLAuthentication.registerSASLMechanism("PLAIN", 
				SASLPlainMechanism.class);
		SASLAuthentication.supportSASLMechanism("PLAIN",0);
		XMPPConnection  Connection = new XMPPConnection(connConfig,null);	
		return Connection;
	}
	public ChatManager getMyChatManager() {
		ChatManager chatManager=XMPPConnectionUtil.
				getInstanceNotPresence().connection.getChatManager();
		return chatManager;
	}

	
	public static void configure(ProviderManager pm) {
	    pm.addIQProvider("query", "jabber:iq:private",
	            new PrivateDataManager.PrivateDataIQProvider());
	    // Time
	    try {
	        pm.addIQProvider("query", "jabber:iq:time",
	                Class.forName("org.jivesoftware.smackx.packet.Time"));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    // Roster Exchange
	    pm.addExtensionProvider("x", "jabber:x:roster",
	            new RosterExchangeProvider());
	    // Message Events
	    pm.addExtensionProvider("x", "jabber:x:event",
	            new MessageEventProvider());
	    // Chat State
	    pm.addExtensionProvider("active",
	            "http://jabber.org/protocol/chatstates",
	            new ChatStateExtension.Provider());
	    pm.addExtensionProvider("composing",
	            "http://jabber.org/protocol/chatstates",
	            new ChatStateExtension.Provider());
	    pm.addExtensionProvider("paused",
	            "http://jabber.org/protocol/chatstates",
	            new ChatStateExtension.Provider());
	    pm.addExtensionProvider("inactive",
	            "http://jabber.org/protocol/chatstates",
	            new ChatStateExtension.Provider());
	    pm.addExtensionProvider("gone",
	            "http://jabber.org/protocol/chatstates",
	            new ChatStateExtension.Provider());
	    // XHTML
	    pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
	            new XHTMLExtensionProvider());
	    // Group Chat Invitations
	    pm.addExtensionProvider("x", "jabber:x:conference",
	            new GroupChatInvitation.Provider());

	    pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
	            new DiscoverItemsProvider());

	    pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
	            new DiscoverInfoProvider());
	    // Data Forms
	    pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
	    // MUC User
	    pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
	            new MUCUserProvider());
	    // MUC Admin
	    pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
	            new MUCAdminProvider());
	    // MUC Owner
	    pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
	            new MUCOwnerProvider());
	    // Delayed Delivery
	    pm.addExtensionProvider("x", "jabber:x:delay",
	            new DelayInformationProvider());
	    // Version
	    try {
	        pm.addIQProvider("query", "jabber:iq:version",
	                Class.forName("org.jivesoftware.smackx.packet.Version"));
	    } catch (ClassNotFoundException e) {
	        // Not sure what's happening here.
	    }
	    // VCard
	    pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
	    // Offline Message Requests
	    pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
	            new OfflineMessageRequest.Provider());
	    // Offline Message Indicator
	    pm.addExtensionProvider("offline",
	            "http://jabber.org/protocol/offline",
	            new OfflineMessageInfo.Provider());
	    // Last Activity
	    pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
	    // User Search
	    pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
	    // SharedGroupsInfo
	    pm.addIQProvider("sharedgroup",
	            "http://www.jivesoftware.org/protocol/sharedgroup",
	            new SharedGroupsInfo.Provider());
	    // JEP-33: Extended Stanza Addressing
	    pm.addExtensionProvider("addresses",
	            "http://jabber.org/protocol/address",
	            new MultipleAddressesProvider());
	    pm.addIQProvider("si", "http://jabber.org/protocol/si",
	            new StreamInitiationProvider());
	    pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
	            new BytestreamsProvider());
	    pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
	    pm.addIQProvider("command", "http://jabber.org/protocol/commands",
	            new AdHocCommandDataProvider());
	    pm.addExtensionProvider("malformed-action",
	            "http://jabber.org/protocol/commands",
	            new AdHocCommandDataProvider.MalformedActionError());
	    pm.addExtensionProvider("bad-locale",
	            "http://jabber.org/protocol/commands",
	            new AdHocCommandDataProvider.BadLocaleError());
	    pm.addExtensionProvider("bad-payload",
	            "http://jabber.org/protocol/commands",
	            new AdHocCommandDataProvider.BadPayloadError());
	    pm.addExtensionProvider("bad-sessionid",
	            "http://jabber.org/protocol/commands",
	            new AdHocCommandDataProvider.BadSessionIDError());
	    pm.addExtensionProvider("session-expired",
	            "http://jabber.org/protocol/commands",
	            new AdHocCommandDataProvider.SessionExpiredError());

	}
}
