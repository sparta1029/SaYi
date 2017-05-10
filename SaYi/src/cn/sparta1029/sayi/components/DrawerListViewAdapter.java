package cn.sparta1029.sayi.components;

import java.util.ArrayList;
import java.util.List;

import cn.saprta1029.sayi.R;
import cn.sparta1029.sayi.components.DeletableAutoCompleteTextViewAdapter.ViewHolder;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DrawerListViewAdapter extends BaseAdapter {

	private List<String> itemListView;
	//private ListView lvDrawer;
	private LayoutInflater inflater;
	private Context context;
	private final static int editProfit=0;
	private final static int editSetting=1;
	private final static int logout=2;
	private final static int exit=3;
	
	public DrawerListViewAdapter(List<String> itemListView,Context context) {
		this.itemListView=itemListView;
        this.context=context;
    }
	
	class ViewHolder {  
		ImageView ivItemIcon;
		TextView tvItemText;
    }
	
	
	
	@Override
	public int getCount() {
		return itemListView.size();
	}

	@Override
	public Object getItem(int position) {
		return itemListView.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if (convertView == null)
		{
		holder=new ViewHolder();
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
		convertView=inflater.inflate(R.layout.item_main_drawer_listview, null); 
		holder.tvItemText = (TextView) convertView.findViewById(R.id.item_text);
		holder.ivItemIcon= (ImageView) convertView.findViewById(R.id.item_icon);
		convertView.setTag(holder);
		} 
		else {
		holder = (ViewHolder)convertView.getTag();
		}
		holder.tvItemText.setText(itemListView.get(position));
	    switch(position)
	    {
//	    case editProfit:
//	    	holder.ivItemIcon.setImageDrawable(convertView.getResources().getDrawable(R.drawable.drawer_edit_profile));
//	    	break;
//	    case editSetting:
//	    	holder.ivItemIcon.setImageDrawable(convertView.getResources().getDrawable(R.drawable.drawer_edit_setting));
//	    	break;
//	    case logout:
//	    	holder.ivItemIcon.setImageDrawable(convertView.getResources().getDrawable(R.drawable.drawer_logout));
//	    	break;
//	    case exit:
//	    	holder.ivItemIcon.setImageDrawable(convertView.getResources().getDrawable(R.drawable.drawer_exit));
//	    	break;
	    
	    case editProfit:
	    	holder.ivItemIcon.setImageDrawable(convertView.getResources().getDrawable(R.drawable.drawer_logout));
	    	break;
	    case editSetting:
    	holder.ivItemIcon.setImageDrawable(convertView.getResources().getDrawable(R.drawable.drawer_exit));
    	break;
	    
	    
	    
	    }
	    holder.tvItemText.setGravity(Gravity.CENTER);
		return convertView;
	}

}
