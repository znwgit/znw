package com.znw.parentsforum.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

public class NetWorkUtil {
	private Context mContext;

	public NetWorkUtil(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@SuppressWarnings("static-access")
	public boolean isNetworkAvailable() {
		ConnectivityManager connectivity = (ConnectivityManager) mContext
				.getSystemService(mContext.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void idOpenNetWorkSetting() {
		if (!isNetworkAvailable()) {
			new AlertDialog.Builder(mContext)
					.setTitle("没有可用的网络")
					.setMessage("是否对网络进行设置？")
					.setPositiveButton("是",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									if (android.os.Build.VERSION.SDK_INT > 10) {
										// 3.0以上打开设置界面
										mContext.startActivity(new Intent(
												android.provider.Settings.ACTION_SETTINGS));
									} else {
										mContext.startActivity(new Intent(
												android.provider.Settings.ACTION_WIRELESS_SETTINGS));
									}
								}
							})
					.setNeutralButton("否",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.cancel();
								}
							}).show();
		}

	}
}
