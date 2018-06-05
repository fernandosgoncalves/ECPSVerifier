package parser;

public class AadlState {
	private boolean complete;
	private boolean initial;
	private boolean finalState;

	private String name;

	public AadlState(String name, boolean initial, boolean complete, boolean finalState) {
		this.name = name;
		this.initial = initial;
		this.complete = complete;
		this.finalState = finalState;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	public boolean isInitial() {
		return initial;
	}

	public void setInitial(boolean initial) {
		this.initial = initial;
	}

	public boolean isFinal() {
		return finalState;
	}

	public void setFinalState(boolean finalState) {
		this.finalState = finalState;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
