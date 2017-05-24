package cn.saprta1029.sayi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
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

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import cn.saprta1029.sayi.R;
import cn.sparta1029.sayi.db.BlacklistDBManger;
import cn.sparta1029.sayi.db.BlacklistDBOpenHelper;
import cn.sparta1029.sayi.db.MessageDBManager;
import cn.sparta1029.sayi.db.MessageDBOpenHelper;
import cn.sparta1029.sayi.db.MessageEntity;
import cn.sparta1029.sayi.utils.HashMapSort;
import cn.sparta1029.sayi.utils.SPUtil;
import cn.sparta1029.sayi.xmpp.UserSearchEntity;
import cn.sparta1029.sayi.xmpp.UsersSearch;
import cn.sparta1029.sayi.xmpp.XMPPConnectionUtil;

public class ChatActivity extends Activity implements OnClickListener {

	ArrayList<HashMap<String, Object>> chatList = null;// ����ÿ����Ϣ������������Ϣ���ˣ�user��other������Ϣ����
	public final static int OTHER = 1;
	public final static int USER = 0;
	protected ListView chatListView = null;
	protected Button btnSendMessage = null;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// ���������û�
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat);
		chatList = new ArrayList<HashMap<String, Object>>();
		btnSendMessage = (Button) findViewById(R.id.chat_bottom_sendbutton);
		editText = (EditText) findViewById(R.id.chat_bottom_edittext);
		chatListView = (ListView) findViewById(R.id.chat_list);

		// ��ȡ��Ļ���
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
		adapter = new MyChatAdapter(this, chatList, userAccount, otherAccount);

		new Thread(new Runnable() {
			@Override
			public void run() {
				connect = XMPPConnectionUtil.getInstanceNotPresence()
						.getConnection(serverAddress);
				// ���������û���������Ϣ
				chatManager = connect.getChatManager();

				BlacklistDBOpenHelper DBHelper = new BlacklistDBOpenHelper(
						ChatActivity.this, "sayi", null, 1);
				SQLiteDatabase db = DBHelper.getWritableDatabase();
				BlacklistDBManger blacklistDBManager = new BlacklistDBManger();
				blacklistAccountList = blacklistDBManager.blacklistAllAccountQuery(db);
				db.close();
				
				if(chatManager.getChatListeners()!=null)
				{
					chatListener=chatManager.getChatListeners();
				}
				
				Iterator<ChatManagerListener> it = chatListener.iterator();
				while (it.hasNext()) {
					ChatManagerListener temp = it.next();
				    chatManager.removeChatListener(temp);
				    Log.i("test", "temp   "+temp);
				}
				
				chat = chatManager.createChat(
						otherAccount + "@" + connect.getServiceName(), null); // �õ�����һ���ʺŵ����ӣ�������һ��һ,@�������㰲װopenfireʱע�����
				chatManager.addChatListener(chatManagerListener);			
			}
		}).start();

				
		btnSendMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String text = editText.getText().toString().trim();
				if ("".equals(text) || text == null)
					Toast.makeText(ChatActivity.this, "�޷����Ϳ���Ϣ",
							Toast.LENGTH_SHORT).show();
				else {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								//���Լ����͵���Ϣ�������ݿ�
								
								MessageDBOpenHelper DBHelper = new MessageDBOpenHelper(
										ChatActivity.this, "sayi", null, 1);
								SQLiteDatabase db = DBHelper.getWritableDatabase();
								MessageDBManager MessageDBManager = new MessageDBManager();
								MessageEntity messageEntity=new MessageEntity(otherAccount,account,text,"readed");
								MessageDBManager.messageInsert(db, messageEntity);
								db.close();
								chat.sendMessage(text);
								addTextToList(text, USER);
								/**
								 * ���������б�����ͨ��setSelection����ʹListViewʼ�չ�������׶�
								 */
								handler.post(new Runnable() {
									@Override
									public void run() {
										editText.setText(" ");
										adapter.notifyDataSetChanged();
										chatListView.setSelection(chatList
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
		
		chatListView.setAdapter(adapter);

		
		MessageDBOpenHelper DBHelper = new MessageDBOpenHelper(
				ChatActivity.this, "sayi", null, 1);
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		MessageDBManager MessageDBManager = new MessageDBManager();
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
		//�ϲ���������
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
					addTextToList(messageAll.get(i).get("message"), USER);
				}
			}
			for (int j= 0; j < otherNumber.size(); j++)
			{
				if(otherNumber.get(j).equals(messageAll.get(i).get("number")))
				{
					addTextToList(messageAll.get(i).get("message"), OTHER);
				}
			}
		}
		handler.post(new Runnable() {
			@Override
			public void run() {
				adapter.notifyDataSetChanged();
				chatListView.setSelection(chatList.size() - 1);
			}
		});
		
		
		ibtnBack = (ImageButton) this.findViewById(R.id.chat_friend_back);
		ibtnBack.setOnClickListener(this);
		ibtnInfoFriend = (ImageButton) this.findViewById(R.id.chat_info_friend);
		ibtnInfoFriend.setOnClickListener(this);
	}

	public ChatManagerListener chatManagerListener = new ChatManagerListener() {
		@Override
		public void chatCreated(Chat allchat, boolean arg1) {
			allchat.addMessageListener(new MessageListener() {
				@Override
				public void processMessage(Chat allchat,
						org.jivesoftware.smack.packet.Message message) {
					// ����ʽ��masterchief@sparta1029/Spark
					// 2.8.3.960����Ҫ�����и���һ���յ���ֵ����Ҫ����
					String sender = message.getFrom().split("@")[0];
					sender = sender.substring(sender.indexOf("/") + 1);
					//���Ե�ǰ�����û���
					if(!blacklistAccountList.contains(sender)){
					if(otherAccount.equals(sender))
					{
						Log.i("messagetest", "message:"+message.getBody());
						if (message.getBody() == null
								|| "".equals(message.getBody())) {
							return;
						} else {
							addTextToList(message.getBody(), OTHER);
							/**
							 * ���������б�
							 * ����ͨ��setSelection����ʹListViewʼ�չ�������׶�
							 */
							handler.post(new Runnable() {
								@Override
								public void run() {
									adapter.notifyDataSetChanged();
									chatListView
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
					MessageEntity messageEntity = new MessageEntity(account,sender,
							message.getBody(), "unreaded");
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
					// ����ʽ��masterchief@sparta1029/Spark
					// 2.8.3.960����Ҫ�����и���һ���յ���ֵ����Ҫ����
					String sender = message.getFrom().split("@")[0];
					sender = sender.substring(sender.indexOf("/") + 1);
					if(!blacklistAccountList.contains(sender)){
					if(message.getBody()==null||"".equals(message.getBody()))
	                	return;
	                else
	                {
	                	//���յ�����Ϣ�������ݿ⣬���Ϊunreaded
					MessageEntity messageEntity = new MessageEntity(account,sender,
							message.getBody(), "unreaded");
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
	
	
	// ��Ӧ���� �ص���ҳ�ͻ�ȡ��Ϣ
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.chat_friend_back: // ������ҳ
			Intent intentBack = new Intent(ChatActivity.this,
					MainActivity.class);
			finish();
			startActivity(intentBack);
			chatManager.removeChatListener(
					chatManagerListener);
			chatManager.addChatListener(allChatManagerListener);
			break;
		case R.id.chat_info_friend: // ��ȡ������Ϣ
			// TODO �����û��߳�
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

	protected void addTextToList(String text, int who) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("person", who);
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
			public TextView textViewName = null;
			public TextView textViewText = null;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			int who = (Integer) chatList.get(position).get("person");
			// who ����ȡֵ0��Ϊ user��/1��Ϊ other��,from
			if (who == 0) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.chat_listitem_user, null);
				holder = new ViewHolder();
				// to��0��1Ϊuser���û�������Ϣ���ݣ�2��3Ϊother���û���������
				holder.textViewName = (TextView) convertView
						.findViewById(R.id.chatlist_user);
				holder.textViewText = (TextView) convertView
						.findViewById(R.id.chatlist_text_user);

				Resources resourcesBG = context.getResources();
				Drawable drawableBG = resourcesBG
						.getDrawable(R.drawable.talk_user);
				holder.textViewText.setBackground(drawableBG);

				holder.textViewName.setText(userAccount);
				holder.textViewText.setText(chatList.get(position).get("text")
						.toString());

				return convertView;
			} else {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.chat_listitem_other, null);
				holder = new ViewHolder();
				// to��0��1Ϊuser���û�������Ϣ���ݣ�2��3Ϊother���û���������
				holder.textViewName = (TextView) convertView
						.findViewById(R.id.chatlist_other);
				holder.textViewText = (TextView) convertView
						.findViewById(R.id.chatlist_text_other);

				Resources resourcesBG = context.getResources();
				Drawable drawableBG = resourcesBG
						.getDrawable(R.drawable.talk_other);
				holder.textViewText.setBackground(drawableBG);

				holder.textViewName.setText(otherAccount);
				holder.textViewText.setText(chatList.get(position).get("text")
						.toString());

				return convertView;
			}
		}
	}
}
