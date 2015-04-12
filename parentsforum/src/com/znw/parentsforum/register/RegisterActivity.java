package com.znw.parentsforum.register;

import cn.smssdk.SMSSDK;

import com.znw.parentsforum.R;
import com.znw.parentsforum.login.LoginActivity;
import com.znw.parentsforum.util.NetWorkUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements OnClickListener {

	private ImageView iv_back_register;
	private Button btn_get_identifyingcode, btn_register;
	private EditText ed_regeister_phone, ed_regeister_pwd;
	private Context context = RegisterActivity.this;
	
	private String phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		NetWorkUtil nwu = new NetWorkUtil(RegisterActivity.this);
		nwu.idOpenNetWorkSetting();
		init();
	}

	private void init() {
		iv_back_register=(ImageView) findViewById(R.id.iv_back_register);
		ed_regeister_phone = (EditText) findViewById(R.id.ed_regeister_phone);
		ed_regeister_pwd = (EditText) findViewById(R.id.ed_regeister_pwd);
		btn_register = (Button) findViewById(R.id.btn_register);
		btn_register.setOnClickListener(this);
		iv_back_register.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back_register:
			finish();
			break;

		case R.id.btn_register:
			String phone = ed_regeister_phone.getText().toString();
			String pwd = ed_regeister_pwd.getText().toString();
			if (!TextUtils.isEmpty(ed_regeister_phone.getText().toString())) {
			} else {
				Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
			}
			if (!TextUtils.isEmpty(ed_regeister_pwd.getText().toString())) {
			} else {
				Toast.makeText(this, "登录密码不能为空", Toast.LENGTH_SHORT).show();
			}
			if ("15612345678".equals(phone)) {
				Toast.makeText(this, "手机号不存在", Toast.LENGTH_SHORT).show();
				ed_regeister_phone.setText("");
			}
			if ("15712345678".equals(phone) && "123456".equals(pwd)) {
				Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(context, LoginActivity.class);
				startActivity(intent);
			}
			break;
		}
	}

	

	protected void onDestroy() {
		SMSSDK.unregisterAllEventHandler();
	};
}
