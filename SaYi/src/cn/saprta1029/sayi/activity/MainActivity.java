package cn.saprta1029.sayi.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.saprta1029.sayi.R;
import cn.sparta1029.sayi.components.ServerAddressDialog;
import cn.sparta1029.sayi.components.TextViewWithImage;
import cn.sparta1029.sayi.utils.SPUtil;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	TextView TextView1, TextView2;
	// ViewPager��google SDk���Դ���һ�����Ӱ���һ���࣬��������ʵ����Ļ����л���
	// android-support-v4.jar
	private ViewPager mPager;// ҳ������
	private List<View> listViews; // Tabҳ���б�
	private ImageView cursor;// ����ͼƬ
	private TextView tvTitleFriend, tvTitleChatroom, t3;// ҳ��ͷ��
	private int offset = 0;// ����ͼƬƫ����
	private int currIndex = 0;// ��ǰҳ�����
	private int bmpW;// ����ͼƬ���
	private String account,password;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// ��ҳ���������
		InitImageView();
		InitTextView();
		InitViewPager();
		
		SPUtil SPUtil=new SPUtil(this);
		account=SPUtil.getString(SPUtil.keyCurrentUser, "");
		password=SPUtil.getString(SPUtil.keyCurrentPassword, "");
		
		android.app.ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.main_actionbar_color));
		actionBar.setTitle(account);
		actionBar.setIcon(R.drawable.default_avatar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		TextViewWithImage tvwitvTitleFriend = (TextViewWithImage) MainActivity.this
				.findViewById(R.id.main_text_friend);
		tvwitvTitleFriend.setTextColor(Color.rgb(158, 203, 226));

		TextViewWithImage tvwiTitleChatroom = (TextViewWithImage) MainActivity.this
				.findViewById(R.id.main_text_chatroom);
		tvwiTitleChatroom.setTextColor(Color.rgb(102, 102, 102));
	}

	/**
	 * ��ʼ��ͷ��
	 */
	private void InitTextView() {
		tvTitleFriend = (TextView) findViewById(R.id.main_text_friend);
		tvTitleChatroom = (TextView) findViewById(R.id.main_text_chatroom);

		tvTitleFriend.setOnClickListener(new MyOnClickListener(0));
		tvTitleChatroom.setOnClickListener(new MyOnClickListener(1));
	}

	/**
	 * ��ʼ��ViewPager
	 */
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		listViews.add(mInflater.inflate(R.layout.main_friend, null));
		listViews.add(mInflater.inflate(R.layout.main_chatroom, null));
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.addOnPageChangeListener(new MyOnPageChangeListener());
		
	}

	/**
	 * ��ʼ������
	 */
	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(),
				R.drawable.horizon_scrollbar).getWidth();// ��ȡͼƬ���
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// ��ȡ�ֱ��ʿ��
		offset = (screenW / 2 - bmpW) / 2;// ����ƫ����
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// ���ö�����ʼλ��
	}

	/**
	 * ViewPager������
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	/**
	 * ͷ��������
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	/**
	 * ҳ���л�����
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// ҳ��1 -> ҳ��2 ƫ����

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					TextViewWithImage tvwitvTitleFriend = (TextViewWithImage) MainActivity.this
							.findViewById(R.id.main_text_friend);
					tvwitvTitleFriend.setTextColor(Color.rgb(158, 203, 226));

					TextViewWithImage tvwiTitleChatroom = (TextViewWithImage) MainActivity.this
							.findViewById(R.id.main_text_chatroom);
					tvwiTitleChatroom.setTextColor(Color.rgb(102, 102, 102));

					animation = new TranslateAnimation(one, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					TextViewWithImage tvwitvTitleFriend = (TextViewWithImage) MainActivity.this
							.findViewById(R.id.main_text_friend);
					tvwitvTitleFriend.setTextColor(Color.rgb(102, 102, 102));

					TextViewWithImage tvwiTitleChatroom = (TextViewWithImage) MainActivity.this
							.findViewById(R.id.main_text_chatroom);
					tvwiTitleChatroom.setTextColor(Color.rgb(158, 203, 226));

					animation = new TranslateAnimation(offset, one, 0, 0);
				}
				break;
			}
			
			currIndex = arg0;
			animation.setFillAfter(true);// True:ͼƬͣ�ڶ�������λ��
			animation.setDuration(300);
			cursor.startAnimation(animation);

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	
	
	
	
	
	private void setIconEnable(Menu menu, boolean enable) {
		try {
			Class<?> clazz = Class
					.forName("com.android.internal.view.menu.MenuBuilder");
			Method m = clazz.getDeclaredMethod("setOptionalIconsVisible",
					boolean.class);
			m.setAccessible(true);
			m.invoke(menu, enable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		setIconEnable(menu, true);
		menu.add(0, 1, 0, " ע��").setIcon(R.drawable.main_logout);
		menu.add(0, 2, 0, " �˳�").setIcon(R.drawable.main_exit);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			Intent intent = new Intent(
					MainActivity.this,
					LoginActivity.class);
			SPUtil SPUtil=new SPUtil(MainActivity.this);
			SPUtil.putString(SPUtil.keyAutoLogin, SPUtil.booleanAutoLoginFalse);
			finish();
			startActivity(intent);
			break;
		case 2:
			finish();
			System.exit(0);
			break;
		case  android.R.id.home:
			Log.i("mytest", "here");
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	//�����ؼ��˳�����
	private static boolean isExit = false;
	
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}
	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����",
					Toast.LENGTH_SHORT).show();
			// ����handler�ӳٷ��͸���״̬��Ϣ
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			finish();
			System.exit(0);
		}
	}

}
