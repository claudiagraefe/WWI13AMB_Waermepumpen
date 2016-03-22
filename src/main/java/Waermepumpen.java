import java.util.LinkedList;
import java.util.List;

public class Waermepumpen {
	private int id;
	private String location;
	private int leistung;
	private double offtime;

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

	public static void main(String[] args) {
		int anzahl_pumpen = 100;

		List<Waermepumpen> pumpen_hd = new LinkedList<>();

		for (int i = 0; i < anzahl_pumpen; i++) {
			Waermepumpen wp = new Waermepumpen();

			wp.setId(i);
			wp.setLeistung(7 + (int) (Math.random() * 8));

			pumpen_hd.add(wp);
		}

		for (Waermepumpen p : pumpen_hd) {
			System.out.println(p.getId() + " Leistung: " + p.getLeistung() + " kW");
		}

	}

}
