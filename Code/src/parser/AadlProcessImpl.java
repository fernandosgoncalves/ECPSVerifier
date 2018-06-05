package parser;

import java.util.ArrayList;

public class AadlProcessImpl {
	private String name;
	private String parentName;
	private ArrayList<AadlSubcomponent> subcomponents;
	private ArrayList<AadlConnection> connections;

	public AadlProcessImpl(String name, String parentName) {
		super();
		this.name = name;
		this.parentName = parentName;
		this.subcomponents = new ArrayList<>();
		this.connections = new ArrayList<>();
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

	public void addSubcomponent(AadlSubcomponent s){
		subcomponents.add(s);
	}

	public void addConnection(AadlConnection c){
		connections.add(c);
	}
}
