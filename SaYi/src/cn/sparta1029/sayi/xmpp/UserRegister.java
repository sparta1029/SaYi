package cn.sparta1029.sayi.xmpp;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.XMPPConnection;
import android.util.Log;

public class UserRegister {

public static boolean registration(UserEntity UserEntity,XMPPConnection connection)
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
		//用户已存在 提示 conflict 409
		Log.i("regtest", e.toString());
		e.printStackTrace();
		return false;
	}
	return true;
}
}
