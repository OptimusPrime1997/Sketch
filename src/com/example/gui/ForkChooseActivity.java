package com.example.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ForkChooseActivity extends Activity {

	private GridView mGridView;
	// private View mBackView;
	private View mBtExit;
	private TextView mTvPath;

	private String myPicturePath; // sdcard 根路径
	private String mLastFilePath; // 当前显示的路径

	private ArrayList<FileInfo> mFileLists;
	private ImageChooserAdapter mAdatper;

	@SuppressLint("SdCardPath")
	protected void onCreate(Bundle savedInstanceState) {
		System.out.println("ssssssssssss");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image_choose);

		// myPicturePath = "/mnt/sdcard/screenShotPicture";
		// mBackView = findViewById(R.id.imgBackFolder);
		// mBackView.setOnClickListener(mClickListener);

		Intent intent = getIntent();
		ArrayList<String> paths = intent.getStringArrayListExtra("sList");
		ArrayList<FileInfo> fileInfoList = convertFileInfo(paths);
		mBtExit = findViewById(R.id.btExit);
		mBtExit.setOnClickListener(mClickListener);

		mTvPath = (TextView) findViewById(R.id.tvPath);

		mGridView = (GridView) findViewById(R.id.gvFileChooser);
		mGridView.setEmptyView(findViewById(R.id.tvEmptyHint));
		mGridView.setOnItemClickListener(mItemClickListener);
		if (fileInfoList == null) {
			Log.w("TAG-T8", "exit the activity");
			setResult(RESULT_CANCELED);
			finish();
		} else {
			Log.w("TAG-M1", fileInfoList.get(0).getFilePath());
			setGridViewAdapter(fileInfoList);
		}
	}

	/**
	 * convert paths list to fileInfo list
	 * 
	 * @param paths
	 * @return
	 */
	private ArrayList<FileInfo> convertFileInfo(ArrayList<String> paths) {
		// TODO Auto-generated method stub
		ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
		for (Iterator<String> t = paths.iterator(); t.hasNext();) {
			String temp = t.next();
			String tempName = getFileName(temp);
			if (tempName == null) {
				continue;
			} else {
				FileInfo info = new FileInfo(temp, tempName, false);
				fileList.add(info);
			}
		}
		if (fileList.size() > 0) {
			return fileList;
		}
		return null;
	}

	/**
	 * split string to get filename
	 * 
	 * @param temp
	 * @return
	 */
	private String getFileName(String temp) {
		// TODO Auto-generated method stub
		Log.w("TAG-T8", "path:" + temp);
		String[] list = temp.split("\\/");
		if (list.length > 1) {
			return list[list.length - 1];
		}
		return null;
	}

	// 配置适配器
	private void setGridViewAdapter(ArrayList<FileInfo> mFileLists) {
		updateFileItems(mFileLists);

		mAdatper = new ImageChooserAdapter(this, mFileLists);
		mGridView.setAdapter(mAdatper);
	}

	// 读取文件
	private void updateFileItems(ArrayList<FileInfo> infos) {
		mTvPath.setText("all fork images");
		mFileLists = infos;
		// mLastFilePath = filePath;
		//
		// if (mFileLists == null)
		// mFileLists = new ArrayList<FileInfo>();
		// if (!mFileLists.isEmpty())
		// mFileLists.clear();
		//
		// File[] files = folderScan(filePath);
		// if (files == null)
		// return;
		//
		// for (int i = 0; i < files.length; i++) {
		// if (files[i].isHidden()) // 不显示隐藏文件
		// continue;
		//
		// String fileAbsolutePath = files[i].getAbsolutePath();
		// String fileName = files[i].getName();
		// boolean isDirectory = false;
		// if (files[i].isDirectory()) {
		// isDirectory = true;
		// }
		// FileInfo fileInfo = new FileInfo(fileAbsolutePath, fileName,
		// isDirectory);
		// mFileLists.add(fileInfo);
		// }
		// When first enter , the object of mAdatper don't initialized
		if (mAdatper != null)
			mAdatper.notifyDataSetChanged(); // 重新刷新
	}

	// 根据路径更新数据，并且通知Adatper数据改变
	private void updateFileItems(String filePath) {
		mLastFilePath = filePath;
		mTvPath.setText(mLastFilePath);

		if (mFileLists == null)
			mFileLists = new ArrayList<FileInfo>();
		if (!mFileLists.isEmpty())
			mFileLists.clear();

		File[] files = folderScan(filePath);
		if (files == null)
			return;

		for (int i = 0; i < files.length; i++) {
			if (files[i].isHidden()) // 不显示隐藏文件
				continue;

			String fileAbsolutePath = files[i].getAbsolutePath();
			String fileName = files[i].getName();
			boolean isDirectory = false;
			if (files[i].isDirectory()) {
				isDirectory = true;
			}
			FileInfo fileInfo = new FileInfo(fileAbsolutePath, fileName, isDirectory);
			mFileLists.add(fileInfo);
		}
		// When first enter , the object of mAdatper don't initialized
		if (mAdatper != null)
			mAdatper.notifyDataSetChanged(); // 重新刷新
	}

	// 获得当前路径的所有文件
	private File[] folderScan(String path) {
		File file = new File(path);
		File[] files = file.listFiles();
		return files;
	}

	private View.OnClickListener mClickListener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			// case R.id.imgBackFolder:
			// backProcess();
			// break;
			case R.id.btExit:
				setResult(RESULT_CANCELED);
				Log.w("TAG-exit", "chooseItem is false");
				finish();
				break;
			default:
				break;
			}
		}
	};
	private AdapterView.OnItemClickListener mItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			Log.w("TAG-T5", "item clicked");
			FileInfo fileInfo = (FileInfo) (((ImageChooserAdapter) adapterView.getAdapter()).getItem(position));
			if (fileInfo.isDirectory()) // 点击项为文件夹, 显示该文件夹下所有文件
				updateFileItems(fileInfo.getFilePath());
			else if (fileInfo.isImage()) { // 是图片 ， 则将该路径通知给调用者
				Intent intent = new Intent();
//				intent.putExtra(MainActivity.EXTRA_FILE_CHOOSER, fileInfo.getFilePath());
				
				intent.putExtra(MainActivity.FORK_ITEM,fileInfo.getFilePath());
				
				
				// intent.putExtra("chooseItem", true);
				// MainActivity.isStartPage=false;
				// Log.w("TAG-select", "choose an image");

				setResult(RESULT_OK, intent);
				finish();
			} else { // 其他文件.....
				toast(getText(R.string.open_file_error_format));
			}
		}
	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			// backProcess();
			setResult(RESULT_CANCELED);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// // 返回上一层目录的操作
	// public void backProcess() {
	// // 判断当前路径是不是最初路径 ， 如果不是，则返回到上一层。
	// if (!mLastFilePath.equals(myPicturePath)) {
	// File thisFile = new File(mLastFilePath);
	// String parentFilePath = thisFile.getParent();
	// updateFileItems(parentFilePath);
	// } else { // 是最初路径 ，直接结束
	// setResult(RESULT_CANCELED);
	// finish();
	// }
	// }

	private void toast(CharSequence hint) {
		Toast.makeText(this, hint, Toast.LENGTH_SHORT).show();
	}

}
