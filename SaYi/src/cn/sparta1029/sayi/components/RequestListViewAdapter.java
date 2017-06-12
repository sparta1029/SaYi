package cn.sparta1029.sayi.components;


import java.util.ArrayList;
import java.util.HashMap;

import cn.saprta1029.sayi.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RequestListViewAdapter extends BaseAdapter {
	
	ArrayList<HashMap<String,String>>  contactRequest;
	Context context;
	public RequestListViewAdapter(Context context, ArrayList<HashMap<String,String>>  contactRequest){
		Log.i("testrequest", "contactRequest:"+contactRequest.toString());
		this.contactRequest=contactRequest;
	this.context=context;
	}
	
	@Override
	public int getCount() {
		return contactRequest.size();
	}

	@Override
	public Object getItem(int position) {
		return contactRequest.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	class ViewHolder {  
		TextView tvRequestAccount;
		TextView tvRequest;
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if (convertView == null)
		{	
		holder=new ViewHolder();
		LayoutInflater inflater = (LayoutInflater)context.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
		convertView=inflater.inflate(R.layout.item_contact_request, null); 
		holder.tvRequestAccount = (TextView) convertView
				.findViewById(R.id.item_contact_request_account);
		holder.tvRequest= (TextView) convertView.
				findViewById(R.id.item_contact_request);
		convertView.setTag(holder);
		} else {
		holder = (ViewHolder)convertView.getTag();
		}
		holder.tvRequestAccount.setText(contactRequest.get(position).get("contactname"));
		holder.tvRequest.setText(contactRequest.get(position).get("request"));
		return convertView;
	}
}

