package com.example.gui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder {

	ImageView imgFileIcon;
	TextView tvFileName;

	public ViewHolder(View view) {
		imgFileIcon = (ImageView) view.findViewById(R.id.imgFileIcon);
		tvFileName = (TextView) view.findViewById(R.id.tvFileName);
	}
}
