package cn.saprta1029.sayi.activity;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.ProviderManager;

import cn.saprta1029.sayi.R;
import cn.sparta1029.sayi.utils.EmailUtil;
import cn.sparta1029.sayi.utils.SPUtil;
import cn.sparta1029.sayi.xmpp.UserSearchEntity;
import cn.sparta1029.sayi.xmpp.UsersSearch;
import cn.sparta1029.sayi.xmpp.XMPPConnectionUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ForgetPasswordActivity extends Activity {

	EditText etForgetPasswordAccount;
	Button btnGetPassword;
	String serverAddress;
	XMPPConnection connect;
	final static String FAKENAMEEXTRA = "sparta1029ssayi";// 长度大于或等于最长用户名

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_password);
		btnGetPassword = (Button) this.findViewById(R.id.forget_password);
		etForgetPasswordAccount = (EditText) this
				.findViewById(R.id.forget_password_account);
		SPUtil SPUtil = new SPUtil(this);
		serverAddress = SPUtil.getString(SPUtil.keyAddress, "");
		connect = XMPPConnectionUtil.getInstanceNotPresence()
				.ConnectServerNotPresence(serverAddress);
		new Thread(new Runnable(){
			@Override
			public void run() {
					try {
						if(!connect.isConnected())
						{
							connect.connect();
							connect.login("temp", "temp");
						}else
						{
							connect.disconnect();
							XMPPConnectionUtil.configure(ProviderManager.getInstance());
							connect.connect();
							connect.login("temp", "temp");
						}
					} catch (XMPPException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			}
		}).start();
		
		btnGetPassword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					final List<UserSearchEntity> user = UsersSearch.searchUsers(
							connect, serverAddress, etForgetPasswordAccount
									.getText().toString().trim());
					final List<UserSearchEntity> fakeUser = UsersSearch.searchUsersForgetPWD(
							connect, serverAddress, etForgetPasswordAccount
									.getText().toString().trim()
									+ FAKENAMEEXTRA);
					if(user.size()!=0)
					{
					
					final AlertDialog getPasswordDialog = new AlertDialog.Builder(
							ForgetPasswordActivity.this).create();
					getPasswordDialog.setTitle("找回密码");
					getPasswordDialog
							.setIcon(R.drawable.ic_launcher);
					getPasswordDialog
							.setMessage("找回密码的用户为："+user.get(0).getUserName()+"\n 点击确定，密码将发到你的邮箱:"+user.get(0).getEmail());
					getPasswordDialog
							.setButton(
									DialogInterface.BUTTON_POSITIVE,
									"确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											sendMail(user.get(0).getEmail(), user.get(0).getUserName(),
													fakeUser.get(0).getName());// 邮箱地址，用户名，密码
										Intent intent=new Intent(ForgetPasswordActivity.this,LoginActivity.class);
										finish();
										startActivity(intent);
										}
									});
					getPasswordDialog
							.setButton(
									DialogInterface.BUTTON_NEGATIVE,
									"取消",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											getPasswordDialog
													.dismiss();
										}
									});
					getPasswordDialog.show();
					
					
					
					
					
					
					
					
					
					
				
					}
					else
					{
						Toast.makeText(ForgetPasswordActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
					}
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

	public void sendMail(final String mailAddress,final String account,final String password){
new Thread(new Runnable() {
@Override
public void run() {
	EmailUtil mail = new EmailUtil();
	try {
		mail.sendMail(mailAddress,account,password);
	} catch (AddressException e) {
		e.printStackTrace();
	} catch (MessagingException e) {
		e.printStackTrace();
	}
}
}).start();

  }
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent=new Intent(ForgetPasswordActivity.this,LoginActivity.class);
			finish();
			startActivity(intent);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	
}
