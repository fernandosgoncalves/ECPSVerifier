package parser;

public class AadlErrorProperty {
	/*
	 * Only Occurrence Distribution will be covered the another's will be
	 * ignored at this time
	 */
	String property;
	String target;

	public AadlErrorProperty(String property, String target) {
		super();
		this.property = property;
		this.target = target;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	public String getProbValue(){
		String aux = property.substring(property.indexOf('>')+1, property.indexOf(';')).trim();
		int auxint = (int) Double.parseDouble(aux);
		aux = Integer.toString(auxint);
		return aux;
	}
}
