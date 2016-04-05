import com.fasterxml.jackson.annotation.JsonProperty;

public class Waermepumpe {
	private int id;
	private String location;
	private int leistung;
	private double offtime;

	/*
	 * Getter & Setter
	 */

	@JsonProperty("id")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLeistung() {
		return leistung;
	}

	public void setLeistung(int leistung) {
		this.leistung = leistung;
	}

	@JsonProperty("location")
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public double getOfftime() {
		return offtime;
	}

	public void setOfftime(double offtime) {
		this.offtime = offtime;
	}

}
