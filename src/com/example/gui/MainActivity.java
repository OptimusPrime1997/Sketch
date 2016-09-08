package com.example.gui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import sketch.gui.testing.AndroidNode;
import sketch.gui.testing.ModelBuilder;
import sketch.gui.testing.ParseXML;
import sketch.gui.testing.TAction;
import sketch.gui.testing.TType;
import sketch.gui.testing.TestGenerator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
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
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
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
	
	/*---------lc---------------------*/
	private String timeInput;
	private boolean timeInput_flag = false;
	/*-------------------------------*/

	private gestureTrain gestureTrain;
	private combinationGestureTrain comGestureTrain;
	private IOOperation ioOperation = new IOOperation();
	private boolean isDrawed = false;// 有没有画过，在MotionEvent.ACTION_UP时置为true
	private boolean isDrawArea = false;// 是否在绘制区域
	private File currentImage;// 当前图片
	private File[] files;// 一个文件夹下所有文件
	private ArrayList<File> imageFiles = new ArrayList<File>();// 一个文件夹下所有的图片文件
/*---- Modify By zhchuch ---*/
	private String imagePath;
	private PointF[] rect;
	private ModelBuilder modelBuilder;
	private ModelBuilder tempMG;
	private int countDrawArea = 0;
	private boolean isModelCompleted = false;
	private TType test_type;
	private boolean accept_flag = false;
	private boolean target_flag = false;
	private boolean input_flag = false;
	private boolean recogRect_flag = false;
