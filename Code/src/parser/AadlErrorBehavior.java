package parser;

import java.util.ArrayList;

public class AadlErrorBehavior {
	private String name;
	private ArrayList<String> events;
	private ArrayList<AadlState> states;
	private ArrayList<AadlTransition> transitions;

	public AadlErrorBehavior(String name) {
		super();
		this.name = name;
		this.events = new ArrayList<>();
		this.states = new ArrayList<>();
		this.transitions = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getEvents() {
		return events;
	}

	public void setEvents(ArrayList<String> events) {
		this.events = events;
	}

	public void addEvents(String event) {
		this.events.add(event);
	}

	public ArrayList<AadlState> getStates() {
		return states;
	}

	public void setStates(ArrayList<AadlState> states) {
		this.states = states;
	}

	public void addState(AadlState state) {
		this.states.add(state);
	}

	public ArrayList<AadlTransition> getTransitions() {
		return transitions;
	}

	public void setTransitions(ArrayList<AadlTransition> transitions) {
		this.transitions = transitions;
	}

	public void addTransitions(AadlTransition transition) {
		this.transitions.add(transition);
	}

}
