package sketch.gui.testing;

public enum TConstraint {
	FORALL,
	REC_FORALL,
	EXIST,
	UNINDENTIFIED;
	
	public static TConstraint valueOf(int cur_action) {
		// TODO Auto-generated method stub
		switch (cur_action)
		{
			case 0: return FORALL;
			case 1: return REC_FORALL;
			case 2: return EXIST;
			default: return UNINDENTIFIED;
		}
	}
}
