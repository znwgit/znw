package com.znw.parentsforum.setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.znw.parentsforum.R;
import com.znw.parentsforum.defineview.MyToggle;
import com.znw.parentsforum.util.NetWorkUtil;
import com.znw.parentsforum.util.VersionUtil;

public class SettingActivity extends Activity implements OnClickListener {
	private LinearLayout ll_version;
	private TextView tv_islast;
	private VersionUtil vu;
	private MyToggle toggle;
	private Handler handler = new Handler() {
		@SuppressLint("ResourceAsColor")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if ((boolean) msg.obj) {
					tv_islast.setText("有最新");
					tv_islast.setTextColor(R.color.azure);
					ll_version.setClickable(true);
				}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		NetWorkUtil nwu = new NetWorkUtil(SettingActivity.this);
		nwu.idOpenNetWorkSetting();
		init();
	}

	private void init() {
		ll_version = (LinearLayout) findViewById(R.id.ll_version);
		tv_islast = (TextView) findViewById(R.id.tv_islast);
		ll_version.setOnClickListener(this);
		toggle=(MyToggle) findViewById(R.id.toggle);
		 //开关的样式
        toggle.setImageRes(R.drawable.bkg_switch, R.drawable.bkg_switch, R.drawable.btn_slip);
//        toggle.setImageRes(R.drawable.switch_on_normal, R.drawable.switch_off_normal, R.drawable.seekbar_thumb);
        
        //指定开关的状态
        toggle.setSwitchState(false);
        
        //设置开关改变的监听
        toggle.setOnSwitchStateListener(new com.znw.parentsforum.defineview.MyToggle.OnSwitchStateListener() {
			
			@Override
			public void onSwitch(boolean state) {
				
				if(state){
					Toast.makeText(getApplicationContext(), "夜间模式开启", 0).show();
				} else {
					Toast.makeText(getApplicationContext(), "夜间模式关闭", 0).show();
				}
				
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
		checkVersion();
	}

	private void checkVersion() {
		String path = "http://192.168.1.101:8080/login/version/version.xml";
		vu = new VersionUtil(SettingActivity.this, handler, path);
		vu.isUpdate();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_version:
			vu.update();
			break;

		default:
			break;
		}

	}
}
