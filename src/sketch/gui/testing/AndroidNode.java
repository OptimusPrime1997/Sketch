package sketch.gui.testing;

/**
 * AndroidNode is used to store the Widget-Information of the GUI.png
 * 
 * @author zhchuch
 */
public class AndroidNode {
	public String index;
	public String text;
	public String widget_name;
	public String package_name;
	public String bounds;
	public float x1, y1, x2, y2;
	
	public AndroidNode()
	{
		x1 = y1 = x2 = y2 = (float) -1.0;
		index = text = widget_name = package_name = bounds = "";
	}
	
	public AndroidNode(String i, String t, String w, String p, String b)
	{
		index = i; text = t; widget_name = w;
		package_name = p; bounds = b;
		convert();
	}
	
	public void convert()
	{
		String mid_s = "";
		char mid_c;
		int pos = 1;
		
		for (int i=0; i<bounds.length(); i++)
		{
			mid_c = bounds.charAt(i);
			if ((mid_c >= '0' && mid_c <= '9') || mid_c == '.') {
				mid_s += mid_c;
			}
			else {
				if (!mid_s.equals("")) {
					switch (pos) {
//						case 1: x1 = Double.parseDouble(mid_s); break;
//						case 2: y1 = Double.parseDouble(mid_s); break;
//						case 3: x2 = Double.parseDouble(mid_s); break;
//						case 4: y2 = Double.parseDouble(mid_s); break;
						case 1: x1 = Float.parseFloat(mid_s); break;
						case 2: y1 = Float.parseFloat(mid_s); break;
						case 3: x2 = Float.parseFloat(mid_s); break;
						case 4: y2 = Float.parseFloat(mid_s); break;
					}
					
					pos++;
					mid_s = "";
				}
			}
		}
	}

	public boolean isLocated(double x, double y)
	{
		if (x >= x1 && x <= x2 && y >= y1 && y <= y2) return true;
		return false;
	}
	
	public void print()
	{
		System.out.println("========== AndoirdNode =============");
		System.out.println("index: "+ index);
		System.out.println("text: "+ text);
		System.out.println("class: " + widget_name);
		System.out.println("package: " + package_name);
		System.out.println("bounds: " + bounds +"\n");
	}
	
	/*
	public static void main(String[] args)
	{
		bounds = "[0,25][540,960]";
		convert();
		System.out.println("x1 = "+x1+", x2 = "+x2+", y1 = "+y1+", y2 = "+y2);
		
		bounds= "[0.2,34.43][400,20.0]";
		convert();
		System.out.println("x1 = "+x1+", x2 = "+x2+", y1 = "+y1+", y2 = "+y2);
	}
	*/
}
