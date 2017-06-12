package cn.saprta1029.sayi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;

import java.util.*;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import cn.saprta1029.sayi.R;
import cn.sparta1029.sayi.utils.SPUtil;
import cn.sparta1029.sayi.xmpp.XMPPConnectionUtil;

/*����Ҫ�ڵ�����Ͱ���ʱ������Ϣ����chatList�У�������addTextToList()��
 * ��ΪPacketListener�ڼ�����������Ϣʱ��ֹ����ձ��˷�����Ϣ��Ҳ���ȡ�Լ����͵���Ϣ*/

public class ChatroomActivity extends Activity implements OnClickListener{

    ArrayList<HashMap<String,String>> chatList=null;// ������Ϣ������������Ϣ���ˣ�user��other������Ϣ����  
    public final static int OTHER=1;
    public final static int USER=0;
    protected ListView chatListView=null;
    protected Button btnSendMessage=null;
    protected EditText editText=null;   
    protected MyChatAdapter adapter=null;
    private Handler handler = new Handler(); 
    protected XMPPConnection connect ;
    protected String account,password,serverAddress;
    protected MultiUserChat multiUserChat;
    protected static int ChatroomHistoryMAX;
    protected ImageButton ibtnBack,ibtnInfoChatroom;
	String chatroomName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chatroom);

        ibtnBack=(ImageButton)this.findViewById(R.id.chatroom_back);
        ibtnBack.setOnClickListener(this);
        ibtnInfoChatroom=(ImageButton)this.findViewById(R.id.chatroom_info);
        ibtnInfoChatroom.setOnClickListener(this);
        
        
        chatList=new ArrayList<HashMap<String,String>>();       
        btnSendMessage=(Button)findViewById(R.id.chatroom_bottom_sendbutton);
        editText=(EditText)findViewById(R.id.chatroom_bottom_edittext);
        chatListView=(ListView)findViewById(R.id.chatroom_list);  
        
		final String chatroomPassword;
        SPUtil SPUtil = new SPUtil(this);
        ChatroomHistoryMAX=Integer.parseInt(SPUtil.getString(SPUtil.keyChatroomHistoryMAX, ""));
		account = SPUtil.getString(SPUtil.keyCurrentUser, "");
		password = SPUtil.getString(SPUtil.keyCurrentPassword, "");
		serverAddress = SPUtil.getString(SPUtil.keyAddress, "");
		
		//��ȡ��MainActivity���ݹ����� �������������������루�������Ϊ�գ�
		Intent intent = getIntent();
		chatroomName=intent.getStringExtra("chatroom");
		chatroomPassword=intent.getStringExtra("password");
		TextView tvFriendAccount=(TextView)this.findViewById(R.id.chatroom_name);
		tvFriendAccount.setText(chatroomName);
        adapter=new MyChatAdapter(this,chatList);
        btnSendMessage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	try {
            		String message=editText.getText().toString().trim();
            		if(message==null||"".equals(message))
            		Toast.makeText(ChatroomActivity.this, "�޷����Ϳ���Ϣ", Toast.LENGTH_SHORT).show();
            		else
            		{
            			editText.setText("");
            		    multiUserChat.sendMessage(message);
            		}
				} catch (XMPPException e) {
					e.printStackTrace();
				}   
            }
        });
        
        chatListView.setAdapter(adapter);
       
        
        new Thread(new Runnable() {
			@Override
			public void run() {	
			connect=XMPPConnectionUtil.getInstanceNotPresence().getConnection(serverAddress);
			if(joinChatRoom(chatroomName,chatroomPassword))
			{
			findMulitUser(multiUserChat);
			}
			else
			{
			Intent intent= new Intent(ChatroomActivity.this, MainActivity.class);
			intent.putExtra("chatroomfail", false);
			finish();
			startActivity(intent);
			}
			}
		}).start();       
        
    } 
  
    
    @Override
	public void onClick(View v) {  
        switch(v.getId()){  
        case R.id.chatroom_back:  //������ҳ
        	Intent intentBack= new Intent(ChatroomActivity.this, MainActivity.class);
        	finish();
            startActivity(intentBack);
        	break;  
        case R.id.chatroom_info:  
        	
			new Thread(new Runnable() {
				@Override
				public void run() {
					ArrayList<String> userList = findMulitUser(multiUserChat);
					Intent intentInfo= new Intent(ChatroomActivity.this, InfoChatroomActivity.class);
					intentInfo.putStringArrayListExtra("userlist", userList);
					intentInfo.putExtra("roomname", chatroomName);
					startActivity(intentInfo);
				}
			}).start();
	    	 
            break;  
        }  
    }  

    //��ѯ�������ڵ��û�
    public ArrayList<String> findMulitUser(MultiUserChat muc) {  
        if (connect == null)  
            return null;  
        ArrayList<String> listUser = new ArrayList<String>();  
        Iterator<String> it = muc.getOccupants();  
        // ��������������Ա����  
        while (it.hasNext()) {  
            // �����ҳ�Ա����  
            String name = StringUtils.parseResource(it.next());  
            listUser.add(name); 
            Log.i("findtest","name"+name);
        }  
        return listUser;  
}  
    
    @Override
    public boolean  onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent=new Intent(ChatroomActivity.this,MainActivity.class);
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}
    
    public boolean joinChatRoom(String roomName,String chatroomPassword) {  
		multiUserChat = new MultiUserChat(connect, roomName+"@conference."+connect.getServiceName());  
		if(chatroomPassword==null||"".equals(chatroomPassword)){
		    try {  
		    	multiUserChat.addMessageListener(new PacketListener() {   
		    		                @Override
									public void processPacket(Packet packet) {   
		    		                    Message message = (Message) packet; 
		    		                    //message.getFrom()Ϊ��ʽ��sparta@conference.sparta1029/masterchief���ַ�������Ҫ�����иֱ�ӻ�ȡ�û���
		    		                    String sender=message.getFrom();
		    		                    sender= sender.substring(sender.indexOf("/")+1);
		    		                    addTextToList(message.getBody(),sender);
						                handler.post(new Runnable() {  
			    		                	                    @Override  
			    		                	                    public void run() {   
			    		                    		                adapter.notifyDataSetChanged();
			    		                    		                chatListView.setSelection(chatList.size()-1);
			    		                	                    }  
			    		                	                });  
		    		                }   
		    		            });
		    	DiscussionHistory history=new DiscussionHistory();
				history.setMaxStanzas(ChatroomHistoryMAX);
		    	multiUserChat.join(account,null, history,SmackConfiguration.getPacketReplyTimeout());
		        return true;
		    } catch (XMPPException e) {  
		  Log.i("jointest", "XMPPException :"+e.toString());
		        e.printStackTrace(); 
		        return false; 
		    }
		}
		    else
		    {
		        try {  
			        //multiUserChat.join(account); //userΪ�㴫����û��� 
			    	multiUserChat.addMessageListener(new PacketListener() {   
			    		                @Override
										public void processPacket(Packet packet) {   
			    		                    Message message = (Message) packet;   
			    		                    String sender=message.getFrom();
	    		                    		sender= sender.substring(sender.indexOf("/")+1);
	    		                    		addTextToList(message.getBody(),sender);
							                handler.post(new Runnable() {  
				    		                	                    @Override  
				    		                	                    public void run() {    
				    		                    		                adapter.notifyDataSetChanged();
				    		                    		                chatListView.setSelection(chatList.size()-1);
				    		                	                    }  
				    		                	                });   }   
			    		            });
			    	DiscussionHistory history=new DiscussionHistory();
					history.setMaxStanzas(ChatroomHistoryMAX);
					multiUserChat.join(account,chatroomPassword, history, SmackConfiguration.getPacketReplyTimeout());
					return true;
			    } catch (XMPPException e) {  
			  Log.i("jointest", "XMPPException :"+e.toString());
			        e.printStackTrace();  
			        return false;
		    }
		}
		
		}

	
    protected void addTextToList(String text, String sender){
        HashMap<String,String> map=new HashMap<String,String>();
        map.put("sender",sender );
        map.put("text", text);
        chatList.add(map);
    }
    
    private class MyChatAdapter extends BaseAdapter{

        Context context=null;
        ArrayList<HashMap<String,String>> chatList=null;
        
        public MyChatAdapter(Context context,
                ArrayList<HashMap<String, String>> chatList) {
            super();
            this.context = context;
            this.chatList = chatList;
        }

        @Override
        public int getCount() {
            return chatList.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class ViewHolder{
            public TextView textViewName=null;
            public TextView textViewText=null;
        
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=null;
            String sender=chatList.get(position).get("sender");
            //who ����ȡֵ0��Ϊ user��/1��Ϊ other��,from
            if(sender.equals(account))
            {
                convertView= LayoutInflater.from(context).inflate(
                		R.layout.chatroom_listitem_user, null);
                holder=new ViewHolder();
                //to��0��1Ϊuser���û�������Ϣ���ݣ�2��3Ϊother���û���������
                holder.textViewName=(TextView)convertView.findViewById( R.id.chatroomlist_user);
                holder.textViewText=(TextView)convertView.findViewById(R.id.chatroomlist_text_user);
                Resources resourcesBG = context.getResources();
                Drawable drawableBG = resourcesBG.getDrawable(R.drawable.talk_user);
                holder.textViewText.setBackground(drawableBG);
                
            holder.textViewName.setText(sender);
            holder.textViewText.setText(chatList.get(position).get("text").toString());
            return convertView;
            }
            else
            {
                convertView= LayoutInflater.from(context).inflate(
                		R.layout.chatroom_listitem_other, null);
                holder=new ViewHolder();
                //to��0��1Ϊuser���û�������Ϣ���ݣ�2��3Ϊother���û���������
                holder.textViewName=(TextView)convertView.findViewById( R.id.chatroomlist_other);
                holder.textViewText=(TextView)convertView.findViewById(R.id.chatroomlist_text_other);
                
                Resources resourcesBG = context.getResources();
                Drawable drawableBG = resourcesBG.getDrawable(R.drawable.talk_other);
                holder.textViewText.setBackground(drawableBG);
                
            holder.textViewName.setText(sender);
            holder.textViewText.setText(chatList.get(position).get("text").toString());
            return convertView;
            }
        }
    }
}
