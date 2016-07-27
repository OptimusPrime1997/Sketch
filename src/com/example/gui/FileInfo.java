package com.example.gui;

import java.util.ArrayList;
import android.annotation.SuppressLint;


public class FileInfo {

	private static ArrayList<String> Image_SUFFIX = new ArrayList<String>();
	static {
		Image_SUFFIX.add(".jpg");
		Image_SUFFIX.add(".png");
		Image_SUFFIX.add(".jpeg");
	}
	private FileType fileType;
	private String fileName;
	private String filePath;

	public FileInfo(String filePath, String fileName, boolean isDirectory) {
		this.filePath = filePath;
		this.fileName = fileName;
		fileType = isDirectory ? FileType.DIRECTORY : FileType.FILE;
	}

	@SuppressLint("DefaultLocale")
	public boolean isImage() {
		if (fileName.lastIndexOf(".") < 0) // Don't have the suffix
			return false;
		String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
		if (!isDirectory()
				&& Image_SUFFIX.contains(fileSuffix.toLowerCase()))
			return true;
		else
			return false;
	}

	public boolean isDirectory() {
		if (fileType == FileType.DIRECTORY)
			return true;
		else
			return false;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public String toString() {
		return "FileInfo [fileType=" + fileType + ", fileName=" + fileName
				+ ", filePath=" + filePath + "]";
	}
}


