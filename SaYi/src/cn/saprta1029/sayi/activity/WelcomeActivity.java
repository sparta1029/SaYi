package cn.saprta1029.sayi.activity;

import cn.saprta1029.sayi.R;
import cn.sparta1029.sayi.components.LoadingDialog;
import cn.sparta1029.sayi.utils.SPUtil;
import cn.sparta1029.sayi.utils.XMPPConnectionUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;

public class WelcomeActivity extends Activity{

	    private static final int GO_MAIN = 0;//去主页
	    private static final int GO_LOGIN = 1;//去登录页
	    String account,password;
	    /**
	     * 跳转判断
	     */
	    
	    
	   

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
	    }
	    
	    private Handler mHandler = new Handler(){
	        @SuppressLint("HandlerLeak")
			@Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	                case GO_MAIN://去主页
	                	 final LoadingDialog dialog = new LoadingDialog(WelcomeActivity.this, "正在登录");
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
	    
	    
	    private Boolean loginServer(LoadingDialog dialog) {
	    	SPUtil SPUtil=new SPUtil(this);
	        String serverAddress=SPUtil.getString(SPUtil.keyAddress, "");
	        serverAddress = serverAddress.replaceAll("服务器地址:", "");
			if (XMPPConnectionUtil.ConnectServer(serverAddress) != null) {
				try {
					Log.i("mytest", account);
					Log.i("mytest", password);
					XMPPConnectionUtil.ConnectServer(serverAddress).login(account,
							password);
					return true;
				} catch (Exception e) {
					Log.e("myerror", "XMPPException:" + e.toString());
					e.printStackTrace();
					return false;
				}
			} else {
				dialog.dismiss();
				final AlertDialog alert = new AlertDialog.Builder(
						this).create();
				alert.setTitle("登陆失败：");
				alert.setMessage("请检查网络以及用户名与密码是否正确");
				alert.show();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						alert.dismiss();
					}
				}, 1000);
				return false;
			}
		}
}
