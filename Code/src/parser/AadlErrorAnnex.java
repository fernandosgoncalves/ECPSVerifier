package parser;

import java.util.ArrayList;

public class AadlErrorAnnex {
	private ArrayList<AadlErrorPropagation> errorPropagations;
	private ArrayList<AadlErrorProperty> errorProperties;
	private ArrayList<AadlTransition> propagations;
	private ArrayList<AadlTransition> transitions;
	private ArrayList<AadlTransition> detections;

	private String behavior;
	private String types;

	public AadlErrorAnnex() {
		this.errorPropagations = new ArrayList<>();
		this.errorProperties = new ArrayList<>();
		this.propagations = new ArrayList<>();
		this.transitions = new ArrayList<>();
		this.detections = new ArrayList<>();
		this.behavior = "";
		this.types = "";
	}

	public ArrayList<AadlErrorPropagation> getErrorPropagations() {
		return errorPropagations;
	}

	public void addErrorPropagation(AadlErrorPropagation errorPropagation) {
		this.errorPropagations.add(errorPropagation);
	}

	public ArrayList<AadlErrorProperty> getProperties() {
		return errorProperties;
	}

	public void addProperty(AadlErrorProperty property) {
		this.errorProperties.add(property);
	}

	public ArrayList<AadlTransition> getPropagations() {
		return propagations;
	}

	public void addPropagation(AadlTransition propagation) {
		this.propagations.add(propagation);
	}

	public ArrayList<AadlTransition> getTransitions() {
		return transitions;
	}

	public void addTransition(AadlTransition transition) {
		this.transitions.add(transition);
	}

	public ArrayList<AadlTransition> getDetections() {
		return detections;
	}

	public void addDetection(AadlTransition detection) {
		this.detections.add(detection);
	}

	public String getBehavior() {
		return behavior;
	}

	public void setBehavior(String behavior) {
		this.behavior = behavior;
	}

	public AadlErrorProperty getErrorPropertyOfError(String name){
		for(AadlErrorProperty a : errorProperties){
			if(a.getTarget().equalsIgnoreCase(name))
				return a;
		}
		return null;
	}
	
	
	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}
}
