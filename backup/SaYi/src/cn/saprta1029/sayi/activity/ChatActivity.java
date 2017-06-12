package cn.saprta1029.sayi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import java.io.File;
import java.util.*;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import cn.saprta1029.sayi.R;
import cn.sparta1029.sayi.db.BlacklistDBManger;
import cn.sparta1029.sayi.db.BlacklistDBOpenHelper;
import cn.sparta1029.sayi.db.MessageDBManager;
import cn.sparta1029.sayi.db.MessageDBOpenHelper;
import cn.sparta1029.sayi.db.MessageEntity;
import cn.sparta1029.sayi.utils.GetNetWorkTime;
import cn.sparta1029.sayi.utils.HashMapSort;
import cn.sparta1029.sayi.utils.SPUtil;
import cn.sparta1029.sayi.xmpp.UserSearchEntity;
import cn.sparta1029.sayi.xmpp.UsersSearch;
import cn.sparta1029.sayi.xmpp.XMPPConnectionUtil;

public class ChatActivity extends Activity implements OnClickListener {

	String sendFilePath="/storage/sdcard1/毕业设计.txt";
	String sendFileName="毕业设计";
	ArrayList<HashMap<String, Object>> chatList = null;// 保存每个信息，包括发送信息的人（user，other）和信息内容
	public final static int OTHER = 1;
	public final static int USER = 0;
	private static final int FILE_SELECT_CODE = 5;
	protected ListView lvChatList = null;
	protected Button btnSendMessage;
	ImageButton btnSendFileMessage;
	protected EditText editText = null;
	protected MyChatAdapter adapter = null;
	private Handler handler = new Handler();
	protected XMPPConnection connect;
	String account, password, serverAddress;
	int tvWidth;
	protected ImageButton ibtnBack, ibtnInfoFriend;
	String userAccount, otherAccount;
	Chat chat;
	ChatManager chatManager;
	TextView tvFriendAccount;
	Collection<ChatManagerListener> chatListener;
	ArrayList<String> blacklistAccountList;
    FileTransferManager fileTransferManager;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 传入两个用户
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat);
		chatList = new ArrayList<HashMap<String, Object>>();
		btnSendMessage = (Button) findViewById(R.id.chat_bottom_sendbutton);
		btnSendFileMessage = (ImageButton) findViewById(R.id.chat_bottom_sendfilebutton);
		editText = (EditText) findViewById(R.id.chat_bottom_edittext);
		lvChatList = (ListView) findViewById(R.id.chat_list);

		// 获取屏幕宽度
		Point point = new Point();
		getWindowManager().getDefaultDisplay().getSize(point);
		tvWidth = (int) (0.7 * point.x);

		SPUtil SPUtil = new SPUtil(this);
		account = SPUtil.getString(SPUtil.keyCurrentUser, "");
		password = SPUtil.getString(SPUtil.keyCurrentPassword, "");
		serverAddress = SPUtil.getString(SPUtil.keyAddress, "");
		Intent intent = getIntent();
		otherAccount = intent.getStringExtra("otherAccount");
		userAccount = account;
		tvFriendAccount = (TextView) this
				.findViewById(R.id.chat_contact_name);
		tvFriendAccount.setText(otherAccount);

		MessageDBOpenHelper DBHelper = new MessageDBOpenHelper(
				ChatActivity.this, "sayi", null, 1);
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		MessageDBManager MessageDBManager = new MessageDBManager();
		
		adapter = new MyChatAdapter(this, chatList, userAccount, otherAccount);

		new Thread(new Runnable() {
			@Override
			public void run() {
				connect = XMPPConnectionUtil.getInstanceNotPresence()
						.getConnection(serverAddress);
				// 监听所有用户发来的信息
				chatManager = connect.getChatManager();

				BlacklistDBOpenHelper DBHelper = new BlacklistDBOpenHelper(
						ChatActivity.this, "sayi", null, 1);
				SQLiteDatabase db = DBHelper.getWritableDatabase();
				BlacklistDBManger blacklistDBManager = new BlacklistDBManger();
				blacklistAccountList = blacklistDBManager.blacklistAllAccountQuery(db,account);
				db.close();
				
				if(chatManager.getChatListeners()!=null)
				{
					chatListener=chatManager.getChatListeners();
				}
				
				Iterator<ChatManagerListener> it = chatListener.iterator();
				while (it.hasNext()) {
					ChatManagerListener temp = it.next();
				    chatManager.removeChatListener(temp);
				}
				chat = chatManager.createChat(
						otherAccount + "@" + connect.getServiceName(), null); // 得到与另一个帐号的连接，这里是一对一,@后面是你安装openfire时注册的域
				chatManager.addChatListener(chatManagerListener);			
			}
		}).start();

				
		btnSendMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String text = editText.getText().toString().trim();
				if ("".equals(text) || text == null)
					Toast.makeText(ChatActivity.this, "无法发送空信息",
							Toast.LENGTH_SHORT).show();
				else {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								//将自己发送的消息存入数据库
								
								MessageDBOpenHelper DBHelper = new MessageDBOpenHelper(
										ChatActivity.this, "sayi", null, 1);
								SQLiteDatabase db = DBHelper.getWritableDatabase();
								MessageDBManager MessageDBManager = new MessageDBManager();
								String messageDate=GetNetWorkTime.getWebsiteDatetime();
								MessageEntity messageEntity=new MessageEntity(otherAccount,account,text,"readed",messageDate);
								MessageDBManager.messageInsert(db, messageEntity);
								db.close();
								chat.sendMessage(text);
								addInfoToList(text, USER,messageDate);
								/**
								 * 更新数据列表，并且通过setSelection方法使ListView始终滚动在最底端
								 */
								handler.post(new Runnable() {
									@Override
									public void run() {
										editText.setText(" ");
										adapter.notifyDataSetChanged();
										lvChatList.setSelection(chatList
												.size() - 1);
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
		
		btnSendFileMessage.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						sendFile(otherAccount,sendFilePath);
						MessageDBOpenHelper DBHelper = new MessageDBOpenHelper(
								ChatActivity.this, "sayi", null, 1);
						SQLiteDatabase db = DBHelper.getWritableDatabase();
						MessageDBManager MessageDBManager = new MessageDBManager();
						String messageDate=GetNetWorkTime.getWebsiteDatetime();
						MessageEntity messageEntity=new MessageEntity(otherAccount,account,"发送文件："+sendFileName,"readed",messageDate);
						MessageDBManager.messageInsert(db, messageEntity);
						db.close();
						addInfoToList("发送文件："+sendFileName, USER,messageDate);
						/**
						 * 更新数据列表，并且通过setSelection方法使ListView始终滚动在最底端
						 */
						handler.post(new Runnable() {
							@Override
							public void run() {
								adapter.notifyDataSetChanged();
								lvChatList.setSelection(chatList
										.size() - 1);
							}
						});
					}
				}).start();
			}
		});
		
		lvChatList.setAdapter(adapter);
		
		//初始化聊天界面
		ArrayList<HashMap<String, String>> messageReceive = MessageDBManager.messageQueryOfSender(db,
				otherAccount);
		ArrayList<HashMap<String, String>> messageSend = MessageDBManager.messageQueryOfUser(db, otherAccount);
		MessageDBManager.messageUpdate(db, otherAccount);
		db.close();
		ArrayList<String> userNumber=new ArrayList<String>();
		ArrayList<String> otherNumber=new ArrayList<String>();
		for(int i=0;i<messageSend.size();i++)
		{
			userNumber.add(messageSend.get(i).get("number"));
		}
		for(int i=0;i<messageReceive.size();i++)
		{
			otherNumber.add(messageReceive.get(i).get("number"));
		}
		ArrayList<HashMap<String, String>> messageAll=new ArrayList<HashMap<String, String>>();
		//合并两组数据
		messageAll.addAll(messageReceive);
		messageAll.addAll(messageSend);

		Collections.sort(messageAll, new HashMapSort(true, true, "number"));	

		Log.i("chattest", "messageAll:"+messageAll.toString());
		for (int i = 0; i < messageAll.size(); i++)
		{
			for (int j= 0; j < userNumber.size(); j++)
			{
				if(userNumber.get(j).equals(messageAll.get(i).get("number")))
				{
					addInfoToList(messageAll.get(i).get("message"), USER,messageAll.get(i).get("time"));
				}
			}
			for (int j= 0; j < otherNumber.size(); j++)
			{
				if(otherNumber.get(j).equals(messageAll.get(i).get("number")))
				{
					addInfoToList(messageAll.get(i).get("message"), OTHER,messageAll.get(i).get("time"));
				}
			}
		}
		handler.post(new Runnable() {
			@Override
			public void run() {
				adapter.notifyDataSetChanged();
				lvChatList.setSelection(chatList.size() - 1);
			}
		});
		
		
		ibtnBack = (ImageButton) this.findViewById(R.id.chat_friend_back);
		ibtnBack.setOnClickListener(this);
		ibtnInfoFriend = (ImageButton) this.findViewById(R.id.chat_info_friend);
		ibtnInfoFriend.setOnClickListener(this);
		
		
		fileTransferManager = new FileTransferManager(connect);  
		fileTransferManager.addFileTransferListener(new FileTransferListener() {  
            	     public void fileTransferRequest(FileTransferRequest request) {  
            	    	 IncomingFileTransfer transfer = request.accept(); 
            	    	 try {
            	    		 String userDire = ChatActivity.this
										.getApplication()
										.getExternalFilesDir(null)
										.getPath()
										+ "/user/" + account + "/";
							transfer.recieveFile(new File(userDire+transfer.getFileName()));
							MessageDBOpenHelper DBHelper = new MessageDBOpenHelper(
									ChatActivity.this, "sayi", null, 1);
							SQLiteDatabase db = DBHelper.getWritableDatabase();
							MessageDBManager MessageDBManager = new MessageDBManager();
							String messageDate=GetNetWorkTime.getWebsiteDatetime();
							MessageEntity messageEntity=new MessageEntity(account,otherAccount
									,"接收文件："+transfer.getFileName(),"readed",messageDate);
							MessageDBManager.messageInsert(db, messageEntity);
							db.close();
							addInfoToList("接收文件"+transfer.getFileName(), OTHER,messageDate);
							handler.post(new Runnable() {
								@Override
								public void run() {
									adapter.notifyDataSetChanged();
									lvChatList.setSelection(chatList
											.size() - 1);
								}
							});
						} catch (XMPPException e) {
							e.printStackTrace();
						}
//            	           if(shouldAccept(request)) {  
//            	                 // Accept it  
//            	                 IncomingFileTransfer transfer = request.accept();  
//            	                 transfer.recieveFile(new File("shakespeare_complete_works.txt"));  
//            	           } else {  
//            	                 // Reject it  
//            	                 request.reject();  // 拒绝接收文件。  
//            	           }  
            	     }  
		});  
		
	}
	public  void sendFile(String to, String filepath) {  
        final OutgoingFileTransfer outgoingFileTransfer = 
        		fileTransferManager.createOutgoingFileTransfer(to 
        				+ "@"+ connect.getServiceName()+"/Spark 2.8.3.960");  
        File insfile = new File(filepath);  
        try {  
            outgoingFileTransfer.sendFile(insfile, "descr");  
        } catch (XMPPException e) {  
            e.printStackTrace();  
        }  
    }   
            
           
