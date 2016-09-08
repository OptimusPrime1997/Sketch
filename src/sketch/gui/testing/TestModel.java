package sketch.gui.testing;

import java.util.ArrayList;
import java.util.List;

public class TestModel {
	
	public List<TestState> state_list;
	public List<TestEdge> edge_list;
	public String test_behavior;
	//public TAction test_action;
	public TType test_type;
	
	public TestModel()
	{
		state_list = new ArrayList<TestState>();
		edge_list = new ArrayList<TestEdge>();
	}
	
	public TestModel(List<TestState> sl, List<TestEdge> el, String beha, TType t)
	{
		state_list = new ArrayList<TestState>();
		edge_list = new ArrayList<TestEdge>();
		state_list = sl; edge_list = el; test_behavior = beha; test_type = t;
	}
	
	public String print()
	{
		String ans = "";
		
		System.out.println("========== Model Information =========");
		System.out.println("State Set:");
		ans += "State Set:\n";
		
		for (int i=0; i<state_list.size(); i++)
			ans += state_list.get(i).print() +"\n";
		//System.out.println(ans_state);
		
		System.out.println("Edge Set:");
		ans += "Edge Set:\n";
		for (int i=0; i<edge_list.size(); i++)
			ans += edge_list.get(i).print();
		ans += "\n";
		//System.out.println(ans_edge);
		
		System.out.println("Test Behavior: " + test_behavior);
		System.out.println("Test Type: " + test_type);
		ans += "Test Behavior: "+test_behavior+"\n";
		ans += "Test Type: "+test_type + "\n";
		System.out.println("======================================");
		
		return ans;
	}
}
