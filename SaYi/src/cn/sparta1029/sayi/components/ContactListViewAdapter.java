package cn.sparta1029.sayi.components;

import java.util.ArrayList;
import java.util.HashMap;

import cn.saprta1029.sayi.R;
import cn.sparta1029.sayi.components.RequestListViewAdapter.ViewHolder;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ContactListViewAdapter extends BaseAdapter{

	ArrayList<String>  contact;
	Context context;
	public ContactListViewAdapter(Context context, ArrayList<String> contact){
		this.contact=contact;
	this.context=context;
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return contact.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return contact.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	class ViewHolder {  
		TextView tvContactAccount;
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if (convertView == null)
		{	
		holder=new ViewHolder();
		LayoutInflater inflater = (LayoutInflater)context.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
		convertView=inflater.inflate(R.layout.item_contact,parent,false); 
		
		holder.tvContactAccount = (TextView) convertView
				.findViewById(R.id.item_contact_account);
		convertView.setTag(holder);
		} else {
		holder = (ViewHolder)convertView.getTag();
		}
		holder.tvContactAccount.setText(""+contact.get(position));
		return convertView;
	}

}