//            new Thread(new Runnable() {
//				@Override
//				public void run() {
//					  while(!outgoingFileTransfer.isDone()) {     //可以直接加到  transfer.sendFile 或者  transfer.recieveFile 后面。  
//	            	        if(outgoingFileTransfer.getStatus().equals(Status.error)) {  
//	            	              System.out.println("ERROR!!! " + outgoingFileTransfer.getError());  
//	            	        } else {  
//	            	              System.out.println(outgoingFileTransfer.getStatus());  
//	            	              System.out.println(outgoingFileTransfer.getProgress());  
//	            	        }  
//	            	        try {
//								Thread.sleep(1000);
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} 
//	            	  }  
//				}
//			}).start();
          
        
	
	
	public ChatManagerListener chatManagerListener = new ChatManagerListener() {
		@Override
		public void chatCreated(Chat allchat, boolean arg1) {
			allchat.addMessageListener(new MessageListener() {
				@Override
				public void processMessage(Chat allchat,
						org.jivesoftware.smack.packet.Message message) {
					// 其形式如masterchief@sparta1029/Spark
					// 2.8.3.960，需要进行切割，并且会接收到空值，需要处理
					String sender = message.getFrom().split("@")[0];
					sender = sender.substring(sender.indexOf("/") + 1);
					//来自当前聊天用户的
					if(!blacklistAccountList.contains(sender)){
					if(otherAccount.equals(sender))
					{
						Log.i("messagetest", "message:"+message.toXML());
						if (message.getBody() == null
								|| "".equals(message.getBody())) {
							return;
						} else {
							String messageDate=GetNetWorkTime.getWebsiteDatetime();
							addInfoToList(message.getBody(), OTHER,messageDate);
							/**
							 * 更新数据列表，
							 * 并且通过setSelection方法使ListView始终滚动在最底端
							 */
							handler.post(new Runnable() {
								@Override
								public void run() {
									adapter.notifyDataSetChanged();
									lvChatList
											.setSelection(chatList
													.size() - 1);
								}
							});
						}
					}
					else{
					if(message.getBody()==null||"".equals(message.getBody()))
	                	return;
	                else
	                {
					String messageDate=GetNetWorkTime.getWebsiteDatetime();
					MessageEntity messageEntity = new MessageEntity(account,sender,
							message.getBody(), "unreaded",messageDate);
					MessageDBOpenHelper DBHelper = new MessageDBOpenHelper(
							ChatActivity.this, "sayi", null, 1);
					SQLiteDatabase db = DBHelper.getWritableDatabase();
					MessageDBManager MessageDBManager = new MessageDBManager();
    				MessageDBManager.messageInsert(db, messageEntity);
    				db.close();
				}
				}
					}
				}
			});
		}
	};
	
	
	
	
	
	
	public ChatManagerListener allChatManagerListener = new ChatManagerListener() {
		@Override
		public void chatCreated(Chat chat, boolean arg1) {
			chat.addMessageListener(new MessageListener() {
				@Override
				public void processMessage(Chat arg0,
						org.jivesoftware.smack.packet.Message message) {
					// 其形式如masterchief@sparta1029/Spark
					// 2.8.3.960，需要进行切割，并且会接收到空值，需要处理
					String sender = message.getFrom().split("@")[0];
					sender = sender.substring(sender.indexOf("/") + 1);
					if(!blacklistAccountList.contains(sender)){
					if(message.getBody()==null||"".equals(message.getBody()))
	                	return;
	                else
	                {
	                	//接收到的消息存入数据库，标记为unreaded
	                String messageDate=GetNetWorkTime.getWebsiteDatetime();
					MessageEntity messageEntity = new MessageEntity(account,sender,
							message.getBody(), "unreaded",messageDate);
					MessageDBOpenHelper DBHelper = new MessageDBOpenHelper(
							ChatActivity.this, "sayi", null, 1);
					SQLiteDatabase db = DBHelper.getWritableDatabase();
					MessageDBManager MessageDBManager = new MessageDBManager();
					MessageDBManager.messageInsert(db, messageEntity);	
					db.close();
				}
					}
				}
			});
		}
	};
	
	
	// 响应按键 回到主页和获取信息
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.chat_friend_back: // 返回主页
			Intent intentBack = new Intent(ChatActivity.this,
					MainActivity.class);
			finish();
			startActivity(intentBack);
			chatManager.removeChatListener(
					chatManagerListener);
			chatManager.addChatListener(allChatManagerListener);
			break;
		case R.id.chat_info_friend: // 获取个人信息
			// TODO 查找用户线程
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						List<UserSearchEntity> listInfoFriend = UsersSearch
								.searchUsers(connect, serverAddress,otherAccount);
						Intent intentInfo = new Intent(ChatActivity.this,
								InfoFriendActivity.class);
						UserSearchEntity infoFriend = listInfoFriend.get(0);
						intentInfo.putExtra("username",
								infoFriend.getUserName());
						intentInfo.putExtra("name", infoFriend.getName());
						intentInfo.putExtra("email", infoFriend.getEmail());
						startActivity(intentInfo);
						chatManager.removeChatListener(
								chatManagerListener);
						chatManager.addChatListener(allChatManagerListener);
					} catch (XMPPException e) {
						e.printStackTrace();
					}
				}
			}).start();

			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(ChatActivity.this, MainActivity.class);
			startActivity(intent);
			chatManager.removeChatListener(
					chatManagerListener);
			chatManager.addChatListener(allChatManagerListener);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void addInfoToList(String text, int who,String time) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("person", who);
		map.put("time", time);
		map.put("text", text);
		chatList.add(map);
	}

	private class MyChatAdapter extends BaseAdapter {

		Context context = null;
		ArrayList<HashMap<String, Object>> chatList = null;
		String userAccount, otherAccount;
		
		public MyChatAdapter(Context context,
				ArrayList<HashMap<String, Object>> chatList,
				String userAccount, String otherAccount) {
			super();
			this.context = context;
			this.chatList = chatList;
			this.userAccount = userAccount;
			this.otherAccount = otherAccount;
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

		class ViewHolder {
			public TextView tvName = null;
			public TextView tvText = null;
			public TextView tvDate = null;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			int who = (Integer) chatList.get(position).get("person");
			// who 两个取值0（为 user）/1（为 other）,from
			if (who == 0) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.chat_listitem_user, null);
				holder = new ViewHolder();
				// to中0，1为user的用户名和消息内容，2，3为other的用户名和内容
				holder.tvName = (TextView) convertView
						.findViewById(R.id.chatlist_user);
				holder.tvText = (TextView) convertView
						.findViewById(R.id.chatlist_text_user);
				holder.tvDate = (TextView) convertView
						.findViewById(R.id.chatlist_time_user);
				Resources resourcesBG = context.getResources();
				Drawable drawableBG = resourcesBG
						.getDrawable(R.drawable.talk_user);
				holder.tvText.setBackground(drawableBG);

				holder.tvName.setText(userAccount);
				holder.tvText.setText(chatList.get(position).get("text")
						.toString());
				holder.tvDate.setText(chatList.get(position).get("time").toString());
				return convertView;
			} else {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.chat_listitem_other, null);
				holder = new ViewHolder();
				// to中0，1为user的用户名和消息内容，2，3为other的用户名和内容
				holder.tvName = (TextView) convertView
						.findViewById(R.id.chatlist_other);
				holder.tvText = (TextView) convertView
						.findViewById(R.id.chatlist_text_other);
				holder.tvDate = (TextView) convertView
						.findViewById(R.id.chatlist_time_other);

				Resources resourcesBG = context.getResources();
				Drawable drawableBG = resourcesBG
						.getDrawable(R.drawable.talk_other);
				holder.tvText.setBackground(drawableBG);
				holder.tvName.setText(otherAccount);
				holder.tvText.setText(chatList.get(position).get("text")
						.toString());
				holder.tvDate.setText(chatList.get(position).get("time").toString());
				return convertView;
			}
		}
	}
		
	
}