/*-------------------------*/
	
	private Intent imageChooseIntent;
	private final int REQUEST_CODE = 1;

	private final int MENU_ITEM_COUNTER = Menu.FIRST;
	public static final String EXTRA_FILE_CHOOSER = "file_chooser";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try {
			ioOperation.CreateMdr(sd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		imageView = (ImageView) findViewById(R.id.imageView1);
		
		modelBuilder = new ModelBuilder();
		
		imageChooseIntent = new Intent(this, ImageChooseActivity.class);
		draw();
		gestureTrain = new gestureTrain();
		try {
			gestureTrain.Train(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void draw() {
		Bitmap bitmap = null;
		if (clickCount == 0) {
			r = this.getResources();
			bitmap = BitmapFactory.decodeResource(r, R.drawable.messi);// 只读,不能直接在bmp上画
		} else {
			try {
				// 读取uri所在的图片
				bitmap = MediaStore.Images.Media.getBitmap(
						this.getContentResolver(), Uri.fromFile(currentImage));
			} catch (Exception e) {
				Log.e("[Android]", e.getMessage());
				Log.e("[Android]", "目录为：" + currentImage.getName());
				e.printStackTrace();
			}

		}

		alterBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), bitmap.getConfig());
		// System.out.println(bitmap.getWidth() + "");
		canvas = new Canvas(alterBitmap);
		paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(10);
		Matrix matrix = new Matrix();
		canvas.drawBitmap(bitmap, matrix, paint);
		imageView.setImageBitmap(alterBitmap);
		imageView.setOnTouchListener(this);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			downx = event.getX();
			downy = event.getY();
			graphics.add(new PointF(downx, downy));
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

			break;
		case MotionEvent.ACTION_UP:

			// 直线画板

			upx = event.getX();
			upy = event.getY();
			canvas.drawLine(downx, downy, upx, upy, paint);
			imageView.invalidate();// 刷新
			graphics.add(new PointF(upx, upy));
			isDrawed = true;
			
			/*
			 * 无论是否在绘制区域都要先创建文件目录
			 */
			if (clickCount > 0) {
				// 创建文件保存目录
				String dirPath = getDirName(getPath()) + "temp";
				try {
					ioOperation.CreateMdr(dirPath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (isDrawArea) { // Selection Area
				
				String filePath = getDirName(getPath()) + "temp" + "/"
						+ getImageName(getPath()) + ".txt";
				rect = logic.getExteriorRect(graphics);
				ioOperation.recordAreaInfo(filePath, graphics, rect);
/*-------------- Modify by zhchuch -----------------*/
				countDrawArea++;
				
				// 找到这个 圈 里面的所有控件
				if (test_type == TType.JOINT) {
					if (recogRect_flag) {
						tempMG = new ModelBuilder(modelBuilder.cur_parser);
						tempMG.identifyArea(rect);
					}
				} else {
					if (recogRect_flag) {
						List<AndroidNode> wid_list = modelBuilder.identifyArea(rect);
					}
				}
				recogRect_flag = false;
				//isDrawArea = false;
				
				System.out.println("test_type = " + test_type);
				
				if (test_type == TType.ATOMIC) {
					System.out.println("GenerateSingState pos[draw]");
					
					//modelBuilder.generateSingleState("s"+modelBuilder.state_counter, getImageName(imagePath), 1);
					/*使用对话框，让用户输入他自己所要想得到的 期待输出结果(Expected-Output)*/
					final TextView tv = new EditText(this);
					new AlertDialog.Builder(this).setTitle("Expected output:").setIcon(android.R.drawable.ic_dialog_info)
						.setView(tv).setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								input = tv.getText().toString();
								
								modelBuilder.generateJointState(modelBuilder.generateStateBasedString("s"+modelBuilder.state_counter, getImageName(imagePath), input), 1);
								System.out.println("Your Input[pos-ATOMIC]: " +input);
							}
						}).setNegativeButton("取消", null).show();

					countDrawArea = 0;
					isModelCompleted = true;
				}
				if (test_type == TType.JOINT) {
					// 为每一个的 accept 状态，生成 单终止状态
					System.out.println("JOINT [AccpetState Generating...]");
					/*使用对话框，让用户输入他自己所要想得到的 期待输出结果(Expected-Output)*/
					final TextView tv = new EditText(this);
					new AlertDialog.Builder(this).setTitle("Expected output:").setIcon(android.R.drawable.ic_dialog_info)
						.setView(tv).setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								input = tv.getText().toString();
								modelBuilder.generateJointState(tempMG.generateStateBasedString("s"+modelBuilder.state_counter, getImageName(imagePath), input), 1);
								System.out.println("Your Input[pos-JOINT]: " +input);
							}
						}).setNegativeButton("取消", null).show();
					//modelBuilder.generateJointState(tempMG.generateSingleState("s"+modelBuilder.state_counter, getImageName(imagePath), 1));
					accept_flag = true;
				}
/*--------------------------------------------------*/
				if(stepCount>2){
					for (int i = 0; i < graphics.size(); i++) {
						jointGraphics.add(graphics.get(i));
					}
				}
				

			} else {
				float[] coordinate = logic.calCoordinate(graphics);// 单击时的中心点
				canvas.drawCircle(coordinate[0], coordinate[1], 10, paint);

				PointF center = new PointF(coordinate[0], coordinate[1]);
				operationPoint.add(center);
				int endPointIndex = logic.calEndPoint(graphics);
				endPoint.add(graphics.get(endPointIndex));
				ArrayList<String> recordList = logic.calRecordList(graphics);// 获得用于训练的十五个点的坐标
				if (clickCount > 0) {
					String path = getDirName(getPath()) + "temp" + "/"
							+ getImageName(getPath()) + ".arff";
					ioOperation.recordPoint(path, recordList);
				}
			}

			graphics = new ArrayList<PointF>();

			break;

		default:
			break;
		}

		return true;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_ITEM_COUNTER, 0, "choose");
		menu.add(0, MENU_ITEM_COUNTER + 1, 0, "next");
		menu.add(0, MENU_ITEM_COUNTER + 2, 0, "save");
		menu.add(0, MENU_ITEM_COUNTER + 3, 0, "clear");
		menu.add(0, MENU_ITEM_COUNTER + 4, 0, "draw Area");
		menu.add(0, MENU_ITEM_COUNTER + 5, 0, "target");
		//menu.add(0, MENU_ITEM_COUNTER + 6, 0, "enter text");
		menu.add(0,MENU_ITEM_COUNTER + 7, 0, "timer");
		menu.add(0,MENU_ITEM_COUNTER + 8, 0, "fork");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ITEM_COUNTER:	// choose
			clickCount++;
			modelBuilder = new ModelBuilder();
			// 判断sd卡是否存在，并且是否具有读写权限
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				startActivityForResult(imageChooseIntent, REQUEST_CODE);
			}
			break;
		case MENU_ITEM_COUNTER + 1:	 // next
			if (clickCount > 0) {
				nextClickOperation();
			}

			break;
		case MENU_ITEM_COUNTER + 2:	 // save
			if (isDrawed) {
/*--------- Modify By zhchuch, in here ---------*/
				if (test_type == TType.JOINT && accept_flag)
				{
					isModelCompleted = true;
				}
				
				if (isModelCompleted)
				{
					/*
					if (test_type == TType.ATOMIC)
						modelBuilder.generateAtomicModel();
					if (test_type == TType.JOINT)
						modelBuilder.generateJointModel();
					*/
					modelBuilder.generateModel();
					isModelCompleted = false;
					accept_flag = false;
					target_flag = false;
					
					modelBuilder.model.print();
					System.out.println("\nModel Building Done!");
					
					/*用已经生成的模型 生成 测试用例*/
					
					System.out.println("\nTestCase Generating ...");
					//TestGenerator testGenerator = new TestGenerator(modelBuilder.model);
					
					String[] temp = getDirName(getPath()).split("/");
					String app_name = temp[temp.length - 1];
					
					String dirPath = getDirName(getPath()) + "TC";
					try {
						// 先要创建目录
						ioOperation.CreateMdr(dirPath);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String filePath = dirPath + "/";
					System.out.println("StorePath = " +filePath);
					
					// 找到被测试APP的包名
					String pack_name = "";
					String mainActivity_name= "MyBox2dActivity"; // for App: AngryBird
					
					if (!modelBuilder.operation_node.package_name.equals(""))
						pack_name = modelBuilder.operation_node.package_name;
					else 
						pack_name = modelBuilder.area_nodes.get(0).package_name;
					TestGenerator testGenerator = new TestGenerator(modelBuilder.model, filePath, app_name, pack_name, mainActivity_name);
					List<String> tst = testGenerator.generateTestCase(0);
					for (String test : tst){
						System.out.println("#########");
						System.out.println(test);
						System.out.println("#########");
					}
					System.out.println("\nTestCase Generating Done!");
					
				}
/*----------------------------------------------*/				
				else {
					//System.out.println("Save: Identify Basic-Test-Action Code is transfered!");
					/*--------- After there, imply the identify action function. ---------*/
					// savePicture();
					String path = getDirName(getPath()) + "temp" + "/"
							+ getImageName(getPath()) + ".arff";
					double[] result;
					result = gestureTrain.TrainResult(path);
					//System.out.println("Result[atomic]: "+result[result.length-1]);
					/*
					 * isDrawed为true并且result等于-1时说明只绘制了区域，并没有单击双击拖动这三种操作
					 */
					if (result[0] != -1) {
						String filePath = getDirName(getPath()) + "temp" + "/"
								+ getImageName(getPath()) + ".txt";
						ArrayList<String> operationList = ioOperation
								.readOperation(filePath);
						for (int i = 0; i < operationPoint.size(); i++) {

							if (operationPoint.get(i).x != 0
									&& operationPoint.get(i).y != 0
									&& result[i] != 3) {
								if (result[i] == 0 || result[i] == 1) { // click
									String operation = result[i] + "-<"
											+ format(operationPoint.get(i).x) + ","
											+ format(operationPoint.get(i).y) + ">";
									if (operationList.indexOf(operation) == -1) {
										// ioOperation.recordOperation(filePath,
										// operation);
									}

								}
								if (result[i] == 2) { // Drag
									String operation = result[i] + "-<"
											+ format(operationPoint.get(i).x) + ","
											+ format(operationPoint.get(i).y)
											+ ">;<" + format(endPoint.get(i).x)
											+ "," + format(endPoint.get(i).y) + ">";
									if (operationList.indexOf(operation) == -1) {
										// ioOperation.recordOperation(filePath,
										// operation);
									}
								}

							}

						}
					}
					//System.out.println("====== After Saving =====");
					//System.out.println("result[operator]= "+result[operationPoint.size()-1]);
					modelBuilder.identifyOperation(result, operationPoint, endPoint);
			/*--------- Before there, imply the identify action function. ---------*/
					if (modelBuilder.operation_action == TAction.CLICK && modelBuilder.operation_node.widget_name == "EditText") {
						// 代表用户想要输入一个编辑框
						final TextView tv = new EditText(this);
						new AlertDialog.Builder(this).setTitle("Please Input Text:").setIcon(android.R.drawable.ic_dialog_info)
							.setView(tv).setPositiveButton("确定", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									// TODO Auto-generated method stub
									input = tv.getText().toString();
									//modelBuilder.generateJointState(tempMG.generateStateBasedString("s"+modelBuilder.state_counter, getImageName(imagePath), input), 1);
									modelBuilder.operation_node.text = input;
									
									System.out.println("Your Input[pos-EditText]: " +input);
								}
							}).setNegativeButton("取消", null).show();
					}
				}
