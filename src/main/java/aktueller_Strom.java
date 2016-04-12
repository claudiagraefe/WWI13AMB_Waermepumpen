import com.fasterxml.jackson.annotation.JsonProperty;

public class aktueller_Strom {
	private int time;
	private double strom;
	
	/*
	 * Getter & Setter
	 */
	
	@JsonProperty("time")
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	@JsonProperty("strom")
	public double getStrom() {
		return strom;
	}
	public void setStrom(double strom) {
		this.strom = strom;
	}
}
