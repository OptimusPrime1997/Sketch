package com.example.gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ImageChooserAdapter extends BaseAdapter {

	private ArrayList<FileInfo> mFileLists;
	private LayoutInflater mLayoutInflater = null;

	private static ArrayList<String> Image_SUFFIX = new ArrayList<String>();

	static {
		Image_SUFFIX.add(".jpg");
		Image_SUFFIX.add(".png");
		Image_SUFFIX.add(".jpeg");
	}

	public ImageChooserAdapter(Context context, ArrayList<FileInfo> fileLists) {
		super();
		mFileLists = fileLists;
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFileLists.size();
	}

	@Override
	public FileInfo getItem(int position) {
		// TODO Auto-generated method stub
		return mFileLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = null;
		ViewHolder holder = null;
		if (convertView == null || convertView.getTag() == null) {
			view = mLayoutInflater.inflate(R.layout.filechooser_gridview_item,
					null);
			holder = new ViewHolder(view);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) convertView.getTag();
		}

		FileInfo fileInfo = getItem(position);
		// TODO

		holder.tvFileName.setText(fileInfo.getFileName());

		if (fileInfo.isDirectory()) { // 文件夹
			holder.imgFileIcon.setImageResource(R.drawable.ic_folder);
			holder.tvFileName.setTextColor(Color.GRAY);
		} else if (fileInfo.isImage()) { // image文件
			Bitmap bitmap = getLoacalBitmap(fileInfo.getFilePath());
			holder.imgFileIcon.setImageBitmap(reSize(bitmap, 85, 70));
			holder.tvFileName.setTextColor(Color.RED);
		} else { // 未知文件
			holder.imgFileIcon.setImageResource(R.drawable.ic_file_unknown);
			holder.tvFileName.setTextColor(Color.GRAY);
		}
		return view;
	}

	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis); // /把流转化为Bitmap图片

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private Bitmap reSize(Bitmap bitmap, int h, int w) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		int newWidth = w;
		int newHeight = h;
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,
				true);
		return newbm;
	}

	
}