/*----------------------------------------------*/
			} else {
				Toast toast = Toast.makeText(this, "no draw",
						Toast.LENGTH_SHORT);
				toast.show();
			}

			//draw();
			break;
		case MENU_ITEM_COUNTER + 3:	  // clear
			draw();
			break;
		case MENU_ITEM_COUNTER + 4:	  // drawArea
			isDrawArea = true;
			recogRect_flag = true;
			break;
		case MENU_ITEM_COUNTER + 5:	  // target
/*-------------- Modify by zhchuch -----------------*/
			System.out.println("After click Target...");
			target_flag = true;
		
			if (modelBuilder.operation_node == null)
			{
				System.out.println("model->operation_node = null!");
			}
			
			if (countDrawArea == 0) test_type = TType.ATOMIC;
			else test_type = TType.JOINT;
			
			System.out.println("GenerateSingState pos[target]");
			modelBuilder.generateJointState(modelBuilder.generateSingleState(
					"s"+modelBuilder.state_counter, getImageName(imagePath), test_type.ordinal()),
					0);
			//System.out.println("After generating (in Target) modelG.stateCounter="+modelBuilder.state_counter);
/*--------------------------------------------------*/
			
			// 先获取所有的 joint 信息，并放入训练器进行识别 
			ArrayList<String> recordList = logic.calRecordList(jointGraphics);
		
			String path = getDirName(getPath()) + "temp"
					+ "/combinationGesture" + getImageName(getPath()) + ".arff";
			ioOperation.recordJointPoint(path, recordList);
			comGestureTrain = new combinationGestureTrain();
			try {
				comGestureTrain.Train(this);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			double[] result = comGestureTrain.TrainResult(path);
		
/*--------------------------------------------------*/
			modelBuilder.identifyConstraint(result);
			
			for (int i = 0; i < result.length; i++) {
				if (result[i] == -1.00) break;
					System.out.println(result[i] + "");
			}
/*--------------------------------------------------*/
			isDrawArea = false;
			jointGraphics = new ArrayList<PointF>();
			stepCount = 0;
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				startActivityForResult(imageChooseIntent, REQUEST_CODE);
			}
			break;
			
		case MENU_ITEM_COUNTER + 6:
			//getUserInputString();
			/*这里涉及到 多线程，主线程要等待子线程输入。*/
			//System.out.println("Your Input Text: "+input);
			
			final TextView tv = new EditText(this);
			new AlertDialog.Builder(this).setTitle("Expected output:").setIcon(android.R.drawable.ic_dialog_info)
			.setView(tv).setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					input = tv.getText().toString();
					input_flag = true;
					System.out.println("Your Input[pos-menu-selected]: " +input);
				}
			}).setNegativeButton("取消", null).show();
			
			break;
		
		case MENU_ITEM_COUNTER + 7:
			
			final TextView timeText = new EditText(this);
			Builder timeDialog = new AlertDialog.Builder(this);
			timeDialog.setTitle("Wait time");
			timeDialog.setIcon(android.R.drawable.ic_dialog_info);
			timeDialog.setView(timeText);
			timeDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					timeInput = timeText.getText().toString();
					timeInput_flag = true;
					System.out.println("Your Input[pos-menu-selected]: " +timeInput);
				}
			});
			timeDialog.setNegativeButton("取消", null);
			timeDialog.show();
			break;
			
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * 找到 可处理当前测试界面(ImageName)的 parser
	 */
	private ParseXML getParserByImagePath(String ImagePath)
	{
		//System.out.println("ImagePath = "+ImagePath);
		String[] temp = ImagePath.split("/");
		String app_name = temp[temp.length - 2];
		String ImageName = temp[temp.length - 1];
		String testXMLName = "screen_data/"+app_name+"/" + ImageName.split("\\.")[0] +".uix";
		//System.out.println("testXMLName = "+testXMLName);
		
		ParseXML parser = new ParseXML();
		
		InputStream is_xml = null;
		try {
			is_xml = this.getAssets().open(testXMLName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parser.parse(is_xml);
		
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
		//String[] nameTemp = name.split("\\.");
		return name;//nameTemp[0];
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			if (data != null) {
				// uri=Uri.parse("content://media/external/images/media/39");
				imagePath = data.getStringExtra(EXTRA_FILE_CHOOSER);				

				currentImage = new File(imagePath);
				Uri imageUri = Uri.fromFile(currentImage);
				imageView.setImageURI(imageUri);
				File dir = new File(getDirName(imagePath));
				files = dir.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (!files[i].isDirectory()) {
						String fileName = files[i].getName();
						String fileType = fileName.substring(fileName
								.indexOf("."));
						if (isImage(fileType)) {
							imageFiles.add(files[i]);
						}
					}

				}
/*--------------- modify by zhchuch ----------*/				
				ParseXML parser = getParserByImagePath(imagePath);
				
				modelBuilder.addXMLParser(parser);
				//tempMG = new ModelBuilder(modelBuilder.cur_parser); 
/*-------------------------------------------*/	
				draw();
			} else {
				return;
			}
		}// if

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
		String CanonicalPath = new String();
		try {
			CanonicalPath = currentImage.getCanonicalPath();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return CanonicalPath;
	}

	private void nextClickOperation() {
		/*
		 * 点击Next按钮的时候，先对 之前的图片上的操作 生成一个状态
		 */
		if (!target_flag) {
			modelBuilder.generateJointState(
					modelBuilder.generateSingleState("s"+modelBuilder.state_counter, getImageName(imagePath), 0),
					0);
		}
		
		int currentIndex = imageFiles.indexOf(currentImage);// 获得所选图片在这个文件夹中的序号
		/*
		 * 点击next时从currentImage之后的一张图片开始，之前的不再出现
		 */
		if (currentIndex == imageFiles.size() - 1) {
			Toast.makeText(this, "已经是最后一张了", Toast.LENGTH_SHORT).show();
			// draw();
		} else {
			currentImage = imageFiles.get(currentIndex + 1);
			Uri imageUri = Uri.fromFile(currentImage);
/*------------ modify by zhchuch ----------*/
			imagePath = imageUri.toString();
			//System.out.println("NextImage: Uri="+imageUri.toString()+", ImageName: "+ImageName);
			ParseXML parser = getParserByImagePath(imagePath);
			
			modelBuilder.addXMLParser(parser);
/*----------------------------------------*/
			imageView.setImageURI(imageUri);
			operationPoint = new ArrayList<PointF>();
			draw();
		}

	}

	/*
	 * 根据Uri获得文件的绝对路径
	 */
	@SuppressWarnings("unused")
	private String getAbsoluteImagePath(Uri uri) {
		// can post image
		Cursor cursor = null;
		String column = MediaStore.Images.Media.DATA;
		String[] projection = { column };
		try {
			cursor = this.getContentResolver().query(uri, projection, null,
					null, null);
			if (cursor != null && cursor.moveToFirst()) {
				int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/*
	 * 保存所画的图片
	 * 
	 * private void savePicture() { // TODO Auto-generated method stub String
	 * dir = getDirName(getPath()) + "temp";
	 * 
	 * File f = new File(dir + "/" + getImageName(getPath()) + ".png"); if
	 * (f.exists()) { f.delete(); } try { FileOutputStream fos = new
	 * FileOutputStream(f); alterBitmap.compress(Bitmap.CompressFormat.PNG, 70,
	 * fos); fos.flush(); fos.close(); } catch (FileNotFoundException e) {
	 * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
	 * Log.v("savePicture", dir); }
	 */
	private String format(double input) {
		DecimalFormat decimalFormat = new DecimalFormat(".00");// 将点的坐标统一保留两位小数
		String Coordinate = decimalFormat.format(input);
		return Coordinate;
	}
}
