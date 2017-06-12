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
		        Log.i("recontest", "�B���P�]");  
		        // �P�]�B��  
		        XMPPConnectionUtil.getInstanceNotPresence().ConnectServerNotPresence(address).disconnect();;
		        // ����������  
		        tExit = new Timer();  
		        tExit.schedule(new timetask(), logintime);  
		    }  
		  
		    @Override  
		    public void connectionClosedOnError(Exception e) {  
		        Log.i("recontest", "�B���P�]����");  
		        // �Д��鎤̖�ѱ����  
		        boolean error = e.getMessage().equals("stream:error (conflict)");  
		        if (!error) {  
		            // �P�]�B��  
			        XMPPConnectionUtil.getInstanceNotPresence().ConnectServerNotPresence(address).disconnect();
		            // ����������  
		            tExit = new Timer();  
		            tExit.schedule(new timetask(), logintime);  
		        }  
		    }  
		  
		    class timetask extends TimerTask {  
		        @Override  
		        public void run() {  
		          Log.i("recontest", "username"+username); 
		            if (username != null && password != null) {  
		                Log.i("recontest", "�Lԇ���");  
		                // ���ӷ�����  
		                try {
							XMPPConnectionUtil.getInstanceNotPresence().ConnectServerNotPresence(address).login(username, password);
							Presence presence = new Presence(Presence.Type.available);
							XMPPConnectionUtil.getInstanceNotPresence().ConnectServerNotPresence(address).sendPacket(presence);
							Log.i("recontest", "��䛳ɹ�"); 
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
