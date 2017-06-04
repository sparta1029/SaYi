package cn.sparta1029.sayi.components;

import cn.saprta1029.sayi.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

/*   加载框用法 
       LoadingDialog dialog = new LoadingDialog(LoginActivity.this, "登陆消息");  
       dialog.setCanceledOnTouchOutside(false);  
       dialog.show();  
             完成操作后     
       dialog.dismiss()*/




public class LoadingDialog extends Dialog {
	private TextView tvLoadingText;
	private String text;

	public LoadingDialog(Context context, String text) {
		super(context, R.style.loadingDialogStyle);
		this.text = text;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_loading);
		tvLoadingText = (TextView) this.findViewById(R.id.loading_text);
		tvLoadingText.setText(text);
		LinearLayout linearLayout = (LinearLayout) this
				.findViewById(R.id.LoadingDialogLinearLayout);
		linearLayout.getBackground().setAlpha(200);
		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return true; 
	}
	
	
}