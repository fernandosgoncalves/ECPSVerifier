package parser;

import java.util.ArrayList;

public class AadlSubprogram {
	private ArrayList<AadlProperty> properties;
	private ArrayList<AadlFeature> features;

	private String name;

	public AadlSubprogram(String name){
		this.name = name;
		features = new ArrayList<>();
		properties = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addFeature(AadlFeature feature){
		features.add(feature);
	}

	public void addProperty(AadlProperty property){
		properties.add(property);
	}


}
