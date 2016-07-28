package com.example.gui.enums;

public enum ClassType {
	IMAGEBUTTON("android.widget.ImageButton"), BUTTON("android.widget.Button"), TEXTVIEW(
			"android.widget.TextView"), TABLEROW("android.widget.TableRow"), IMAGEVIEW(
					"android.widget.ImageView"), TABWIDGET("android.widget.TabWidget"), VIEWSWITCHER(
							"android.widget.ViewSwitcher"), TABLEHOST("android.widget.TableHost");
	private String value;

	private ClassType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}
}
