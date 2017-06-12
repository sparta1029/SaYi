package cn.saprta1029.sayi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.saprta1029.sayi.R;
import cn.sparta1029.sayi.components.FriendListViewAdapter;
import cn.sparta1029.sayi.components.TextViewWithImage;
import cn.sparta1029.sayi.db.BlacklistDBManger;
import cn.sparta1029.sayi.db.BlacklistDBOpenHelper;
import cn.sparta1029.sayi.db.UserInfoDBManager;
import cn.sparta1029.sayi.db.UserInfoDBOpenHelper;
import cn.sparta1029.sayi.db.UserInfoEntity;
import cn.sparta1029.sayi.utils.SPUtil;
import cn.sparta1029.sayi.xmpp.UserSearchEntity;
import cn.sparta1029.sayi.xmpp.UsersSearch;
import cn.sparta1029.sayi.xmpp.XMPPConnectionUtil;

public class ProfileActivity extends Activity {
	TextView TextView1, TextView2;
	// ViewPager是google SDk中自带的一个附加包的一个类，可以用来实现屏幕间的切换。
	// android-support-v4.jar
	private ViewPager mPager;// 页卡内容
	private List<View> listViews; // Tab页面列表
	private ImageView cursor;// 动画图片
	private TextView tvTitlePasswordEdit, tvTitleBlacklist;// 页卡头标
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private String account, password,savedPassword;
	private List<String> itemListView = null;
	private ListView lvDrawer, lvMainFriend;
	private BlacklistListViewAdapter adapter;
	private FriendListViewAdapter adapterFriendList;
	private DrawerLayout drawerLayout;
	private boolean drawerOpen = false;
	private Map<String, Chat> chatManage = new HashMap<String, Chat>();// 聊天窗口管理map集合
	ArrayList<String> senderList;
	ArrayList<Integer> count;
	private Handler handler = new Handler();
	String serverAddress;
	XMPPConnection connect;
	EditText etPasswordOld, etPasswordNew, etPasswordNewAgain;
	Button btnPasswordConfirm;
	SPUtil SPUtil;
	SQLiteDatabase db;
	ListView lvBlacklistResult;
	EditText etBlacklistAccount;
	Button btnSearchConfirm ,btnAddBlacklist;
	ArrayList<String> blacklistAccountList,tempBlacklistAccountList;
	BlacklistDBOpenHelper DBHelper;
	
	//TODO
	 private List<String> selectid = new ArrayList<String>();
	 private boolean isMulChoice = false; //是否多选
	 private RelativeLayout Layout;
	 private Button btnBlacklistCancle,btnBlacklistDelete;
	 private TextView tvSelectedCount;

	@Override
	protected void onDestroy() {
		db.close();
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		// 新页面接收数据
		InitImageView();
		InitTextView();
		InitViewPager();

		SPUtil = new SPUtil(this);
		password = SPUtil.getString(SPUtil.keyCurrentPassword, "");
		serverAddress = SPUtil.getString(SPUtil.keyAddress, "");
		account= SPUtil.getString(SPUtil.keyCurrentUser, "");
		savedPassword = SPUtil.getString(SPUtil.keySavedPassword, "");
		new Thread(new Runnable() {
			@Override
			public void run() {
				connect = XMPPConnectionUtil.getInstanceNotPresence()
						.getConnection(serverAddress);
			}
		}).start();
	}

	
	
	
	
	
	
	
	
	
	class BlacklistListViewAdapter extends BaseAdapter{
	    private LayoutInflater inflater=null;
	    private HashMap<Integer, View> mView ;
	    public  HashMap<Integer, Integer> isVisible ;//用来记录是否显示checkBox
	    public  HashMap<Integer, Boolean> isChecked;
	    private TextView tvSelectedCount;
		        Context context=null;
		        public BlacklistListViewAdapter(Context context,TextView tvSelectedCount) {
		            super();
		            this.context = context;
		            this.tvSelectedCount=tvSelectedCount;
		            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		            mView = new HashMap<Integer, View>();
		            isVisible = new HashMap<Integer, Integer>();
		            isChecked      = new HashMap<Integer, Boolean>();
		            if(isMulChoice){
		                for(int i=0;i<blacklistAccountList.size();i++){
		                    isChecked.put(i, false);
		                    isVisible.put(i, View.VISIBLE);
		                }
		            }else{
		                for(int i=0;i<blacklistAccountList.size();i++)
		                {
		                    isChecked.put(i, false);
		                    isVisible.put(i, View.INVISIBLE);
		                }
		            }
		        }

