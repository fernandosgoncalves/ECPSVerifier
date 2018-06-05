package parser;

public class AadlConnection {
	private String name;
	private int type;
	private String sourceName;
	private String targetName;

	private static final String PORT = "port";

	private static final int PORTID = 1;

	public AadlConnection(String name, String type, String sourceName, String targetName) {
		super();
		this.name = name;
		this.type = getConnectionTypeId(type);
		this.sourceName = sourceName;
		this.targetName = targetName;
	}

	private int getConnectionTypeId(String type) {
		switch (type) {
		case PORT:
			return PORTID;
		}

		return -1;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getPortType(){
		switch (type) {
		case PORTID:
			return PORT;
		}
		return null;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}
}
