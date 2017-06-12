package cn.sparta1029.sayi.components;

import java.util.ArrayList;

import com.readystatesoftware.viewbadger.BadgeView;

import cn.saprta1029.sayi.R;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendListViewAdapter extends BaseAdapter {
	
	private ArrayList<String> account;
	private ArrayList<Integer> count;
	private ArrayList<String> newMessage,messageTime;
	private Context context;
	private LayoutInflater inflator;
	
	
	public FriendListViewAdapter(Context context,ArrayList<String> account,ArrayList<Integer> count,ArrayList<String> newMessage,ArrayList<String> messageTime){
		this.account=account;
		this.count=count;
		this.newMessage=newMessage;
		this.context=context;
		this.messageTime=messageTime;
	}
	
	@Override
	public int getCount() {
		return account.size();
	}

	@Override
	public Object getItem(int position) {
		return account.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	class ViewHolder {  
		TextView tvAccount;
		ImageView ivCount;
		TextView tvMessage;
		TextView tvMessageTime;
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if (convertView == null)
		{	
		holder=new ViewHolder();
		LayoutInflater inflater = (LayoutInflater)context.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
		convertView=inflater.inflate(R.layout.item_main_friend, null); 
		holder.tvAccount = (TextView) convertView
				.findViewById(R.id.main_friend_account);
		holder.ivCount= (ImageView) convertView.
				findViewById(R.id.main_friend_unreaded_message_count);
		holder.tvMessage= (TextView) convertView
				.findViewById(R.id.main_friend_message_text);
		holder.tvMessageTime= (TextView) convertView
				.findViewById(R.id.main_friend_message_time);
		convertView.setTag(holder);
		} else {
		holder = (ViewHolder)convertView.getTag();
		}
		BadgeView badgeView = new BadgeView(context,holder.ivCount);  
		badgeView.setText(""+count.get(position));
		badgeView.setTextColor(Color.WHITE);
		badgeView.setBadgeMargin(BadgeView.POSITION_TOP_RIGHT);
		badgeView.show();
		holder.tvMessage.setText(this.newMessage.get(position));
		holder.tvMessageTime.setText(this.messageTime.get(position));
	
		holder.tvAccount.setText(this.account.get(position));
		return convertView;
	}
}
