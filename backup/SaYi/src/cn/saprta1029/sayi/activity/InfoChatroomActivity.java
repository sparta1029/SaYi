package cn.saprta1029.sayi.activity;

import java.util.ArrayList;

import cn.saprta1029.sayi.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class InfoChatroomActivity extends Activity {
	protected TextView tvChatroomName, tvCount;
	protected ListView lvUserList;
	private ArrayList<String> userList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_chatroom);
		tvChatroomName = (TextView) this
				.findViewById(R.id.chatroom_info_roomname);
		tvCount = (TextView) this.findViewById(R.id.chatroom_info_count);
		lvUserList = (ListView) this.findViewById(R.id.chatroom_info_userlist);

		Intent intent = getIntent();
		userList = intent.getStringArrayListExtra("userlist");
		String roomName = intent.getStringExtra("roomname");
		String count=userList.size()+"";
		
		tvCount.setText("������������"+count);
		tvChatroomName.setText("���������ƣ�"+roomName);
		lvUserList.setEnabled(false);
		UserListAdapter adapter = new UserListAdapter(this); // �õ�һ��MyAdapter����
		lvUserList.setAdapter(adapter); // ΪListView��Adapter

	}

	class UserListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public UserListAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return userList.size(); // ��������ĳ���
		}

		public final class ViewHolder {
			public TextView userName;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.chatroom_info_userlist_item, null);
				holder = new ViewHolder();
				/* �õ������ؼ��Ķ��� */
				holder.userName = (TextView) convertView
						.findViewById(R.id.item_user);
				convertView.setTag(holder); // ��ViewHolder����
			} else {
				holder = (ViewHolder) convertView.getTag(); // ȡ��ViewHolder����
			}
			holder.userName.setText(userList.get(position).toString());
			return convertView;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(InfoChatroomActivity.this, ChatroomActivity.class);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
