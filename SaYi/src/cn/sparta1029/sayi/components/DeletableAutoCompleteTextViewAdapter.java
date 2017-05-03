package cn.sparta1029.sayi.components;

import java.util.ArrayList;  
import java.util.List;  
  
  





import cn.saprta1029.sayi.R;
import cn.saprta1029.sayi.activity.LoginActivity;
import cn.sparta1029.sayi.db.UserInfoDBManager;
import cn.sparta1029.sayi.db.UserInfoDBOpenHelper;
import android.content.Context;  
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.view.View.OnClickListener;  
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;  
import android.widget.EditText;
import android.widget.Filter;  
import android.widget.Filterable;  
import android.widget.ImageView;  
import android.widget.TextView;
public class DeletableAutoCompleteTextViewAdapter extends BaseAdapter implements Filterable {
	private Context context;  
	    private ArrayFilter mFilter;  
	    private ArrayList<String> mOriginalValues;//所有的Item  
	    private List<String> mObjects;//过滤后的item  
	    private final Object mLock = new Object();  
	    private int maxMatch=-2;//最多显示多少个选项,负数表示全部  
	    public DeletableAutoCompleteTextViewAdapter(Context context,ArrayList<String> mOriginalValues,int maxMatch){  
	        this.context=context;  
	        this.mOriginalValues=mOriginalValues;  
	        this.maxMatch=maxMatch;  
	    }  
	      
	    @Override  
	    public Filter getFilter() {  
	        if (mFilter == null) {    
	            mFilter = new ArrayFilter();    
	        }    
	        return mFilter;  
	    }  
	      
	    private class ArrayFilter extends Filter {  
	  
	        @Override  
	        protected FilterResults performFiltering(CharSequence prefix) {  
	            FilterResults results = new FilterResults();    
	            if (prefix == null || prefix.length() == 0) {    
	                synchronized (mLock) {  
	                    ArrayList<String> list = new ArrayList<String>(mOriginalValues);    
	                    results.values = list;    
	                    results.count = list.size();   
	                    return results;  
	                }    
	            } else {  
	                String prefixString = prefix.toString().toLowerCase();    
	    
	                final int count = mOriginalValues.size();    
	    
	                final ArrayList<String> newValues = new ArrayList<String>(count);    
	    
	                for (int i = 0; i < count; i++) {  
	                    final String value = mOriginalValues.get(i);    
	                    final String valueText = value.toLowerCase();     
	                    if (valueText.startsWith(prefixString)) {  //源码 ,匹配开头  
	                        newValues.add(value);    
	                    }   
	                    if(maxMatch>0){//有数量限制    
	                        if(newValues.size()>maxMatch-1){//不要太多    
	                            break;    
	                        }    
	                    }    
	                }    
	    
	                results.values = newValues;    
	                results.count = newValues.size();    
	            }    
	            return results;  
	        }  
	  
	        @Override  
	        protected void publishResults(CharSequence constraint,  
	                FilterResults results) {  
	            mObjects = (List<String>) results.values;    
	            if (results.count > 0) {    
	                notifyDataSetChanged();    
	            } else {    
	                notifyDataSetInvalidated();    
	            }  
	        }  
	          
	    }  
	  
	    @Override  
	    public int getCount() { 
	        return mObjects.size();  
	    }  
	  
	    @Override  
	    public Object getItem(int position) {  
	        //此方法有误，尽量不要使用  
	        return mObjects.get(position);  
	    }  
	  
	    @Override  
	    public long getItemId(int position) {  
	        return position;  
	    }  
	  
	    @Override  
	    public View getView(final int position, View convertView, ViewGroup parent) {  
	        ViewHolder holder = null;  
	        if(convertView==null){  
	            holder=new ViewHolder();  
	            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
	            convertView=inflater.inflate(R.layout.item_deletableautocomplete, null);  
	            holder.tv=(TextView)convertView.findViewById(R.id.simple_item_0);    
	            holder.iv=(ImageView)convertView.findViewById(R.id.simple_item_1);  
	            convertView.setTag(holder);  
	        }else{  
	            holder = (ViewHolder) convertView.getTag();  
	        }
			
	        holder.tv.setText(mObjects.get(position));
	        holder.iv.setOnClickListener(new OnClickListener() {  
	              
	            @Override  
	            public void onClick(View v) {  
	                String obj=mObjects.remove(position);  
	                mOriginalValues.remove(obj); 
	                UserInfoDBOpenHelper DBHelper=new UserInfoDBOpenHelper(context, "sayi", null, 1);
	        		SQLiteDatabase db = DBHelper.getWritableDatabase();
	        		UserInfoDBManager userInfoManager=new UserInfoDBManager();
	        		userInfoManager.userInfoDelete(db, obj);
	                notifyDataSetChanged();  
	            }  
	        });  
	        return convertView;  
	    }  
	  
	    class ViewHolder {  
	        TextView tv;  
	        ImageView iv;  
	    }  
	      
	    public ArrayList<String> getAllItems(){  
	        return mOriginalValues;  
	    }  
}
