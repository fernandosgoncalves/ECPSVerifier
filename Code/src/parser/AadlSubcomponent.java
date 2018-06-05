package parser;

public class AadlSubcomponent {
	private String implName;
	private String name;

	private int type;

	private static final String SUBPROGRAM = "subprogram";
	private static final String PROCESS = "process";
	private static final String SYSTEM = "system";
	private static final String DEVICE = "device";
	private static final String THREAD = "thread";

	private static final int SUBPROGRAMID = 5;
	private static final int PROCESSID = 3;
	private static final int SYSTEMID = 1;
	private static final int DEVICEID = 2;
	private static final int THREADID = 4;

	public AadlSubcomponent(String implName, String name, String type) {
		super();
		this.implName = implName;
		this.name = name;
		this.type = getTypeId(type);
	}

	protected int getTypeId(String type) {
		switch (type.toLowerCase()) {
		case SYSTEM:
			return SYSTEMID;
		case DEVICE:
			return DEVICEID;
		case PROCESS:
			return PROCESSID;
		case THREAD:
			return THREADID;
		case SUBPROGRAM:
			return SUBPROGRAMID;
		}
		return -1;
	}

	protected String getType(int type) {
		switch (type) {
		case SYSTEMID:
			return SYSTEM;
		case DEVICEID:
			return DEVICE;
		case PROCESSID:
			return PROCESS;
		case THREADID:
			return THREAD;
		case SUBPROGRAMID:
			return SUBPROGRAM;
		}
		return null;
	}

	public String getImplName() {
		return implName;
	}

	public void setImplName(String implName) {
		this.implName = implName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTypeName() {
		return getType(type);
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setType(String type) {
		this.type = getTypeId(type);
	}


}
