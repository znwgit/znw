package com.znw.parentsforum;

import com.znw.parentsforum.guide.GuideActivity;
import com.znw.parentsforum.login.LoginActivity;
import com.znw.parentsforum.main.MainPageActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

@SuppressLint("WorldReadableFiles")
public class MainActivity extends Activity {

	SharedPreferences preferences;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tiezi_list);

		preferences = getSharedPreferences("count", MODE_PRIVATE);

		int count = preferences.getInt("count", 0);

		if (count == 0) {

			Intent intent = new Intent();

			intent.setClass(getApplicationContext(), GuideActivity.class);

			startActivity(intent);

			finish();

		} else {
			startActivity(new Intent(MainActivity.this, LoginActivity.class));
		}

		Editor editor = preferences.edit();
		editor.putInt("count", ++count);
		editor.commit();

	}

}
