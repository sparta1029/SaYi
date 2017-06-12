package cn.saprta1029.sayi.activity;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import cn.saprta1029.sayi.R;
import cn.sparta1029.sayi.utils.SPUtil;
import cn.sparta1029.sayi.xmpp.ContactManager;
import cn.sparta1029.sayi.xmpp.UserSearchEntity;
import cn.sparta1029.sayi.xmpp.UsersSearch;
import cn.sparta1029.sayi.xmpp.XMPPConnectionUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AddContactActivity extends Activity {
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
		setContentView(R.layout.activity_add_contact);
		etSearchAccount=(EditText)this.findViewById(R.id.contact_search_account);
		btnSearch=(Button)this.findViewById(R.id.contact_search);
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
		lvSearchResult=(ListView)this.findViewById(R.id.contact_search_result);
		SPUtil SPUtil = new SPUtil(this);
		account = SPUtil.getString(SPUtil.keyCurrentUser, "");
		password = SPUtil.getString(SPUtil.keyCurrentPassword, "");
		serverAddress = SPUtil.getString(SPUtil.keyAddress, "");
		new Thread(new Runnable() {
			@Override
			public void run() {	
			connect=XMPPConnectionUtil.getInstanceNotPresence().getConnection(serverAddress);
			}
		}).start();  
		adapter=new SearchResultAdapter(AddContactActivity.this, searchResultList);
		lvSearchResult.setAdapter(adapter);
		lvSearchResult.setOnItemClickListener(new lvSearchResultItemClickListener() );
	}

	  
    protected void addToList(String searchAccount){
		searchResultList.add(searchAccount);
    }
    
    class lvSearchResultItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			final int Position=position;
		 	final AlertDialog contactDialog = new AlertDialog.Builder(
        			AddContactActivity.this).create();
			contactDialog.setTitle("用户选择");
			contactDialog
					.setIcon(R.drawable.ic_launcher);
			contactDialog
					.setMessage("选中用户："+searchResultList.get(position));
			contactDialog
					.setButton(
							DialogInterface.BUTTON_POSITIVE,
							"进入聊天",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialog,
										int which) {
									Intent intent = new Intent(AddContactActivity.this,
											ChatActivity.class);
									intent.putExtra("otherAccount",searchResultList.get(Position));
									startActivity(intent);
									finish();
								}
							});
			contactDialog
					.setButton(
							DialogInterface.BUTTON_NEGATIVE,
							"添加好友",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialog,
										int which) {
									ContactManager.addFriend(searchResultList.get(Position), connect);
								}
							});
			contactDialog.setButton(DialogInterface.BUTTON_NEUTRAL, 
					"取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(
								DialogInterface dialog,
								int which) {
							contactDialog
									.dismiss();
						}
					});
			contactDialog.show();
			
		}
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
                		R.layout.item_search_contact_result, null);
            holder=new ViewHolder();
                //to中0，1为user的用户名和消息内容，2，3为other的用户名和内容
            holder.tvResultName=(TextView)convertView.findViewById( R.id.search_contact_account);
            
            holder.tvResultName.setText("用户名："+List.get(position));
            return convertView;
          
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(AddContactActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
