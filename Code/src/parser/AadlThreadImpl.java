package parser;

import java.util.ArrayList;

public class AadlThreadImpl {
	private String parentName;
	private String name;

	private ArrayList<AadlTransition> transitions;
	private ArrayList<AadlProperty> properties;
	private ArrayList<AadlVariable> variables;
	private ArrayList<AadlState> states;
	private ArrayList<AadlCall> calls;

	public AadlThreadImpl(String name, String parentName) {
		//TODO Evaluate the possibility to migrate the behavior annex to a separated file
		this.name = name;
		this.parentName = parentName;

		this.transitions = new ArrayList<>();
		this.properties = new ArrayList<>();
		this.variables = new ArrayList<>();
		this.states = new ArrayList<>();
		this.calls = new ArrayList<>();
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addCall(AadlCall call) {
		calls.add(call);
	}

	public void addProperty(AadlProperty property) {
		properties.add(property);
	}

	public void addVariable(AadlVariable var) {
		variables.add(var);
	}

	public void addState(AadlState state) {
		states.add(state);
	}

	public ArrayList<AadlProperty> getProperties(){
		return properties;
	}

	public ArrayList<AadlState> getStates(){
		return this.states;
	}

	public ArrayList<AadlTransition> getTransitions(){
		return this.transitions;
	}

	public ArrayList<AadlVariable> getVariables(){
		return this.variables;
	}


	public void addTransition(AadlTransition transition) {
		transitions.add(transition);
	}
}
