package com.example.gui;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.io.XMLWriter;

import android.annotation.SuppressLint;
import android.os.Environment;


@SuppressLint("SimpleDateFormat")
public class WriteXML {

	
	public static File createTestFile(){
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String fileName = simpleDateFormat.format(date);
		File skRoot = Environment.getExternalStorageDirectory();
		File totalDir = new File(skRoot.getPath()+"/transdata",fileName+".xml");
		if (!totalDir.exists()) {		
			totalDir.getParentFile().mkdirs();
			try {
				totalDir.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return totalDir;		 
	}
	
	public static File createTestFileForRec(int i){
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss_"+(i+""));
		String fileName = simpleDateFormat.format(date);
		File skRoot = Environment.getExternalStorageDirectory();
		File totalDir = new File(skRoot.getPath()+"/transdata",fileName+".xml");
		if (!totalDir.exists()) {		
			totalDir.getParentFile().mkdirs();
			try {
				totalDir.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return totalDir;		 
	}
	
	public static void writeObject(Document document, String fileName) {
		try {
			XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
			writer.write(document);
			writer.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
    
	
	
}
