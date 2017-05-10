package cn.sparta1029.sayi.xmpp;

import java.io.IOException;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.sasl.SASLPlainMechanism;

import android.util.Log;

public class XMPPConnectionUtil {
	public static XMPPConnection ConnectServer(String serverAddress) {
		serverAddress=serverAddress.trim();
		ConnectionConfiguration connConfig=new ConnectionConfiguration(serverAddress,5222,"sparta1029");
		connConfig.setReconnectionAllowed(true);
		connConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
		connConfig.setSendPresence(true);
		SASLAuthentication.registerSASLMechanism("PLAIN", SASLPlainMechanism.class);
		SASLAuthentication.supportSASLMechanism("PLAIN",0);
		XMPPConnection  Connection = new XMPPConnection(connConfig,null);	
			try {
				Connection.connect();
				return Connection;
			}  catch (XMPPException e) {
				Log.i("mytest", "XMPPException£º" + e.toString());
				e.printStackTrace();
				return null;
			}
	}
	public static boolean disconnect(String serverAddress) {
				ConnectServer(serverAddress).disconnect();
				return true;
	}
	
}
