package parser;

public class AadlErrorPropagation {
	private String name;
	private String direction;
	private String propagation;

	public AadlErrorPropagation(String name, String direction, String propagation) {
		this.name = name;
		this.direction = direction;
		this.propagation = propagation;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getPropagation() {
		return propagation;
	}
	public void setPropagation(String propagation) {
		this.propagation = propagation;
	}



}
