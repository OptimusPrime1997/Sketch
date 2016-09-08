package sketch.gui.testing;

import java.util.ArrayList;
import java.util.List;

/**
 * TestEdge is used for storing the test-path-info of Android-AUT.
 * 
 * @author zhchuch
 */
public class TestEdge {
	public List<TestState> start_state;
	public List<TestState> end_state;
	
	public TestEdge()
	{
		start_state = new ArrayList<TestState>();
		end_state = new ArrayList<TestState>();
	}
	
	public TestEdge(TestState ss, TestState es)
	{
		start_state = new ArrayList<TestState>();
		end_state = new ArrayList<TestState>();
		
		start_state.add(ss); end_state.add(es);
	}
	
	public TestEdge(List<TestState> ss, List<TestState> es)
	{
		start_state = new ArrayList<TestState>();
		end_state = new ArrayList<TestState>();
		
		start_state = ss;
		end_state = es;
	}
	
	public TestEdge(List<TestState> ss, TestState es)
	{
		start_state = new ArrayList<TestState>();
		end_state = new ArrayList<TestState>();
		
		start_state = ss;
		end_state.add(es);
	}
	
	public void edgeAddStartState(TestState ss)
	{
		start_state.add(ss);
	}
	public void edgeAddEndState(TestState es)
	{
		end_state.add(es);
	}
	
	public String print()
	{
		String ans = "";
		
		ans += "<";
		for (TestState ts1: start_state)
			ans += ts1.state_code + "-";
		ans.substring(0, ans.length()-1);
		ans += ">";
		ans += " ---> ";
		ans += "<";
		for (TestState ts2: end_state)
			ans += ts2.state_code + "-";
		ans.substring(0, ans.length()-1);
		ans += ">";
		
		System.out.println("#Edge-Info# "+ans);
		
		return ans;
	}
	
	public String print_concrete()
	{
		String ans = "";
		
		ans += "<";
		for (TestState ts1: start_state){
			ans += ts1.state_code+","+ts1.test_widget+","+ts1.widget_text + "-";
		}
		ans.substring(0, ans.length()-1);
		ans += ">";
		ans += " ---> ";
		ans += "<";
		for (TestState ts2: end_state){
			ans += ts2.state_code+","+ts2.test_widget+","+ts2.widget_text + "-";
		}
		ans.substring(0, ans.length()-1);
		ans += ">";
		
		System.out.println("#Edge-Info# "+ans);
		
		return ans;
	}
}
