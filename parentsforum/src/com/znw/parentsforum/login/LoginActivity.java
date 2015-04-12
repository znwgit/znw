package com.znw.parentsforum.login;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.znw.parentsforum.R;
import com.znw.parentsforum.customview.ClearEditText;
import com.znw.parentsforum.main.MainPageActivity;
import com.znw.parentsforum.register.RegisterActivity;
import com.znw.parentsforum.util.NetWorkUtil;
import com.znw.parentsforum.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {

	private ClearEditText edit_username, edit_pwd;
	private Button btn_login;
	private ImageView ib_qqlogin, iv_register;
	private TextView tv_register;

	public static QQAuth mQQAuth;
	private Tencent mTencent;

	Context context = LoginActivity.this;
	Context ctxContext = null;

	private final String APP_ID = "222222";
	public static String mAppid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		NetWorkUtil nwu = new NetWorkUtil(context);
		nwu.idOpenNetWorkSetting();
		init();
	}

	@Override
	protected void onStart() {
		ctxContext = context.getApplicationContext();
		mAppid = APP_ID;
		mQQAuth = QQAuth.createInstance(mAppid, ctxContext);
		mTencent = Tencent.createInstance(mAppid, context);
		super.onStart();
	}

	private void init() {
		edit_username = (ClearEditText) findViewById(R.id.edit_username);
		edit_pwd = (ClearEditText) findViewById(R.id.edit_pwd);
		btn_login = (Button) findViewById(R.id.btn_login);
		ib_qqlogin = (ImageView) findViewById(R.id.ib_qqlogin);
		iv_register = (ImageView) findViewById(R.id.iv_register);
		tv_register = (TextView) findViewById(R.id.tv_register);

		btn_login.setOnClickListener(this);
		ib_qqlogin.setOnClickListener(this);
		iv_register.setOnClickListener(this);
		tv_register.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			getLogin();
			break;

		case R.id.ib_qqlogin:
			qqLogin();
			break;

		case R.id.iv_register:
			register();
			break;

		case R.id.tv_register:
			register();
			break;
		}

	}

	private void register() {
		Intent intent = new Intent(context, RegisterActivity.class);
		startActivity(intent);
	}

	public void getLogin() {
		String username = edit_username.getText().toString();
		String pwd = edit_pwd.getText().toString();
		if (username == null || "".equals(username.trim())) {
			Toast.makeText(context, R.string.username + "不能为空",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (pwd == null || "".equals(pwd.trim())) {
			Toast.makeText(context, R.string.pwd + "不能为空", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		AsyncHttpClient client = new AsyncHttpClient();
		String path = "http://192.168.1.100:8080/login/LoginServlet?username="
				+ username + "&pwd=" + pwd;
		client.get(path, new TextHttpResponseHandler("GBK") {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					String responseString) {
				switch (responseString) {
				case "0":
					Toast.makeText(context, "用户不存在", Toast.LENGTH_LONG).show();
					break;
				case "1":
					Toast.makeText(context, "登陆成功", Toast.LENGTH_LONG).show();
					Intent intent = new Intent(context, MainPageActivity.class);
					startActivity(intent);
					break;
				case "2":
					Toast.makeText(context, "登录失败", Toast.LENGTH_LONG).show();
					break;
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				Toast.makeText(context, "请求失败", Toast.LENGTH_LONG).show();

			}
		});
	}

	private void qqLogin() {
		if (!mQQAuth.isSessionValid()) {
			IUiListener listener = new BaseUiListener() {
				@Override
				protected void doComplete(JSONObject values) {
					// updateUserInfo();
					// updateLoginButton();
				}
			};
			mQQAuth.login(this, "all", listener);
			// mTencent.loginWithOEM(this, "all",
			// listener,"10000144","10000144","xxxx");
			mTencent.login(this, "all", listener);
		} else {
			mQQAuth.logout(this);
			// updateUserInfo();
			// updateLoginButton();
		}
	}

	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			// Util.showResultDialog(context, response.toString(), "��¼�ɹ�");
			doComplete((JSONObject) response);
			Intent intent = new Intent(context, MainPageActivity.class);
			startActivity(intent);
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onError(UiError e) {
			// Util.toastMessage(LoginActivity.this, "onError: " +
			// e.errorDetail);
			Util.dismissDialog();
		}

		@Override
		public void onCancel() {
			// Util.toastMessage(LoginActivity.this, "onCancel: ");
			Util.dismissDialog();
		}
	}

	private void updateUserInfo() {
		if (mQQAuth != null && mQQAuth.isSessionValid()) {
			IUiListener listener = new IUiListener() {

				@Override
				public void onError(UiError e) {

				}

				@Override
				public void onComplete(final Object response) {
					Message msg = new Message();
					msg.obj = response;
					msg.what = 0;
					mHandler.sendMessage(msg);
					new Thread() {

						@Override
						public void run() {
							JSONObject json = (JSONObject) response;
							if (json.has("figureurl")) {
								Bitmap bitmap = null;
								try {
									bitmap = Util.getbitmap(json
											.getString("figureurl_qq_2"));
								} catch (JSONException e) {

								}
								Message msg = new Message();
								msg.obj = bitmap;
								msg.what = 1;
								mHandler.sendMessage(msg);
							}
						}

					}.start();
				}

				@Override
				public void onCancel() {
				}
			};
			// mInfo = new UserInfo(this, mQQAuth.getQQToken());
			// mInfo.getUserInfo(listener);

		} else {
			// mUserInfo.setText("");
			// mUserInfo.setVisibility(android.view.View.GONE);
			// mUserLogo.setVisibility(android.view.View.GONE);
		}
	}

	private void updateLoginButton() {
		if (mQQAuth != null && mQQAuth.isSessionValid()) {
			// mNewLoginButton.setTextColor(Color.RED);
			// mNewLoginButton.setText(R.string.qq_logout);
		} else {
			// mNewLoginButton.setTextColor(Color.BLUE);
			// mNewLoginButton.setText(R.string.qq_login);
		}
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				JSONObject response = (JSONObject) msg.obj;
				if (response.has("nickname")) {
					try {
						// mUserInfo.setVisibility(android.view.View.VISIBLE);
						// mUserInfo.setText(response.getString("nickname"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (msg.what == 1) {
				Bitmap bitmap = (Bitmap) msg.obj;
				// mUserLogo.setImageBitmap(bitmap);
				// mUserLogo.setVisibility(android.view.View.VISIBLE);
			}
		}

	};
}
