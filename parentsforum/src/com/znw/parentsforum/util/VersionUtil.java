package com.znw.parentsforum.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;

public class VersionUtil {
	private Context context;
	private ProgressDialog dioalog;
	private String versionXmlUrl;
	private Handler handler;
	private Handler progresshandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 2:
				int num = Integer.parseInt(msg.obj.toString());
				dioalog.setProgress(num);
				if (num == 100) {
					installApk(new File("/mnt/sdcard/parentsforum.apk"));
				}
				break;
			}
		}
	};
	private InputStream is;

	public VersionUtil(Context context, Handler handler, String versionXmlUrl) {
		this.handler = handler;
		this.context = context;
		this.versionXmlUrl = versionXmlUrl;
		dioalog = new ProgressDialog(context);
		dioalog.setTitle("提示");
		dioalog.setMessage("正在下载，请稍后....");
		dioalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dioalog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						new BreakpointDownloader().pause();

					}
				});
	}

	public void update() {
		dioalog.show();
		dioalog.setProgress(0);
		UpdataInfo upinfo = getUpdataInfo();
		new Thread() {
			public void run() {
				try {
					new BreakpointDownloader(
							"http://192.168.1.101:8080/login/version/parentsforum.apk",
							progresshandler).download();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	public void isUpdate() {
		new Thread() {
			public void run() {
				try {
					URL url = new URL(versionXmlUrl);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(5000);
					is = conn.getInputStream();
					String serverVersion = getUpdataInfo().getVersion().trim();
					String currentVersion = getVersion().trim();
					if (serverVersion.equals(currentVersion)) {
						Message msg = new Message();
						msg.obj = false;
						msg.what = 1;
						handler.sendMessage(msg);
					} else {
						Message msg = new Message();
						msg.obj = true;
						msg.what = 1;
						handler.sendMessage(msg);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	public UpdataInfo getUpdataInfo() {
		UpdataInfo info = new UpdataInfo();
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(is, "utf-8");
			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("version".equals(parser.getName())) {
						info.setVersion(parser.nextText());
					} else if ("apkurl".equals(parser.getName())) {
						info.setUrl(parser.nextText());
					}
					break;
				}
				type = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}

	public String getVersion()// 获取版本号
	{
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void showUpdataDialog() {

	}

	private void installApk(File apkfile) {
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		context.startActivity(i);

	}

}

class UpdataInfo {
	private String version;
	private String url;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
