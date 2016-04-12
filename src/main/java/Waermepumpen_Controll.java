import static spark.Spark.*;

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

import com.fasterxml.jackson.databind.ObjectMapper;

public class Waermepumpen_Controll extends ApplicationFrame {

	private static final long serialVersionUID = 1L;
	final static DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	final static String series1 = "aktueller Verbrauch";
	public static List<Waermepumpe> wpliste;
	public static List<aktueller_Strom> stromliste;
	public static ObjectMapper mapper = new ObjectMapper();

	public static void main(String[] args) throws Exception {

		// Umwandlung der Ergebnisse in einen JSON String zur Weiterverarbeitung
		// in Javascript.
		// Abrufbar unter der URL /wp/list bzw. /strom/list

		// Übergabe wp-Liste aus Waermepumpe.java
		externalStaticFileLocation("src/main/resources");
		get("/wp/list", (req, res) -> {
			return mapper.writeValueAsString(wpliste);

		});
		// Übergabe stromliste aus aktueller_Strom.java
		get("/strom/list", (req, res) -> {
			return mapper.writeValueAsString(stromliste);

		});

		// Line Chart Initialisierung in der Main
		final Waermepumpen_Controll demo = new Waermepumpen_Controll(
				"Wärmepumpenregelung Heidelberg");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);

		Stromfluss sf = new Stromfluss();
		sf.setIntervall(5);

		// Initialisierung von Waermepumpen und Stromverbrauch
		setWaermepumpenliste(sf);
		setStromliste(sf);

		// Visualisierung des Stromverlaufes mittels Lane Chart Befüllen des
		// Datasets mit den ermittelten Werten
		for (aktueller_Strom st : stromliste) {
			// System.out.println(st.getTime() + " Strom: " + st.getStrom() +
			// " kW" );
			dataset.addValue(st.getStrom(), series1,
					Double.toString(st.getTime()));
		}

		// Ausgabe der Daten der Waermepumpen
		for (Waermepumpe p : wpliste) {
			System.out.println(p.getId() + " Leistung: " + p.getLeistung()
					+ " kW" + ", " + p.getLocation());
		}

		ApacheSpark spark = new ApacheSpark();
		spark.testKwhMonotonSteigend();

	}

	/**
	 * @Describtion Constructor fuer Line Chart
	 * @param title
	 */
	public Waermepumpen_Controll(final String title) {
		super(title);
		// final CategoryDataset dataset1 = dataset;
		final JFreeChart chart = createChart(dataset);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(500, 270));
		setContentPane(chartPanel);
	}

	/**
	 * @Describtion Personalisierung der Ausgabe GUI
	 * @param dataset
	 * @return
	 */
	private JFreeChart createChart(final CategoryDataset dataset) {

		// create the chart...
		final JFreeChart chart = ChartFactory.createLineChart(
				"Stromauslastung", // chart title
				"Zeit", // domain axis label
				"kWh", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				true, // tooltips
				false // urls
				);

		chart.setBackgroundPaint(Color.white);

		final CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.white);

		// customise the range axis...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setAutoRangeIncludesZero(true);

		// customise the renderer...
		final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot
				.getRenderer();
		// renderer.setDrawShapes(true);

		renderer.setSeriesStroke(0, new BasicStroke(2.0f,
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f,
				new float[] { 10.0f, 6.0f }, 0.0f));
		renderer.setSeriesStroke(1, new BasicStroke(2.0f,
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f,
				new float[] { 6.0f, 6.0f }, 0.0f));
		renderer.setSeriesStroke(2, new BasicStroke(2.0f,
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f,
				new float[] { 2.0f, 6.0f }, 0.0f));
		// OPTIONAL CUSTOMISATION COMPLETED.

		return chart;
	}

	/**
	 * @Describtion Initialisierung der Werte der Wärmepumpen
	 * @param sf
	 */
	public static void setWaermepumpenliste(Stromfluss sf) {
		int anzahl_pumpen = 100;
		wpliste = new LinkedList<>();

		for (int i = 0; i < anzahl_pumpen; i++) {
			Waermepumpe wp = new Waermepumpe(i);
			sf.setMax_strom(sf.getMax_strom() + wp.getLeistung());
			wpliste.add(wp);
		}

		for (Waermepumpe p : wpliste) {
			System.out.println(p.getId() + " Leistung: " + p.getLeistung()
					+ " kW" + ", " + p.getLocation() + ", " + "Name: "
					+ p.getName());
		}
	}

	/**
	 * @Description Initialisierung der Werte des Stromgraphen
	 * @param sf
	 */
	public static void setStromliste(Stromfluss sf) {
		stromliste = new LinkedList<>();

		double alpha = 0.3;
		double nw = sf.getMax_strom() / 2;
		int time_max = 300;

		for (int i = 0; i < time_max; i++) {
			aktueller_Strom s = new aktueller_Strom();
			s.setTime(i);
			s.setStrom(nw);
			nw = Math
					.round((nw * alpha) + (Math.random() * (1 - alpha) * 1000));
			/*
			 * Um auszuschließen, dass Werte über unserem maximalen Strom
			 * ausgegeben werden
			 */
			if (nw > sf.getMax_strom()) {
				nw = sf.getMax_strom() / 2;
			}
			stromliste.add(s);
		}

		// Visualisierung des Stromverlaufes mittels Lane Chart Befüllen
		// desDatasets mit den ermittelten Werten

		for (aktueller_Strom st : stromliste) {
			// System.out.println(st.getTime() + " Strom: " + st.getStrom() +
			// " kW" );
			dataset.addValue(st.getStrom(), series1,
					Double.toString(st.getTime()));
		}

		new JavaSpark(new Waermepumpen_Controll(null));

	}

}
