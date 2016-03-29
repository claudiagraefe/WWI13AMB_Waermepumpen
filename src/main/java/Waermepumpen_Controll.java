import java.util.LinkedList;
import java.util.List;

public class Waermepumpen_Controll {
	public static void main(String[] args) {
		int anzahl_pumpen = 100;

		List<Waermepumpe> pumpen_hd = new LinkedList<>();
		Stromfluss sf = new Stromfluss();
		sf.setIntervall(5);

		/*
		 * Initialisieren der Waermepumpen
		 */
		for (int i = 0; i < anzahl_pumpen; i++) {
			Waermepumpe wp = new Waermepumpe();

			wp.setId(i + 1);
			wp.setLeistung(7 + (int) (Math.random() * 9));
			sf.setMax_strom(sf.getMax_strom() + wp.getLeistung());
			
			double x_kord = Math.round((49.3587 + (Math.random() * 0.0762))*10000)/10000.0;
			double y_kord = Math.round((8.6171 + (Math.random() * 0.1009))*10000)/10000.0;
			wp.setLocation(x_kord+";"+y_kord);

			wp.setOfftime(0);
			pumpen_hd.add(wp);
		}

		
		  for (Waermepumpe p : pumpen_hd) { System.out.println(p.getId() +
		  " Leistung: " + p.getLeistung() + " kW" + ", " +p.getLocation()); }
		 

		/*
		 * Initialisierung der Werte des Stromgraphen 
		 */
		List<aktueller_Strom> stromgraph = new LinkedList<>();
		double alpha = 0.3;
		double nw = sf.getMax_strom() / 2;
		int time_max = 300;

		for (int i = 0; i < time_max; i++) {
			aktueller_Strom s = new aktueller_Strom();
			s.setTime(i);
			s.setStrom(nw);
			nw = Math.round((nw * alpha) + (Math.random() * (1 - alpha) * 1000));
			/*
			 * Um auszuschließen, dass Werte über unserem maximalen Strom ausgegeben werden
			 */
			if(nw > sf.getMax_strom()){
				nw = sf.getMax_strom() / 2;
			}
			stromgraph.add(s);
		}

		/*for (aktueller_Strom st : stromgraph) {
		*	System.out.println(st.getTime() + " Strom: " + st.getStrom() + " kW" );
		*}
		*/
		

	}
}
