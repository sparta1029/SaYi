package cn.saprta1029.sayi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;

import java.util.*;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import cn.saprta1029.sayi.R;
import cn.sparta1029.sayi.utils.SPUtil;
import cn.sparta1029.sayi.xmpp.XMPPConnectionUtil;

public class ChatActivity extends Activity{

    ArrayList<HashMap<String,Object>> chatList=null;// 保存每个信息，包括发送信息的人（user，other）和信息内容  
    public final static int OTHER=1;
    public final static int USER=0;
    protected ListView chatListView=null;
    protected Button btnSendMessage=null;
    protected EditText editText=null;   
    protected MyChatAdapter adapter=null;
    private Handler handler = new Handler(); 
    protected XMPPConnection connect ;
    String account,password,serverAddress;
    
    private Map<String, Chat> chatManage = new HashMap<String, Chat>();// 聊天窗口管理map集合  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 传入两个用户
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        chatList=new ArrayList<HashMap<String,Object>>();       
        btnSendMessage=(Button)findViewById(R.id.chat_bottom_sendbutton);
        editText=(EditText)findViewById(R.id.chat_bottom_edittext);
        chatListView=(ListView)findViewById(R.id.chat_list);  

        String userAccount,otherAccount;

        SPUtil SPUtil = new SPUtil(this);
		account = SPUtil.getString(SPUtil.keyCurrentUser, "");
		password = SPUtil.getString(SPUtil.keyCurrentPassword, "");
		serverAddress = SPUtil.getString(SPUtil.keyAddress, "");
		Intent intent = getIntent();
		otherAccount=intent.getStringExtra("otherAccount");
		userAccount=account;
		TextView tvFriendAccount=(TextView)this.findViewById(R.id.chat_contact_name);
		tvFriendAccount.setText(otherAccount);
        adapter=new MyChatAdapter(this,chatList,userAccount,otherAccount);
        btnSendMessage.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                final String text=editText.getText().toString();
                if("".equals(text)||text==null)
                    Toast.makeText(ChatActivity.this, "无法发送空信息", Toast.LENGTH_SHORT).show();
                else{
                	new Thread(new Runnable() {
    				@Override
    				public void run() {	
    					Chat chat = getFriendChat("tomato",null);
    					try {
    						chat.sendMessage(text);
    		                addTextToList(text, USER);
    		                /**
    		                 * 更新数据列表，并且通过setSelection方法使ListView始终滚动在最底端
    		                 */
    		                  handler.post(new Runnable() {  
    		                	                    @Override  
    		                	                    public void run() {  
    		                	                    	editText.setText(" ");  
    		                    		                adapter.notifyDataSetChanged();
    		                    		                chatListView.setSelection(chatList.size()-1);
    		                	                    }  
    		                	                });  
    					} catch (XMPPException e) {
    						e.printStackTrace();
    					}
    				}
    			}).start();
                }     
            }
        });
        chatListView.setAdapter(adapter);
    	
        new Thread(new Runnable() {
			@Override
			public void run() {	
			connect=XMPPConnectionUtil.ConnectServer(serverAddress);
			connect.getChatManager().addChatListener(new ChatManagerListener() {  
				    @Override  
				    public void chatCreated(Chat chat, boolean createdLocally) {  
				        chat.addMessageListener(new MessageListener() {  
				            @Override  
				            public void processMessage(Chat chat, Message message) {  
				                String messageBody = message.getBody();   
				                if(messageBody==null||"".equals(messageBody))
				                	return;
				                else
				                {
				                addTextToList(messageBody, OTHER);
	    		                /**
	    		                 * 更新数据列表，并且通过setSelection方法使ListView始终滚动在最底端
	    		                 */
	    		                  handler.post(new Runnable() {  
	    		                	                    @Override  
	    		                	                    public void run() {  
	    		                	                    	editText.setText(" ");  
	    		                    		                adapter.notifyDataSetChanged();
	    		                    		                chatListView.setSelection(chatList.size()-1);
	    		                	                    }  
	    		                	                });     
				            }
				            }  
				        });  
				    }  
				}); 
			}
		}).start();
           
    } 
    
    public Chat getFriendChat(String friend, MessageListener listenter) { 
	    loginServer();
	    if(connect==null)  
	        return null;  
	    /** 判断是否创建聊天窗口 */  
	    for (String fristr : chatManage.keySet()) {  
	        if (fristr.equals(friend)) {  
	            // 存在聊天窗口，则返回对应聊天窗口  
	            return chatManage.get(fristr);  
	        }  
	    }  
	    /** 创建聊天窗口 */  
	    Chat chat = connect.getChatManager().createChat(friend + "@"+  
	    		connect.getServiceName(), listenter);  
	    /** 添加聊天窗口到chatManage */  
	    chatManage.put(friend, chat);  
	    return chat;  
	} 
    

	private Boolean loginServer() {
				try {
					connect.login(account,password);
					return true;
				} catch (Exception e) {
					Log.e("logintest", "error     " + e.toString());
					e.printStackTrace();
				} 
				return false;
	}
    
    
    
    
    protected void addTextToList(String text, int who){
        HashMap<String,Object> map=new HashMap<String,Object>();
        map.put("person",who );
        map.put("text", text);
        chatList.add(map);
    }
    
    private class MyChatAdapter extends BaseAdapter{

        Context context=null;
        ArrayList<HashMap<String,Object>> chatList=null;
        String userAccount,otherAccount;
        
        public MyChatAdapter(Context context,
                ArrayList<HashMap<String, Object>> chatList,String userAccount,String otherAccount) {
            super();
            this.context = context;
            this.chatList = chatList;
            this.userAccount=userAccount;
            this.otherAccount=otherAccount;
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
            int who=(Integer)chatList.get(position).get("person");
            //who 两个取值0（为 user）/1（为 other）,from
            if(who==0)
            {
                convertView= LayoutInflater.from(context).inflate(
                		R.layout.chat_listitem_user, null);
                holder=new ViewHolder();
                //to中0，1为user的用户名和消息内容，2，3为other的用户名和内容
                holder.textViewName=(TextView)convertView.findViewById( R.id.chatlist_user);
                holder.textViewText=(TextView)convertView.findViewById(R.id.chatlist_text_user);
            holder.textViewName.setText(userAccount);
            holder.textViewText.setText(chatList.get(position).get("text").toString());
            return convertView;
            }
            else
            {
                convertView= LayoutInflater.from(context).inflate(
                		R.layout.chat_listitem_other, null);
                holder=new ViewHolder();
                //to中0，1为user的用户名和消息内容，2，3为other的用户名和内容
                holder.textViewName=(TextView)convertView.findViewById( R.id.chatlist_other);
                holder.textViewText=(TextView)convertView.findViewById(R.id.chatlist_text_other);
            holder.textViewName.setText(otherAccount);
            holder.textViewText.setText(chatList.get(position).get("text").toString());
            return convertView;
            }
        }
    }
}
