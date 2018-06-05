package parser;

import java.util.ArrayList;

public class AadlProcess {
	private ArrayList<AadlFeature> features;

	private String name;


	public AadlProcess(String name) {
		this.name = name;
		this.features = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addFeature(AadlFeature feature){
		this.features.add(feature);
	}

}
