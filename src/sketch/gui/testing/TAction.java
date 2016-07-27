package sketch.gui.testing;

public enum TAction {
	CLICK, 
	LONG_CLICK, 
	DRAG,
	TIMER,
	FORK,
	
	UNINDENTIFIED;

	public static TAction valueOf(int cur_action) {
		// TODO Auto-generated method stub
		switch (cur_action)
		{
			case 0: return CLICK;
			case 1: return LONG_CLICK;
			case 2: return DRAG;
			default: return UNINDENTIFIED;
		}
	}
}
