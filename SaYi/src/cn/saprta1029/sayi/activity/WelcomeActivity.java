package cn.saprta1029.sayi.activity;

import java.util.ArrayList;
import java.util.Iterator;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.OfflineMessageManager;
import cn.saprta1029.sayi.R;
import cn.sparta1029.sayi.components.LoadingDialog;
import cn.sparta1029.sayi.db.BlacklistDBManger;
import cn.sparta1029.sayi.db.BlacklistDBOpenHelper;
import cn.sparta1029.sayi.db.MessageDBManager;
import cn.sparta1029.sayi.db.MessageDBOpenHelper;
import cn.sparta1029.sayi.db.MessageEntity;
import cn.sparta1029.sayi.utils.SPUtil;
import cn.sparta1029.sayi.xmpp.XMPPConnectionUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class WelcomeActivity extends Activity{
	int error=0;
	    private static final int GO_MAIN = 0;//去主页
	    private static final int GO_LOGIN = 1;//去登录页
	    String account,password;
	    
	    
	    
	    /**
	     * 跳转判断
	     */
	    XMPPConnection connection;
	    ArrayList<String> blacklistAccountList;
	   

	    
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_welcome);
	      
	        SPUtil SPUtil=new SPUtil(this);
	        String autoLogin=SPUtil.getString(SPUtil.keyAutoLogin, "");
	        account=SPUtil.getString(SPUtil.keyCurrentUser, "");
	        password=SPUtil.getString(SPUtil.keyCurrentPassword, "");
	        if("true".equals(autoLogin))
	        {
				mHandler.sendEmptyMessageDelayed(GO_MAIN, 2000);
	        } else {
	            mHandler.sendEmptyMessageDelayed(GO_LOGIN, 2000);
	        }
	        
	        BlacklistDBOpenHelper DBHelper = new BlacklistDBOpenHelper(
	        		WelcomeActivity.this, "sayi", null, 1);
			SQLiteDatabase db = DBHelper.getWritableDatabase();
			BlacklistDBManger blacklistDBManager = new BlacklistDBManger();
			blacklistAccountList = blacklistDBManager.blacklistAllAccountQuery(db);
			db.close();
	    }
	    
	    private Handler mHandler = new Handler(){
	        @SuppressLint("HandlerLeak")
			@Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	                case GO_MAIN://去主页
	                	 final LoadingDialog dialog = new LoadingDialog(WelcomeActivity.this, "正在登录");
	                	 dialog.setCanceledOnTouchOutside(false);
	                	 dialog.show();
	                	new Thread(new Runnable() {
	    					@Override
	    					public void run() {
	    					   if (loginServer(dialog)) {
	    							SPUtil SPUtil=new SPUtil(WelcomeActivity.this);
	    							SPUtil.putString(SPUtil.keyCurrentUser, account);
	    							Intent intent = new Intent(
	    									WelcomeActivity.this,
	    									MainActivity.class);
	    							intent.putExtra("currentUser", account);
	    							startActivity(intent);
	    							finish();
	    							dialog.dismiss();
	    						}
	    						else
	    						{
	    							//TODO 提醒无法连接到服务器
	    							    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
	    			                    startActivity(intent);
	    			                    finish();
	    						}
	    					}
	    				}).start();
	                    break;
	                case GO_LOGIN://去登录页
	                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
	                    startActivity(intent);
	                    finish();
	                    break;
	            }
	        }
	    };
	    
	    
//	    private Boolean loginServer(LoadingDialog dialog) {
//	    	SPUtil SPUtil=new SPUtil(this);
//	        String serverAddress=SPUtil.getString(SPUtil.keyAddress, "");
//	        serverAddress = serverAddress.replaceAll("服务器地址:", "");
//	        XMPPConnection connect = XMPPConnectionUtil.getInstanceNotPresence().getConnection(serverAddress);
//			if ( connect!= null) {
//				try {
//					connect.connect();
//					connect.login(account,
//							password);
//					return true;
//				}catch (XMPPException e) {
//					Log.i("test", "连接服务器失败  XMPPException：" + e.toString());
//					e.printStackTrace();
//					return false;
//				}
//			} else {
//				dialog.dismiss();
//				return false;
//			}
//		}
//}
	    
	    
	    private Boolean loginServer(LoadingDialog dialog) {
	    	SPUtil SPUtil=new SPUtil(this);
	        String serverAddress=SPUtil.getString(SPUtil.keyAddress, "");

	        
				// 创建连接，状态未上线
				XMPPConnectionUtil.configure(ProviderManager.getInstance());
				connection = XMPPConnectionUtil.getInstanceNotPresence().getConnection(serverAddress);
				if (connection != null) {
					try {
						connection.connect();
						connection.login(account, password);
						// 离线消息获取
						OfflineMessageManager offlineManager = new OfflineMessageManager(
								connection);
				//		Log.i("offlinetestv ",
				//				"离线消息数量:" + offlineManager.getMessageCount());
						Iterator<org.jivesoftware.smack.packet.Message> it = offlineManager
								.getMessages();
						
						MessageDBOpenHelper DBHelper = new MessageDBOpenHelper(
								WelcomeActivity.this, "sayi", null, 1);
						SQLiteDatabase db = DBHelper.getWritableDatabase();
						MessageDBManager MessageDBManager = new MessageDBManager();
						
						while (it.hasNext()) {
							org.jivesoftware.smack.packet.Message message = it
									.next();
							String sender = message.getFrom().split("@")[0];
							sender = sender.substring(sender.indexOf("/") + 1);
							if(!blacklistAccountList.contains(sender)){
								
							MessageEntity messageEntity=new MessageEntity(account,sender, message.getBody(), "unreaded");
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
						e.printStackTrace();
					}
					return false;
				} 
				return false;
			}
		}
	    
	    
	    
