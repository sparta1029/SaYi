package cn.sparta1029.sayi.components;
import cn.saprta1029.sayi.R;
import cn.sparta1029.sayi.db.UserInfoDBManager;
import cn.sparta1029.sayi.db.UserInfoDBOpenHelper;
import cn.sparta1029.sayi.utils.SPUtil;
import android.content.Context;  
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;  
import android.view.View;  
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;  
import android.widget.ImageView;  
import android.widget.RelativeLayout;  
import android.widget.ImageView.ScaleType;  


public class DeletableAutoCompleteTextView extends RelativeLayout{
	  private Context context;  
	    private AutoCompleteTextView actv;
	    public DeletableAutoCompleteTextView(Context context) {  
	        super(context);  
	        // TODO Auto-generated constructor stub  
	        this.context=context;  
	    }  
	    public DeletableAutoCompleteTextView(Context context, AttributeSet attrs) {  
	        super(context, attrs);  
	        // TODO Auto-generated constructor stub  
	        this.context=context;  
	    }  
	    @Override  
	    protected void onFinishInflate() {  
	        super.onFinishInflate();  
	        initViews();  
	    }  
	    private void initViews() {  
	        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT);  
	        actv=new AutoCompleteTextView(context);  
	        actv.setLayoutParams(params);  
	        actv.setPadding(0, 0,0, 0);
	        actv.setHint("用户名");  
	        actv.setSingleLine(true);
	        
			SPUtil SPUtil=new SPUtil(context);
			actv.setText(SPUtil.getString(SPUtil.keyCurrentUser,""));

	        Drawable leftDrawable = getResources().getDrawable(R.drawable.login_user);  
	                    leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable.getMinimumHeight());  
	                    actv.setCompoundDrawables(leftDrawable,null, null, null);  
	        
	        RelativeLayout.LayoutParams deleteIcon=new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT);  
	        deleteIcon.addRule(RelativeLayout.ALIGN_PARENT_RIGHT); 
	        deleteIcon.rightMargin=10;
	        
	        //右侧删除图片，可点击删除
	        final ImageView ivDelete=new ImageView(context);  
	        if("".equals(actv.getText().toString()))
	        	ivDelete.setVisibility(View.INVISIBLE);
	        ivDelete.setLayoutParams(deleteIcon);  
	        ivDelete.setScaleType(ScaleType.FIT_CENTER);  
	        ivDelete.setImageResource(R.drawable.login_delete);  
	        ivDelete.setClickable(true);  
	        //监听删除图片的点击事件
	        ivDelete.setOnClickListener(new View.OnClickListener() {  
	            @Override  
	            public void onClick(View v) {  
	                // TODO Auto-generated method stub  
	            	actv.setText("");  
	            	Intent intent=new Intent(); 
					intent.setAction("cn.sparta1029.sayi.pwdbroadcast");
	    	        intent.putExtra("account", "");
	    	        context.sendBroadcast(intent);
	            }  
	        });  

	       
	        //点击item，获取相应的密码，发送广播通知更新ui
	        actv.setOnItemClickListener(new OnItemClickListener() {
	        	@Override
	        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        	    Object obj = parent.getItemAtPosition(position);
	        	    //这个就是取点击的条目绑定的值,
	        	    //实际上返回的就是适配器的 Adapter.getItem(position);

	        	    UserInfoDBOpenHelper DBHelper=new UserInfoDBOpenHelper(context, "sayi", null, 1);
	    			SQLiteDatabase db = DBHelper.getWritableDatabase();
	    			UserInfoDBManager userInfoManager=new UserInfoDBManager();  
	    			String password =userInfoManager.userInfoAccountQuery(db,obj.toString());
					Intent intent=new Intent(); 
					intent.setAction("cn.sparta1029.sayi.pwdbroadcast");
	    	        intent.putExtra("pwd", password);
	    	        intent.putExtra("account", actv.getText().toString().trim());
	    	        context.sendBroadcast(intent);
	        	}
	        });
	        
	        //监听actv的输入，若用户框中为空，隐藏删除图片，显示默认提示信息“用户名”，否则显示删除图标。
	        actv.addTextChangedListener(new TextWatcher() {
	            @Override
	            public void onTextChanged(CharSequence s, int start, int before, int count) {
	            	if("".equals(actv.getText().toString()))
	            	{
	    	        	ivDelete.setVisibility(View.INVISIBLE);
	            	}
	            	else
	            	{
	            		
	            		ivDelete.setVisibility(View.VISIBLE);
	            }
	            	Intent intent=new Intent(); 
					intent.setAction("cn.sparta1029.sayi.pwdbroadcast");
	    	        intent.putExtra("account", actv.getText().toString().trim());
	    	        context.sendBroadcast(intent);
	            }
	            @Override
	            public void beforeTextChanged(CharSequence s, int start, int count,
	                    int after) {
	                  }
	            @Override
	            public void afterTextChanged(Editable s) {
	                }
	        });
	        
	        this.addView(actv);  
	        this.addView(ivDelete);  
	    }  
	    public void setAdapter(DeletableAutoCompleteTextViewAdapter adapter){  
	    	actv.setAdapter(adapter);  
	    }  
	    public void setThreshold(int threshold){  
	    	actv.setThreshold(threshold);  
	    }  
	    public AutoCompleteTextView getAutoCompleteTextView(){  
	        return actv;  
	    }  
}
