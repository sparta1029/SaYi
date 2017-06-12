package cn.saprta1029.sayi.activity;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.saprta1029.sayi.R;
import cn.sparta1029.sayi.db.MessageDBManager;
import cn.sparta1029.sayi.db.MessageDBOpenHelper;
import cn.sparta1029.sayi.utils.FileUtil;
import cn.sparta1029.sayi.utils.SPUtil;

public class SettingActivity extends Activity implements OnClickListener {

	TextView tvMexxageCount, tvCurrentChatroomHistory,tvFolderSize;
	EditText etHistory;
	Button btnSetHistoryMax, btnMessageClear;
	SPUtil SPUtil;
	File file;
	private Button btnFolderClear;
	 String userDire;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		SPUtil = new SPUtil(this);
		String current = SPUtil.getString(SPUtil.keyChatroomHistoryMAX, "");
		String account= SPUtil.getString(SPUtil.keyCurrentUser, "");
		userDire = SettingActivity.this
					.getApplication()
					.getExternalFilesDir(null)
					.getPath()
					+ "/user/" + account + "/";
		 file=new File(userDire);
		tvMexxageCount = (TextView) this.findViewById(R.id.message_count); // tv�洢����Ϣ����
		tvCurrentChatroomHistory = (TextView) this
				.findViewById(R.id.setting_chatroom_history_current);// tv��ǰ��Ϣ���
		etHistory = (EditText) this.findViewById(R.id.setting_history_max);// et�����������ʷ��Ϣ��ֵ
		tvFolderSize = (TextView) this
				.findViewById(R.id.file_count);// tv��ǰ��Ϣ���
		tvFolderSize.setText("�����ļ��д�СΪ��"+FileUtil.getFolderSize(file));
		tvCurrentChatroomHistory.setText(tvCurrentChatroomHistory.getText()
				.toString() + current);

		btnMessageClear = (Button) this
				.findViewById(R.id.setting_clear_message);// btn�����Ϣ
		btnFolderClear = (Button) this
				.findViewById(R.id.setting_clear_file);// btn����ļ���
		btnMessageClear.setOnClickListener(this);
		btnFolderClear.setOnClickListener(this);
		btnSetHistoryMax = (Button) this.findViewById(R.id.setting_history);// btn���������ʷ��Ϣ
		btnSetHistoryMax.setOnClickListener(this);
		MessageDBOpenHelper DBHelper = new MessageDBOpenHelper(this, "sayi",
				null, 1);
		SQLiteDatabase db = DBHelper.getWritableDatabase();
		MessageDBManager MessageDBManager = new MessageDBManager();
		int count = MessageDBManager.messageAllCountQuery(db);
		db.close();
		tvMexxageCount.setText(tvMexxageCount.getText().toString().trim() + " "
				+ count);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting_clear_message: // �����Ϣ
			MessageDBOpenHelper DBHelper = new MessageDBOpenHelper(this,
					"sayi", null, 1);
			SQLiteDatabase db = DBHelper.getWritableDatabase();
			MessageDBManager MessageDBManager = new MessageDBManager();
			MessageDBManager.messageDelete(db);
			db.close();
			break;
		case R.id.setting_history: // ���������ʷ��Ϣ
			if ("".equals(etHistory.getText().toString()))
				Toast.makeText(SettingActivity.this, "������Ҫ���õ������������ʷ��Ϣֵ",
						Toast.LENGTH_LONG).show();
			else {
				SPUtil.putString(SPUtil.keyChatroomHistoryMAX, etHistory
						.getText().toString());
			}
			break;
		case R.id.setting_clear_file:
			FileUtil.deleteFolderFile(userDire, true);
			tvFolderSize.setText("�����ļ��д�СΪ��0");
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(SettingActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	class SettingAdapter extends BaseAdapter {
		public SettingAdapter() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
