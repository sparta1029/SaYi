package cn.sparta1029.sayi.xmpp;

import java.util.Timer;  
import java.util.TimerTask;  
  








import org.jivesoftware.smack.ConnectionListener;  
  



import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import cn.saprta1029.sayi.activity.LoginActivity;
import cn.saprta1029.sayi.activity.MainActivity;
import cn.sparta1029.sayi.utils.SPUtil;
import android.util.Log;  
import android.widget.TextView;
  

public class ReconnectListener implements ConnectionListener { 
		    private Timer tExit;  
		    private String username,password,address;  
		    private int logintime = 2000;  
		    
		   

			public ReconnectListener(String serverAddressText, String account,
					String password) {
				this.username=account;
				this.password=password;
				this.address=serverAddressText;
			}

			@Override  
		    public void connectionClosed() {  
		        Log.i("recontest", "連接關閉");  
		        // 關閉連接  
		        XMPPConnectionUtil.getInstanceNotPresence().ConnectServerNotPresence(address).disconnect();;
		        // 重连服务器  
		        tExit = new Timer();  
		        tExit.schedule(new timetask(), logintime);  
		    }  
		  
		    @Override  
		    public void connectionClosedOnError(Exception e) {  
		        Log.i("recontest", "連接關閉異常");  
		        // 判斷為帳號已被登錄  
		        boolean error = e.getMessage().equals("stream:error (conflict)");  
		        if (!error) {  
		            // 關閉連接  
			        XMPPConnectionUtil.getInstanceNotPresence().ConnectServerNotPresence(address).disconnect();
		            // 重连服务器  
		            tExit = new Timer();  
		            tExit.schedule(new timetask(), logintime);  
		        }  
		    }  
		  
		    class timetask extends TimerTask {  
		        @Override  
		        public void run() {  
		          Log.i("recontest", "username"+username); 
		            if (username != null && password != null) {  
		                Log.i("recontest", "嘗試登錄");  
		                // 连接服务器  
		                try {
							XMPPConnectionUtil.getInstanceNotPresence().ConnectServerNotPresence(address).login(username, password);
							Presence presence = new Presence(Presence.Type.available);
							XMPPConnectionUtil.getInstanceNotPresence().ConnectServerNotPresence(address).sendPacket(presence);
							Log.i("recontest", "登錄成功"); 
		                    } catch (XMPPException|IllegalStateException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		              
		            }  
		        }  
		    }  
		  
		    @Override  
		    public void reconnectingIn(int arg0) {  
		    }  
		  
		    @Override  
		    public void reconnectionFailed(Exception arg0) {  
		    }  
		  
		    @Override  
		    public void reconnectionSuccessful() {  
		}  
}
