package parser;

public class AadlSystem {
	//private boolean implemented = false;
	private boolean root = false;

	private String name;

	public AadlSystem(String name) {
		this.name = name;
	}

	public AadlSystem(String name, boolean root) {
		this.root = root;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}

	/*public boolean isImplemented() {
		return implemented;
	}

	public void setImplemented(boolean implemented) {
		this.implemented = implemented;
	}*/

}
