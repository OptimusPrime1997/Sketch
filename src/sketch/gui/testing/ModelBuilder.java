package sketch.gui.testing;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PointF;
import android.widget.EditText;
import android.widget.TextView;

public class ModelBuilder {
	
	public TestModel model;
	
	public List<ParseXML> parser_list;
	public ParseXML cur_parser;
	
	public AndroidNode operation_node;
	public TAction operation_action;
	public List<AndroidNode> area_nodes;
	public TConstraint test_constraint;
	
	public TestState single_state0;
	public TestState single_state1;
	public List<TestState> multi_state0;
	public List<TestState> multi_state1;
	public int state_counter;
	
	public ModelBuilder(){
		model = new TestModel();
		parser_list = new ArrayList<ParseXML>();
		area_nodes = new ArrayList<AndroidNode>();
		multi_state0 = new ArrayList<TestState>();
		multi_state1 = new ArrayList<TestState>();
		state_counter = 0;
		operation_action = TAction.UNINDENTIFIED;
	}
	
	public ModelBuilder(ParseXML p){
		model = new TestModel();
		parser_list = new ArrayList<ParseXML>();
		area_nodes = new ArrayList<AndroidNode>();
		multi_state0 = new ArrayList<TestState>();
		multi_state1 = new ArrayList<TestState>();
		state_counter = 0;
		operation_action = TAction.UNINDENTIFIED;
		
		parser_list.add(p);
		cur_parser = parser_list.get(parser_list.size()-1);
	}
	
	public void addXMLParser(ParseXML p) {
		parser_list.add(p);
		cur_parser = parser_list.get(parser_list.size()-1);
	}
	
	public void identifyOperation(double[] result, ArrayList<PointF> operationPoint, ArrayList<PointF> endPoint)
	{
		System.out.println("IdentifyOperation: result[]= ");
		PointF operate_point = operationPoint.get(operationPoint.size() - 1);
		PointF end_point;
		int op_cursor = 0;
		for (double t : result)
		{
			System.out.print(t+" ");
			if (t == -1) break;
			op_cursor++;
		}
		int op = (int)(result[op_cursor-1]);
		//System.out.println("operationPoint.size()-1="+(operationPoint.size()-1)+"\nresult[size-1]="+result[operationPoint.size()-1]+"\nint(result)="+op);
		operation_action = TAction.valueOf(op);
		
		System.out.print("Operation Location: <"+operate_point.x+", "+operate_point.y+">");
		
		operation_node = cur_parser.findWidgetByLocation(operate_point.x, operate_point.y);
		if (operation_node == null)
		{
			System.out.println("operation_node.equals(null) True");
			operation_node = new AndroidNode();
			operation_node.widget_name = operate_point.x+"|"+operate_point.y;
			// 需要指定待测应用的包名
			operation_node.package_name = "com.bn.box2d.sndls";
			operation_node.text = "NULL";
			
			if (operation_action == TAction.DRAG) {
				end_point = endPoint.get(endPoint.size() - 1);
				System.out.print("End Location: <"+end_point.x+", "+end_point.y+">");
				operation_node.widget_name += "#"+end_point.x+"|"+end_point.y;
			}
		}
		
		System.out.println("Model-Operation: [action] = " + operation_action 
				+", [widget_name] = " + operation_node.widget_name);		
	}
	
	public void identifyConstraint(double[] result)
	{
		System.out.println("IdentifyConstraint: result[]= ");
		int cons_cursor = 0;
		for (double t : result) {
			System.out.print(t+" ");
			if (t == -1.00) break;
			cons_cursor++;
		}
		int cons = (int)(result[cons_cursor-1]);
		test_constraint = TConstraint.valueOf(cons);
		System.out.println("\nModel-Constraint: [const] = " + test_constraint);
	}
	
