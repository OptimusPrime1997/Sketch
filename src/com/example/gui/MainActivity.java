package com.example.gui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.dom4j.Document;
import org.dom4j.Element;

import sketch.gui.testing.AndroidNode;
import sketch.gui.testing.ParseXML;
import sketch.gui.testing.TType;

import weka.gui.LogWindow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnTouchListener {
	private ArrayList<PointF> graphics = new ArrayList<PointF>();// 所有点的集合
	private ArrayList<PointF> operationPoint = new ArrayList<PointF>();// 点击点的集合
	private ArrayList<PointF> endPoint = new ArrayList<PointF>();// 拖动结束点的集合
	private ArrayList<PointF> jointGraphics = new ArrayList<PointF>();// 两次及以上点的集合
	@SuppressLint("SdCardPath")
	private String sd = "/mnt/sdcard/screenShotPicture";
	private String input;
	private Logic logic = new Logic();

	private Resources r;
	private ImageView imageView;

	private Canvas canvas;
	private Paint paint;
	private Bitmap alterBitmap;
	private float downx = 0;
	private float downy = 0;
	private float upx = 0;
	private float upy = 0;
	private int clickCount = 0;
	private int stepCount = 0;// 绘制过程一共有四步，单击/双击/拖拽，绘制区域，forAll/resursiveForAll/forSome,选择target

	private gestureTrain gestureTrain;
	private combinationGestureTrain comGestureTrain;
	private IOOperation ioOperation = new IOOperation();
	private boolean isDrawed = false;// 有没有画过，在MotionEvent.ACTION_UP时置为true
	// private boolean isMove=false;//有没有移动，在MotionEvent.ACTION_Move时置为true
	// private boolean isPutDown=
	// false;//有没有按下过，在MotionEvent.ACTION_DOWN时置为true,用于判断单个点击事件

	private boolean isDrawArea = false;// 是否在绘制区域
	private File currentImage;// 当前图片
	private ParseXML parser;

	private File[] files;// 一个文件夹下所有文件
	private ArrayList<File> imageFiles = new ArrayList<File>();// 一个文件夹下所有的图片文件
	/*---- Modify By zhchuch ---*/
	private String imagePath;
	private PointF[] rect;
	private PointF[] drawedArea;
	private List<AndroidNode> nodes = new ArrayList<AndroidNode>();
	private TType test_type;
	private int countDrawArea = 0;

	private boolean myDrawFlag = false;
	private boolean accept_flag = false;

	private boolean input_flag = false;
	private boolean recogRect_flag = false;

	/**
	 * imagechooseactivity choose an item,it must be true
	 */
	private boolean chooseItem = false;
	/**
	 * when target_flag and target_menu_flag are all true,into menu case
	 */
	private boolean target_flag = false;
	public static boolean target_menu_flag = true;// true时显示target，false不显示,最开始时显示

	/**
	 * startPage and flag are false,into menu case
	 */
	public static boolean startPage_Flag = false;
	public static boolean isStartPage = true;

	private boolean fork_flag = false;
	/*-------------------------*/

	private String timeInput;
	private File currentWrittenFile;
	private Document currentDocument;
	private List<String> onePictureOPerations = null;
	private List<String> targetResult = null;
	private String combaOperation = null;

	private int operationId = 0;
	private List<Document> forkDocuments = new ArrayList<Document>(); // 用于记录fork之前的操作
	private Document recForAllDocument = null; // 用于记录recForAll之前的操作
	private boolean recFlag = false;

	private List<File> forkImages = new ArrayList<File>();
	private boolean isCompleted = false;

	private Intent imageChooseIntent;
	private final int REQUEST_CODE = 1;
	private final int FORK_REQUES_CODE = 2;

	private final int MENU_ITEM_COUNTER = Menu.FIRST;
	public static final String EXTRA_FILE_CHOOSER = "file_chooser";
	private Bundle bundle;

	private int leftBracketCount = 0;
	private int rightBracketCount = 0;
	private boolean hasOracle = false;
	private boolean isPointOperation = false;
	
	private boolean isInputOperation= false;

	public static final String FORK_ITEM = "fork_item";

	/**
	 * startactivity init the variables
	 */
	private void init() {
		isStartPage = true;
		startPage_Flag = false;

		myDrawFlag = false;
		countDrawArea = 0;
		accept_flag = false;
		recogRect_flag = false;
		input_flag = false;

		chooseItem = false;
		target_flag = false;
		target_menu_flag = true;// true时显示target，false不显示,最开始时显示

		fork_flag = false;
	}

	/**
	 * return to fork use it to init variable
	 */
	private void backHomeInit() {
		isStartPage = false;
		startPage_Flag = false;

		myDrawFlag = false;
		countDrawArea = 0;
		accept_flag = false;
		recogRect_flag = false;
		input_flag = false;

		chooseItem = false;
		target_flag = false;
		target_menu_flag = true;// true时显示target，false不显示,最开始时显示

		fork_flag = false;
	}

	private void setLanguage() {
		// 应用内配置语言
		Resources resources = getResources();// 获得res资源对象
		Configuration config = resources.getConfiguration();// 获得设置对象
		DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
		config.locale = Locale.ENGLISH; // 英文
		resources.updateConfiguration(config, dm);
	}

	private void putDownMenu() {
		Thread menuThread = new Thread() {
			public void run() {
				try {
					Instrumentation inst = new Instrumentation();
					inst.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
				} catch (Exception e) {
				}
			}
		};
		menuThread.start();
		try {
			menuThread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		menuThread.run();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.bundle = savedInstanceState;

		init();

		setLanguage();
		Intent intent = getIntent();

		// setContentView(R.layout.activity_welcome);
		// mHandler.postDelayed(new Runnable() {
		// @Override
		// public void run() {
		// startAndFinishThis();
		// }
		// }, 2500);

		setContentView(R.layout.activity_main);
		try {
			ioOperation.CreateMdr(sd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		onePictureOPerations = new ArrayList<String>();
		targetResult = new ArrayList<String>();

		imageView = (ImageView) findViewById(R.id.imageView1);

		currentWrittenFile = WriteXML.createTestFile();
		currentDocument = BuildDocument.getDocument();

		Log.w("TAG-T1", "start use com.example.gui.getSerializer");

		imageChooseIntent = new Intent(this, ImageChooseActivity.class);
		draw();
		gestureTrain = new gestureTrain();
		try {
			gestureTrain.Train(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		putDownMenu();
	}

	private void draw() {
		Bitmap bitmap = null;
		if (clickCount == 0) {
			r = this.getResources();
			bitmap = BitmapFactory.decodeResource(r, R.drawable.messi);// 只读,不能直接在bmp上画
			return;
		} else {
			try {
				// 读取uri所在的图片
				bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(currentImage));
			} catch (Exception e) {
				Log.e("[Android]", e.getMessage());
				Log.e("[Android]", "目录为：" + currentImage.getName());
				e.printStackTrace();
			}

		}
		

		alterBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
		// System.out.println(bitmap.getWidth() + "");
		canvas = new Canvas(alterBitmap);
		paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(10);
		Matrix matrix = new Matrix();
		canvas.drawBitmap(bitmap, matrix, paint);
		imageView.setDrawingCacheEnabled(true);

		imageView.setImageBitmap(alterBitmap);
		imageView.setOnTouchListener(this);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub

		int action = event.getAction();
		long lastIndex = 0;
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			downx = event.getX();
			downy = event.getY();
			graphics.add(new PointF(downx, downy));
			if (stepCount >= 2) {
				jointGraphics.add(new PointF(upx, upy));
			}
			stepCount++;
			break;
		case MotionEvent.ACTION_MOVE:
			// 路径画板
			upx = event.getX();
			upy = event.getY();
			canvas.drawLine(downx, downy, upx, upy, paint);
			imageView.invalidate();
			downx = upx;
			downy = upy;
			graphics.add(new PointF(upx, upy));// 记录点的坐标
			if (stepCount > 2) {
				jointGraphics.add(new PointF(upx, upy));
			}
			break;
		case MotionEvent.ACTION_UP:

			// 直线画板
			upx = event.getX();
			upy = event.getY();
			canvas.drawLine(downx, downy, upx, upy, paint);
			imageView.invalidate();// 刷新
			graphics.add(new PointF(upx, upy));
			isDrawed = true;
			if (stepCount > 2) {
				jointGraphics.add(new PointF(upx, upy));
			}
			/*
			 * 无论是否在绘制区域都要先创建文件目录
			 */
			if (clickCount > 0) {
				// 创建文件保存目录
				if (getPath() != null) {
					String dirPath = getDirName(getPath()) + "temp";
					try {
						ioOperation.CreateMdr(dirPath);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			if (isDrawArea) { // Selection Area

				String filePath = getDirName(getPath()) + "temp" + "/" + getImageName(getPath()) + ".txt";
				rect = logic.getExteriorRect(graphics); // 计算出矩形的4个点
				if (myDrawFlag) {
					drawedArea = rect;
					myDrawFlag = false;
				}
				ioOperation.recordAreaInfo(filePath, graphics, rect);

				/*-------------- Modify by zhchuch -----------------*/
				countDrawArea++;

				if (test_type == TType.ATOMIC) {
					System.out.println("GenerateSingState pos[draw]");

					// modelBuilder.generateSingleState("s"+modelBuilder.state_counter,
					// getImageName(imagePath), 1);
					/* 使用对话框，让用户输入他自己所要想得到的 期待输出结果(Expected-Output) */
					final TextView tv = new EditText(this);
					Builder inputDailog = new AlertDialog.Builder(this);
					inputDailog.setTitle("Expected output:");
					inputDailog.setIcon(android.R.drawable.ic_dialog_info);
					inputDailog.setView(tv).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							input = tv.getText().toString();
							hasOracle = true;
							targetResult.add("expectedValue;" + input + ";" + String.valueOf(rect[0].x) + ","
									+ String.valueOf(rect[0].y) + "," + String.valueOf(rect[3].x) + ","
									+ String.valueOf(rect[3].y));
						}
					});
					inputDailog.setNegativeButton("Cancel", null);
					inputDailog.show();

					countDrawArea = 0;

				}

				if (test_type == TType.JOINT) {
					// 为每一个的 accept 状态，生成 单终止状态
					System.out.println("JOINT [AccpetState Generating...]");
					/* 使用对话框，让用户输入他自己所要想得到的 期待输出结果(Expected-Output) */
					final TextView tv = new EditText(this);
					Builder inputDialog = new AlertDialog.Builder(this);
					inputDialog.setTitle("Expected output:");
					inputDialog.setIcon(android.R.drawable.ic_dialog_info);
					inputDialog.setView(tv);
					inputDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							input = tv.getText().toString();
							hasOracle = true;
							targetResult.add("expectedValue;" + input + ";" + String.valueOf(rect[0].x) + ","
									+ String.valueOf(rect[0].y) + "," + String.valueOf(rect[3].x) + ","
									+ String.valueOf(rect[3].y));

							System.out.println("Your Input[pos-JOINT]: " + input);
						}
					});
					inputDialog.setNegativeButton("取消", null);
					inputDialog.show();
					accept_flag = true;
				}

			}

			// 没有draw area,只是单独的手势
			else {

				float[] coordinate = logic.calCoordinate(graphics);// 单击时的中心点
				canvas.drawCircle(coordinate[0], coordinate[1], 10, paint);

				PointF center = new PointF(coordinate[0], coordinate[1]);
				operationPoint.add(center);
				int endPointIndex = logic.calEndPoint(graphics);
				endPoint.add(graphics.get(endPointIndex));
				ArrayList<String> recordList = logic.calRecordList(graphics);// 获得用于训练的十五个点的坐标
				if (clickCount > 0)

				{
					if (getPath() != null) {
						String path = getDirName(getPath()) + "temp" + "/" + getImageName(getPath()) + ".arff";
						ioOperation.recordPoint(path, recordList);
					}
				}

				if (isDrawed)

				{
					/*--------- After there, imply the identify action function. ---------*/
					// savePicture();
					String operation = "";
					if (getPath() != null) {
						Log.w("TAG-C", "path:" + getPath());
						String path = getDirName(getPath()) + "temp" + "/" + getImageName(getPath()) + ".arff";
						double[] result;
						result = gestureTrain.TrainResult(path);
						System.out.println("=============");
						System.out.println(result.length);
						double resultNum = -1;
						for (int i = 0; i < result.length; i++) {
							System.out.println(result[i]);
						}
						for (int i = result.length-1; i >= 0 ; i--) {
							if (result[i] != -1) {
								resultNum = result[i];
								break;
							}
						}
						System.out.println(resultNum);
						/*
						 * isDrawed为true并且result等于-1时说明只绘制了区域，并没有单击双击拖动这三种操作
						 */
						System.out.println(operationPoint.get(0).x);
						System.out.println(operationPoint.get(0).y);

						if (result[0] != -1) {

							for (int i = 0; i < operationPoint.size(); i++) {

								// 如果是没有识别的动作坐标会设为0
								if (operationPoint.get(i).x != 0 && operationPoint.get(i).y != 0) {
									System.out.println("wwwwwww" + resultNum);
									if (resultNum == 0) { // click
										
										if (isInputOperation) {
											operation = "input;" + "\r\n" + format(operationPoint.get(i).x) + ","
													+ format(operationPoint.get(i).y) + "\r\n";
											final TextView tv = new EditText(this);
											Builder inputDailog = new AlertDialog.Builder(this);
											inputDailog.setTitle("Expected input:");
											inputDailog.setIcon(android.R.drawable.ic_dialog_info);
											inputDailog.setView(tv).setPositiveButton("确定", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface arg0, int arg1) {
													// TODO Auto-generated method stub
													input = tv.getText().toString();													
												}
											});
											inputDailog.setNegativeButton("取消", null);
											inputDailog.show();
											
											System.out.println("sosossos"+operation);
											isInputOperation = false;
											onePictureOPerations.add(operation);
											break;							
										}
										operation = "click;" + "\r\n" + format(operationPoint.get(i).x) + ","
												+ format(operationPoint.get(i).y) + "\r\n";
										if (isPointOperation) {
											targetResult.add("point;" + format(operationPoint.get(i).x) + ","
													+ format(operationPoint.get(i).y));
											break;
										}
										onePictureOPerations.add(operation);

									} else if (resultNum == 1) { // longclick
										if (isInputOperation) {
											operation = "input;" + "\r\n" + format(operationPoint.get(i).x) + ","
													+ format(operationPoint.get(i).y) + "\r\n";
											final TextView tv = new EditText(this);
											Builder inputDailog = new AlertDialog.Builder(this);
											inputDailog.setTitle("Expected input:");
											inputDailog.setIcon(android.R.drawable.ic_dialog_info);
											inputDailog.setView(tv).setPositiveButton("确定", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface arg0, int arg1) {
													// TODO Auto-generated method stub
													input = tv.getText().toString();
													System.out.println("--->>"+input);
												}
											});
											inputDailog.setNegativeButton("取消", null);
											inputDailog.show();
											//operation += input;
											System.out.println("sosossos"+operation);
											isInputOperation = false;
											onePictureOPerations.add(operation);
											break;							
										}
										operation = "lClick;" + "\r\n" + format(operationPoint.get(i).x) + ","
												+ format(operationPoint.get(i).y) + "\r\n";
										if (isPointOperation) {
											targetResult.add("point;" + format(operationPoint.get(i).x) + ","
													+ format(operationPoint.get(i).y));
											break;
										}
										onePictureOPerations.add(operation);
									} else if (resultNum == 2) { // Drag

										operation = "drag;" + "\r\n" + format(operationPoint.get(i).x) + ","
												+ format(operationPoint.get(i).y) + "," + format(endPoint.get(i).x)
												+ "," + format(endPoint.get(i).y) + "\r\n";
										if (isPointOperation) {
											break;
										}
										onePictureOPerations.add(operation);
									}

								} else {

									if (i == (operationPoint.size() - 1)) {
										final Toast toast = Toast.makeText(this, "unidentified draw",
												Toast.LENGTH_SHORT);
										toast.show();
										new Handler().postDelayed(new Runnable() {

											@Override
											public void run() {
												// TODO Auto-generated method
												// stub
												toast.cancel();
											}
										}, 800);

										onePictureOPerations.clear();
										graphics.clear();
										operationPoint.clear();
										stepCount = 0;
										draw();
									}
								}

							}
							// if (operationPoint.size() > 0) {
							// Log.w("TAG-p", "into unrecognize case,added:" +
							// (graphics.size() - lastIndex));
							// if ((operationPoint.get(operationPoint.size() -
							// 1).toString())
							// .indexOf("PointF(0.0, 0.0)") != -1) {
							// if (graphics.size() - lastIndex < 10) {
							// Log.w("TAG-p1", "putDownMenu");
							// Log.w("TAG-size",
							// onePictureOPerations.size()+"");
							// //draw();
							// //graphics.clear();
							// //operationPoint.clear();
							// putDownMenu();
							//
							//
							// }
							// }
							// }
						}
					} else {
						// Log.w("TAG-C", "gePath() is null,pointSize:" +
						// operationPoint.size());
						// if (operationPoint.size() > 0) {
						// Log.w("TAG-P", "into unrecognize case,added:" +
						// (graphics.size() - lastIndex));
						// if ((operationPoint.get(operationPoint.size() -
						// 1).toString())
						// .indexOf("PointF(0.0, 0.0)") != -1) {
						// if (graphics.size() - lastIndex < 10) {
						// Log.w("TAG-P1", "putDownMenu");
						// //draw();
						// //graphics.clear();
						// //operationPoint.clear();
						// Log.w("TAG-size", onePictureOPerations.size()+"");
						// putDownMenu();
						//
						//
						// }
						// }
						// }
					}
					/*--------- Before there, imply the identify action function. ---------*/
				}

			}
			//graphics = new ArrayList<PointF>();

			//lastIndex = graphics.size();
			graphics.clear();
			operationPoint.clear();
			break;

		default:
			break;
		}

		return true;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_ITEM_COUNTER, 0, "choose").setIcon(R.drawable.menu_choose);
		menu.add(0, MENU_ITEM_COUNTER + 9, 1, "exit").setIcon(R.drawable.menu_exit);
		// menu.add(0, MENU_ITEM_COUNTER + 1, 0, "next");
		// menu.add(0, MENU_ITEM_COUNTER + 2, 0, "save");
		// menu.add(0, MENU_ITEM_COUNTER + 3, 0, "clear");
		// menu.add(0, MENU_ITEM_COUNTER + 4, 0, "draw Area");
		//
		// SubMenu sub1=menu.addSubMenu("more");
		// sub1.add(1, MENU_ITEM_COUNTER + 5, 0, "target");
		// // menu.add(0, MENU_ITEM_COUNTER + 6, 0, "enter text");
		// sub1.add(1, MENU_ITEM_COUNTER + 7, 0, "timer");
		// sub1.add(1, MENU_ITEM_COUNTER + 8, 0, "fork");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.w("TAG-T0", "onPrePareOptionsMenu:isStartPage:" + isStartPage + ",startPage_Flag:" + startPage_Flag
				+ "---target_flag:" + target_flag + ",target_menu_flag:" + target_menu_flag);

		if (isStartPage == false && startPage_Flag == false) {
			Log.w("TAG-T1", "isStartPage:" + isStartPage + ",startPage_Flag:" + startPage_Flag);
			menu.clear();
			menu.add(0, MENU_ITEM_COUNTER, 0, "choose").setIcon(R.drawable.menu_choose);
			menu.add(0, MENU_ITEM_COUNTER + 9, 1, "exit").setIcon(R.drawable.menu_exit);
			menu.add(0, MENU_ITEM_COUNTER + 1, 2, "next").setIcon(R.drawable.menu_next);
			menu.add(0, MENU_ITEM_COUNTER + 2, 3, "save").setIcon(R.drawable.menu_save);
			menu.add(0, MENU_ITEM_COUNTER + 3, 4, "clear").setIcon(R.drawable.menu_clear);

			SubMenu sub1 = menu.addSubMenu(0, MENU_ITEM_COUNTER + 10, 5, "more").setIcon(R.drawable.menu_more);
			sub1.clear();
			sub1.add(1, MENU_ITEM_COUNTER + 4, 6, "draw Area").setIcon(R.drawable.menu_area);
			sub1.add(1, MENU_ITEM_COUNTER + 5, 7, "target").setIcon(R.drawable.menu_target);
			// menu.add(0, MENU_ITEM_COUNTER + 6, 0, "enter text");
			sub1.add(1, MENU_ITEM_COUNTER + 7, 8, "timer").setIcon(R.drawable.menu_timer);
			sub1.add(1, MENU_ITEM_COUNTER + 8, 9, "fork").setIcon(R.drawable.menu_fork);
			sub1.add(1, MENU_ITEM_COUNTER + 17, 10, "input");
			startPage_Flag = true;
		}
		if (target_flag == true && target_menu_flag == true) {
			Log.w("TAG-T2", "target_flag:" + target_flag + ",target_menu_flag:" + target_menu_flag);
			SubMenu sub2 = menu.findItem(MENU_ITEM_COUNTER + 10).getSubMenu();
			sub2.removeItem(MENU_ITEM_COUNTER + 5);
			sub2.removeItem(MENU_ITEM_COUNTER + 7);
			sub2.removeItem(MENU_ITEM_COUNTER + 8);

			sub2.add(2, MENU_ITEM_COUNTER + 11, 11, "and").setIcon(R.drawable.and);
			sub2.add(2, MENU_ITEM_COUNTER + 12, 12, "or").setIcon(R.drawable.or);
			sub2.add(2, MENU_ITEM_COUNTER + 13, 13, "invert").setIcon(R.drawable.invert);

			sub2.add(2, MENU_ITEM_COUNTER + 16, 14, "point").setIcon(R.drawable.invert);

			sub2.add(2, MENU_ITEM_COUNTER + 14, 15, "left bracket").setIcon(R.drawable.menu_left_bracket);
			sub2.add(2, MENU_ITEM_COUNTER + 15, 16, "right bracket").setIcon(R.drawable.menu_right_bracket);
			// menu.removeItem(MENU_ITEM_COUNTER + 10);
			// menu.add(1, MENU_ITEM_COUNTER + 4, 6, "draw Area");
			target_menu_flag = false;
		}

		return true;
	}

	/**
	 * exit app confirm dialog
	 */
	protected void exitDialog() {
		AlertDialog.Builder builder = new Builder(MainActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		builder.setMessage("Exit now?");
		builder.setTitle("Promption");
		builder.setPositiveButton("Confirm", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				MainActivity.this.finish();
			}
		});
		builder.setNegativeButton("Cancel", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
//		CustomDialog.Builder builder = new CustomDialog.Builder(this);  
//		builder.setMessage("Exit now?");  
//		builder.setTitle("Prompt");  
//		builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {  
//		    public void onClick(DialogInterface dialog, int which) {  
//		        dialog.dismiss();  
//		        MainActivity.this.finish();
//		    }  
//		});  
//		  
//		builder.setNegativeButton("Cancel",  
//		        new android.content.DialogInterface.OnClickListener() {  
//		            public void onClick(DialogInterface dialog, int which) {  
//		                dialog.dismiss();  
//		            }  
//		        });  
//		builder.create().show();
	}

	/**
	 * handle target->save without fork tag
	 */
	protected void saveNoForkDialog() {
		AlertDialog.Builder builder = new Builder(MainActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		builder.setMessage("Save success!");
		builder.setTitle("Choose Next Step");
		builder.setPositiveButton("Back to home", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				backHome();
			}
		});
		builder.setNegativeButton("Exit", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				MainActivity.this.finish();
			}
		});
		builder.create().show();
	}
	
		/**
		 * handle target->save with fork tag
		 */
	protected void saveForkDialog() {
		AlertDialog.Builder builder = new Builder(MainActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		builder.setMessage("This path savesed!");
		builder.setTitle("Choose Dialog");
		builder.setPositiveButton("Back to fork", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// handleFork();
				// PopupMenu popupMenu=new PopupMenu(this,);
				Intent intent = new Intent(MainActivity.this, ForkChooseActivity.class);
				ArrayList<String> lists = new ArrayList<String>();
				for (int i = 0; i < forkImages.size(); i++) {
					lists.add(forkImages.get(i).getPath());
				}
				intent.putExtra("sList", lists);
				startActivityForResult(intent, FORK_REQUES_CODE);
			}
		});
		builder.setNegativeButton("Exit", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				MainActivity.this.finish();
			}
		});
		builder.setNeutralButton("Back to home", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				backHome();
			}
		});
		builder.create().show();
	}

	/**
	 * back to home page function
	 */
	public void backHome() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(MainActivity.this, MainActivity.class);
		// init();
		// isStartPage=false;
		startActivity(intent);
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ITEM_COUNTER: // choose
			onePictureOPerations.clear();
			graphics.clear();
			operationPoint.clear();
			stepCount = 0;
			clickCount++;
			// 判断sd卡是否存在，并且是否具有读写权限
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				startActivityForResult(imageChooseIntent, REQUEST_CODE);
			}
			// putDownMenu();
			break;
		case MENU_ITEM_COUNTER + 1: // next
			if (clickCount > 0) {
				nextClickOperation();
			}

			break;
		case MENU_ITEM_COUNTER + 2: // save
			if (stepCount > 2) {
				// 先获取所有的 joint 信息，并放入训练器进行识别
				ArrayList<String> recordList = logic.calRecordList(jointGraphics);
				String path = getDirName(getPath()) + "temp" + "/combinationGesture" + getImageName(getPath())
						+ ".arff";
				ioOperation.recordJointPoint(path, recordList);
				comGestureTrain = new combinationGestureTrain();
				try {
					comGestureTrain.Train(this);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				double[] result = comGestureTrain.TrainResult(path);

				for (int i = 0; i < result.length; i++) {
					if (result[i] == -1.00)
						break;
					if (result[i] == 0.00) {
						combaOperation = "FORALL;" + String.valueOf(drawedArea[0].x) + ","
								+ String.valueOf(drawedArea[0].y) + "," + String.valueOf(drawedArea[3].x) + ","
								+ String.valueOf(drawedArea[3].y);
						System.out.println("------------" + combaOperation);

					}
					if (result[i] == 1.00) {
						combaOperation = "REC_FORALL;" + String.valueOf(drawedArea[0].x) + ","
								+ String.valueOf(drawedArea[0].y) + "," + String.valueOf(drawedArea[3].x) + ","
								+ String.valueOf(drawedArea[3].y);
						System.out.println("------------" + combaOperation);

					}
					if (result[i] == 2.00) {
						combaOperation = "EXIST;" + String.valueOf(drawedArea[0].x) + ","
								+ String.valueOf(drawedArea[0].y) + "," + String.valueOf(drawedArea[3].x) + ","
								+ String.valueOf(drawedArea[3].y);
						System.out.println("------------" + combaOperation);

					}
				}
			}

			Element lastElement = null; // 用lastElement保存上一个操作，用来保存时延
			String path = currentImage.getPath();
			Element stateElement = CreateElement.createState(currentDocument.getRootElement(), path);
			for (int i = 0; i < onePictureOPerations.size(); i++) {
				String temp = onePictureOPerations.get(i);
				System.out.println("->>>>>" + temp);

				String[] result = temp.split("\r\n");
				String[] operations = result[0].split(";");
				String[] points = result[1].split(",");

				if (operations[0].equals("delayTime")) {
					String time = result[1];
					BuildDocument.addAttribute(lastElement, "delayTime", time);
					continue;
				}

				Element operationElement = CreateElement.createOPeration(currentDocument.getRootElement(), operationId);
				operationId++;
				lastElement = operationElement;
				BuildDocument.addAttribute(operationElement, "delayTime", "0");

				if (operations[0].equals("click") || operations[0].equals("lClick")) {
					System.out.println("come here");
					CreateElement.addSubInfo(operationElement, operations[0], points, parser);

					if (combaOperation != null) {
						String[] combaResults = combaOperation.split(";");
						String[] combaPoints = combaResults[1].split(",");
						operationElement.remove(operationElement.element("singlePoint"));
						if (combaResults[0].equals("FORALL") || combaResults[0].equals("EXIST")) {
							CreateElement.addCombaInfo(operationElement, "multiComponent", combaPoints, null, parser);
						}
						if (combaResults[0].equals("REC_FORALL")) {
							// recForAllDocument = (Document)
							// currentDocument.clone();
							PointF[] twoPoints = new PointF[2];
							twoPoints[0] = drawedArea[0];
							twoPoints[1] = drawedArea[3];
							System.out.println(twoPoints[0].x);
							System.out.println(twoPoints[1].x);
							nodes = parser.findWidgetByRect(twoPoints);

							CreateElement.addVirtual(operationElement, nodes.get(0));
						}
					}

				} else if (operations[0].equals("drag")) {

					CreateElement.addSubInfo(operationElement, operations[0], points, parser);

					if (combaOperation != null) {
						String[] combaResults = combaOperation.split(";");
						String[] combaPoints = combaResults[1].split(",");
						if (combaResults[0].equals("FORALL") || combaResults[0].equals("EXIST")) {
							operationElement.remove(operationElement.element("doublePoint"));
							CreateElement.addCombaInfo(operationElement, "point_to_area", combaPoints, points, parser);

						}
						if (combaResults[0].equals("REC_FORALL")) {

						}
					}

				} else if ("input".equals(operations[0])) {
					System.out.println("get input.....");
					CreateElement.addSubInfo(operationElement, operations[0], points, parser);
					Element inputElement = BuildDocument.addElement(operationElement, "input");
					BuildDocument.addText(inputElement, input);
				}
			}

			// 写入结果
			for (int j = 0; j < targetResult.size(); j++) {

				String temp = targetResult.get(j);
				String[] results = temp.split(";");

				if (results.length > 1) {
					if (!results[0].equals("point")) {
						String[] points = results[2].split(",");
						CreateElement.setEndState(stateElement, "single_component", results[1], points, parser);
						recFlag = true;
					} else {
						String[] points = results[1].split(",");
						Bitmap bitmap = null;
						try {
							bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
									Uri.fromFile(currentImage));

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						int color = bitmap.getPixel((int) Double.parseDouble(points[0]),
								(int) Double.parseDouble(points[1]));
						CreateElement.addRGB(stateElement, points, color);
					}

				} else if (results.length == 1) {
					BuildDocument.addElement(stateElement, results[0]);
				}

			}

			onePictureOPerations.clear();
			targetResult.clear();
			combaOperation = null;
			isDrawArea = false;
			graphics.clear();
			operationPoint.clear();
			WriteXML.writeObject(currentDocument, currentWrittenFile.getPath());
			System.out.println("--------+" + nodes.size());

			// rec_forall
			Element recOperation = null;
			if (nodes.size() > 0 && recFlag) {
				System.out.println("recrec....");
				Element root = currentDocument.getRootElement();
				@SuppressWarnings("unchecked")
				List<Element> operations = root.elements("Operation");
				for (Element element : operations) {
					if (element.attribute("isVirutal") != null) {
						recOperation = element;
						System.out.println("```````````" + element);
						break;
					}
				}
				for (int i = 1; i < nodes.size(); i++) {
					System.out.println("rec2rec2....");
					CreateElement.replaceElement(recOperation, nodes.get(i));
					File recWrittenFile = WriteXML.createTestFileForRec(i);
					System.out.println(recWrittenFile.getPath());
					WriteXML.writeObject(currentDocument, recWrittenFile.getPath());
				}
			}
			Log.w("TAG-Q10", "target_flag:" + target_flag + "fork_flag:" + fork_flag);
			if (target_flag) {
				if (fork_flag) {
					saveForkDialog();
				} else {
					saveNoForkDialog();
				}
			}
			break;
		case MENU_ITEM_COUNTER + 3: // clear
			onePictureOPerations.clear();
			graphics.clear();
			operationPoint.clear();
			stepCount = 0;
			if (isDrawArea) {
				myDrawFlag = true;
			}
			draw();

			// imagePath = data.getStringExtra(EXTRA_FILE_CHOOSER);
			// Log.w("TAG-P", "onActivityResult:print the uix androidNode");
			// /*--------------- modify by zhchuch ----------*/
			// ParseXML parser = getParserByImagePath(imagePath);

			break;
		case MENU_ITEM_COUNTER + 4: // drawArea
			// onePictureOPerations.add("draw area");
			myDrawFlag = true;
			isDrawArea = true;
			recogRect_flag = true;
			break;
		case MENU_ITEM_COUNTER + 5: // target
			/*-------------- Modify by zhchuch -----------------*/
			// WriteXML.writeObject("targe/r/n", currentWrittenFile.getPath());
			onePictureOPerations.clear();
			System.out.println("After click Target...");
			if (countDrawArea == 0)
				test_type = TType.ATOMIC;
			else
				test_type = TType.JOINT;

			System.out.println("GenerateSingState pos[target]");

			/*--------------------------------------------------*/
			isDrawArea = false;
			jointGraphics = new ArrayList<PointF>();
			stepCount = 0;
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				startActivityForResult(imageChooseIntent, REQUEST_CODE);
				Log.w("TAG-Q9", "case fork chooseItem:" + chooseItem);
				if (chooseItem == true) {
					Log.w("TAG-Q9", "change the target_flag to true");
					target_flag = true;
					chooseItem = false;
				}
			}
			break;
		case MENU_ITEM_COUNTER + 6:// enter text
			// getUserInputString();
			/* 这里涉及到 多线程，主线程要等待子线程输入。 */
			final TextView tv = new EditText(this);
			Builder expectDialog = new AlertDialog.Builder(this);
			expectDialog.setTitle("Expected output:").setIcon(android.R.drawable.ic_dialog_info);
			expectDialog.setView(tv);
			expectDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					input = tv.getText().toString();
					hasOracle = true;
					// WriteXML.writeObject("Expected output:"+input+"\r\r\n",
					// currentWrittenFile.getPath());
					// onePictureOPerations.add("expectedValue;"+"\r\n"+input+"\r\n");
					input_flag = true;
					System.out.println("Your Input[pos-menu-selected]: " + input);
				}
			});
			expectDialog.setNegativeButton("Cancel", null).show();
			break;

		case MENU_ITEM_COUNTER + 7: // timer
			final TextView timeText = new EditText(this);
			Builder timeDialog = new AlertDialog.Builder(this);
			timeDialog.setTitle("Wait time");
			timeDialog.setIcon(android.R.drawable.ic_dialog_info);
			timeDialog.setView(timeText);
			timeDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					timeInput = timeText.getText().toString();
					// WriteXML.writeObject("Delay time:"+timeInput+"\r\n",
					// currentWrittenFile.getPath());
					onePictureOPerations.add("delayTime;" + "\r\n" + timeInput + "\r\n");
					System.out.println("Your Input[pos-menu-selected]: " + timeInput);
				}
			});
			timeDialog.setNegativeButton("Cancel", null);
			timeDialog.show();
			break;

		case MENU_ITEM_COUNTER + 8: // fork, fork到之前save的那张的图片
			handleFork();
			break;
		case MENU_ITEM_COUNTER + 9:// exit
			exitDialog();
			// Intent intent = new Intent(MainActivity.this, SubMenuTest.class);
			// startActivity(intent);
			break;
		case MENU_ITEM_COUNTER + 11:// and
			if (!hasOracle) {
				PromptDialog.showPromptDialog(this, "You should have at least one Oracle before use this operation!");
				break;
			}
			targetResult.add("and;");
			startActivityForResult(imageChooseIntent, REQUEST_CODE);
			break;
		case MENU_ITEM_COUNTER + 12:// or
			if (!hasOracle) {
				PromptDialog.showPromptDialog(this, "You should have at least one Oracle before use this operation!");
				break;
			}
			targetResult.add("or;");
			startActivityForResult(imageChooseIntent, REQUEST_CODE);
			break;
		case MENU_ITEM_COUNTER + 13:// invert( not operation)

			targetResult.add("not;");
			startActivityForResult(imageChooseIntent, REQUEST_CODE);
			break;

		case MENU_ITEM_COUNTER + 14:// left bracket
			leftBracketCount++;
			targetResult.add("leftBracket;");
			startActivityForResult(imageChooseIntent, REQUEST_CODE);
			break;
		case MENU_ITEM_COUNTER + 15:// right bracket
			if (leftBracketCount <= rightBracketCount) {
				PromptDialog.showPromptDialog(this,
						"You should have at least one left bracket before use this operation!");
				break;
			}
			targetResult.add("rightBrack;");
			rightBracketCount++;
			break;
		case MENU_ITEM_COUNTER + 16:// point
			isDrawArea = false;
			stepCount = 0;
			graphics.clear();
			operationPoint.clear();
			isPointOperation = true;
			draw();
			break;
			
		case MENU_ITEM_COUNTER + 17:   //input
			isInputOperation = true;		
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void handleFork() {

		forkImages.add(currentImage);
		Document temp = (Document) currentDocument.clone();
		forkDocuments.add(temp);
		fork_flag = true;
		Toast.makeText(this, "fork success", Toast.LENGTH_SHORT).show();

	}

	public void returnToFork(String forkedPath) {
		onePictureOPerations.clear();
		int queryIndex = 0;
		for (int i = forkImages.size() - 1; i >= 0; i--) {
			if (forkImages.get(i).getPath().equals(forkedPath)) {
				queryIndex = i;
				break;
			}
		}
		File forkImage = forkImages.get(queryIndex);
		forkImages.remove(queryIndex);
		currentDocument = (Document) forkDocuments.get(queryIndex).clone();
		forkDocuments.remove(queryIndex);
		currentWrittenFile = WriteXML.createTestFile();
		// isStartPage = true;
		// startPage_Flag = false;
		// target_flag = false;
		// target_menu_flag = true;
		// isDrawArea = false;

		backHomeInit();
		Log.w("TAG-T10", "---init the variable");

		int forkIndex = imageFiles.indexOf(forkImage);
		currentImage = imageFiles.get(forkIndex);
		Uri imageUri = Uri.fromFile(currentImage);

		imagePath = imageUri.toString();
		imageView.setImageURI(imageUri);
		parser = getParserByImagePath(imageUri.toString());

		operationPoint = new ArrayList<PointF>();
		draw();
	}

	/**
	 * 找到 可处理当前测试界面(ImageName)的 parser
	 */
	private ParseXML getParserByImagePath(String ImagePath) {
		Log.w("TAG-P9", "ImagePath = " + ImagePath);
		String[] temp = ImagePath.split("/");
		String app_name = temp[temp.length - 2];
		String ImageName = temp[temp.length - 1];
		String testXMLName = "screen_data/" + app_name + "/" + ImageName.split("\\.")[0] + ".uix";
		Log.w("TAG-P9", "testXMLName :" + testXMLName);

		// InputStream is_xml = null;
		// try {
		// is_xml = this.getAssets().open(testXMLName);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// Log.w("TAG-P9", "inputStream:not fund the file");
		// }

		// String path="/mnt/sdcard/assets/screen_data/MyScreenShot/1.uix";
		if (ImagePath.contains("file://")) {
			ImagePath = ImagePath.substring(7);
		}
		String[] list = ImagePath.split("\\.");
		String path = "/mnt/sdcard/screenShotPicture/MyScreenShot/1.uix";
		if (list.length > 1) {
			path = list[0] + ".uix";
		}
		Log.w("TAG-Pn9", "path:" + path);
		InputStream input = null;
		try {
			input = new BufferedInputStream(new FileInputStream(new File(path)));
			Log.w("TAG-P9", "inputTest=fund the file success!");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.w("TAG-P9", "inputTest=not fund the file");
		}
		ParseXML parser = new ParseXML(input);
		// parser.parse(is_xml);
		return parser;
	}

	private String getDirName(String image_Path) {
		// TODO Auto-generated method stub
		String[] temp = image_Path.split("/");
		String path = temp[0];
		for (int i = 1; i < temp.length - 1; i++) {
			path = path + "/" + temp[i];
		}
		return path;
	}

	private String getImageName(String image_Path) {
		// TODO Auto-generated method stub
		String[] temp = image_Path.split("/");
		String name = temp[temp.length - 1];
		// split(".")要转义
		// String[] nameTemp = name.split("\\.");
		return name;// nameTemp[0];
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		onePictureOPerations.clear();
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			if (data != null) {
				// uri=Uri.parse("content://media/external/images/media/39");
				imagePath = data.getStringExtra(EXTRA_FILE_CHOOSER);

				currentImage = new File(imagePath);

				System.out.println(currentImage.toString());
				Uri imageUri = Uri.fromFile(currentImage);

				parser = getParserByImagePath(imageUri.toString());

				imageView.setImageURI(imageUri);
				File dir = new File(getDirName(imagePath));
				files = dir.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (!files[i].isDirectory()) {
						String fileName = files[i].getName();
						String fileType = fileName.substring(fileName.indexOf("."));
						if (isImage(fileType)) {
							imageFiles.add(files[i]);
						}
					}
				}
				Log.v("TAG-Q2", "mainactivity chooseItem:" + data.getBooleanExtra("chooseItem", false));
				if (data.getBooleanExtra("chooseItem", false) == true) {
					chooseItem = true;
				} else {
					chooseItem = false;
				}
				/*--------------- modify by zhchuch ----------*/

				// tempMG = new ModelBuilder(modelBuilder.cur_parser);
				/*-------------------------------------------*/
				draw();
			} else {
				return;
			}
		} else if (resultCode == RESULT_OK && requestCode == FORK_REQUES_CODE) {
			if (data != null) {
				String choosePath = data.getStringExtra(FORK_ITEM);
				System.out.println("this is the returned..." + choosePath);
				returnToFork(choosePath);
			}
		}

	}// OnActivityResult

	@SuppressLint("DefaultLocale")
	private boolean isImage(String fileType) {
		// TODO Auto-generated method stub
		String temp = fileType.toLowerCase();
		if (temp.equals(".jpg") || temp.equals(".png") || temp.equals("jpeg")) {
			return true;
		}
		return false;
	}

	/*
	 * 根据File获得文件的绝对路径
	 */
	private String getPath() {
		// TODO Auto-generated method stub
		String CanonicalPath = null;
		if (currentImage != null) {
			try {
				CanonicalPath = currentImage.getCanonicalPath();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return CanonicalPath;
	}

	private void nextClickOperation() {

		/*
		 * 点击Next按钮的时候，先对 之前的图片上的操作 生成一个状态
		 */
		// if (target_flag&&onePictureOPerations.size()!=0) {
		// WriteXML.writeObject(currentImage.getPath(),
		// currentWrittenFile.getPath());
		// for (int i = 0; i < onePictureOPerations.size(); i++) {
		// WriteXML.writeObject(onePictureOPerations.get(i),
		// currentWrittenFile.getPath());
		// }
		//
		// }
		stepCount = 0;
		graphics.clear();
		operationPoint.clear();
		onePictureOPerations.clear();
		int currentIndex = imageFiles.indexOf(currentImage);// 获得所选图片在这个文件夹中的序号
		/*
		 * 点击next时从currentImage之后的一张图片开始，之前的不再出现
		 */
		if (currentIndex == imageFiles.size() - 1) {
			Toast.makeText(this, "This is already last picture!", Toast.LENGTH_SHORT).show();
			// draw();
		} else {
			Log.w("TAG-Pn2", "test get android");
			currentImage = imageFiles.get(currentIndex + 1);
			Uri imageUri = Uri.fromFile(currentImage);
			/*------------ modify by zhchuch ----------*/
			imagePath = imageUri.toString();
			Log.w("TAG-Pn2", "nextClick:print the uix androidNode");
			parser = getParserByImagePath(imagePath);

			// AndroidNode node = parser.findWidgetByLocation(20, 270);
			// if (node != null) {
			// Log.w("TAG-Pn2", "found:" + node.getPrintString());
			// }else{
			// Log.w("TAG-Pn2", "not found");
			// Toast.makeText(this, "not found the
			// node",Toast.LENGTH_SHORT).show();
			// }
			// Log.w("TAG-Pn2", "get android node lat");

			/*
			 * PointF[] points=new PointF[2]; points[0]=new PointF(5,180);
			 * points[1]=new PointF(540,270); List<AndroidNode> nodeList =
			 * parser.findWidgetByRect(points); if (nodeList != null) {
			 * if(nodeList.size()>0){ ParseXML.print(nodeList); }else{
			 * Log.w("TAG-Pn2", "found:not found the rect widget");
			 * Toast.makeText(this, "not found the node"
			 * ,Toast.LENGTH_SHORT).show(); } } Log.w("TAG-Pn2",
			 * "get android node lat");
			 */

			/*----------------------------------------*/
			imageView.setImageURI(imageUri);
			operationPoint = new ArrayList<PointF>();
			draw();
		}

	}

	private String format(double input) {
		DecimalFormat decimalFormat = new DecimalFormat(".00");// 将点的坐标统一保留两位小数
		String Coordinate = decimalFormat.format(input);
		return Coordinate;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// do something...
			exitDialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
