package cn.sparta1029.sayi.xmpp;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.packet.VCard;

import android.util.Log;

public class UserRegister {

public static String registration(UserEntity UserEntity,XMPPConnection connection)
{
	try {
	AccountManager accountManager=new AccountManager(connection);
	Map<String,String> attributes = new HashMap<String, String>(2);
	attributes.put("name", UserEntity.nickname);
	attributes.put("email", UserEntity.email);
	if (accountManager.supportsAccountCreation())
	{
		accountManager.createAccount(UserEntity.account,UserEntity.password,attributes);
	}
		else 
	Log.i("test","Server doesn't support creating new accounts");

	}catch (Exception e) {
		Log.i("regtest", e.toString());
		e.printStackTrace();
		return "×¢²áÊ§°Ü";
	}
	
	return "×¢²á³É¹¦";
}
}
