package cn.saprta1029.sayi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import cn.saprta1029.sayi.R;
import cn.sparta1029.sayi.components.ChatroomPasswordDialog;
import cn.sparta1029.sayi.utils.SPUtil;
import cn.sparta1029.sayi.xmpp.XMPPConnectionUtil;

public class ChatroomSearchActivity extends Activity {

	EditText etSearchAccount;
	Button btnSearch;
	ListView lvSearchResult;
	XMPPConnection connect;
	String account, password, serverAddress;
	String searchAccount;
	ArrayList<HashMap<String,String>> searchResultList = new ArrayList<HashMap<String,String>>();
	SearchResultAdapter adapter;
	Handler handler = new Handler();
	protected MultiUserChat multiUserChat;
	ChatroomPasswordDialog chatroomPasswordDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_chatroom);
		etSearchAccount = (EditText) this
				.findViewById(R.id.chatroom_search_account);
		btnSearch = (Button) this.findViewById(R.id.chatroom_search);
		btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						searchAccount = etSearchAccount.getText().toString();
						searchResultList.clear();
						try {
							List<String> allChatroom = getConferenceRoom();
							for(int i=0;i<allChatroom.size();i++)
							if(allChatroom.get(i).contains(searchAccount))
							{
								HashMap<String,String> searchResultHashMap=new HashMap<String,String>();
								searchResultHashMap.put("isPasswordProtected", isPasswordProtected(allChatroom.get(i)));
								searchResultHashMap.put("roomName", allChatroom.get(i));	
								searchResultList.add(searchResultHashMap);
							}	
							handler.post(new Runnable() {
								@Override
								public void run() {
									adapter.notifyDataSetChanged();
									lvSearchResult.setSelection(searchResultList
											.size() - 1);
								}
							});
						} catch (XMPPException e) {
							
							Log.i("roomsearchtesterror",
									"XMPPException:" +e.toString());
							e.printStackTrace();
						}
					
					}
				}).start();
			}
		});
		
		
		


		
		lvSearchResult = (ListView) this
				.findViewById(R.id.chatroom_search_result);
		lvSearchResult.setOnItemClickListener(new lvChatroomSearchResultItemClickListener());
		SPUtil SPUtil = new SPUtil(this);
		serverAddress = SPUtil.getString(SPUtil.keyAddress, "");

		
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 查询服务器上所有房间
				try {
					searchResultList.clear();
					connect = XMPPConnectionUtil.getInstanceNotPresence()
							.getConnection(serverAddress);
					List<String> allRoomList = getConferenceRoom();
					for (int i = 0; i < allRoomList.size(); i++) {
						HashMap<String,String> searchResultHashMap=new HashMap<String,String>();
						searchResultHashMap.put("isPasswordProtected", isPasswordProtected(allRoomList.get(i)));
						searchResultHashMap.put("roomName", allRoomList.get(i));
						searchResultList.add(searchResultHashMap);
					}
					handler.post(new Runnable() {
						@Override
						public void run() {
							adapter = new SearchResultAdapter(ChatroomSearchActivity.this,
									searchResultList);
							lvSearchResult.setAdapter(adapter);
						}
					});
					
				} catch (XMPPException e) {
					Log.i("roomsearchtest", e.toString());
					e.printStackTrace();
				}

			}
		}).start();

		
		
	}
	
	
	
	public String isPasswordProtected(String chatroomName) {
		try {
			String text = chatroomName + "@conference."
					+ connect.getServiceName();
			RoomInfo roomInfo;
			roomInfo = MultiUserChat.getRoomInfo(connect, text);
			if (roomInfo != null) {
				if (roomInfo.isPasswordProtected())
					return "true";
				else
					return "false";
			} else
				return "false";
		} catch (XMPPException e) {
			e.printStackTrace();
			Log.i("esittest", "error :" + e.toString());
			return "false";
		}
	}

	
	
	class lvChatroomSearchResultItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(searchResultList.get(position).get("isPasswordProtected").equals("true"))
			{
				final String chatroomNmae=searchResultList.get(position).get("roomName");
				chatroomPasswordDialog = new ChatroomPasswordDialog(ChatroomSearchActivity.this,
						"输入聊天室密码",
						new ChatroomPasswordDialog.OnCustomDialogListener() {
							@Override
							public void back(boolean result, String password) {
								if (!result)
									Toast.makeText(ChatroomSearchActivity.this,
											"请输入正确的密码", Toast.LENGTH_SHORT)
											.show();
								else {
									if(joinChatRoom(chatroomNmae ,password))
									{
										Intent intent = new Intent(ChatroomSearchActivity.this, ChatroomActivity.class);
										intent.putExtra("chatroom",chatroomNmae);
										intent.putExtra("password",password);
										
										startActivity(intent);
										finish();
									}
									else
									{
										Toast.makeText(ChatroomSearchActivity.this,
												"密码错误", Toast.LENGTH_SHORT)
												.show();
								}
								}
							}
						});
				chatroomPasswordDialog.show();
			}
			else
			{
				Log.i("test", "roomName"+searchResultList.get(position).get("roomName"));
				Intent intent = new Intent(ChatroomSearchActivity.this, ChatroomActivity.class);
				intent.putExtra("chatroom", searchResultList.get(position).get("roomName"));
				startActivity(intent);
				finish();
			}
		}
	}
	
	
	 public boolean joinChatRoom(String roomName,String chatroomPassword) {  
		try{		
		 multiUserChat = new MultiUserChat(connect, roomName+"@conference."+connect.getServiceName());  
		
		 DiscussionHistory history=new DiscussionHistory();
		  SPUtil SPUtil = new SPUtil(this);
	        int ChatroomHistoryMAX = Integer.parseInt(SPUtil.getString(SPUtil.keyChatroomHistoryMAX, ""));
	        account = SPUtil.getString(SPUtil.keyCurrentUser, "");
			history.setMaxStanzas(ChatroomHistoryMAX);
		    multiUserChat.join(account,chatroomPassword, history, SmackConfiguration.getPacketReplyTimeout());
				return true;
					    } catch (XMPPException e) {  
					  Log.i("jointest", "XMPPException :"+e.toString());
					        e.printStackTrace();  
					        return false;
			}
}
	
	
	
	public List<String> getConferenceRoom() throws XMPPException {
		List<String> list = new ArrayList<String>();
		new ServiceDiscoveryManager(connect);
		if (!MultiUserChat.getHostedRooms(connect, connect.getServiceName())
				.isEmpty()) {
			for (HostedRoom k : MultiUserChat.getHostedRooms(connect,
					connect.getServiceName())) {
				for (HostedRoom j : MultiUserChat.getHostedRooms(connect,
						k.getJid())) {
					RoomInfo info2 = MultiUserChat.getRoomInfo(connect,
							j.getJid());
					if (j.getJid().indexOf("@") > 0) {
						String FriendRooms = j.getName();// 聊天室的名称
						list.add(FriendRooms);
					}
				}
			}
		}
		return list;
	}

	private class SearchResultAdapter extends BaseAdapter {

		Context context = null;
		ArrayList<HashMap<String,String>> List = new ArrayList<HashMap<String,String>>();

		public SearchResultAdapter(Context context, ArrayList<HashMap<String,String>> List) {
			super();
			this.context = context;
			this.List = List;
		}

		@Override
		public int getCount() {
			return List.size();
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
			public TextView tvResultName = null;
			public ImageView isPasswordProtected=null;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			// who 两个取值0（为 user）/1（为 other）,from

			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_search_chatroom_result, null);
			holder = new ViewHolder();
			// to中0，1为user的用户名和消息内容，2，3为other的用户名和内容
			holder.tvResultName = (TextView) convertView
					.findViewById(R.id.search_chatroom_account);
			holder.isPasswordProtected = (ImageView) convertView
					.findViewById(R.id.search_chatroom_password_protected);
			holder.tvResultName.setText("聊天室名：" + List.get(position).get("roomName"));
			
			String stringPassword=List.get(position).get("isPasswordProtected");
			if("true".equals(stringPassword))
				holder.isPasswordProtected.setVisibility(View.VISIBLE);
			else
				holder.isPasswordProtected.setVisibility(View.INVISIBLE);
			return convertView;

		}
	}
	  //返回键
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				Intent intent = new Intent(ChatroomSearchActivity.this, MainActivity.class);
				startActivity(intent);			
				finish();
			}
			return super.onKeyDown(keyCode, event);
		}
		@Override
	    protected void onDestroy() {
	        try{
	            chatroomPasswordDialog.dismiss();
	        }catch (Exception e) {
	        }
	        super.onDestroy();
	    }
}
