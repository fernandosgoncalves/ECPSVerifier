package parser;

public class AadlTransition {
	private String action;
	private String source;
	private String target;
	private String guard;
	private String name;

	public AadlTransition(String name, String source, String guard, String target, String action) {
		this.name = name;
		this.source = source;
		this.guard = guard;
		this.target = target;
		this.action = action;
	}

	public AadlTransition(String source, String guard, String target, String action) {
		this.source = source;
		this.guard = guard;
		this.target = target;
		this.action = action;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getGuard() {
		return guard;
	}

	public void setGuard(String guard) {
		this.guard = guard;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String update) {
		this.action = update;
	}
}
