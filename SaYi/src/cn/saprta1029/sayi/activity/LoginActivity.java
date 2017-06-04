package cn.saprta1029.sayi.activity;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.harmony.javax.security.sasl.SaslException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.packet.DelayInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.saprta1029.sayi.R;
import cn.sparta1029.sayi.components.DeletableAutoCompleteTextView;
import cn.sparta1029.sayi.components.DeletableAutoCompleteTextViewAdapter;
import cn.sparta1029.sayi.components.LoadingDialog;
import cn.sparta1029.sayi.components.ServerAddressDialog;
import cn.sparta1029.sayi.db.BlacklistDBManger;
import cn.sparta1029.sayi.db.BlacklistDBOpenHelper;
import cn.sparta1029.sayi.db.MessageDBManager;
import cn.sparta1029.sayi.db.MessageDBOpenHelper;
import cn.sparta1029.sayi.db.MessageEntity;
import cn.sparta1029.sayi.db.UserInfoDBManager;
import cn.sparta1029.sayi.db.UserInfoDBOpenHelper;
import cn.sparta1029.sayi.db.UserInfoEntity;
import cn.sparta1029.sayi.utils.EmailUtil;
import cn.sparta1029.sayi.utils.GetDateTime;
import cn.sparta1029.sayi.utils.SPUtil;
import cn.sparta1029.sayi.xmpp.ReconnectListener;
import cn.sparta1029.sayi.xmpp.UserRegister;
import cn.sparta1029.sayi.xmpp.XMPPConnectionUtil;

public class LoginActivity extends Activity {
	EditText etPassword;
	CheckBox chkRememberPassword, chkAutoLogin;
	Button btnLogin;
	private DeletableAutoCompleteTextView dactvAccount;
	private DeletableAutoCompleteTextViewAdapter dactvApter;
	private ArrayList<String> listAccount = new ArrayList<String>();
	String account = null, password = null;
	Receiver receiver = new Receiver();
	XMPPConnection connection;
	int error = 0;
	BlacklistDBOpenHelper BlacklistDBHelper ;
	private static final int TIME_OUT = 0;
	private static final int SUCCESS = 1;
	private static final int TIME_LIMIT = 15000; // 登陆超时时间 15秒
	Timer timer;
	Thread thread;
	String defaultChatroomHistoryMAX = "5";
	LoadingDialog dialog;
	String addressText = "服务器地址     ";
	ArrayList<String> blacklistAccountList;
	Handler handler=new Handler();
	public Context context=LoginActivity.this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// TODO
		BlacklistDBHelper = new BlacklistDBOpenHelper(
				LoginActivity.this, "sayi", null, 1);

		IntentFilter filter = new IntentFilter();
		filter.addAction("cn.sparta1029.sayi.pwdbroadcast");
		this.registerReceiver(receiver, filter);

		chkRememberPassword = (CheckBox) findViewById(R.id.remember_password);
		chkAutoLogin = (CheckBox) findViewById(R.id.auto_login);

		dactvAccount = (DeletableAutoCompleteTextView) findViewById(R.id.account);
		etPassword = (EditText) findViewById(R.id.password);
		btnLogin = (Button) findViewById(R.id.login);

		UserInfoDBOpenHelper DBHelper = new UserInfoDBOpenHelper(
				LoginActivity.this, "sayi", null, 1);
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		UserInfoDBManager userInfoManager = new UserInfoDBManager();
		Hashtable<String, String> tempListAccount = userInfoManager
				.userInfoQueryAll(db);
		Enumeration<String> keys = tempListAccount.keys();
		db.close();
		while (keys.hasMoreElements()) {
			listAccount.add(keys.nextElement());
		}

		// 将数据库中的用户信息传递给DeletableAutoCompleteTextView的适配器
		dactvAccount.setThreshold(1);
		dactvApter = new DeletableAutoCompleteTextViewAdapter(this,
				listAccount, -2);
		dactvAccount.setAdapter(dactvApter);

		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*
				 * 账号密码的输入判断完成，根据以下情况操作后,根据输入的账号密码进行服务器的登录,登陆成功，跳转到主界面，创建用户文件夹
				 * 1.数据库中未存储相应的账号，勾选“记住密码”，将账号密码都存入数据库
				 * 2.数据库中未存储相应的账号，未勾选“记住密码”，将账号存入数据库
				 * 3.数据库中已存入相应账号，未存入密码，勾选“记住密码”，将密码更新到数据库
				 * 4.数据库中已存入相应账号和密码，取消勾选“记住密码”，将密码更新为空
				 */

