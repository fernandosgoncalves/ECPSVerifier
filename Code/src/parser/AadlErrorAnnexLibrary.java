package parser;

import java.util.ArrayList;

public class AadlErrorAnnexLibrary {
	private ArrayList<String> errorTypes;
	private ArrayList<AadlErrorBehavior> errorBehaviorModels;

	public AadlErrorAnnexLibrary() {
		this.errorTypes = new ArrayList<>();
		this.errorBehaviorModels = new ArrayList<>();
	}

	public ArrayList<String> getErrorTypes() {
		return errorTypes;
	}

	public void addErrorType(String error) {
		this.errorTypes.add(error);
	}

	public void setErrorTypes(ArrayList<String> errorTypes) {
		this.errorTypes = errorTypes;
	}

	public ArrayList<AadlErrorBehavior> getErrorBehaviorModels() {
		return errorBehaviorModels;
	}

	public void setErrorBehaviorModels(ArrayList<AadlErrorBehavior> errorBehaviorModels) {
		this.errorBehaviorModels = errorBehaviorModels;
	}

	public void addErrorBehaviorModel(AadlErrorBehavior errorBehaviorModel) {
		this.errorBehaviorModels.add(errorBehaviorModel);
	}

	public AadlErrorBehavior getErrorBehaviorByName(String name){
		for(AadlErrorBehavior e : errorBehaviorModels){
			if(e.getName().equalsIgnoreCase(name))
				return e;
		}
		return null;
	}

}