		        @Override
		        public int getCount() {
		            return blacklistAccountList.size();
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
		            public TextView tvResultName=null;
		        
		        }
		        
		        @Override
		        public View getView(int position, View convertView, ViewGroup parent) {
		            ViewHolder holder=null;
		            
		            //who 两个取值0（为 user）/1（为 other）,from
		            final int Position=position;
		            convertView= LayoutInflater.from(context).inflate(
		                		R.layout.item_blacklist, null);
		            holder=new ViewHolder();
		                //to中0，1为user的用户名和消息内容，2，3为other的用户名和内容
		            holder.tvResultName=(TextView)convertView.findViewById( R.id.item_blacklist_result_account);	            
		            holder.tvResultName.setText("用户名："+blacklistAccountList.get(position));
		            final CheckBox chkSelected=(CheckBox)convertView.findViewById( R.id.black_check);	
		          if(isChecked.get(position)!=null)
		            chkSelected.setChecked(isChecked.get(position));
		          if(isVisible.get(position)!=null)
		            chkSelected.setVisibility(isVisible.get(position));
		            convertView.setOnLongClickListener(new Onlongclick());
		            convertView.setOnClickListener(new OnClickListener() {
	                    
	                    @Override
						public void onClick(View v) {
	                        // TODO Auto-generated method stub
	                    	//进入多选状态
	                        if(isMulChoice){
	                        	//多选响应
	                            if(chkSelected.isChecked()){
	                            	chkSelected.setChecked(false);
	                                selectid.remove(blacklistAccountList.get(Position));
	                            }else{
	                            	chkSelected.setChecked(true);
	                                selectid.add(blacklistAccountList.get(Position));
	                            }
	                            tvSelectedCount.setText("共选择了"+selectid.size()+"项");
	                        }else {
	                        	//单选响应
	                        	
	                        	final AlertDialog deleteDialog = new AlertDialog.Builder(
	                        			ProfileActivity.this).create();
								deleteDialog.setTitle("删除黑名单");
								deleteDialog
										.setIcon(R.drawable.ic_launcher);
								deleteDialog
										.setMessage("从黑名单中删除："+blacklistAccountList.get(Position));
								deleteDialog
										.setButton(
												DialogInterface.BUTTON_POSITIVE,
												"确定",
												new DialogInterface.OnClickListener() {
													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {
														DBHelper = new BlacklistDBOpenHelper(
																ProfileActivity.this, "sayi", null, 1);
														db = DBHelper.getWritableDatabase();
														BlacklistDBManger blacklistDBManager = new BlacklistDBManger();
														blacklistDBManager.blacklistDelete(db, blacklistAccountList.get(Position),account);		
													}
												});
								deleteDialog
										.setButton(
												DialogInterface.BUTTON_NEGATIVE,
												"取消",
												new DialogInterface.OnClickListener() {
													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {
														deleteDialog
																.dismiss();
													}
												});
								deleteDialog.show();
	                        	
	                            Toast.makeText(context, "点击了"+blacklistAccountList.get(Position), Toast.LENGTH_LONG).show();
	                        }
	                    }
	                });
		            
	                mView.put(Position, convertView);
		            return convertView;
		          
		        }
		        
		        class Onlongclick implements OnLongClickListener{

