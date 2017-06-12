package cn.saprta1029.sayi.activity;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import cn.saprta1029.sayi.R;
import cn.sparta1029.sayi.utils.SPUtil;
import cn.sparta1029.sayi.xmpp.UserSearchEntity;
import cn.sparta1029.sayi.xmpp.UsersSearch;
import cn.sparta1029.sayi.xmpp.XMPPConnectionUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class FriendSearchActivity extends Activity {
EditText etSearchAccount;
Button btnSearch;
ListView lvSearchResult;
XMPPConnection connect;
String account,password,serverAddress;
String searchAccount;
ArrayList<String> searchResultList=new ArrayList<String>();
SearchResultAdapter adapter;
Handler handler=new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_friend);
		Intent intent=getIntent();
		searchAccount = intent.getStringExtra("account");
		etSearchAccount=(EditText)this.findViewById(R.id.friend_search_account);
		btnSearch=(Button)this.findViewById(R.id.friend_search);
		btnSearch.setOnClickListener(new View.OnClickListener() {  	      
			    @Override  
			    public void onClick(View v) {  
			    	new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								searchAccount=etSearchAccount.getText().toString();
								List<UserSearchEntity> list = UsersSearch.searchUsers(connect,serverAddress, searchAccount);
								searchResultList.clear();
								for(int i=0;i<list.size();i++)
								{
									addToList(list.get(i).getUserName());
								}
								handler.post(new Runnable() {  
	         	                    @Override  
	         	                    public void run() {   
	             		                adapter.notifyDataSetChanged();
	         	                    }  
	         	                });  
							} catch (XMPPException e) {
								e.printStackTrace();
							}
						}
					}).start();
			    }  
			});  
		lvSearchResult=(ListView)this.findViewById(R.id.friend_search_result);
		 SPUtil SPUtil = new SPUtil(this);
		account = SPUtil.getString(SPUtil.keyCurrentUser, "");
		password = SPUtil.getString(SPUtil.keyCurrentPassword, "");
		serverAddress = SPUtil.getString(SPUtil.keyAddress, "");
		new Thread(new Runnable() {
			@Override
			public void run() {	
			connect=XMPPConnectionUtil.getInstanceNotPresence().getConnection(serverAddress);
			List<UserSearchEntity> list;
			try {
				list = UsersSearch.searchUsers(connect,serverAddress, searchAccount);
				for(int i=0;i<list.size();i++)
				{
					addToList(list.get(i).getUserName());
				}
				
				
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}).start();  
		adapter=new SearchResultAdapter(FriendSearchActivity.this, searchResultList);
		lvSearchResult.setAdapter(adapter);
	}

	  
    protected void addToList(String searchAccount){
		searchResultList.add(searchAccount);
    }
    
    private class SearchResultAdapter extends BaseAdapter{

        Context context=null;
        ArrayList<String> List=null;
        
        public SearchResultAdapter(Context context,
        		ArrayList<String> List) {
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

        class ViewHolder{
            public TextView tvResultName=null;
        
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=null;
            
            //who 两个取值0（为 user）/1（为 other）,from
            
            convertView= LayoutInflater.from(context).inflate(
                		R.layout.item_search_friend_result, null);
            holder=new ViewHolder();
                //to中0，1为user的用户名和消息内容，2，3为other的用户名和内容
            holder.tvResultName=(TextView)convertView.findViewById( R.id.search_friend_account);
            
            holder.tvResultName.setText("用户名："+List.get(position));
            return convertView;
          
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(FriendSearchActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
