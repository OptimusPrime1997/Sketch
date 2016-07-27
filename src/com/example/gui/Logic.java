package com.example.gui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import android.annotation.SuppressLint;
import android.graphics.PointF;

public class Logic {

	private float recordNum = 15;// 取十五个点用于分析
	/*
	 * 由两点获得经过这两点的直线的k,b(直线方程为y=kx+b;)
	 */
	private float[] constantOfEquation(PointF p1, PointF p2) {
		float[] constant = new float[2];
		constant[0] = (p2.y - p1.y) / (p2.x - p1.x);
		constant[1] = (p2.x * p1.y - p1.x * p2.y) / (p2.x - p1.x);
		return constant;
	}

	/*
	 * 获得两条直线的交点
	 */
	private PointF getIntersection(float[] c1, float[] c2) {
		PointF intersection = null;
		float k1 = c1[0];
		float b1 = c1[1];
		float k2 = c2[0];
		float b2 = c2[1];
		if (k1 != k2) {
			float x = (b2 - b1) / (k1 - k2);
			float y = (b2 * k1 - b1 * k2) / (k1 - k2);

			intersection = new PointF(x, y);// 交点
		} else
			intersection = new PointF(0, 0);

		return intersection;
	}

	private int[] calMiddleIndex(ArrayList<PointF> graphics) {
		int[] middleIndex = new int[2];
		middleIndex[0] = 0;
		middleIndex[1] = 0;
		boolean isStop = false;// 标志位，当isStop为true时跳出外层循环
		for (int i = 0; i < graphics.size() - 1; i++) {

			for (int j = 0; j < graphics.size() - 1; j++) {

				if (j != i && j != i - 1 && j != i + 1) {
					float[] c1 = constantOfEquation(graphics.get(i),
							graphics.get(i + 1));
					float[] c2 = constantOfEquation(graphics.get(j),
							graphics.get(j + 1));
					PointF interSection = getIntersection(c1, c2);
					if (interSection.x > graphics.get(i).x
							&& interSection.x < graphics.get(i + 1).x
							&& interSection.x > graphics.get(j).x
							&& interSection.x < graphics.get(j + 1).x
							&& interSection.y > graphics.get(i).y
							&& interSection.y < graphics.get(i + 1).y
							&& interSection.y < graphics.get(j).y
							&& interSection.y > graphics.get(j + 1).y) {

						middleIndex[0] = i + 1;
						middleIndex[1] = j;
						isStop = true;
						break;
					}
					if (isStop == true) {
						break;
					}
				}
			}
		}

		return middleIndex;
	}

	/*
	 * 获得中间点的坐标
	 */
	public float[] calCoordinate(ArrayList<PointF> graphics) {
		float[] coordinate = new float[2];
		ArrayList<Integer> properIndex = getProperIndex(graphics);

		if (properIndex.size() != 0) {
			for (int i = 0; i < properIndex.size(); i++) {
				coordinate[0] += graphics.get(properIndex.get(i)).x;
				coordinate[1] += graphics.get(properIndex.get(i)).y;
			}
			coordinate[0] = coordinate[0] / properIndex.size();
			coordinate[1] = coordinate[1] / properIndex.size();
		} else {
			coordinate[0] = 0;
			coordinate[1] = 0;
		}
		return coordinate;
	}

	/*
	 * 计算两点的距离
	 */
	private double getDistance(PointF p1, PointF p2) {
		double distance = 0;
		double x1 = p1.x;
		double y1 = p1.y;
		double x2 = p2.x;
		double y2 = p2.y;
		distance = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
		distance = Math.sqrt(distance);
		return distance;
	}

	/*
	 * 根据calMiddleIndex算出的中间点下标，利用最初两个点之间的距离为基准，将不小于这个基准的点的下标保存
	 */
	private ArrayList<Integer> getProperIndex(ArrayList<PointF> graphics) {
		ArrayList<Integer> properIndex = new ArrayList<Integer>();
		int[] middleIndex = calMiddleIndex(graphics);
		if (middleIndex[1] == 0) {
			return properIndex;
		} else {
			properIndex.add(middleIndex[0]);
			double standardDistance = getDistance(graphics.get(middleIndex[0]),
					graphics.get(middleIndex[0] + 1));// 用作进行比较的标准距离
			for (int i = middleIndex[0]; i < middleIndex[1]; i++) {
				double distance = getDistance(graphics.get(i),
						graphics.get(i + 1));
				int j = i + 1;
				while (distance < standardDistance) {
					j = j + 1;
					distance = getDistance(graphics.get(i), graphics.get(j));
				}
				i = j;
				properIndex.add(i);
			}
			return properIndex;
		}
	}

	/*
	 * 计算一个double数组中最小值的下标
	 */
	@SuppressLint("Assert")
	private int getMinValueIndex(int startIndex, double[] point) {
		assert (startIndex < point.length && startIndex >= 0);
		double minValue = point[startIndex];
		int minValueIndex = startIndex;
		for (int i = startIndex; i < point.length; i++) {
			if (minValue > point[i]) {
				minValue = point[i];
				minValueIndex = i;
			}
		}
		return minValueIndex;
	}

	/*
	 * 计算一个double数组中最大值的下标
	 */
	@SuppressLint("Assert")
	private int getmaxValueIndex(int startIndex, double[] point) {
		assert (startIndex < point.length && startIndex >= 0);
		double maxValue = point[startIndex];
		int maxValueIndex = startIndex;
		for (int i = startIndex; i < point.length; i++) {
			if (maxValue < point[i]) {
				maxValue = point[i];
				maxValueIndex = i;
			}
		}
		return maxValueIndex;
	}

