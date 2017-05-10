package cn.sparta1029.sayi.xmpp;

import org.jivesoftware.smack.XMPPConnection;

import android.content.Context;

public class ConnectionData {
public static GlobaData getData(Context context)
{
	return (GlobaData)context.getApplicationContext();
	}
public static XMPPConnection getConnection(Context context)
{
	return getData(context).connection;
	}

public static void setConnection(Context context,XMPPConnection conn)
{
	getData(context).connection=conn;
	}
}