	public List<AndroidNode> identifyArea(PointF[] rect)
	{
		area_nodes = cur_parser.findWidgetByRect(rect);
		System.out.print("Identify this area! area_nodes number = " +area_nodes.size());
		
		if (area_nodes.size() == 0){
			// 区域内没找到控件，我们就讲这个区域作为控件名存储起来
			if (operation_action == TAction.DRAG) {
				operation_node.widget_name = operation_node.widget_name.split("#")[0];
				operation_node.widget_name += "#"+rect[0].x+"|"+rect[0].y;
				operation_node.widget_name += "#"+rect[3].x+"|"+rect[3].y;
				System.out.print("Identify this area for DRAG: widget_name = "+operation_node.widget_name);
			}
		}
		
		return area_nodes;
	}
	
	/*
	public TestState generateSingleSState(String code, String image_name)
	{
		single_state0 = new TestState(code, image_name, operation_node.widget_name, operation_node.text);
		state_counter++;
		System.out.print("single_state0:\n");single_state0.print();
		return single_state0;
	}
	
	public TestState generateSingleJState(String code, String image_name, String[] expect_text)
	{
		System.out.println("Generate Single-End State!");
		String wid_name="", wid_text="";
		for (int i=0; i<area_nodes.size(); i++)
		{
			if (i == area_nodes.size()-1){
				wid_name += area_nodes.get(i).widget_name;
				wid_text += expect_text[i];
			}
			else {
				wid_name += area_nodes.get(i).widget_name+"|";
				wid_text += expect_text[i]+"|";
			}
		}
		single_state1 = new TestState(code, image_name, wid_name, wid_text);
		state_counter++;
		System.out.print("single_end_state:\n");single_state1.print();
		return single_state1;
	}
	*/
	
	public TestState generateStateBasedString(String code, String image_name, String expect_output)
	{
		String wid_name="";
		for (int i=0; i<area_nodes.size(); i++)
		{
			if (i == area_nodes.size()-1){
				wid_name += area_nodes.get(i).widget_name;
			}
			else {
				wid_name += area_nodes.get(i).widget_name+"|";
			}
		}
		single_state1 = new TestState(code, image_name, wid_name, expect_output, operation_action);
		state_counter++;
		
		return single_state1;
	}
	
	public TestState generateStateBasedTAction(String code, String image_name, TAction tst_action)
	{
		single_state0 = new TestState(code, image_name, operation_node.widget_name, operation_node.text, tst_action);
		state_counter++;
		
		return single_state0;
	}
	
	//public TestState generateSingleState(String state_code, String image_name, int type)
	public TestState generateSingleState(String code, String image_name, int type)
	{
		switch (type)
		{
		case 0: // 0型：根据操作节点生成 state
				single_state0 = new TestState(code, image_name, operation_node.widget_name, operation_node.text, operation_action);
				state_counter++;
				System.out.print("single_state0:\n");single_state0.print();
				return single_state0;
		case 1:	// 1型：根据区域内的节点集生成 state
				System.out.println("Generate SState[type:1]");
				String wid_name="", wid_text="";
				
				System.out.println("+++area_nodes.size = "+area_nodes.size());
				if (area_nodes.size() != 0)
				{
					for (int i=0; i<area_nodes.size(); i++)
					{
						if (i == area_nodes.size()-1){
							wid_name += area_nodes.get(i).widget_name;
							wid_text += area_nodes.get(i).text;
						}
						else {
							wid_name += area_nodes.get(i).widget_name+"|";
							wid_text += area_nodes.get(i).text+"|";
						}
					}
					single_state1 = new TestState(code, image_name, wid_name, wid_text, operation_action);
				}
				else {
					System.out.println("area_nodes.size = 0, and widget_name = "+operation_node.widget_name);
					single_state1 = new TestState(code, image_name, operation_node.widget_name, "NULL", operation_action);
				}
				
				state_counter++;
				System.out.print("single_state1:\n");single_state1.print();
				return single_state1;
		default:
				System.out.println("Generate SState[type:other]--Error");
		}
		
		return null;
	}
	
