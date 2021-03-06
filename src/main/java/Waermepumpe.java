import java.io.Serializable;
import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Waermepumpe implements Serializable, Comparable<Waermepumpe> {
	private int id;
	private String location;
	private double x_koord;
	private double y_koord;
	private int leistung;
	private int offtime;
	private String name;

	public Waermepumpe(int i) {
		setName("" + i);
		setId(i + 1);
		setLeistung(7 + (int) (Math.random() * 9));

		y_koord = Math.round((49.3587 + (Math.random() * 0.0762)) * 10000) / 10000.0;
		x_koord = Math.round((8.6171 + (Math.random() * 0.1009)) * 10000) / 10000.0;
		setLocation(x_koord + ", " + y_koord);

		setOfftime(0);

	}

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

	@JsonProperty("leistung")
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

	@JsonProperty("offtime")
	public int getOfftime() {
		return offtime;
	}

	public void setOfftime(int offtime) {
		this.offtime = offtime;
	}

	public double getX_koord() {
		return x_koord;
	}

	public void setX_koord(double x_kord) {
		this.x_koord = x_kord;
	}

	public double getY_koord() {
		return y_koord;
	}

	public void setY_koord(double y_koord) {
		this.y_koord = y_koord;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int compareTo(Waermepumpe waermepumpe) {
		int offtime = waermepumpe.getOfftime();
		return this.offtime - offtime;
	}

	public static Comparator<Waermepumpe> WaermepumpeComparator = new Comparator<Waermepumpe>() {
		public int compare(Waermepumpe waermepumpe1, Waermepumpe waermepumpe2) {
			return waermepumpe1.compareTo(waermepumpe2);
		}
	};

}
