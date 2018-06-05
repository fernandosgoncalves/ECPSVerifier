package parser;

import java.util.ArrayList;

public class UppaalVariable {
	private String initialValue;

	private boolean typedef;
	private boolean broadcast;
	private boolean constant;
	private boolean vector;

	private int size;
	private int type;

	private ArrayList<String> values;

	private String name;
	private String comment;

	private static final int INTID = 1;
	private static final int CLOCKID = 2;
	private static final int CHANID = 3;
	private static final int INTVECTORID = 4;

	private static final String INT = "int";
	private static final String INT1 = "integer";
	private static final String CLOCK = "clock";
	private static final String CHAN = "chan";
	private static final String INTVECTOR = "int vector";


	public UppaalVariable(String name, String type, String initialValue) {
		this.name = name;
		this.type = getTypeId(type);
		this.initialValue = initialValue;

		this.broadcast = false;
		this.constant = false;
		this.typedef = false;
		this.vector = false;

		this.comment = "";

		values = new ArrayList<>();
	}

	public void setComment(String c){
		this.comment = c;
	}

	public String getComment(){
		return this.comment;
	}

	private int getTypeId( String type){
		switch (type) {
		case INT:
			return INTID;
		case INT1:
			return INTID;
		case CLOCK:
			return CLOCKID;
		case CHAN:
			return CHANID;
		case INTVECTOR:
			return INTVECTORID;
		default:
			return -1;
		}
	}

	public String getStringType(){
		switch (type) {
		case INTID:
			return INT;
		case CLOCKID:
			return CLOCK;
		case CHANID:
			return CHAN;
		case INTVECTORID:
			return INT+"[";
		default:
			return null;
		}
	}

	public String getInitialValue() {
		return initialValue;
	}


	public void setInitialValue(String initialValue) {
		this.initialValue = initialValue;
	}


	public boolean isTypedef() {
		return typedef;
	}


	public void setTypedef(boolean typedef) {
		this.typedef = typedef;
	}


	public boolean isBroadcast() {
		return broadcast;
	}


	public void setBroadcast(boolean broadcast) {
		this.broadcast = broadcast;
	}


	public boolean isConstant() {
		return constant;
	}


	public void setConstant(boolean constant) {
		this.constant = constant;
	}


	public boolean isVector() {
		return vector;
	}


	public void setVector(boolean vector) {
		this.vector = vector;
	}


	public int getSize() {
		return size;
	}


	public void setSize(int size) {
		this.size = size;
	}


	public int getType() {
		return type;
	}


	public void setTypeString(String type) {
		this.type = getTypeId(type);
	}

	public void setType(int type) {
		this.type = type;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public ArrayList<String> getValues() {
		return values;
	}

	public void addValue(String value){
		this.values.add(value);
	}
}
