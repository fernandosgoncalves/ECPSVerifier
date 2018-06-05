package parser;

import java.util.ArrayList;

public class AadlThread {
	private ArrayList<AadlFeature> features;

	private String name;

	public AadlThread(String name) {
		this.name = name;
		this.features = new ArrayList<>();
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

	public ArrayList<AadlFeature> getFeatures() {
		return this.features;
	}

	public boolean isSubprogramFeature(String name) {
		for (AadlFeature f : features) {
			if (f.getName().equalsIgnoreCase(name) && f.getType().equalsIgnoreCase("subprogram"))
				return true;
		}
		return false;
	}
}
