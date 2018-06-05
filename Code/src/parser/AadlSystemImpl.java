package parser;

import java.util.ArrayList;

public class AadlSystemImpl {
	private ArrayList<AadlSubcomponent> subcomponents;
	private ArrayList<AadlConnection> connections;

	private String parentName;
	private String name;

	public AadlSystemImpl(String name, String parentName) {
		super();
		this.name = name;
		this.parentName = parentName;
		subcomponents = new ArrayList<>();
		connections = new ArrayList<>();
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

	public AadlSubcomponent getSubSystemByName(String name) {
		if (!subcomponents.isEmpty()) {
			for (AadlSubcomponent s : subcomponents) {
				if (s.getName().equals(name))
					return s;
			}
		}
		return null;
	}

	public void updateSubcomponentByName(AadlSubcomponent subSystem) {
		if (!subcomponents.isEmpty()) {
			for (int i = 0; i < subcomponents.size(); i++) {
				if (subcomponents.get(i).getName().equals(subSystem.getName())) {
					subcomponents.set(i, subSystem);
				}
			}
		}
	}

	public void addSubcomponent(AadlSubcomponent subSystem) {
		subcomponents.add(subSystem);
	}

	public void addConnection(AadlConnection connection) {
		connections.add(connection);
	}

}
