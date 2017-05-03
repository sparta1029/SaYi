package cn.sparta1029.sayi.components;
import java.util.regex.Pattern;
import cn.saprta1029.sayi.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
public class ServerAddressDialog extends Dialog  {
	 //定义回调事件，用于dialog的点击事件
	private Button btnServerAddressConfirm,btnServerAddressCanel;
	    public interface OnCustomDialogListener{
	        public void back(boolean result,String serverAddress);
	    }
	    private String name;
	    private OnCustomDialogListener customDialogListener;
	    EditText etServerAddress;
	    
	    public ServerAddressDialog(Context context,String name,OnCustomDialogListener customDialogListener) {
	        super(context);
	        this.name = name;
	        this.customDialogListener = customDialogListener;
	    }
	    @Override
	    protected void onCreate(Bundle savedInstanceState) { 
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.dialog_server_address);
	        //设置标题
	        setTitle(name); 
	        etServerAddress= (EditText)findViewById(R.id.server_address);
	        btnServerAddressConfirm= (Button) findViewById(R.id.server_address_confirm);
	        btnServerAddressCanel= (Button) findViewById(R.id.server_address_cancel);
	        btnServerAddressConfirm.setOnClickListener( new View.OnClickListener() {
		        @Override
		        public void onClick(View v) {
		        	String regex = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}"; 
		        	Pattern pattern = Pattern.compile(regex);
		        	if(pattern.matcher(etServerAddress.getText().toString()).matches())
		        	{
		            customDialogListener.back(true,etServerAddress.getText().toString().trim());
		            ServerAddressDialog.this.dismiss();
		        	}
		        	else 
		        		customDialogListener.back(false,null);
		        		}
		    });
	        btnServerAddressCanel.setOnClickListener( new View.OnClickListener() {
		        @Override
		        public void onClick(View v) {
		            ServerAddressDialog.this.dismiss();
		        }
		    });
	    }
}