	public void generateJointState(TestState ts, int type)
	{
		if (type == 0)
			multi_state0.add(ts);
		if (type == 1)
			multi_state1.add(ts);
	}
	
	/**
	 * 根据 single and single1 分别作为起始和终止节点生成 model
	 */
	public void generateAtomicModel()
	{
		//System.out.println("Start--GenerateAtomicModel");
		//System.out.println("SingleState0:"+single_state0.print());
		
		model.state_list.add(single_state0);
		//System.out.print("Start--1: "); single_state0.print();
		model.state_list.add(single_state1);
		//System.out.print("Start--2: "); single_state1.print();
		model.edge_list.add(new TestEdge(single_state0, single_state1));
		//System.out.println("Start--3: "+model.edge_list.toString());
		model.test_behavior = operation_action.toString();
		model.test_type = TType.ATOMIC;
		System.out.println("END--GenerateAtomicModel, model_#of_states: " + state_counter);
	}
	public void generateJointModel()
	{
		model.state_list.add(single_state1);
		model.state_list.addAll(multi_state1);
		for (TestState mid : multi_state1)
			model.edge_list.add((new TestEdge(single_state1, mid)));
		model.test_behavior = test_constraint.toString();
		model.test_type = TType.JOINT;
		System.out.println("END--GenerateAtomicModel, model_#of_states: " + state_counter);
	}
	
	public void generateModel()
	{
		model.state_list.addAll(multi_state0);
		model.state_list.addAll(multi_state1);
		
		for (TestState ts : multi_state1)
			model.edge_list.add(new TestEdge(multi_state0, ts));
		model.test_behavior = "Test Sequence";
		if (multi_state1.size() == 1)
			model.test_type = TType.ATOMIC;
		else
			model.test_type = TType.JOINT;
		System.out.println("END--GenerateAtomicModel, model_#of_states: " + state_counter);
	}
	//public void model_generating(String image, String xml, double[] result)
}
/*
public void model_generating(String image, String xml, double[] result)
	{
		System.out.println("====== After Saving =====");

		System.out.println("Parsing XML...");
		ParseXML parser = new ParseXML();
		InputStream is_xml = null;
		try {
			is_xml = this.getAssets().open(xml);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parser.parse(is_xml);

		System.out.println("Find Widget...");
		AndroidNode operate_node = null;
		
		if (isDrawArea)	// 代表是在选定区域
		{
			System.out.println("Widgets in this Area:");
			List<AndroidNode> wid_list = parser.findWidgetByRect(rect);
			
			for (AndroidNode mid : wid_list){
				mid.print();
			}
		} else {
			int cur_pos = operationPoint.size() - 1;
			String cur_operation = result[result.length-1] + "-<"+ operationPoint.get(cur_pos).x + "," + operationPoint.get(cur_pos).y + ">";
			System.out.println("cur_operation = " + cur_operation);
			operate_node = parser.findWidgetByLocation(
					operationPoint.get(cur_pos).x,
					operationPoint.get(cur_pos).y);
			//System.out.println("cur_operate_node(widget) = ");
			//operate_node.print();
		}
		
		System.out.println("Widget finded!\n\nModel Generating...");
		TestState s0 = new TestState("s0", image,
				operate_node.widget_name, operate_node.text);
		TestState s1 = new TestState("s1", "simple_2.png", "",
				"Result=4");
		List<TestState> slist = new ArrayList<TestState>();
		List<TestEdge> elist = new ArrayList<TestEdge>();
		slist.add(s0);
		slist.add(s1);
		elist.add(new TestEdge(s0, s1));

		TestModel model = new TestModel(slist, elist, TAction.CLICK,
				TType.ATOMIC);
		String str_model = model.print();

		System.out.println("\nModel Generating Done!");

		// System.out.println(str_model);
		// Write the model into a external file
		String modelPath = getDirName(getPath()) + "temp" + "/"
				+ "model.txt";
		ioOperation.recordOperation(modelPath, str_model);
	}
 */