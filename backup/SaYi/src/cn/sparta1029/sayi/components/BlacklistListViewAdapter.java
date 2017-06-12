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
	            
	            //who ����ȡֵ0��Ϊ user��/1��Ϊ other��,from
	            
	            convertView= LayoutInflater.from(context).inflate(
	                		R.layout.item_blacklist, null);
	            holder=new ViewHolder();
	                //to��0��1Ϊuser���û�������Ϣ���ݣ�2��3Ϊother���û���������
	            holder.tvResultName=(TextView)convertView.findViewById( R.id.item_blacklist_result_account);
	            
	            holder.tvResultName.setText("�û�����"+List.get(position));
	            return convertView;
	          
	        }
	}
