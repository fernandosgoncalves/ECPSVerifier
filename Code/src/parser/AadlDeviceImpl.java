package parser;

public class AadlDeviceImpl {
	private AadlErrorAnnex errorAnnex;

	private String parentName;
	private String implName;
	private String name;

	public AadlDeviceImpl(String name, String implName, String parentName) {
		this.name = name;
		this.implName = implName;
		this.parentName = parentName;
		this.errorAnnex = new AadlErrorAnnex();
	}

	public AadlDeviceImpl(String name, String parentName) {
		this.parentName = parentName;
		this.implName = null;
		this.name = name;
		this.errorAnnex = new AadlErrorAnnex();
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
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public void setErrorAnnex(AadlErrorAnnex error){
		this.errorAnnex = error;
	}

	public AadlErrorAnnex getAnnexError(){
		return this.errorAnnex;
	}

}
