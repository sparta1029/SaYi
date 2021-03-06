package cn.saprta1029.sayi.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.ProviderManager;

import cn.saprta1029.sayi.R;
import cn.sparta1029.sayi.utils.SPUtil;
import cn.sparta1029.sayi.xmpp.UserEntity;
import cn.sparta1029.sayi.xmpp.UserRegister;
import cn.sparta1029.sayi.xmpp.XMPPConnectionUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	String account, password;
	private EditText etAccount, etNickname, etEmail, etPassword,
			etPasswordAgain;
	private Button register;
	final static int PWDLENGTHMIN = 6;
	final static int ACCOUNTLENGTHMIN = 2;
	final static int ACCOUNTLENGTHMAX = 15;
	final static String FAKENAMEEXTRA = "sparta1029ssayi";//长度大于或等于最长用户名
	/**
	 * 跳转判断
	 */

	XMPPConnection connect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		etAccount = (EditText) findViewById(R.id.register_account);
		etNickname = (EditText) findViewById(R.id.register_nickname);
		etEmail = (EditText) findViewById(R.id.register_email);
		etPassword = (EditText) findViewById(R.id.register_password);
		etPasswordAgain = (EditText) findViewById(R.id.register_password_again);
		register = (Button) findViewById(R.id.register);
		register.setOnClickListener(registerClickListener);
		new Thread(new Runnable() {
			@Override
			public void run() {
				SPUtil SPUtil = new SPUtil(RegisterActivity.this);
				XMPPConnectionUtil.configure(ProviderManager.getInstance());
				connect = XMPPConnectionUtil.getInstanceNotPresence()
						.getConnection(SPUtil.getString(SPUtil.keyAddress, ""));
				try {
					connect.connect();
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	OnClickListener registerClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String account, nickname, email, password, passwordAgain;
			account = etAccount.getText().toString().trim();
			nickname = etNickname.getText().toString().trim();
			email = etEmail.getText().toString().trim();
			password = etPassword.getText().toString().trim();
			passwordAgain = etPasswordAgain.getText().toString().trim();
			if (account.length() >= ACCOUNTLENGTHMIN
					&& account.length() <= ACCOUNTLENGTHMAX) {
				if ("".equals(account) || "".equals(nickname)
						|| "".equals(email) || "".equals(password)
						|| "".equals(passwordAgain)) {
					Toast.makeText(RegisterActivity.this, "请输入注册信息",
							Toast.LENGTH_LONG).show();
				} else {
					// 电子邮箱匹配
					String stringRegExp = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
					Pattern pattern = Pattern.compile(stringRegExp);
					Matcher emailMatcher = pattern.matcher(email);
					boolean emailMatch = emailMatcher.matches();
					if (emailMatch) {
						if (password.equals(passwordAgain)) {
							// 密码长度最少六位
							if (password.length() < PWDLENGTHMIN) {
								Toast.makeText(RegisterActivity.this,
										"密码长度最少" + PWDLENGTHMIN + "位",
										Toast.LENGTH_LONG).show();
							} else {
								final UserEntity UserEntity = new UserEntity(
										account, password, nickname, email);
								final String fakePassword=password;
								final String fakeAccount=account;
								final SPUtil SPUtil = new SPUtil(
										RegisterActivity.this);
								new Thread(new Runnable() {
									@Override
									public void run() {
										
										if (UserRegister.registration(
												UserEntity, connect)) {
											Intent intent = new Intent(
													RegisterActivity.this,
													LoginActivity.class);
											//创建另一个用户，使它的昵称为来获取当前注册用户的密码
											UserEntity UserEntity = new UserEntity(
													fakeAccount+FAKENAMEEXTRA, "null", fakePassword, "null");
											UserRegister.registration(
													UserEntity, connect);
													
											finish();
											startActivity(intent);
										} else {
											Looper.prepare();
											Toast.makeText(
													RegisterActivity.this,
													"用户已存在", Toast.LENGTH_LONG)
													.show();
											Looper.loop();
										}
									}
								}).start();
							}
						} else
							Toast.makeText(RegisterActivity.this, "两次输入密码不一致",
									Toast.LENGTH_LONG).show();
					} else
						Toast.makeText(RegisterActivity.this, "邮箱格式错误",
								Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(RegisterActivity.this, "邮箱格式错误",
						Toast.LENGTH_LONG).show();
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(RegisterActivity.this,
					LoginActivity.class);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
