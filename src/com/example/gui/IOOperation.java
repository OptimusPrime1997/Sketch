package com.example.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.graphics.PointF;

public class IOOperation {

	public void CreateMdr(String path) throws IOException {
		File file = new File(path);

		if (!file.exists()) {
			try {
				// 在指定的文件夹中创建文件
				file.mkdir();

			} catch (Exception e) {

			}// catch
		}// if
	}// CreateMdr

	public void recordPoint(String filePath, ArrayList<String> recordList) {

		File fi = new File(filePath);
		if (!(fi.exists())) {
			try {
				fi.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FileWriter fw;
			try {
				fw = new FileWriter(fi, true);
				fw.write("@relation gesture" + "\r\n");
				fw.write("@attribute x1 numeric" + "\r\n");
				fw.write("@attribute y1 numeric" + "\r\n");
				fw.write("@attribute x2 numeric" + "\r\n");
				fw.write("@attribute y2 numeric" + "\r\n");
				fw.write("@attribute x3 numeric" + "\r\n");
				fw.write("@attribute y3 numeric" + "\r\n");
				fw.write("@attribute x4 numeric" + "\r\n");
				fw.write("@attribute y4 numeric" + "\r\n");
				fw.write("@attribute x5 numeric" + "\r\n");
				fw.write("@attribute y5 numeric" + "\r\n");
				fw.write("@attribute x6 numeric" + "\r\n");
				fw.write("@attribute y6 numeric" + "\r\n");
				fw.write("@attribute x7 numeric" + "\r\n");
				fw.write("@attribute y7 numeric" + "\r\n");
				fw.write("@attribute x8 numeric" + "\r\n");
				fw.write("@attribute y8 numeric" + "\r\n");
				fw.write("@attribute x9 numeric" + "\r\n");
				fw.write("@attribute y9 numeric" + "\r\n");
				fw.write("@attribute x10 numeric" + "\r\n");
				fw.write("@attribute y10 numeric" + "\r\n");
				fw.write("@attribute x11 numeric" + "\r\n");
				fw.write("@attribute y11 numeric" + "\r\n");
				fw.write("@attribute x12 numeric" + "\r\n");
				fw.write("@attribute y12 numeric" + "\r\n");
				fw.write("@attribute x13 numeric" + "\r\n");
				fw.write("@attribute y13 numeric" + "\r\n");
				fw.write("@attribute x14 numeric" + "\r\n");
				fw.write("@attribute y14 numeric" + "\r\n");
				fw.write("@attribute x15 numeric" + "\r\n");
				fw.write("@attribute y15 numeric" + "\r\n");
				fw.write("@attribute action {click, dbclick, drag, undefined}"
						+ "\r\n");
				fw.write("@data" + "\r\n");
				fw.flush();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}// if

		FileWriter fwriter;
		try {
			fwriter = new FileWriter(fi, true);
			for (int i = 0; i < recordList.size(); i++) {
				String Coordinate = recordList.get(i);
				fwriter.write(Coordinate + ",");
				if (i == recordList.size() - 1) {
					fwriter.write("?");
					fwriter.write("\r\n");
				}// if
			}// for
			fwriter.flush();
			fwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}// recordPoint

	public void recordJointPoint(String filePath, ArrayList<String> recordList) {

		File fi = new File(filePath);
		if (!(fi.exists())) {
			try {
				fi.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FileWriter fw;
			try {
				fw = new FileWriter(fi, true);
				fw.write("@relation gesture" + "\r\n");
				fw.write("@attribute x1 numeric" + "\r\n");
				fw.write("@attribute y1 numeric" + "\r\n");
				fw.write("@attribute x2 numeric" + "\r\n");
				fw.write("@attribute y2 numeric" + "\r\n");
				fw.write("@attribute x3 numeric" + "\r\n");
				fw.write("@attribute y3 numeric" + "\r\n");
				fw.write("@attribute x4 numeric" + "\r\n");
				fw.write("@attribute y4 numeric" + "\r\n");
				fw.write("@attribute x5 numeric" + "\r\n");
				fw.write("@attribute y5 numeric" + "\r\n");
				fw.write("@attribute x6 numeric" + "\r\n");
				fw.write("@attribute y6 numeric" + "\r\n");
				fw.write("@attribute x7 numeric" + "\r\n");
				fw.write("@attribute y7 numeric" + "\r\n");
				fw.write("@attribute x8 numeric" + "\r\n");
				fw.write("@attribute y8 numeric" + "\r\n");
				fw.write("@attribute x9 numeric" + "\r\n");
				fw.write("@attribute y9 numeric" + "\r\n");
				fw.write("@attribute x10 numeric" + "\r\n");
				fw.write("@attribute y10 numeric" + "\r\n");
				fw.write("@attribute x11 numeric" + "\r\n");
				fw.write("@attribute y11 numeric" + "\r\n");
				fw.write("@attribute x12 numeric" + "\r\n");
				fw.write("@attribute y12 numeric" + "\r\n");
				fw.write("@attribute x13 numeric" + "\r\n");
				fw.write("@attribute y13 numeric" + "\r\n");
				fw.write("@attribute x14 numeric" + "\r\n");
				fw.write("@attribute y14 numeric" + "\r\n");
				fw.write("@attribute x15 numeric" + "\r\n");
				fw.write("@attribute y15 numeric" + "\r\n");
				fw.write("@attribute action {forAll, resursiveForAll, forSome}"
						+ "\r\n");
				fw.write("@data" + "\r\n");
				fw.flush();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}// if

		FileWriter fwriter;
		try {
			fwriter = new FileWriter(fi, true);
			for (int i = 0; i < recordList.size(); i++) {
				String Coordinate = recordList.get(i);
				fwriter.write(Coordinate + ",");
				if (i == recordList.size() - 1) {
					fwriter.write("?");
					fwriter.write("\r\n");
				}// if
			}// for
			fwriter.flush();
			fwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}// recordPoint
	public void recordOperation(String filePath, String operation) {
		File fi = new File(filePath);
		if (!(fi.exists())) {
			try {
				fi.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		FileWriter fwriter;
		try {
			fwriter = new FileWriter(fi, true);
			fwriter.write(operation);
			fwriter.write("\r\n");
			System.out.println(operation);
			fwriter.flush();
			fwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void recordAreaInfo(String filePath, ArrayList<PointF> graphics,
			PointF[] point) {
		File fi = new File(filePath);
		if (!(fi.exists())) {
			try {
				fi.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		FileWriter fwriter;
		try {
			fwriter = new FileWriter(fi, true);

			for (int i = 0; i < graphics.size(); i++) {
				DecimalFormat decimalFormat = new DecimalFormat(".00");// 将点的坐标统一保留两位小数
				String HCoordinate = decimalFormat.format(graphics.get(i).x);
				String VCoordinate = decimalFormat.format(graphics.get(i).y);
				String temp = "<" + HCoordinate + "," + VCoordinate + ">;";
				fwriter.write(temp);
			}
			fwriter.write("Rect:");
			for (int i = 0; i < point.length; i++) {
				DecimalFormat decimalFormat = new DecimalFormat(".00");// 将点的坐标统一保留两位小数
				String HCoordinate = decimalFormat.format(point[i].x);
				String VCoordinate = decimalFormat.format(point[i].y);
				String temp = "<" + HCoordinate + "," + VCoordinate + ">;";
				fwriter.write(temp);
			}
			fwriter.write("\r\n");

			fwriter.flush();
			fwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<String> readOperation(String filePath) {
		ArrayList<String> operationList = new ArrayList<String>();
		File file = new File(filePath);
		if (file.exists()) {
			try {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String lineTxt = null;
				while ((lineTxt = br.readLine()) != null) {
					operationList.add(lineTxt);
				}
				br.close();
				fr.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return operationList;
	}
}
