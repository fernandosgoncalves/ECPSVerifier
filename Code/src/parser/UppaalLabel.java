package parser;

public class UppaalLabel {
	private String expression;
	private String x;
	private String y;

	private int kind;

	protected static final String EXPO = "exponentialrate";
	protected static final String SYNC = "synchronisation";
	protected static final String ASSIG = "assignment";
	protected static final String PROB = "probability";
	protected static final String INVA = "invariant";
	protected static final String COMM = "comments";
	protected static final String GUARD = "guard";
	
	public UppaalLabel(String expression, int kind, String x, String y) {
		this.expression = expression;
		this.kind = kind;
		this.x = x;
		this.y = y;
	}

	public UppaalLabel(String expression, String kind) {
		this.expression = expression;
		this.kind = getKindId(kind);
		this.x = "";
		this.y = "";
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getKind() {
		switch (kind) {
		case 1:
			return GUARD;
		case 2:
			return SYNC;
		case 3:
			return ASSIG;
		case 4:
			return INVA;
		case 5:
			return EXPO;
		case 6: 
			return COMM;
		case 7:
			return PROB;
		}
		return null;
	}

	public int getKindId(String kind) {
		kind = kind.toLowerCase();
		switch (kind) {
		case GUARD:
			return 1;
		case SYNC:
			return 2;
		case ASSIG:
			return 3;
		case INVA:
			return 4;
		case EXPO:
			return 5;
		case COMM:
			return 6;
		case PROB:
			return 7;
		}
		return -1;
	}

	public void setKind(int kind) {
		this.kind = kind;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

}
