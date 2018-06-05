package parser;

import java.util.ArrayList;

public class AadlDevice {
	private ArrayList<AadlProperty> properties;
	private ArrayList<AadlFeature> features;

	private String name;

	public AadlDevice(String name) {
		this.name = name;
		this.features = new ArrayList<>();
		this.properties = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addFeature(AadlFeature feature) {
		this.features.add(feature);
	}

	public void addProperty(AadlProperty property) {
		this.properties.add(property);
	}

}
