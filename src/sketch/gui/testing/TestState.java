package sketch.gui.testing;

/**
 * TestState is used for storing the state of Android-AUT.
 * 
 * @author zhchuch
 */
public class TestState {
	public String state_code;
	public String state_image;
	public String test_widget;
	public String widget_text;
	public TAction test_action;
	
	public TestState(TestState ts) 
	{
		state_code = ts.state_code; state_image = ts.state_image; test_widget = ts.test_widget;
		widget_text = ts.widget_text; test_action = ts.test_action;
	}
	
	public TestState(String c, String s, String t, String w, TAction tac)
	{
		state_code = c; state_image = s; test_widget = t; widget_text = w; test_action = tac;
	}
	
	public String print()
	{
		String ans = state_code+":"+state_image+","+test_widget+","+widget_text+","+test_action;
		System.out.println("#State-Info# "+ans);
		
		return ans;
	}
}
