package cn.saprta1029.sayi.activity;

import cn.saprta1029.sayi.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

public class InfoFriendActivity extends Activity{
protected TextView tvAcount,tvName,tvState,tvEmail;
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_info_friend);
	tvAcount=(TextView)this.findViewById(R.id.friend_info_account);
	tvName=(TextView)this.findViewById(R.id.friend_info_name);
	tvState=(TextView)this.findViewById(R.id.friend_info_state);
	tvState.setVisibility(View.INVISIBLE);
	tvEmail=(TextView)this.findViewById(R.id.friend_info_email);

	Intent intent = getIntent();
	String username = intent.getStringExtra("username");
	String name = intent.getStringExtra("name");
	String email = intent.getStringExtra("email");
	tvAcount.setText("”√ªß√˚£∫"+username);
	tvName.setText("Í«     ≥∆£∫"+name);
	tvEmail.setText("”      œ‰£∫"+email);
}

public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK) {
		Intent intent = new Intent(InfoFriendActivity.this, ChatActivity.class);
		startActivity(intent);
		finish();
	}
	return super.onKeyDown(keyCode, event);
}


}