				timer = new Timer();

				password = etPassword.getText().toString().trim();
				if (password == null || account == null || "".equals(password)
						|| "".equals(account)) {
					Toast.makeText(LoginActivity.this, "请输入正确的账号和密码",
							Toast.LENGTH_SHORT).show();
				} else {
					TextView address = (TextView) findViewById(R.id.serverText);
					String serverAddress = address.getText().toString().trim();
					serverAddress = serverAddress.replaceAll("服务器地址     ", "");
					if ("".equals(serverAddress)) {
						Toast.makeText(LoginActivity.this, "请输入服务器地址",
								Toast.LENGTH_LONG).show();
					} else {
						dialog = new LoadingDialog(LoginActivity.this, "正在登录");
						dialog.setCanceledOnTouchOutside(false);
						dialog.show();

						UserInfoDBOpenHelper DBHelper = new UserInfoDBOpenHelper(
								LoginActivity.this, "sayi", null, 1);
						SQLiteDatabase db = DBHelper.getWritableDatabase();
						UserInfoDBManager userInfoManager = new UserInfoDBManager();

						if (userInfoManager.userInfoAccountQuery(db, account) == null)// 未存入用户名
						{
							if (chkRememberPassword.isSelected()) {
								UserInfoEntity userInfo = new UserInfoEntity(
										account, password);
								userInfoManager.userInfoInsert(db, userInfo);
							} else {
								UserInfoEntity userInfo = new UserInfoEntity(
										account, "");
								userInfoManager.userInfoInsert(db, userInfo);
							}
						} else {
							if (chkRememberPassword.isSelected()) {
								UserInfoEntity userInfo = new UserInfoEntity(
										account, password);
								userInfoManager.userInfoUpdate(db, userInfo);
								;
							} else {
								UserInfoEntity userInfo = new UserInfoEntity(
										account, "");
								userInfoManager.userInfoUpdate(db, userInfo);
							}
						}
						{
							// 进行登录，登陆成功后，创建用户文件夹
							thread = new Thread(new Runnable() {
								@Override
								public void run() {
									if (loginServer(dialog)) {
										SPUtil SPUtil = new SPUtil(
												LoginActivity.this);
										SPUtil.putString(SPUtil.keyCurrentUser,
												account);
										SPUtil.putString(
												SPUtil.keyCurrentPassword,
												password);
										String userDire = LoginActivity.this
												.getApplication()
												.getExternalFilesDir(null)
												.getPath()
												+ "/user/" + account + "/";
										File destDir = new File(userDire);
										if (!destDir.exists()) {
											destDir.mkdirs();
										}
										Intent intent = new Intent(
												LoginActivity.this,
												MainActivity.class);
										intent.putExtra("currentUser", account);
										startActivity(intent);
										timer.cancel();
										finish();
										dialog.dismiss();

									} else {
										timer.cancel();
										dialog.dismiss();
										if (error == 0) {
											Intent intent = new Intent();
											intent.setAction("cn.sparta1029.sayi.pwdbroadcast");
											intent.putExtra("loginError",
													"登陆失败,检查密码账号是否正确");
											LoginActivity.this
													.sendBroadcast(intent);
										} else
											error = 0;
									}
								}
							});
							thread.start();
							// 设定定时器
							timer.schedule(new TimerTask() {
								@Override
								public void run() {
									sendTimeOutMsg();
								}
							}, TIME_LIMIT);
						}
					}
				}
			}
		});

		final SPUtil SPUtil = new SPUtil(this);
		if ("".equals(SPUtil.getString(SPUtil.keyChatroomHistoryMAX, "")))// 如果未设置，使用默认5条
			SPUtil.putString(SPUtil.keyChatroomHistoryMAX,
					defaultChatroomHistoryMAX);
		account = SPUtil.getString(SPUtil.keyCurrentUser, "");
		password = SPUtil.getString(SPUtil.keySavedPassword, "");
		if ("".equals(password))
			chkRememberPassword.setChecked(false);
		else
			chkRememberPassword.setChecked(true);
		// chkAutoLogin 响应 勾选，向sharedpreferences写入true ，取消勾选，向
		// sharedpreferences写入false
		chkAutoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					chkRememberPassword.setChecked(true);
					SPUtil.putString(SPUtil.keyAutoLogin,
							SPUtil.booleanAutoLoginTrue);
				} else {
					SPUtil.putString(SPUtil.keyAutoLogin,
							SPUtil.booleanAutoLoginFalse);
				}
			}
		});

		// chkRememberPassword 响应:勾选，向sharedpreferences写入password
		// ，取消勾选，删除在sharedpreferences中的password
		chkRememberPassword
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							SPUtil.putString(SPUtil.keySavedPassword,
									etPassword.getText().toString().trim());
						} else {
							SPUtil.remove(SPUtil.keySavedPassword);
						}
					}
				});

		// create时从sharedpreferences中获取当前用户和密码信息，用当前用户信息更新界面，再获取自动登录信息，若为true，直接进行登陆
		TextView tvServerText = (TextView) LoginActivity.this
				.findViewById(R.id.serverText);
		tvServerText.setText(addressText
				+ SPUtil.getString(SPUtil.keyAddress, ""));
		etPassword.setText(password);

	}

	final Handler myHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case TIME_OUT:
				// 打断线程
				thread.interrupt();
				dialog.dismiss();
				Toast.makeText(LoginActivity.this, "登录超时，请稍后重试",
						Toast.LENGTH_LONG).show();
				break;
			case SUCCESS:
				// 取消定时器
				timer.cancel();
				break;
			default:
				break;
			}
		};
	};

	// 向handler发送超时信息
	private void sendTimeOutMsg() {
		Message timeOutMsg = new Message();
		timeOutMsg.what = TIME_OUT;
		myHandler.sendMessage(timeOutMsg);
	}

	private void errorDialog(String errorText) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this); // 先得到构造器
		builder.setTitle("提示"); // 设置标题
		builder.setMessage(errorText); // 设置内容
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { // 设置确定按钮
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss(); // 关闭dialog
					}
				});
		// 参数都设置完成了，创建并显示出来
		builder.create().show();
	}

	/*
	 * 接收有两种情况，1.点击DeletableAutoCompleteTextView的item触发广播，
	 * 这时广播传送的intent中有pwd和account两项
	 * 2.直接在DeletableAutoCompleteTextView中输入账号，password
	 * （EditText）中输入密码，此时广播传送的只有account，没有pwd 此时的密码直接利用password.gettext()获取
	 */
	public class Receiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			String loginText = intent.getStringExtra("loginError");
			if (loginText != null) {
				errorDialog(loginText);
			} else {
				password = intent.getStringExtra("pwd");
				account = intent.getStringExtra("account");

				if (password == null || "".equals(password))
					chkRememberPassword.setChecked(false);
				else {
					chkRememberPassword.setChecked(true);
					etPassword.setText(password);
				}
			}
		}
	}

	// 登陆失败（密码不匹配或连接错误）返回false，登陆成功返回true
	private Boolean loginServer(LoadingDialog dialog) {
		TextView address = (TextView) findViewById(R.id.serverText);
		String serverAddressText = address.getText().toString()
				.replaceAll(addressText, "").trim();
		if ("".equals(serverAddressText)) {
			handler.post(new Runnable() {
				@Override
				public void run() {
			Toast.makeText(LoginActivity.this, "请输入服务器地址", Toast.LENGTH_LONG)
					.show();
				}
			});
			
			return false;
		} else {
			// 创建连接，状态未上线
			XMPPConnectionUtil.configure(ProviderManager.getInstance());
			connection = XMPPConnectionUtil.getInstanceNotPresence()
					.getConnection(serverAddressText);
			if (connection != null) {
				try {
					connection.connect();
					connection.login(account, password,"SaYi");
					ReconnectListener connectListener = new ReconnectListener(serverAddressText,account,password);  
					connection.addConnectionListener(connectListener);  
					// 离线消息获取
					OfflineMessageManager offlineManager = new 
							OfflineMessageManager(
							connection);
					Iterator<org.jivesoftware.smack.packet.Message> it = 
							offlineManager
							.getMessages();

					MessageDBOpenHelper DBHelper = new MessageDBOpenHelper(
							LoginActivity.this, "sayi", null, 1);
					SQLiteDatabase db = DBHelper.getWritableDatabase();
					MessageDBManager MessageDBManager = new 
							MessageDBManager();

					SQLiteDatabase dbBlacklist = BlacklistDBHelper.
							getWritableDatabase();
					BlacklistDBManger blacklistDBManager = new 
							BlacklistDBManger();
					blacklistAccountList = blacklistDBManager
							.blacklistAllAccountQuery(dbBlacklist, account);
					dbBlacklist.close();
					while (it.hasNext()) {
						org.jivesoftware.smack.packet.Message message = it
								.next();
						DelayInfo info=(DelayInfo) message
								.getExtension("delay", "urn:xmpp:delay");
						Date date=info.getStamp();
						@SuppressWarnings("deprecation")
						String sender = message.getFrom().split("@")[0];
						sender = sender.substring(sender.indexOf("/") + 1);
						if (!blacklistAccountList.contains(sender)) {
							String offlineMessageDate=GetDateTime.
									getDateTimeStringFromDate(date);	
		                	Log.i("timetest", "offlineMessageDate:"+offlineMessageDate);
							MessageEntity messageEntity = new MessageEntity(
									account, sender, message.getBody(),
									"unreaded",offlineMessageDate);
							MessageDBManager.messageInsert(db, messageEntity);
						}
					}
					// 删除离线消息
					offlineManager.deleteMessages();
					// 将状态设置成在线
					Presence presence = new Presence(Presence.Type.available);
					connection.sendPacket(presence);
					return true;
				} catch (Exception e) {
					Log.e("logintest", "error     " + e.toString());
					if(e.toString().contains("authentication failed"))
					{
						dialog.dismiss();
						timer.cancel();
						error = 0;
						return false;
					}
					else

					{	dialog.dismiss();
						timer.cancel();
						Intent intent = new Intent();
						intent.setAction("cn.sparta1029.sayi.pwdbroadcast");
						intent.putExtra("loginError", "连接服务器失败");
						error = 1;
						LoginActivity.this.sendBroadcast(intent);
						return false;
					}
				}
			} else {
				dialog.dismiss();
				timer.cancel();
				Intent intent = new Intent();
				intent.setAction("cn.sparta1029.sayi.pwdbroadcast");
				intent.putExtra("loginError", "连接服务器失败");
				error = 1;
				LoginActivity.this.sendBroadcast(intent);
				return false;
			}
		}
	}

	private void setIconEnable(Menu menu, boolean enable) {
		try {
			Class<?> clazz = Class
					.forName("com.android.internal.view.menu.MenuBuilder");
			Method m = clazz.getDeclaredMethod("setOptionalIconsVisible",
					boolean.class);
			m.setAccessible(true);
			m.invoke(menu, enable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		setIconEnable(menu, true);
		menu.add(0, 1, 0, " 设置地址").setIcon(R.drawable.login_setting);
		menu.add(0, 2, 0, " 注册用户").setIcon(R.drawable.login_register);
		// TODO 忘记密码
		menu.add(0, 3, 0, " 忘记密码").setIcon(R.drawable.login_forget_password);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			ServerAddressDialog dialog = new ServerAddressDialog(this,
					"输入服务器地址",
					new ServerAddressDialog.OnCustomDialogListener() {
						@Override
						public void back(boolean result, String serverAddress) {
							if (!result)
								Toast.makeText(LoginActivity.this,
										"请输入正确的服务器地址", Toast.LENGTH_SHORT)
										.show();
							else {
								SPUtil SPUtil = new SPUtil(LoginActivity.this);
								SPUtil.putString(SPUtil.keyAddress,
										serverAddress);
								TextView tvServerText = (TextView) LoginActivity.this
										.findViewById(R.id.serverText);
								tvServerText.setText(addressText
										+ serverAddress);
							}
						}
					});
			dialog.show();
			break;

		case 2:
			SPUtil SPUtil = new SPUtil(LoginActivity.this);
			String serverAddress = SPUtil.getString(SPUtil.keyAddress, "");
			if ("".equals(serverAddress)) {
				Toast.makeText(this, "请设置地址", Toast.LENGTH_SHORT).show();
			} else {
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				finish();
				startActivity(intent);
			}
			break;
		case 3:
			Intent intent=new Intent(LoginActivity.this,ForgetPasswordActivity.class);
			finish();
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private static boolean isExit = false;
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(getApplicationContext(), "再按一次退出程序",
					Toast.LENGTH_SHORT).show();
			// 利用handler延迟发送更改状态信息
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			System.exit(0);
		}
	}

 


	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(receiver);
	}
}
