package sketch.gui.testing;

import java.util.HashMap;

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
	public String resourceId;
	public String bounds;

	public float x1, y1, x2, y2;

	public AndroidNode() {
		x1 = y1 = x2 = y2 = (float) -1.0;
		index = text = widget_name = package_name = bounds = "";
	}

	public AndroidNode(String i, String t, String w, String p, String b, String r) {
		index = i;
		text = t;
		widget_name = w;
		package_name = p;
		bounds = b;
		resourceId = r;
		convert();
	}

	public void convert() {
		String mid_s = "";
		char mid_c;
		int pos = 1;

		for (int i = 0; i < bounds.length(); i++) {
			mid_c = bounds.charAt(i);
			if ((mid_c >= '0' && mid_c <= '9') || mid_c == '.') {
				mid_s += mid_c;
			} else {
				if (!mid_s.equals("")) {
					switch (pos) {
					// case 1: x1 = Double.parseDouble(mid_s); break;
					// case 2: y1 = Double.parseDouble(mid_s); break;
					// case 3: x2 = Double.parseDouble(mid_s); break;
					// case 4: y2 = Double.parseDouble(mid_s); break;
					case 1:
						x1 = Float.parseFloat(mid_s);
						break;
					case 2:
						y1 = Float.parseFloat(mid_s);
						break;
					case 3:
						x2 = Float.parseFloat(mid_s);
						break;
					case 4:
						y2 = Float.parseFloat(mid_s);
						break;
					}

					pos++;
					mid_s = "";
				}
			}
		}
		x1 = Math.min(x1, x2);
		x2 = Math.max(x1, x2);
		y1 = Math.min(y1, y2);
		y2 = Math.max(y1, y2);
	}

	public boolean isLocated(double x, double y) {
		if (x >= x1 && x <= x2 && y >= y1 && y <= y2)
			return true;
		return false;
	}

	public String getPrintString() {
		String str = "node index=\"" + index + "\";text=\"" + text + "\";resource-id=\"" + resourceId + "\";class=\""
				+ widget_name + "\";package=\"" + package_name + "\";bounds=\"" + bounds + "\"";
		return str;
	}

	/**
	 *return the map to transfer the module 3
	 * @return
	 */
	public HashMap<String, String> getMap() {
		HashMap<String, String> node = new HashMap<>();
		node.put("index", index);
		node.put("text", text);
		node.put("resource-id", resourceId);
		node.put("class", widget_name);
		node.put("package", "package_name");
		node.put("bounds", bounds);
		return node;
	}
}
