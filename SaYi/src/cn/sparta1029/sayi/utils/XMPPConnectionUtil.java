package cn.sparta1029.sayi.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;








import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.sasl.SASLPlainMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import android.util.Log;

public class XMPPConnectionUtil {
	public static XMPPConnection ConnectServer(String serverAddress) {
		try {
			serverAddress=serverAddress.trim();
			Log.i("mytest", serverAddress);
			ConnectionConfiguration connConfig=new ConnectionConfiguration(serverAddress,5222);
			connConfig.setReconnectionAllowed(true);
			connConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
			connConfig.setSendPresence(true);
			SASLAuthentication.registerSASLMechanism("PLAIN", SASLPlainMechanism.class);
			SASLAuthentication.supportSASLMechanism("PLAIN",0);
			XMPPConnection XMPPConnection = new XMPPTCPConnection(connConfig,null);
			XMPPConnection.connect();
			return XMPPConnection;
		} catch (Exception e) {
			Log.i("mytest", "连接服务器失败 ：" + e.toString());
			e.printStackTrace();
			return null;
		}
	}

}
