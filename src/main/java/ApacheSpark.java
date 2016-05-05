import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.*;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.StorageLevels;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;

public class ApacheSpark {

	private static final String host = "localhost";
	private static final int port = 9999;
	public static JavaDStream<Double> result;
	public static double currentValue = 0;
	public static String currentValue2;
	public static int hilfsvariable = 0;
	public static int offtime = 0;
	public static int offtime_helfer = 0;
	public static int zeit_zaehler = 0;

	public static List<Double> avg_strom;
	public static List<Waermepumpe> wpliste_neu;
	public static PriorityQueue<Waermepumpe> waermepumpenQueue;

	private JavaSparkContext sc;
	private SQLContext sqlContext;
	private JavaStreamingContext jssc;

	public ApacheSpark() {
		avg_strom = new LinkedList<>();
		wpliste_neu = new ArrayList<>(Waermepumpen_Controll.wpliste);

		//Initialisieren eines Server-Socket
		// Listen on a server socket and on connection send some \n-delimited
		// text to the client
		new Thread(() -> {
			try {
				ServerSocket serverSocket = new ServerSocket(port);

				while (true) {
					Socket clientSocket = serverSocket.accept();
					PrintWriter out = new PrintWriter(
							clientSocket.getOutputStream());
					// clientSocket.getOutputStream(), true);

				for (aktueller_Strom st : Waermepumpen_Controll.stromliste) {
					out.println(st.getStrom());
					out.flush();

					Thread.sleep(1000);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}	).start();

		//Erstellen eines neuen JavaSparkContext mit dem Namen "Waermepumpenregelung"
		SparkConf conf = new SparkConf().setMaster("local[2]")
				.setAppName("Waermepumpenregelung")
				.set("spark.driver.allowMultipleContexts", "true");
		;
		sc = new JavaSparkContext(conf);
		sqlContext = new SQLContext(sc);
		jssc = new JavaStreamingContext(sc, Durations.seconds(1));

	}
	
	/*Funktionen für Apache Spark SQL s. Kommentar unten*/
	private static Function<Row, String> function = new Function<Row, String>() {
		public String call(Row row) {
			return "id: " + row.getInt(0) + ", leistung: " + row.getInt(1)
					+ ", offtime: " + row.getInt(2);
		}
	};

	private static Function<Row, String> strom_function = new Function<Row, String>() {
		public String call(Row row) {
			return "aktuellerStrom: " + row.getDouble(0);
		}
	};

	public void sortierenAnhandDerOfftime() throws Exception {

		//Java DStream 
		JavaReceiverInputDStream<String> lines = jssc.socketTextStream(host,
				port, StorageLevels.MEMORY_AND_DISK_SER);

		JavaDStream<Double> numbers = lines.map(x -> Double.parseDouble(x));
		JavaDStream<Tuple2<Double, Integer>> numbersAndCount = numbers
				.map(x -> new Tuple2<Double, Integer>(x, 1));
		JavaDStream<Tuple2<Double, Integer>> sumationResult = numbersAndCount
				.reduce((tuple1, tuple2) -> new Tuple2<Double, Integer>(
						tuple1._1 + tuple2._1, tuple1._2 + tuple2._2));

		result = sumationResult.map(x -> x._1 / x._2);

		result.foreachRDD(rdd -> {
			List<Double> values = rdd.collect();
			for (Double value : values) {
				System.out.println("nnnnnn: " + value);
				currentValue = value;

				// AVG-Strom befüllen
				avg_strom.add(currentValue);

				// 100 Pumpen durchlaufen
				for (int j = 0; j <= 100; j++) {

					// Arrayliste wpliste_neu durchlaufen
					for (int q = 0; q < 100; q++) {

						hilfsvariable = hilfsvariable
								+ wpliste_neu.get(q).getLeistung();
						offtime_helfer = wpliste_neu.get(q).getOfftime();

						if (hilfsvariable <= currentValue) {
							offtime = wpliste_neu.get(q).getOfftime() + 5;
							wpliste_neu.get(q).setOfftime(offtime);

						}
						// HIER MÜSSTE SORTIERT WERDEN !
						Collections.sort(wpliste_neu);
					}
				}

				hilfsvariable = 0;
				for (Waermepumpe p : wpliste_neu) {
					System.out.println("Offtime: " + p.getOfftime()
							+ " der Id: " + p.getId());
				}

			}
			for (Double avg : avg_strom) {
				System.out.println(avg);
			}

		});

		/*-----------------------jetzt folgt Apache Spark SQL, was für Erweiterungen 
		 *  von Bedeutung sein könnte für den aktuellen Programmablauf
		 *  jedoch nicht relevant ist---------------------------------------------*/

		// WP Liste als RDD laden.
		JavaRDD<Waermepumpe> distData = sc
				.parallelize(Waermepumpen_Controll.wpliste);
		// akueller_Strom
		JavaRDD<aktueller_Strom> aktuellerStrom = sc
				.parallelize(Waermepumpen_Controll.stromliste);

		// Table mit dem Namen "waermepumpen" erstellen
		DataFrame schemaWaermepumpe = sqlContext.createDataFrame(distData,
				Waermepumpe.class);
		schemaWaermepumpe.registerTempTable("waermepumpen");

		// Table mit dem Namen "stromliste" erstellen
		DataFrame schemaStromliste = sqlContext.createDataFrame(aktuellerStrom,
				aktueller_Strom.class);
		schemaStromliste.registerTempTable("stromliste");

		// neu
		sqlContext.cacheTable("waermepumpen");
		sqlContext.cacheTable("stromliste");

		// SQL can be run over RDDs that have been registered as tables.
		// Select Statement Offtime, ID, Leistung aus Waermepumpe.java
		// Aufsteigend nach offtime sortieren

		// Average berechnen direkt in SQL mit dazugeben
		DataFrame aktiveWaermepumpenResult = sqlContext
				.sql("SELECT id, leistung, offtime FROM waermepumpen ORDER BY offtime ASC");

		// The results of SQL queries are DataFrames and support all the normal
		// RDD operations.
		// The columns of a row in the result can be accessed by ordinal.
		List<String> aktiveWaermepumpen = aktiveWaermepumpenResult.javaRDD()
				.map(function).collect();
		System.out.println(aktiveWaermepumpen);

		DataFrame aktueller_StromResult = sqlContext
				.sql("SELECT strom FROM stromliste");
		List<String> aktueller_Strom = aktueller_StromResult.javaRDD()
				.map(strom_function).collect();
		// System.out.println(aktueller_Strom);

		jssc.start();
		jssc.awaitTermination();
		jssc.close();

	}

}
