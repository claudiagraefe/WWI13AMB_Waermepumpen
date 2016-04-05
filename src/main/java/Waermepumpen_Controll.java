import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;


public class Waermepumpen_Controll extends ApplicationFrame {
	private static final long serialVersionUID = 1L;
	final static DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	final static String series1 = "aktueller Verbrauch";
	public static List<Waermepumpe> wpliste;
	public static List<aktueller_Strom> stromliste;
	
	
	public static void main(String[] args) {
		
		/*
		 * Line Chart Initialisierung in der Main 
		 */
        final Waermepumpen_Controll demo = new Waermepumpen_Controll("Wärmepumpenregelung Heidelberg");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
		
		int anzahl_pumpen = 100;
		wpliste  = new LinkedList<>();
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
			wpliste.add(wp);
		}
		
		  for (Waermepumpe p : wpliste) { System.out.println(p.getId() +
		  " Leistung: " + p.getLeistung() + " kW" + ", " +p.getLocation()); }
		 

		/*
		 * Initialisierung der Werte des Stromgraphen 
		 */
		stromliste = new LinkedList<>();
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
			stromliste.add(s);
		}

		/*
		 * Visualisierung des Stromverlaufes mittels Lane Chart
		 * Befüllen des Datasets mit den ermittelten Werten
		 */
		for (aktueller_Strom st : stromliste) {
		//	System.out.println(st.getTime() + " Strom: " + st.getStrom() + " kW" );
			dataset.addValue(st.getStrom(), series1, Double.toString(st.getTime()));
		}

	}
	
	/*
	 * Line Chart
	 */
	
	 public Waermepumpen_Controll (final String title) {
	        super(title);
	        //final CategoryDataset dataset1 = dataset;
	        final JFreeChart chart = createChart(dataset);
	        final ChartPanel chartPanel = new ChartPanel(chart);
	        chartPanel.setPreferredSize(new Dimension(500, 270));
	        setContentPane(chartPanel);
	    }

	    /*
	     * Personalisierung der Ausgabe GUI
	     */
	    private JFreeChart createChart(final CategoryDataset dataset) {
	        
	        // create the chart...
	        final JFreeChart chart = ChartFactory.createLineChart(
	            "Stromauslastung",       // chart title
	            "Zeit",                    // domain axis label
	            "kWh",                   // range axis label
	            dataset,                   // data
	            PlotOrientation.VERTICAL,  // orientation
	            true,                      // include legend
	            true,                      // tooltips
	            false                      // urls
	        );

	        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
//	        final StandardLegend legend = (StandardLegend) chart.getLegend();
	  //      legend.setDisplaySeriesShapes(true);
	    //    legend.setShapeScaleX(1.5);
	      //  legend.setShapeScaleY(1.5);
	        //legend.setDisplaySeriesLines(true);

	        chart.setBackgroundPaint(Color.white);

	        final CategoryPlot plot = (CategoryPlot) chart.getPlot();
	        plot.setBackgroundPaint(Color.lightGray);
	        plot.setRangeGridlinePaint(Color.white);

	        // customise the range axis...
	        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        rangeAxis.setAutoRangeIncludesZero(true);
	        
	        // customise the renderer...
	        final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
//	        renderer.setDrawShapes(true);

	        renderer.setSeriesStroke(
	            0, new BasicStroke(
	                2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
	                1.0f, new float[] {10.0f, 6.0f}, 0.0f
	            )
	        );
	        renderer.setSeriesStroke(
	            1, new BasicStroke(
	                2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
	                1.0f, new float[] {6.0f, 6.0f}, 0.0f
	            )
	        );
	        renderer.setSeriesStroke(
	            2, new BasicStroke(
	                2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
	                1.0f, new float[] {2.0f, 6.0f}, 0.0f
	            )
	        );
	        // OPTIONAL CUSTOMISATION COMPLETED.
	        
	        return chart;
	    }
	
}
