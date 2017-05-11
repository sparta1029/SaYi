package cn.saprta1029.sayi.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.MultiUserChat;
import cn.saprta1029.sayi.R;
import cn.sparta1029.sayi.components.DrawerListViewAdapter;
import cn.sparta1029.sayi.components.TextViewWithImage;
import cn.sparta1029.sayi.utils.SPUtil;
import cn.sparta1029.sayi.xmpp.UsersSearch;
import cn.sparta1029.sayi.xmpp.XMPPConnectionUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	TextView TextView1, TextView2;
	// ViewPager是google SDk中自带的一个附加包的一个类，可以用来实现屏幕间的切换。
	// android-support-v4.jar
	private ViewPager mPager;// 页卡内容
	private List<View> listViews; // Tab页面列表
	private ImageView cursor;// 动画图片
	private TextView tvTitleFriend, tvTitleChatroom;// 页卡头标
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private String account, password;
	private List<String> itemListView = null;
	private ListView lvDrawer;
	private DrawerListViewAdapter adapter;
	private DrawerLayout drawerLayout;
	private boolean drawerOpen=false;
	private Map<String, Chat> chatManage = new HashMap<String, Chat>();// 聊天窗口管理map集合  
	String serverAddress;
	XMPPConnection connect ;
	
	

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 新页面接收数据
		InitImageView();
		InitTextView();
		InitViewPager();

		SPUtil SPUtil = new SPUtil(this);
		account = SPUtil.getString(SPUtil.keyCurrentUser, "");
		password = SPUtil.getString(SPUtil.keyCurrentPassword, "");

		drawerLayout=(DrawerLayout) MainActivity.this
				.findViewById(R.id.main_drawerLayout);
		android.app.ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.main_actionbar_color));
		actionBar.setTitle(account);
		actionBar.setIcon(R.drawable.default_avatar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		TextViewWithImage tvwitvTitleFriend = (TextViewWithImage) MainActivity.this
				.findViewById(R.id.main_text_friend);
		tvwitvTitleFriend.setTextColor(Color.rgb(158, 203, 226));

		TextViewWithImage tvwiTitleChatroom = (TextViewWithImage) MainActivity.this
				.findViewById(R.id.main_text_chatroom);
		tvwiTitleChatroom.setTextColor(Color.rgb(102, 102, 102));

		TextView tvDrawerAccount = (TextView) MainActivity.this
				.findViewById(R.id.main_drawer_account);
		tvDrawerAccount.setText(account);
		
		initDrawerListViewData();
		lvDrawer=(ListView)this.findViewById(R.id.main_drawer_listview);
		adapter = new DrawerListViewAdapter(itemListView,MainActivity.this);
		lvDrawer.setAdapter(adapter);
		lvDrawer.setOnItemClickListener(new lvDrawerItemClickListener());
		
		serverAddress = SPUtil.getString(SPUtil.keyAddress, "");
		 new Thread(new Runnable() {
				@Override
				public void run() {	
				connect=XMPPConnectionUtil.ConnectServer(serverAddress);
				loginServer();
				}
			}).start();
	}
	
	class lvDrawerItemClickListener implements OnItemClickListener
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			/*位置 0~3:
			0  修改信息
			1  应用设置
			2  注销登录
			3  退出应用*/
			switch(position)
			{
			case 0:
				//TODO 修改信息
				Intent intent = new Intent(MainActivity.this, LoginActivity.class);
				SPUtil SPUtil = new SPUtil(MainActivity.this);
				SPUtil.putString(SPUtil.keyAutoLogin, SPUtil.booleanAutoLoginFalse);
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						if(connect.isConnected())
						connect.disconnect();
					
					}
				}).start();
				finish();
				startActivity(intent);
				break;
			case 1:	
				finish();
			    System.exit(0);
				break;
			case 2:
//				Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//				SPUtil SPUtil = new SPUtil(MainActivity.this);
//				SPUtil.putString(SPUtil.keyAutoLogin, SPUtil.booleanAutoLoginFalse);
//				String serverAddress = SPUtil.getString(SPUtil.keyAddress, "");
//				//TODO
//				
//				new Thread(new Runnable() {
//					@Override
//					public void run() {	
//						Presence presence = new Presence(Presence.Type.available);
//						XMPPConnection conn = ConnectionData.getConnection(MainActivity.this);
//						conn.disconnect(presence);
//					
//					}
//				}).start();
//				
//				startActivity(intent);
				break;
			case 3:
				finish();
				System.exit(0);
				break;
				
			}
		}
		
	}
	
	
	
	
	
	public void initDrawerListViewData() {
		itemListView = new ArrayList<String>();
	//	itemListView.add("修改信息");
	//	itemListView.add("应用设置");
		itemListView.add("注销登录");
		itemListView.add("退出应用");
	}
	

	
	/**
	 * 初始化头标
	 */
	private void InitTextView() {
		tvTitleFriend = (TextView) findViewById(R.id.main_text_friend);
		tvTitleChatroom = (TextView) findViewById(R.id.main_text_chatroom);

		tvTitleFriend.setOnClickListener(new MyOnClickListener(0));
		tvTitleChatroom.setOnClickListener(new MyOnClickListener(1));
	}

	
	
	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		listViews.add(mInflater.inflate(R.layout.main_friend, null));
		listViews.add(mInflater.inflate(R.layout.main_chatroom, null));
		
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.addOnPageChangeListener(new MyOnPageChangeListener());

	}

	/**
	 * 初始化动画
	 */
	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(),
				R.drawable.horizon_scrollbar).getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 2 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置
		
	}

	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		
		
		
		
		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			// 响应viewpager中子view的控件
			if(arg1==0)
			{
				 Button btnConfirm= (Button) mListViews.get(arg1).findViewById(R.id.friend_chat);
				 final EditText etFriendAccount= (EditText) mListViews.get(arg1).findViewById(R.id.friend_account);
				 btnConfirm.setOnClickListener(new View.OnClickListener() {
				     @Override
				     public void onClick(View view) {
				    	 //TODO
//				    	 if(etFriendAccount.getText()==null||"".equals(etFriendAccount.getText().toString()))
//				    	 Toast.makeText(MainActivity.this, "请输入聊天对象用户名", Toast.LENGTH_SHORT).show();
//				    	 else{
//				    	 Intent intent = new Intent(MainActivity.this, ChatActivity.class);
//				    	 intent.putExtra("otherAccount", etFriendAccount.getText().toString());
//							startActivity(intent);
//				    	 }
				    	 //TODO 查找用户线程
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									UsersSearch.searchUsers(serverAddress,
											account, password, "hza");

								} catch (XMPPException e) {
									e.printStackTrace();
								}
							}
						}).start();
				    	 
				    	 
				    	 
				     }
				 });
			}
			else if(arg1==1)
			{
				 final EditText etChatroomName=(EditText) mListViews.get(arg1).findViewById(R.id.chatroom_name);
				 final EditText etChatroomPassword=(EditText) mListViews.get(arg1).findViewById(R.id.chatroom_password);
				 final EditText etChatroomPasswordAgain=(EditText) mListViews.get(arg1).findViewById(R.id.chatroom_password_again);
				
				
				 Button btnChatroomCreate= (Button) mListViews.get(arg1).findViewById(R.id.chatroom_create);
				 btnChatroomCreate.setOnClickListener(new View.OnClickListener() {
				     @Override
				     public void onClick(View view) {
				    	    String chatroomName;
							String chatroomPassword;
							String chatroomPasswordAgain;
				    		chatroomName=etChatroomName.getText().toString().trim();
							chatroomPassword=etChatroomPassword.getText().toString().trim();
							chatroomPasswordAgain=etChatroomPasswordAgain.getText().toString().trim();
							Log.i("createtest", "chatroomName:"+chatroomName+"   chatroomPassword:"+chatroomPassword+"   chatroomPasswordAgain:"+chatroomPasswordAgain);
							
					    	 if(chatroomName==null||"".equals(chatroomName))
					    		 Toast.makeText(MainActivity.this, "请输入聊天室名", Toast.LENGTH_SHORT).show();			    		 
					    	 else
					    		 if(chatroomPasswordAgain.equals(chatroomPassword)||chatroomPasswordAgain==chatroomPassword)//密码相同
					    		 {
					    			 if("".equals(chatroomPassword)||chatroomPassword==null)
					    				 //显示窗口 提醒将要创建一个没有密码的聊天室
					    				 
					    			 {
					    				  final String roomName = chatroomName;
					    				  final AlertDialog noPasswordDialog=new AlertDialog.Builder(MainActivity.this).create();  
					    				  noPasswordDialog.setTitle("创建聊天室");  
					    				  noPasswordDialog.setIcon(R.drawable.ic_launcher);  
					    				  noPasswordDialog.setMessage("创建一个没有密码保护的聊天室");  
					    				  noPasswordDialog.setButton(DialogInterface.BUTTON_POSITIVE,"确定", new DialogInterface.OnClickListener() {  
					    				                     @Override  
					    				                     public void onClick(DialogInterface dialog, int which) {  
					    				                    	 if(createChatRoom(roomName, null, null))
					    					    					 Toast.makeText(MainActivity.this, "创建聊天室成功", Toast.LENGTH_SHORT).show();			    		 
					    					    				 else
					    					    					 Toast.makeText(MainActivity.this, "创建聊天室失败,聊天室或已存在", Toast.LENGTH_SHORT).show();	   
					    				                           
					    				                     }  
					    				                 });  
					    				  noPasswordDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"取消", new DialogInterface.OnClickListener() {  
					    				                       
					    				                     @Override  
					    				                     public void onClick(DialogInterface dialog, int which) {  
					    				                    	 noPasswordDialog.dismiss();
					    				                     }  
					    				                 });  
					    				  noPasswordDialog.show();
					    			 }
					    			 else
					    				 //直接创建聊天室
					    			 {
					    				 if(createChatRoom(chatroomName, null, chatroomPassword))
					    					 Toast.makeText(MainActivity.this, "创建聊天室成功", Toast.LENGTH_SHORT).show();			    		 
					    				 else
					    					 Toast.makeText(MainActivity.this, "创建聊天室失败", Toast.LENGTH_SHORT).show();			    		 
					    		 }
					    		 }
					    		 else
					    			 Toast.makeText(MainActivity.this, "密码输入不一致", Toast.LENGTH_SHORT).show();			    		 
						    	
			}
				 });
				 
				 
				 
				 Button btnChatroomEnter= (Button) mListViews.get(arg1).findViewById(R.id.chatroom_enter);
				 btnChatroomEnter.setOnClickListener(new View.OnClickListener() {
				     @Override
				     public void onClick(View view) {
				    	 Intent intent = new Intent(MainActivity.this, ChatroomActivity.class);
				    	 intent.putExtra("chatroom",etChatroomName.getText().toString().trim());
				    	 if(etChatroomPassword==null||"".equals(etChatroomPassword))
				    	 {
							startActivity(intent);
				    	 }
				    	 else
				    	 { 
				    		 intent.putExtra("password",etChatroomName.getText().toString().trim());
				    		 startActivity(intent);
				    		 }
						    	
			}
				 });
				 
				 
				 
			}
			return mListViews.get(arg1);
		}
				
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量

		
		
		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
		
			 
			switch (arg0) {
			case 0:
				
					TextViewWithImage tvwitvTitleFriend = (TextViewWithImage) MainActivity.this
							.findViewById(R.id.main_text_friend);
					tvwitvTitleFriend.setTextColor(Color.rgb(158, 203, 226));
					TextViewWithImage tvwiTitleChatroom = (TextViewWithImage) MainActivity.this
							.findViewById(R.id.main_text_chatroom);
					tvwiTitleChatroom.setTextColor(Color.rgb(102, 102, 102));
					animation = new TranslateAnimation(one, 0, 0, 0);
						
				break;
			case 1:
					tvwitvTitleFriend = (TextViewWithImage) MainActivity.this
							.findViewById(R.id.main_text_friend);
					tvwitvTitleFriend.setTextColor(Color.rgb(102, 102, 102));

					tvwiTitleChatroom = (TextViewWithImage) MainActivity.this
							.findViewById(R.id.main_text_chatroom);
					tvwiTitleChatroom.setTextColor(Color.rgb(158, 203, 226));
					animation = new TranslateAnimation(offset, one, 0, 0);
					
				break;
			}

			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			cursor.startAnimation(animation);

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	
	
	
	
	
	
	
	
	private void setIconEnable(Menu menu, boolean enable) {
		try {
			Class<?> clazz = Class
					.forName("com.android.internal.view.menu.MenuBuilder");
			Method m = clazz.getDeclaredMethod("setOptionalIconsVisible",
					boolean.class);
			m.setAccessible(true);
			m.invoke(menu, enable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		setIconEnable(menu, true);
		//TODO 右上角菜单
//		menu.add(0, 1, 0, "私聊").setIcon(R.drawable.drawer_logout);
//		menu.add(0, 5, 0, "群聊").setIcon(R.drawable.drawer_exit);
		return super.onCreateOptionsMenu(menu);
	}
	public static boolean addUser(Roster roster, String userName, String name) {  
        try {  
            roster.createEntry(userName+"@10.101.146.187", name, null);  
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
    } 
	
	private Boolean loginServer() {
				try {
					connect.login(account,password);
					return true;
				} catch (Exception e) {
					Log.e("maintest", "error     " + e.toString());
					e.printStackTrace();
				} 
				return false;
	}
	
	
	
	
	public boolean createChatRoom(String roomName,String description,String roomPassword){  
		        loginServer();
		        boolean result = false;  
		        try{  
		            MultiUserChat muc = new MultiUserChat(connect, roomName+"@conference."+connect.getServiceName());  
		            muc.create(account);   //用户在用户群中的昵称
		            Form form = muc.getConfigurationForm();   
		            Form submitForm = form.createAnswerForm();   
		            for (Iterator<?> fields = form.getFields(); fields.hasNext();) {   
		               FormField field = (FormField) fields.next();   
		               if (!FormField.TYPE_HIDDEN.equals(field.getType()) && field.getVariable() != null) {   
		                   submitForm.setDefaultAnswer(field.getVariable());   
		               }   
		           }   
		           List<String> list =  new ArrayList<String>();  
		           list.add("20");  
		           submitForm.setAnswer("muc#roomconfig_maxusers", list); //最大用户
		           submitForm.setAnswer("muc#roomconfig_persistentroom", true);  //房间永久
		           submitForm.setAnswer("muc#roomconfig_membersonly", false);  //仅对成员开放
		           submitForm.setAnswer("muc#roomconfig_allowinvites", true);  //允许邀请 
		           submitForm.setAnswer("muc#roomconfig_enablelogging", true); //登陆房间对话
		           if(roomPassword!=null){
		           submitForm.setAnswer("muc#roomconfig_roomsecret",roomPassword);//设置密码
		           submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);//进入房间，密码验证
		           }
		           submitForm.setAnswer("x-muc#roomconfig_reservednick", true);   //仅允许注册的宁城登陆
		           submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);   //允许修改昵称
		           submitForm.setAnswer("x-muc#roomconfig_registration", false);   //允许用户注册房间
		           muc.sendConfigurationForm(submitForm);
                   if(description!=null)
		           muc.changeSubject(description);  
		           result = true;  
		        } catch (Exception e) {  
		              e.printStackTrace();  
		        }  
		        return result;  
		    }  
	
	//右上角响应
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			
			// TODO 获取全部好友			
			break;
		case 5:
			
			break;
			
		case android.R.id.home:
			if(drawerOpen==false)
			{
			drawerLayout.openDrawer(Gravity.START);
			drawerOpen=true;
			}else
			{
				drawerLayout.closeDrawers();;
				drawerOpen=false;
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// 按返回键退出程序
	private static boolean isExit = false;

	static Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}
	};

	public boolean  onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return true;
		}
		else if(keyCode == KeyEvent.KEYCODE_MENU) {
			if(drawerOpen==false)
			{
			drawerLayout.openDrawer(Gravity.START);
			drawerOpen=true;
			}else
			{
				drawerLayout.closeDrawers();;
				drawerOpen=false;
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(getApplicationContext(), "再按一次退出程序",
					Toast.LENGTH_SHORT).show();
			// 利用handler延迟发送更改状态信息
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			finish();
			System.exit(0);
		}
	}

}