		            @Override
					public boolean onLongClick(View v) {
		                // TODO Auto-generated method stub
		            	
		                isMulChoice = true;
		                selectid.clear();
		                Layout.setVisibility(View.VISIBLE);
		                for(int i=0;i<blacklistAccountList.size();i++)
		                {
		                    adapter.isVisible.put(i, View.VISIBLE);
		                }
		                adapter = new BlacklistListViewAdapter(context,tvSelectedCount);
		                lvBlacklistResult.setAdapter(adapter);
		                return true;
		            }
		        }  
		}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 初始化头标
	 */
	private void InitTextView() {
		tvTitlePasswordEdit = (TextView) findViewById(R.id.profilepasswordedit_text);
		tvTitleBlacklist = (TextView) findViewById(R.id.profileblack_text);
		tvTitlePasswordEdit.setOnClickListener(new MyOnClickListener(0));
		tvTitleBlacklist.setOnClickListener(new MyOnClickListener(1));
	}

	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		listViews.add(mInflater.inflate(R.layout.profile_password, null));
		listViews.add(mInflater.inflate(R.layout.profile_blacklist, null));
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
			if (arg1 == 0) {
				etPasswordOld = (EditText) mListViews.get(arg1).findViewById(
						R.id.profile_password_old);
				etPasswordNew = (EditText) mListViews.get(arg1).findViewById(
						R.id.profile_password_new);
				etPasswordNewAgain = (EditText) mListViews.get(arg1)
						.findViewById(R.id.profile_password_new_again);
				btnPasswordConfirm = (Button) mListViews.get(arg1)
						.findViewById(R.id.profile_password_confirm);
				btnPasswordConfirm
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								String passwordOld = etPasswordOld.getText()
										.toString().trim();
								String passwordNew = etPasswordNew.getText()
										.toString().trim();
								String passwordNewAgain = etPasswordNewAgain
										.getText().toString().trim();
								if (password.equals(passwordOld)) {
									if (passwordNew.equals(passwordNewAgain)) {
										if (passwordNew.length() < 6)
											Toast.makeText(
													ProfileActivity.this,
													"密码长度至少六位",
													Toast.LENGTH_LONG).show();
										else {
											try {
												connect.getAccountManager()
														.changePassword(
																passwordNewAgain);
												Toast.makeText(ProfileActivity.this, "修改成功", Toast.LENGTH_LONG).show();     
												connect.disconnect();
												if(!savedPassword.equals(""))//如果已经保存了密码，数据库更新，
												{
													SPUtil.putString(SPUtil.keySavedPassword, passwordNewAgain);
													UserInfoDBOpenHelper DBHelper = new UserInfoDBOpenHelper(
														ProfileActivity.this, "sayi", null, 1);
												SQLiteDatabase db = DBHelper.getWritableDatabase();
												UserInfoDBManager userInfoManager = new UserInfoDBManager();
												UserInfoEntity userInfo=new UserInfoEntity(account, passwordNewAgain);
												userInfoManager.userInfoUpdate(db, userInfo);
												db.close();
												}
												Intent intent=new Intent(ProfileActivity.this,LoginActivity.class);
												startActivity(intent);
												finish();
											} catch (XMPPException e) {
												Log.i("profilepasswordtest",
														"XMPPException:"
																+ e.toString());
												e.printStackTrace();
											}
										}
									} else {
										Toast.makeText(ProfileActivity.this,
												"密码输入不一致", Toast.LENGTH_LONG)
												.show();
									}
								} else {
									Toast.makeText(ProfileActivity.this,
											"密码错误", Toast.LENGTH_LONG).show();
								}

							}
						});
			} else if (arg1 == 1) {
				
				DBHelper = new BlacklistDBOpenHelper(
						ProfileActivity.this, "sayi", null, 1);
				db = DBHelper.getWritableDatabase();
				BlacklistDBManger blacklistDBManager = new BlacklistDBManger();
				blacklistAccountList=blacklistDBManager.blacklistAllAccountQuery(db,account);
				
				lvBlacklistResult = (ListView)mListViews.get(arg1).findViewById(R.id.profile_blacklist_search_result);
				
				etBlacklistAccount = (EditText) mListViews.get(arg1).findViewById(
						R.id.profile_blacklist_search_account);
				btnSearchConfirm = (Button) mListViews.get(arg1).findViewById(
						R.id.profile_blacklist_search);
				btnSearchConfirm.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(etBlacklistAccount.getText().toString().trim().equals("")||etBlacklistAccount==null)
						{
						//不输入时显示所有黑名单
						db = DBHelper.getWritableDatabase();
						BlacklistDBManger blacklistDBManager = new BlacklistDBManger();
						blacklistAccountList.clear();
						blacklistAccountList.addAll( blacklistDBManager.blacklistAllAccountQuery(db,account));
						Log.i("mytest", "test1:"+blacklistAccountList.toString());
						
						handler.post(new Runnable() {  
     	                    @Override  
     	                    public void run() {   
         		                adapter.notifyDataSetChanged();
     	                    }  
     	                });  
						}
						else
						{
							ArrayList<String> allResult=new ArrayList<String>();
							db = DBHelper.getWritableDatabase();
							BlacklistDBManger blacklistDBManager = new BlacklistDBManger();
							blacklistAccountList.clear();
							blacklistAccountList .addAll(blacklistDBManager.blacklistAllAccountQuery(db,account));
							for(int i=0;i<blacklistAccountList.size();i++)
							if(blacklistAccountList.get(i).contains(etBlacklistAccount.getText().toString().trim()))
								{
								allResult.add(blacklistAccountList.get(i));
									}
							blacklistAccountList.clear();
							blacklistAccountList.addAll(allResult);

							Log.i("mytest", "test2:"+blacklistAccountList.toString());
							handler.post(new Runnable() {  
         	                    @Override  
         	                    public void run() {   
             		                adapter.notifyDataSetChanged();
         	                    }  
         	                });  
						}
							
					}
				});
				
				btnAddBlacklist = (Button) mListViews.get(arg1).findViewById(
						R.id.profile_blacklist_add);
				btnAddBlacklist.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						db = DBHelper.getWritableDatabase();
						BlacklistDBManger blacklistDBManager = new BlacklistDBManger();
						try {
							if(etBlacklistAccount==null||"".equals(etBlacklistAccount.getText().toString().trim()))
							{
								Toast.makeText(ProfileActivity.this, "请输入加入黑名单的用户名", Toast.LENGTH_LONG).show();
							}
							else
							{
								boolean isExist=false;
								List<UserSearchEntity>	list = UsersSearch.searchUsers(connect,serverAddress, etBlacklistAccount.getText().toString());	
							
								
								
								for(int i=0;i<list.size();i++)
								{
									Log.i("mytest", "test:"+list.get(i).getUserName().toString());
									if(list.get(i).getUserName().equals(etBlacklistAccount.getText().toString()))
									{
										isExist=true;
										i=list.size();
									}
								}
								if(!isExist)
								{
									Toast.makeText(ProfileActivity.this, "用户不存在", Toast.LENGTH_LONG).show();										
								}
								else{
									blacklistDBManager.blacklistInsert(db, etBlacklistAccount.getText().toString(),account);							
									blacklistAccountList.clear();
									blacklistAccountList.addAll(blacklistDBManager.blacklistAllAccountQuery(db,account));
									Log.i("mytest", "test3:"+blacklistAccountList.toString());
									handler.post(new Runnable() {  
		         	                    @Override  
		         	                    public void run() {   
		             		                adapter.notifyDataSetChanged();
		         	                    }  
		         	                });  
										}	
							}
						} catch (XMPPException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
					}});

				Layout = (RelativeLayout)mListViews.get(arg1).findViewById(R.id.profile_longclick_layout);
				btnBlacklistCancle   = (Button)mListViews.get(arg1).findViewById(R.id.blacklist_cancle);
				btnBlacklistDelete   = (Button)mListViews.get(arg1).findViewById(R.id.blacklist_delete);
				tvSelectedCount=(TextView)mListViews.get(arg1).findViewById(R.id.blacklist_textcount);
				btnBlacklistCancle.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						  isMulChoice = false;
						 selectid.clear();
						adapter = new BlacklistListViewAdapter(ProfileActivity.this,tvSelectedCount);
						lvBlacklistResult.setAdapter(adapter);
						Layout.setVisibility(View.INVISIBLE);

					}
					
				});
				
				
				btnBlacklistDelete.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						 isMulChoice =false;
						            for(int i=0;i<selectid.size();i++){
					              for(int j=0;j<blacklistAccountList.size();j++){
						                  if(selectid.get(i).equals(blacklistAccountList.get(j))){			                	
						                	BlacklistDBManger blacklistDBManager = new BlacklistDBManger();
						                	blacklistDBManager.blacklistDelete(db, blacklistAccountList.get(j),account);  
						                	blacklistAccountList.remove(j);						                	  
						                  }
						               }
						              }
						            selectid.clear();
						             adapter = new BlacklistListViewAdapter(ProfileActivity.this,tvSelectedCount);
						             lvBlacklistResult.setAdapter(adapter);
						             Layout.setVisibility(View.INVISIBLE);


					}
				});

				adapter=new BlacklistListViewAdapter(ProfileActivity.this,tvSelectedCount);
				lvBlacklistResult.setAdapter(adapter);
				
				
			
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
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
				TextViewWithImage tvwitvTitlePasswordEdit = (TextViewWithImage) ProfileActivity.this
						.findViewById(R.id.profilepasswordedit_text);
				tvwitvTitlePasswordEdit.setTextColor(Color.rgb(158, 203, 226));
				TextViewWithImage tvTitleBlacklist = (TextViewWithImage) ProfileActivity.this
						.findViewById(R.id.profileblack_text);
				tvTitleBlacklist.setTextColor(Color.rgb(102, 102, 102));
				animation = new TranslateAnimation(one, 0, 0, 0);
				break;
			case 1:
				tvwitvTitlePasswordEdit = (TextViewWithImage) ProfileActivity.this
						.findViewById(R.id.profilepasswordedit_text);
				tvwitvTitlePasswordEdit.setTextColor(Color.rgb(102, 102, 102));
				tvTitleBlacklist = (TextViewWithImage) ProfileActivity.this
						.findViewById(R.id.profileblack_text);
				tvTitleBlacklist.setTextColor(Color.rgb(158, 203, 226));
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

	
}
