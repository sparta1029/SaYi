package cn.sparta1029.sayi.components;
import cn.saprta1029.sayi.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
public class ChatroomPasswordDialog extends Dialog  {
	 //定义回调事件，用于dialog的点击事件
	private Button btnChatroomPasswordConfirm,btnChatroomPasswordCanel;
	    public interface OnCustomDialogListener{
	        public void back(boolean result,String ChatroomPassword);
	    }
	    
	    private String name;
	    private OnCustomDialogListener customDialogListener;
	    EditText etChatroomPassword;
	    
	    public ChatroomPasswordDialog(Context context,String name,OnCustomDialogListener customDialogListener) {
	        super(context);
	        this.name = name;
	        this.customDialogListener = customDialogListener;
	    }
	    @Override
	    protected void onCreate(Bundle savedInstanceState) { 
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.dialog_chatroom_password);
	        //设置标题
	        setTitle(name); 
	        etChatroomPassword= (EditText)findViewById(R.id.chatroom_password);
	        btnChatroomPasswordConfirm= (Button) findViewById(R.id.chatroom_password_confirm);
	        btnChatroomPasswordCanel= (Button) findViewById(R.id.chatroom_password_cancel);
	        btnChatroomPasswordConfirm.setOnClickListener( new View.OnClickListener() {
		        @Override
		        public void onClick(View v) {		   
		        	if(etChatroomPassword.getText().toString().equals(""))
		        	{
		        		customDialogListener.back(false,null);
		           
		        	}
		        	else 
		        	{
		        		 customDialogListener.back(true,etChatroomPassword.getText().toString().trim());
				         ChatroomPasswordDialog.this.dismiss();
		        	}
		        		}
		    });
	        btnChatroomPasswordCanel.setOnClickListener( new View.OnClickListener() {
		        @Override
		        public void onClick(View v) {
		            ChatroomPasswordDialog.this.dismiss();
		        }
		    });
	    }
}
