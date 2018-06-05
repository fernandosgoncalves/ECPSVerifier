package parser;

public class AadlFeature {
	private int direction;
	private int type;

	private String subprogramName;
	private String featureName = "";
	private String typeName = ""; // PARAMETER, EVENT, EVENT DATA, DATA,
									// SUBPROGRAM
	// ACCESS
	private int dataType;
	private String name;

	private static final String SUBPROGRAM = "subprogram access";
	private static final String SUBPROGRAM1 = "subprogram";
	private static final String EVENTDATA = "event data";
	private static final String PARAMETER = "parameter";
	private static final String REQUIRES = "requires";
	private static final String PROVIDES = "provides";
	private static final String INOUT = "in out";
	private static final String EVENT = "event";
	private static final String DATA = "data";
	private static final String OUT = "out";
	private static final String IN = "in";

	private static final int SUBPROGRAMID = 5;
	private static final int EVENTDATAID = 4;
	private static final int PARAMETERID = 2;
	private static final int EVENTID = 1;
	private static final int DATAID = 3;

	private static final int REQUIRESID = 5;
	private static final int PROVIDESID = 4;
	private static final int INOUTID = 3;
	private static final int OUTID = 2;
	private static final int INID = 1;

	private static final int INTID = 1;
	private static final int CHANID = 2;

	private static final String INT = "int";
	private static final String INT1 = "integer";
	private static final String CHAN = "chan";

	public AadlFeature(String name, String direction, String type, String subname, String dataType) {
		this.direction = getDirectionId(direction);
		this.subprogramName = subname;
		this.type = getTypeId(type.toLowerCase());
		this.name = name;
		this.dataType = getDataTypeId(dataType);
	}

	public String getDirection() {
		switch (direction) {
		case INID:
			return IN;
		case OUTID:
			return OUT;
		case INOUTID:
			return INOUT;
		case REQUIRESID:
			return REQUIRES;
		case PROVIDESID:
			return PROVIDES;
		}
		return null;
	}

	public void setDirection(String direction) {
		this.direction = getDirectionId(direction);
	}

	public String getDataType() {
		switch (this.dataType) {
		case INTID:
			return INT;
		case CHANID:
			return CHAN;
		default:
			return "";
		}

	}

	public int getDataTypeId(String type) {
		switch (type) {
		case INT:
			return INTID;
		case INT1:
			return INTID;
		case CHAN:
			return CHANID;
		default:
			return -1;
		}
	}

	public int getDirectionId(String direction) {
		switch (direction) {
		case IN:
			return INID;
		case OUT:
			return OUTID;
		case INOUT:
			return INOUTID;
		case REQUIRES:
			return REQUIRESID;
		case PROVIDES:
			return PROVIDESID;
		}
		return -1;
	}

	public String getType() {
		switch (type) {
		case EVENTID:
			return EVENT;
		case DATAID:
			return DATA;
		case PARAMETERID:
			return PARAMETER;
		case EVENTDATAID:
			return EVENTDATA;
		case SUBPROGRAMID:
			return SUBPROGRAM1;
		}
		return null;
	}

	public void setType(String type) {
		this.type = getTypeId(type);
	}

	public int getTypeId(String type) {
		switch (type) {
		case EVENT:
			return EVENTID;
		case DATA:
			return DATAID;
		case PARAMETER:
			return PARAMETERID;
		case EVENTDATA:
			return EVENTDATAID;
		case SUBPROGRAM:
			return SUBPROGRAMID;
		}
		return -1;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubprogramName() {
		return subprogramName;
	}

	public void setSubprogramName(String subprogramName) {
		this.subprogramName = subprogramName;
	}

}
