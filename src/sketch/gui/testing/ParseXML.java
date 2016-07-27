package sketch.gui.testing;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.graphics.PointF;
import android.util.Log;

/**
 * Parsing a XML file to an Android-NodeList.
 * 
 * @author zhchuch
 */

public class ParseXML {
	public static String CLASS_PRE = "android.widget.";
	public List<AndroidNode> node_list;

	public ParseXML(InputStream input) {
		node_list = new ArrayList<AndroidNode>();
		parse(input);
		// print(node_list);
	}

	public void parse(InputStream xmlFile) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(xmlFile);
			String id, tt, wn, pn, bd, rd;

			doc.getDocumentElement().normalize();
			Log.w("TAG-Pn1", doc.getDocumentElement().getNodeName());

			NodeList nodes = doc.getElementsByTagName("node");
			Log.w("TAG-Pn2", nodes.getLength() + "");

			for (int i = 0; i < nodes.getLength(); i++) {
				Element node = (Element) nodes.item(i);
				id = node.getAttribute("index");
				tt = node.getAttribute("text");
				wn = node.getAttribute("class");
				pn = node.getAttribute("package");
				bd = node.getAttribute("bounds");
				rd = node.getAttribute("resource-id");
				Log.w("TAG-Pn9", "id=" + id + " text=" + tt + " class=" + wn + " resource-id=" + rd + " package=" + pn
						+ " bounds=" + bd);
				if (wn.startsWith(CLASS_PRE) && !wn.contains("Layout")) {
					AndroidNode mid = new AndroidNode(id, tt, wn.substring(CLASS_PRE.length()), pn, bd, rd);
					node_list.add(mid);
					System.out.println(">>>>>>>>>>>"+node_list.size());
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("A Exception occurred! error = " + e.toString());
			e.printStackTrace();
		}
	}

	public AndroidNode findWidgetByLocation(double x, double y) {
		Log.w("TAG-Pn3", "use findwidgetByLocation");
		AndroidNode mid_and;
		//for (int i = 0; i < node_list.size(); i++) {
		for (int i = node_list.size()-1; i>=0; i--) {
			mid_and = node_list.get(i);
			// mid_and.print();
			if (mid_and.isLocated(x, y)) {
				Log.w("TAG-Pn3", "found the widget:" + mid_and.getPrintString() + "");
				return mid_and;
			}
		}
		Log.w("TAG-Pn3", "found the id failed");
		return null;
	}

	public List<AndroidNode> findWidgetByRect(PointF[] rect) {
		// rect中包含两个点：左下，右上
		List<AndroidNode> wid_list = new ArrayList<AndroidNode>();

		// 设定判断 控件是否在Rect区域内的 比例 (2016.04.27)
		double ratio = 0.5;
		float x_cup, x1_cup, y_cup, y1_cup;

		for (AndroidNode mid : node_list) {
			/*
			 * if (mid.x1 >= rect[0].x && mid.x2 <= rect[3].x && mid.y1 >=
			 * rect[0].y && mid.y2 <= rect[3].y) wid_list.add(mid);
			 */

			x_cup = Math.max(mid.x1, rect[0].x);
			x1_cup = Math.min(mid.x2, rect[1].x);
			y_cup = Math.max(mid.y1, rect[0].y);
			y1_cup = Math.min(mid.y2, rect[1].y);
			// 计算两者重合部分的面积，面积大于比例则算选中了控件
			if ((calRectArea(x_cup, y_cup, x1_cup, y1_cup) / calRectArea(mid.x1, mid.y1, mid.x2, mid.y2)) >= ratio
					&&rect[1].x<mid.x1&&rect[0].x<mid.x2&&rect[0].y<mid.y2&&rect[1].y>mid.y1) {
				wid_list.add(mid);
			}
		}
		//System.out.println("<<<<<<<"+wid_list.size());
		if (wid_list.size()==0) {
			double centerX = (rect[0].x+rect[1].x)/2;
			double centerY = (rect[0].y+rect[1].y)/2;
			AndroidNode temp = findWidgetByLocation(centerX, centerY);
			wid_list.add(temp);
		}
		print(wid_list);
		return wid_list;
	}

	public float calRectArea(float x1, float y1, float x2, float y2) {
		return (x2 - x1) * (y2 - y1);
	}

	public static void print(List<AndroidNode> nodes) {
		if (nodes != null) {
			Log.w("TAG-Pn1", "++++++++ Node List (size = " + nodes.size() + ") +++++++++");
			for (int i = 0; i < nodes.size(); i++) {
				Log.w("TAG-Pn2", nodes.get(i).getPrintString());
			}
		}
	}

	/*
	 * public static void main(String args[]) {
	 * parse("screen_data/simple_1.uix"); }
	 */
}
