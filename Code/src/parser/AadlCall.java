package parser;

public class AadlCall {
	String subprogram;
	String name;

	public AadlCall(String name, String subprogram) {
		this.name = name;
		this.subprogram = subprogram;
	}

	public String getSubprogram() {
		return subprogram;
	}

	public void setSubprogram(String subprogram) {
		this.subprogram = subprogram;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
