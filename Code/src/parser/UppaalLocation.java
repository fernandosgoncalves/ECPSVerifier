package parser;

import java.util.ArrayList;

public class UppaalLocation {

	private boolean commited;

	private ArrayList<UppaalLabel> labels;
	private String nameX;
	private String nameY;
	private String name;
	private String id;
	private String x;
	private String y;

	public UppaalLocation() {
		commited = false;
		this.labels = new ArrayList<>();
	}

	public UppaalLocation(String id, String x, String y, String name, String nameX, String nameY, boolean commited) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.name = name;
		this.nameX = nameX;
		this.nameY = nameY;
		this.commited = commited;
		this.labels = new ArrayList<>();
	}

	public boolean isCommited() {
		return commited;
	}

	public void setCommited(boolean commited) {
		this.commited = commited;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public String getNameX() {
		return nameX;
	}

	public void setNameX(String nameX) {
		this.nameX = nameX;
	}

	public String getNameY() {
		return nameY;
	}

	public void setNameY(String nameY) {
		this.nameY = nameY;
	}

	public ArrayList<UppaalLabel> getLabels() {
		return labels;
	}

	public void setLabels(ArrayList<UppaalLabel> labels) {
		this.labels = labels;
	}

	public void addLabel(UppaalLabel label) {
		this.labels.add(label);
	}

}
