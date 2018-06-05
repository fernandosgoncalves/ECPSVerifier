package parser;

public class AadlProperty {
	private int initTime;
	private int deadline;
	private int endTime;
	private int period;

	private int type;

	private String measurement;
	private String dispatch;
	private String value;

	private static final String COMPUTE = "compute_execution_time";
	private static final String DISPATCH = "dispatch_protocol";
	private static final String LANGUAGE = "source_language";
	private static final String PRIORITY = "priority";
	private static final String DEADLINE = "deadline";
	private static final String PERIOD = "period";
	private static final String TEXT = "source_text";

	private static final int PRIORITYID = 7;
	private static final int LANGUAGEID = 2;
	private static final int DISPATCHID = 4;
	private static final int DEADLINEID = 5;
	private static final int COMPUTEID = 1;
	private static final int PERIODID = 6;
	private static final int TEXTID = 3;

	public AadlProperty(String type, int initTime, int endTime, String measurement) {
		//System.out.println("1 " + type);
		this.measurement = measurement;
		this.initTime = initTime;
		this.endTime = endTime;
		this.type = getTypeId(type);

	}

	public AadlProperty(String type, String value) {
		//System.out.println("2 " + type);
		this.value = value;
		this.type = getTypeId(type);
	}

	public AadlProperty(String type, String value, String measurement) {
		//System.out.println("3 " + type);
		this.value = value;
		this.type = getTypeId(type);
		this.measurement = measurement;
	}

	protected int getTypeId(String type) {
		switch (type) {
		case COMPUTE:
			return COMPUTEID;
		case LANGUAGE:
			return LANGUAGEID;
		case TEXT:
			return TEXTID;
		case DEADLINE:
			return DEADLINEID;
		case DISPATCH:
			return DISPATCHID;
		case PERIOD:
			return PERIODID;
		case PRIORITY:
			return PRIORITYID;
		default:
			return -1;
		}
	}

	public String getType(){
		//System.out.println(type);
		switch (this.type) {
		case COMPUTEID:
			return COMPUTE;
		case LANGUAGEID:
			return LANGUAGE;
		case TEXTID:
			return TEXT;
		case DEADLINEID:
			return DEADLINE;
		case DISPATCHID:
			return DISPATCH;
		case PERIODID:
			return PERIOD;
		case PRIORITYID:
			return PRIORITY;
		default:
			return null;
		}
	}

	public int getInitTime() {
		return initTime;
	}

	public void setInitTime(int initTime) {
		this.initTime = initTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMeasurement() {
		return measurement;
	}

	public void setMeasurement(String measurement) {
		this.measurement = measurement;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getDeadline() {
		return deadline;
	}

	public int getPeriod() {
		return period;
	}

	public String getDispatch() {
		return dispatch;
	}

}
