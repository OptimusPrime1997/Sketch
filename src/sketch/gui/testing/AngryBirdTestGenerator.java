package sketch.gui.testing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.gui.IOOperation;

/**
 * AngryBirdTestGenerator used for: 1. writing the model 
 * 2. generating test-scripts automatically for AngryBird.
 * 
 * @author zhchuch
 */
public class AngryBirdTestGenerator {
	
	public static void main(String[] args)
	{
		IOOperation ioOperation = new IOOperation();
		// step-1: Generate Model
		List<TestState> state_set = new ArrayList<TestState>();
		List<TestEdge> edge_set = new ArrayList<TestEdge>();
		String behavior="Test Sequence";
		TType ttype = TType.ATOMIC;
		// step-1.1: Specify State-info.
		// click the "PLAY" Button
		TestState ts0 = new TestState("s0", "1.png", "480|270", "NULL", TAction.CLICK);
		
		// drag the "Bird" Object
		//TestState ts1 = new TestState("s1", "3.png", "237|365#140|400", "NULL", TAction.DRAG);
		// drag the "Bird" Object to an Area.
		TestState ts1_cup = new TestState("s1", "3.png", "237|365#100|200#300|500", "NULL", TAction.DRAG);
		
		// compare the expect output
		TestState ts2 = new TestState("s2", "5.png", "pixel-matching", "NULL", TAction.UNINDENTIFIED);
		
		// step-1.2: Specify Edge-info.
		List<TestState> start_states = new ArrayList<TestState>();
		List<TestState> end_states = new ArrayList<TestState>();
		start_states.add(ts0);
		start_states.add(ts1_cup);
		end_states.add(ts2);
		state_set.addAll(start_states);
		state_set.addAll(end_states);
		edge_set.add(new TestEdge(start_states, end_states));
		
		// step-1.3: Create a model.
		TestModel model = new TestModel(state_set, edge_set, behavior, ttype);
		model.print();
		
		// step-2: Specify App-info.
		//String fileDir = "/storage/sdcard0/screenShotPicture/APPAngryBirdTC";
		String fileDir = "./APPAngryBirdTC";
		String filePath = fileDir + "/";
		String app_name = "APPAngryBird";
		String pack_name = "com.bn.box2d.sndls";
		String mainActivity_name = "MyBox2dActivity";
		
		try {
			// 先要创建目录
			ioOperation.CreateMdr(fileDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// step-3: Generate TestCases
		System.out.println("\nTestCase Generating ...");
		TestGenerator testGenerator = new TestGenerator(model, filePath, app_name, pack_name, mainActivity_name);
		List<String> tst = testGenerator.generateTestCase(0);
		for (String test : tst){
			System.out.println("#########");
			System.out.println(test);
			System.out.println("#########");
		}
		System.out.println("\nTestCase Generating Done!");
	}
}