	/*
	 * 计算一个double数组中最小值的下标
	 */
	@SuppressLint("Assert")
	private float getMinValue(int startIndex, float[] point) {
		assert (startIndex < point.length && startIndex >= 0);
		float minValue = point[startIndex];
		for (int i = startIndex; i < point.length; i++) {
			if (minValue > point[i]) {
				minValue = point[i];
			}
		}
		return minValue;
	}

	/*
	 * 计算一个double数组中最大值的下标
	 */
	@SuppressLint("Assert")
	private float getMaxValue(int startIndex, float[] point) {
		assert (startIndex < point.length && startIndex >= 0);
		float maxValue = point[startIndex];
		for (int i = startIndex; i < point.length; i++) {
			if (maxValue < point[i]) {
				maxValue = point[i];
			}
		}
		return maxValue;
	}

	/*
	 * 计算外接矩形，返回4个顶点的坐标
	 */
	public PointF[] getExteriorRect(ArrayList<PointF> graphics) {
		PointF[] rect = new PointF[4];
		float[] rectX = new float[graphics.size()];// graphics的横坐标的集合
		float[] rectY = new float[graphics.size()];// graphics的纵坐标的集合
		for (int i = 0; i < graphics.size(); i++) {
			rectX[i] = graphics.get(i).x;
			rectY[i] = graphics.get(i).y;
		}
		float minX = getMinValue(0, rectX);
		float maxX = getMaxValue(0, rectX);
		float minY = getMinValue(0, rectY);
		float maxY = getMaxValue(0, rectY);
		rect[0] = new PointF(minX, minY);
		rect[1] = new PointF(maxX, minY);
		rect[2] = new PointF(minX, maxY);
		rect[3] = new PointF(maxX, maxY);
		return rect;
	}

	/*
	 * 计算一个double数组中最小值的下标
	 */
	@SuppressLint("Assert")
	private int getMinValueIndex(int startIndex, float[] point) {
		assert (startIndex < point.length && startIndex >= 0);
		double minValue = point[startIndex];
		int minValueIndex = startIndex;
		for (int i = startIndex; i < point.length; i++) {
			if (minValue > point[i]) {
				minValue = point[i];
				minValueIndex = i;
			}
		}
		return minValueIndex;
	}

	/*
	 * 计算一个double数组中最大值的下标
	 */
	@SuppressLint("Assert")
	private int getmaxValueIndex(int startIndex, float[] x) {
		assert (startIndex < x.length && startIndex >= 0);
		double maxValue = x[startIndex];
		int maxValueIndex = startIndex;
		for (int i = startIndex; i < x.length; i++) {
			if (maxValue < x[i]) {
				maxValue = x[i];
				maxValueIndex = i;
			}
		}
		return maxValueIndex;
	}

	/*
	 * 计算拖动时的结束点
	 */
	public int calEndPoint(ArrayList<PointF> graphics) {
		int startIndex = calMiddleIndex(graphics)[1];
		float[] X = new float[graphics.size()];// graphics的横坐标的集合
		float[] Y = new float[graphics.size()];// graphics的纵坐标的集合
		for (int i = 0; i < graphics.size(); i++) {
			X[i] = graphics.get(i).x;
			Y[i] = graphics.get(i).y;
		}
		int xMaxIndex = getmaxValueIndex(startIndex, X);
		int xMinIndex = getMinValueIndex(startIndex, X);
		int yMaxIndex = getmaxValueIndex(startIndex, Y);
		int yMinIndex = getMinValueIndex(startIndex, Y);
		ArrayList<Integer> temp = new ArrayList<Integer>();
		temp.add(xMaxIndex);
		temp.add(xMinIndex);
		temp.add(yMaxIndex);
		temp.add(yMinIndex);
		Collections.sort(temp);
		return temp.get(2);

	}
	
	/*
	 * 从graphics中等距离取十五个点 不足用"？"代替
	 */
	public ArrayList<String> calRecordList(ArrayList<PointF> Graphics) {
		// TODO Auto-generated method stub
		ArrayList<String> recordList = new ArrayList<String>();
		if (Graphics.size() < recordNum) {
			for (int i = 0; i < Graphics.size(); i++) {
				DecimalFormat decimalFormat = new DecimalFormat(".00");// 将点的坐标统一保留两位小数
				String HCoordinate = decimalFormat.format(Graphics.get(i).x);
				String VCoordinate = decimalFormat.format(Graphics.get(i).y);
				recordList.add(HCoordinate);
				recordList.add(VCoordinate);
			}
			for (int j = 0; j < recordNum - Graphics.size(); j++) {
				recordList.add("?");
				recordList.add("?");
			}
		} else {
			float size = (float) Graphics.size();
			float k = size / recordNum;
			for (int i = 0; i < recordNum; i++) {
				DecimalFormat decimalFormat = new DecimalFormat(".00");
				String HCoordinate = decimalFormat.format(Graphics
						.get((int) (i * k)).x);
				String VCoordinate = decimalFormat.format(Graphics
						.get((int) (i * k)).y);
				recordList.add(HCoordinate);
				recordList.add(VCoordinate);
				
			}
		}
		return recordList;
	}
}
