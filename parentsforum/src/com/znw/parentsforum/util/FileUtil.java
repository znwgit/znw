package com.znw.parentsforum.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.util.Log;

public class FileUtil {
	// 读取文本文件中的内容
	public static String readFile(Context context, String strFilePath) {

		String content = ""; // 文件内容字符串
		try {
			FileInputStream fis = context.openFileInput(strFilePath);
			if (fis != null) {
				InputStreamReader inputreader = new InputStreamReader(fis);
				BufferedReader buffreader = new BufferedReader(inputreader);
				String line;
				// 分行读取
				while ((line = buffreader.readLine()) != null) {
					content += line + "\n";
				}
				fis.close();
			}
		} catch (java.io.FileNotFoundException e) {
			Log.d("TestFile", "The File doesn't not exist.");
		} catch (IOException e) {
			Log.d("TestFile", e.getMessage());
		}
		return content;
	}
}
