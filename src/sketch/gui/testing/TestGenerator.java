package sketch.gui.testing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestGenerator {
	public TestModel model;
	public String storePath;
	public static String testCasePre = "public class ";
	public static String testCaseExtend = " extends ActivityInstrumentationTestCase2";
	public static String soloCreator = "private Solo solo";
	public static String overrideState = "@Override\n";
	public static String testResultName = "test_result";
	public static String setupFunction = 
			"public void setUp() throws Exception { \n solo = new Solo(getInstrumentation(), getActivity()); \n }\n\n";
	public static String teardownFunction = 
			"public void tearDown() throws Exception { \n solo.finishOpenedActivities(); \n }\n\n";
	
	public String testApplicationName; // = "SimpleGUI"
	public String testApplicationPackageName;
	public String MainActivityName = "MainActivity";
	public String TestCaseClassName;
	
	public TestGenerator(){ model = new TestModel(); storePath="";}
	
	public TestGenerator(TestModel tm)
	{
		new TestGenerator(tm, "");
	}
	
	public TestGenerator(TestModel tm, String path)
	{
		new TestGenerator(tm, path, "");
	}
	
	public TestGenerator(TestModel tm, String path, String app_name)
	{
		new TestGenerator(tm, path, app_name, "", "MainActivity");
	}
	
	public TestGenerator(TestModel tm, String path, String app_name, String package_name, String main_act)
	{
		model = tm;
		storePath = path;
		testApplicationName = app_name;
		testApplicationPackageName = package_name;
		MainActivityName = main_act;
	}
	
	public String generateTestCore(TestEdge test_edge)
	{
		String ans = "";
		boolean is_drag = false;
		
		ans += "/*------ Test Core Function ------*/\n";
		System.out.print("Current Test-Edge: "); test_edge.print();
		
		List<TestState> t_start = test_edge.start_state;
		List<TestState> t_end = test_edge.end_state;
		
		ans += "public void testOnClick()"+"\n";
		ans += "{\n";
		
		// Test Action Sequence: t_start [s1, s2, s3 ... ]
		for (TestState ts : t_start)
		{
			switch (ts.test_action)
			{
			case CLICK: 
				// state: <s1, 1.png, button, "Training", click>
				ans += "// Click-TestAction-In-TestState\n";
				
				if (ts.widget_text.equals("NULL")){	// 代表点击的位置处没有控件，需要根据坐标点击
					String[] pos = ts.test_widget.split("\\|");
					ans += "solo.clickOnScreen((float)"+pos[0]+", (float)"+pos[1]+");\n\n"; 
				}
				else {
					if (ts.test_widget.contains("EditText"))
						ans += "solo.enterText( 0, "+ts.widget_text+")";
					else
						ans += "solo.clickOnText(\""+ts.widget_text+"\");\n\n"; //fetchWidgetName(ts.test_widget)+
				}
				break;
			case LONG_CLICK: 
				// state: <s1, 1.png, button, "Training", long_click>
				ans += "// Long_Click-TestAction-In-TestState\n";
				ans += "solo.clickLongOnText(\""+ts.widget_text+"\");\n\n";
				break;
			case DRAG: 
				// state: <s1, 1.png, 237|365(#140|400)*, NULL, drag>
				String point_loc[] = ts.test_widget.split("#");
				System.out.println("ts.test_widget = " + ts.test_widget);
				if (point_loc.length == 2) // average-drag
				{
					ans += "// Drag-TestAction-In-TestState\n";
					String from_x = point_loc[0].split("\\|")[0];
					String from_y = point_loc[0].split("\\|")[1];
					String to_x = point_loc[1].split("\\|")[0];
					String to_y = point_loc[1].split("\\|")[1];
					String stepCount = "5";
					
					ans += "solo.drag((float)"+from_x+", (float)"+to_x+", (float)"+from_y+", (float)"+to_y+", "+stepCount+");\n\n";
					
					ans += "solo.sleep(10000);\n\n";
					
					ans += "// ScreenShot and Judgment\n";
					ans += "ScreenShot ss = new ScreenShot(\""+ TestCaseClassName +"_sc\");\n\n";
					
					ans += "// Read-in the screenshot\n";
					ans += "Bitmap bitmap = ss.getScreenShot();\n\n";
					
					is_drag = true;
				}
				else 
				{	// 随机在区域内产生 拖拽终止点(End-Point)
					System.out.println("Drag Error: point_loc.length = "+point_loc.length);
				}
				break;
			case UNINDENTIFIED:
				ans += "// No-TestAction-In-TestState\n";
				 break;
			default:
				break;
			}
		}
		
		// Test Oracle Sequence: t_start [s1', s2', s3' ... ]
		if (is_drag) 
		{
			ans += "// Compare the result\n";
			ans += "boolean " + testResultName + " = (bitmap.getPixel(307, 436) == -1);\n\n";
		} else {
			for (TestState ts : t_end) 
			{
				// t_end.get(0)
				ans += "boolean " + testResultName + " = " + "solo.searchText(\"" + ts.widget_text+ "\");\n\n";
			}
		}
		ans += "assertTrue(\"" + "Test: Failed." + "\", " + testResultName +");\n";
		
		ans += "}\n";
		ans += "/*--------------------------------*/\n";
		
		return ans;
	}
	
	public String generateTestAssist(TestEdge test_edge)
	{
		String ans = "";
		
		if (!testApplicationPackageName.equals("")) {
			ans += "package "+testApplicationPackageName+".test;\n\n";
			ans += "import "+testApplicationPackageName+"."+MainActivityName+";\n";
		}
		ans += "import com.robotium.solo.Solo;\n";
		ans += "import android.annotation.SuppressLint;\n";
		ans += "import android.test.ActivityInstrumentationTestCase2;\n\n";
		
		ans += testCasePre + TestCaseClassName + testCaseExtend + "<" + MainActivityName + ">" +"\n";
		ans += "{\n";
		ans += soloCreator + ";\n\n";
		
		ans += "@SuppressLint(\"NewApi\")\n";
		ans += "public " + TestCaseClassName + "()";
		ans += "{\n";
		ans += "super("+ MainActivityName +".class)" +";\n";
		ans += "}\n\n";
		
		ans += overrideState;
		ans += setupFunction;
		ans += overrideState;
		ans += teardownFunction;
		
		// 加入针对该 测试边 的测试代码
		ans += generateTestCore(test_edge);
		ans += "}\n";
		
		return ans;
	}
	
	public List<String> generateTestCase(int serial_num)
	{
		List<String> testcases = new ArrayList<String>();
		String mid;
		
		for (TestEdge test_edge : model.edge_list)
		{
			// 看看不同边的输出内容
			System.out.println("GenerateTestCase => TestEdge: "+test_edge.print_concrete());
			
			boolean is_conflate = false;
			
			TestState temp_ts;
			for (int i=0; i<test_edge.start_state.size(); i++) {
				temp_ts = test_edge.start_state.get(i);
				// 如果是含有 | 或 # 字符的合并状态，需要分解开来
				// case-1: Button|Button|Button
				if (temp_ts.test_action != TAction.DRAG && temp_ts.test_widget.contains("\\|")) {
					mid = "";
					
					String[] temp_wid = temp_ts.test_widget.split("\\|");
					String[] temp_tex = temp_ts.widget_text.split("\\|");
					for (int j=0; j<temp_wid.length; j++)
					{
						TestState _ts = new TestState(temp_ts.state_code, temp_ts.state_image, temp_wid[j], temp_tex[j], temp_ts.test_action);
						TestEdge _te = new TestEdge();
						for (int k=0; k<test_edge.start_state.size(); k++)
						{
							if (k == i)
								_te.start_state.add(_ts);
							else
								_te.start_state.add(test_edge.start_state.get(k));
						}
						_te.end_state.addAll(test_edge.end_state);
					}
					
					is_conflate = true; break;
				}
				// case-2: point#p_start#p_end
				else if (temp_ts.test_action == TAction.DRAG && temp_ts.test_widget.split("#").length == 3) {
					System.out.println("Drag && conflate.");
					/**
					 * Drag-Conflate: Split && Generate && Store
					 */
					int random_test_num = 5;
					String[] start_area = temp_ts.test_widget.split("#")[1].split("\\|");
					String[] end_area = temp_ts.test_widget.split("#")[2].split("\\|");
					
					Random rand = new Random();
					for (int k = 0; k<random_test_num; k++)
					{
						float rand_x = rand.nextFloat();
						float drag_end_x = (Float.parseFloat(end_area[0]) - Float.parseFloat(start_area[0]))*rand_x+Float.parseFloat(start_area[0]);
						float rand_y = rand.nextFloat();
						float drag_end_y = (Float.parseFloat(end_area[1]) - Float.parseFloat(start_area[1]))*rand_y+Float.parseFloat(start_area[1]);
						
						TestState new_state = new TestState(temp_ts);
						new_state.test_widget = temp_ts.test_widget.split("#")[0] + "#" + drag_end_x + "|" + drag_end_y;
						// 为每一个点生成一个单独的测试边，从而生成相应的测试用例
						TestEdge rand_edge = new TestEdge();
						rand_edge.start_state = test_edge.start_state;
						rand_edge.start_state.set(i, new_state);
						rand_edge.end_state = test_edge.end_state;
						System.out.print("drag-number: " + k + ": " + drag_end_x+"|"+drag_end_y+"\n");
						
						//rand_edge.print_concrete(); System.out.println("----------------");
						
						TestCaseClassName = testApplicationName + "Test" +serial_num;
						mid = generateTestAssist(rand_edge);
						testcases.add(mid);
						StoreTestCase(mid); serial_num++;
					}
					
					is_conflate = true; break;
				}
				// case-3: normally test edge
				else {
					
				}
			}
			
			if (!is_conflate){
				TestCaseClassName = testApplicationName + "Test" +serial_num;
				mid = generateTestAssist(test_edge);
				testcases.add(mid);
				StoreTestCase(mid); serial_num++;
			}
		}
		
		return testcases;
	}
	
	public void StoreTestCase(String mid)
	{
		//System.out.println("StorePath[pos:1] = " +storePath);
		if (!storePath.equals(""))
		{
			//System.out.println("StorePath[pos:2] = " +storePath+"testcase"+i+".java");
			File fi = new File(storePath+TestCaseClassName+".java");
			if (!(fi.exists())) {
				try {
					fi.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println(storePath+TestCaseClassName+".java"+": "+e.toString());
					e.printStackTrace();
				}
			}
			FileWriter fw;
			try {
				//fw = new FileWriter(fi, true);[不覆盖原文件]
				fw = new FileWriter(fi); //[覆盖原文件]
				fw.write(mid);
				fw.flush();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String fetchWidgetName(String wn)
	{
		String[] name = wn.split("\\.");
		return name[name.length-1];
	}
}
