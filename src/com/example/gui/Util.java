package com.example.gui;

import java.io.File;

import android.os.Environment;

public class Util {

	public static void deleteTempFile() {
		
		File skRoot = Environment.getExternalStorageDirectory();
		File tempDir = new File(skRoot.getPath() + "/screenShotPicture/screen_data/APPMusicNotetemp");
		String[] tempList = tempDir.list();
		System.out.println(tempList.length);
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			System.out.println(tempDir.getParent() + File.separator + tempList[i]);
			temp = new File(tempDir.getParent() + File.separator + tempList[i]);
			
			if (temp.isFile()) {
				System.out.println("delete");
				temp.delete();
			}
		}
	}
}
