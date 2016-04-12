import com.fasterxml.jackson.annotation.JsonProperty;

public class Stromfluss {
	private int max_strom;
	private int aktueller_verbrauch;
	private int aktuell_verfuegbar;
	private int intervall;
	
	@JsonProperty("max_strom")
	public int getMax_strom() {
		return max_strom;
	}
	public void setMax_strom(int max_strom) {
		this.max_strom = max_strom;
	}
	@JsonProperty("aktueller_verbrauch")
	public int getAktueller_verbrauch() {
		return aktueller_verbrauch;
	}
	public void setAktueller_verbrauch(int aktueller_verbrauch) {
		this.aktueller_verbrauch = aktueller_verbrauch;
	}
	@JsonProperty("aktueller_verfuegbar")
	public int getAktuell_verfuegbar() {
		return aktuell_verfuegbar;
	}
	public void setAktuell_verfuegbar(int aktuell_verfuegbar) {
		this.aktuell_verfuegbar = aktuell_verfuegbar;
	}
	@JsonProperty("Intervall")
	public int getIntervall() {
		return intervall;
	}
	public void setIntervall(int intervall) {
		this.intervall = intervall;
	}
	
	
}
