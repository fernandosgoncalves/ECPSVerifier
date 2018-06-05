package parser;

import java.util.ArrayList;

public class UppaalTransition {

	private ArrayList<UppaalLabel> labels;
	private ArrayList<UppaalNail> nails;

	private String source;
	private String target;

	public UppaalTransition(String source, String target) {
		super();
		this.source = source;
		this.target = target;
		nails = new ArrayList<>();
		labels = new ArrayList<>();
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

	public ArrayList<UppaalNail> getNails() {
		return nails;
	}

	public void setNails(ArrayList<UppaalNail> nails) {
		this.nails = nails;
	}

	public void addNail(UppaalNail nail) {
		this.nails.add(nail);
	}

}
