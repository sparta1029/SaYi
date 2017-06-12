package cn.sparta1029.sayi.components;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.saprta1029.sayi.R;

public class BlacklistListViewAdapter extends BaseAdapter{

	        Context context=null;
	        ArrayList<String> List=null;
	        
	        public BlacklistListViewAdapter(Context context,
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
	                		R.layout.item_blacklist, null);
	            holder=new ViewHolder();
	                //to中0，1为user的用户名和消息内容，2，3为other的用户名和内容
	            holder.tvResultName=(TextView)convertView.findViewById( R.id.item_blacklist_result_account);
	            
	            holder.tvResultName.setText("用户名："+List.get(position));
	            return convertView;
	          
	        }
	}
