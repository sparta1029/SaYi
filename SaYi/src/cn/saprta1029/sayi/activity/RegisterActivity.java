package cn.saprta1029.sayi.activity;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPConnection;

import cn.saprta1029.sayi.R;
import cn.sparta1029.sayi.utils.SPUtil;
import cn.sparta1029.sayi.xmpp.UserEntity;
import cn.sparta1029.sayi.xmpp.UserRegister;
import cn.sparta1029.sayi.xmpp.XMPPConnectionUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity{
    String account,password;
    private EditText etAccount,etNickname,etEmail,etPassword,etPasswordAgain;
    private Button register;
    /**
     * 跳转判断
     */
    

	XMPPConnection connect;
   

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etAccount=(EditText)findViewById(R.id.register_account);
        etNickname=(EditText)findViewById(R.id.register_nickname);
        etEmail=(EditText)findViewById(R.id.register_email);
        etPassword=(EditText)findViewById(R.id.register_password);
        etPasswordAgain=(EditText)findViewById(R.id.register_password_again);
        register=(Button)findViewById(R.id.register);
        register.setOnClickListener(registerClickListener);
        
      
        new Thread(new Runnable() {
			@Override
			public void run() {
				SPUtil SPUtil=new SPUtil(RegisterActivity.this);
		        connect = XMPPConnectionUtil.ConnectServer(
						SPUtil.getString(SPUtil.keyAddress, ""));
			}
		}).start();
       
    }
    
    OnClickListener registerClickListener = new OnClickListener(){  
    	    @Override  
    	    public void onClick(View v) {  
    	    	String account,nickname,email,password,passwordAgain;
    	    	account=etAccount.getText().toString().trim();
    	    			nickname=etNickname.getText().toString().trim();
    	    					email=etEmail.getText().toString().trim();
    	    					password=etPassword.getText().toString().trim();
    	    					passwordAgain=etPasswordAgain.getText().toString().trim();
    	        if("".equals(account)||"".equals(nickname)||"".equals(email)||"".equals(password)||"".equals(passwordAgain))
    	        {
    	        	Toast.makeText(RegisterActivity.this, "请输入登录信息", Toast.LENGTH_LONG).show();
    	        }
    	        else if(password.equals(passwordAgain))
    	        {
					final UserEntity UserEntity=new UserEntity(account, password, nickname, email);
					final SPUtil SPUtil=new SPUtil(RegisterActivity.this);
					new Thread(new Runnable() {
						@Override
						public void run() {
							XMPPConnection connection = XMPPConnectionUtil.ConnectServer(
									SPUtil.getString(SPUtil.keyAddress, ""));
							UserRegister.registration(UserEntity,connection);
							loginServer();
							Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
							finish();
							startActivity(intent);
						}
					}).start();
    	        }
    	        else
    	        	Toast.makeText(RegisterActivity.this, "两次输入密码不一致", Toast.LENGTH_LONG).show();
    	        
    	    }  
    	};  
   
    	private Boolean loginServer() {
			try {
				account=etAccount.getText().toString().trim();
    			password=etPassword.getText().toString().trim();
    			Log.i("logintest", account+"  //   "+password);
				connect.login(account,password);
				return true;
			} catch (Exception e) {
				Log.e("logintest", "error     " + e.toString());
				e.printStackTrace();
			} 
			return false;
}
    	
    	
    	
}
