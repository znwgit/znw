package com.znw.parentsforum.main;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

import com.example.residemenutest.ResideMenu.ResideMenu;
import com.example.residemenutest.ResideMenu.ResideMenuItem;
import com.znw.parentsforum.MainActivity;
import com.znw.parentsforum.R;
import com.znw.parentsforum.login.LoginActivity;
import com.znw.parentsforum.mainpage.FinderFragment;
import com.znw.parentsforum.mainpage.FriendFragment;
import com.znw.parentsforum.mainpage.MainFragment;
import com.znw.parentsforum.mainpage.MessageFragment;
import com.znw.parentsforum.mainpage.MoreFragment;
import com.znw.parentsforum.setting.SettingActivity;

public class MainPageActivity extends FragmentActivity implements
		OnClickListener {
	private ResideMenu resideMenu;
    private ResideMenuItem itemversion;
	 private ResideMenuItem itemProfile;
	// private ResideMenuItem itemCalendar;
	private ResideMenuItem itemSettings;

	// ����FragmentTabHost����
	private FragmentTabHost mTabHost;

	// ����һ������
	private LayoutInflater layoutInflater;

	// �������������Fragment����
	private Class fragmentArray[] = { MainFragment.class, FinderFragment.class,
			FriendFragment.class, MessageFragment.class, MoreFragment.class };

	// Tabѡ�������
	private String mTextviewArray[] = { "��ҳ", "����", "����", "��Ϣ", "����" };

	// ������������Ű�ťͼƬ
	private int mImageViewArray[] = { R.drawable.tab_home_btn,
			R.drawable.tab_square_btn, R.drawable.tab_selfinfo_btn,
			R.drawable.tab_message_btn, R.drawable.tab_more_btn };

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initView();
		// ������� �໬
		setUpMenu();
	}

	/**
	 * ��ʼ�����
	 */
	private void initView() {
		// ʵ�������ֶ���
		layoutInflater = LayoutInflater.from(this);

		// ʵ����TabHost���󣬵õ�TabHost
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		// �õ�fragment�ĸ���
		final int count = fragmentArray.length;

		for (int i = 0; i < count; i++) {
			// Ϊÿһ��Tab��ť����ͼ�ꡢ���ֺ�����
			TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i])
					.setIndicator(getTabItemView(i));
			// ��Tab��ť��ӽ�Tabѡ���
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
			// ����Tab��ť�ı���
			mTabHost.getTabWidget().getChildAt(i)
					.setBackgroundResource(R.drawable.selector_tab_background);
		}
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {

				if (tabId.equals("����")) {
					resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
				}

			}
		});
		//�������¼����жϲ໬�Ƿ�򿪣�
		mTabHost.getTabWidget().getChildAt(4)
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (resideMenu.isOpened()) {
							resideMenu.closeMenu();
						}else{
							resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
							//mTabHost.setCurrentTab(4);
						}
					}
				});

	}

	/**
	 * ��Tab��ť����ͼ�������
	 */
	private View getTabItemView(int index) {
		View view = layoutInflater.inflate(R.layout.tab_item_view, null);

		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		imageView.setImageResource(mImageViewArray[index]);

		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setText(mTextviewArray[index]);

		return view;
	}

	private void setUpMenu() {
		resideMenu = new ResideMenu(this);
		resideMenu.setBackground(R.drawable.menu_background);
		resideMenu.attachToActivity(this);
		resideMenu.setMenuListener(menuListener);
		// valid scale factor is between 0.0f and 1.0f. leftmenu'width is
		// 150dip.
		resideMenu.setScaleValue(0.6f);
		
		itemProfile= new ResideMenuItem(this, R.drawable.icon_profile, "��");
		itemversion=new ResideMenuItem(this, R.drawable.icon_calendar, "�ҵ��ղ�");
		itemSettings = new ResideMenuItem(this, R.drawable.icon_settings, "����");

		itemSettings.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainPageActivity.this, SettingActivity.class));
			}
		});

		// resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
		 resideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_RIGHT);
		 resideMenu.addMenuItem(itemversion, ResideMenu.DIRECTION_RIGHT);
		// resideMenu.addMenuItem(itemCalendar, ResideMenu.DIRECTION_RIGHT);
		resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_RIGHT);
		// You can disable a direction by setting ->
		// resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

	}

	private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
		@Override
		public void openMenu() {
			// Toast.makeText(mContext, "Menu is opened!", Toast.LENGTH_SHORT)
			// .show();
		}

		@Override
		public void closeMenu() {
			// Toast.makeText(mContext, "Menu is closed!", Toast.LENGTH_SHORT)
			// .show();
		}
	};
	private int keyBackClickCount = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (resideMenu.isOpened()) {
				resideMenu.closeMenu();
			} else {
				// resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
			}
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (resideMenu.isOpened()) {
				resideMenu.closeMenu();
				return true;
			} else {
				switch (keyBackClickCount++) {
				case 0:
					Toast.makeText(MainPageActivity.this, "�ٰ�һ�η��ؼ��˳�",
							Toast.LENGTH_SHORT).show();
					Timer timer = new Timer();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							keyBackClickCount = 0;
						}
					}, 3000);
					break;
				case 1:
					finish();
					break;
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		//����໬û�򿪣���ֹ�һ���  
		return resideMenu.dispatchTouchEvent(ev);
	}

	@Override
	public void onClick(View v) {
		resideMenu.closeMenu();
	}
}
