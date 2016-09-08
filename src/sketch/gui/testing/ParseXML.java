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

/**
 * Parsing a XML file to an Android-NodeList.
 * 
 * @author zhchuch
 */

public class ParseXML {
	public static String CLASS_PRE="android.widget.";
	public List<AndroidNode> node_list;
	
	public ParseXML()
	{
		node_list = new ArrayList<AndroidNode>();
	}
	
	public void parse(InputStream xmlFile)
	{
		try {			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(xmlFile);
			String id, tt, wn, pn, bd;
			
			doc.getDocumentElement().normalize();	
			//System.out.println("Root-Element: "+doc.getDocumentElement().getNodeName());
			
			NodeList nodes = doc.getElementsByTagName("node");
			//System.out.println("the size of nodes = " + nodes.getLength());
			
			for (int i=0; i<nodes.getLength(); i++)
			{
				Element node = (Element) nodes.item(i);
				id = node.getAttribute("index");
				tt = node.getAttribute("text");
				wn = node.getAttribute("class");
				pn = node.getAttribute("package");
				bd = node.getAttribute("bounds");
				
				if (wn.startsWith(CLASS_PRE) && !wn.contains("Layout"))
				{
					AndroidNode mid = new AndroidNode(id, tt, wn, pn, bd);
					node_list.add(mid);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("A Exception occurred! error = "+e.toString());
			e.printStackTrace();
		}
	}
	
	public AndroidNode findWidgetByLocation(double x, double y)
	{
		AndroidNode mid_and;
		for (int i=0; i < node_list.size(); i++)
		{
			mid_and = node_list.get(i);
			// mid_and.print();
			if (mid_and.isLocated(x, y))
			{
				//System.out.println("<133.0, 365.0> is in here! HaHaHa...");
				return mid_and;
			}
		}
		
		return null;
	}
	
	public List<AndroidNode> findWidgetByRect(PointF[] rect)
	{
		List<AndroidNode> wid_list = new ArrayList<AndroidNode>();
		
		// 设定判断 控件是否在Rect区域内的 比例 (2016.04.27)
		double ratio = 0.5;
		float x_cup, x1_cup, y_cup, y1_cup;
		
		for (AndroidNode mid : node_list)
		{
			/*
			if (mid.x1 >= rect[0].x && mid.x2 <= rect[3].x && mid.y1 >= rect[0].y && mid.y2 <= rect[3].y)
				wid_list.add(mid);
			*/
			
			x_cup = Math.max(mid.x1, rect[0].x);
			x1_cup = Math.min(mid.x2, rect[3].x);
			y_cup = Math.max(mid.y1, rect[0].y);
			y1_cup = Math.min(mid.y2, rect[3].y);
			// 计算两者重合部分的面积，面积大于比例则算选中了控件
			if ((calRectArea(x_cup,y_cup,x1_cup,y1_cup) / calRectArea(mid.x1,mid.y1,mid.x2,mid.y2)) >= ratio)
			{
				wid_list.add(mid);
			}
		}
		
		return wid_list;
	}
	
	public float calRectArea(float x1, float y1, float x2, float y2)
	{
		return (x2-x1)*(y2-y1);
	}
	
	public void print()
	{
		System.out.println("++++++++ Node List (size = "+node_list.size()+") +++++++++");
		for (int i=0; i<node_list.size(); i++)
		{
			node_list.get(i).print();
		}
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}
	
	/*
	public static void main(String args[])
	{
		parse("screen_data/simple_1.uix");
	}
	*/
}
