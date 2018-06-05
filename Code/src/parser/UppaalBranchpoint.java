package parser;

public class UppaalBranchpoint {
	private String locationId;
	private String id;
	private String x;
	private String y;

	public UppaalBranchpoint(String id, String locationId) {
		super();
		this.id = id;
		this.locationId = locationId;
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

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

}
