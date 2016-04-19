import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.StorageLevels;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.api.java.JavaDStream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import scala.Tuple2;

public class ApacheSpark {

	private static final String host = "localhost";
	private static final int port = 9999;
	public static JavaDStream<Double> result;
	public static double currentValue = 0;
	public static String currentValue2;
	
	public static List<Double> avg_strom;

	private JavaSparkContext sc;
	private SQLContext sqlContext;
	private JavaStreamingContext jssc;

	public ApacheSpark() {
		avg_strom = new LinkedList<>();

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
				// for (; true;) {
				// out.println(distData2);
				// // out.println("Das ist ein Test");
				// out.flush();
				// Thread.sleep(1000);
				// }
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}	).start();

		SparkConf conf = new SparkConf().setMaster("local[2]")
				.setAppName("Waermepumpenregelung")
				.set("spark.driver.allowMultipleContexts", "true");
		;
		sc = new JavaSparkContext(conf);
		sqlContext = new SQLContext(sc);
		jssc = new JavaStreamingContext(sc, Durations.seconds(1));

	}

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

	public void testKwhMonotonSteigend() throws Exception {

		// Create a JavaReceiverInputDStream on target ip:port and count the
		// words in input stream of \n delimited text

		JavaReceiverInputDStream<String> lines = jssc.socketTextStream(host,
				port, StorageLevels.MEMORY_AND_DISK_SER);
		// JavaReceiverInputDStream<String> lines =
		// jssc.sparkContext().parallelize(Waermepumpen_Controll.wpliste);

		// map

//		JavaDStream<String> words = lines.flatMap(x -> Lists.newArrayList(x
//				.split(" ")));

		JavaDStream<Double> numbers = lines.map(x -> Double.parseDouble(x));
		JavaDStream<Tuple2<Double, Integer>> numbersAndCount = numbers
				.map(x -> new Tuple2<Double, Integer>(x, 1));
		JavaDStream<Tuple2<Double, Integer>> sumationResult = numbersAndCount
				.reduce((tuple1, tuple2) -> new Tuple2<Double, Integer>(
						tuple1._1 + tuple2._1, tuple1._2 + tuple2._2));

		result = sumationResult.map(x -> x._1 / x._2);
		
		result.foreachRDD(rdd -> {
			List<Double> values = rdd.collect();
			for(Double value : values) {
				System.out.println("nnnnnn: " + value);
				currentValue = value;

				avg_strom.add(currentValue);
				
				
			}
			for (Double avg : avg_strom) {
				System.out.println(avg);
			}
			
		});
	
	// double test = Double.valueOf(result.glom());
	//	avg_strom.add(test);
			
		// JavaPairDStream<String, Integer> wordCounts = words.mapToPair(
		// s -> new Tuple2<String, Integer>(s, 7)).reduceByKey(
		// (i1, i2) -> i1 + i2);

		// JavaPairDStream<String, Integer> wordCounts = words.mapToPair(
		// s -> new Tuple2<String, Integer>(s, 7)).reduceByKey(
		// (i1, i2) -> i1 + i2);

		// WP Liste als RDD laden.
		JavaRDD<Waermepumpe> distData = sc
				.parallelize(Waermepumpen_Controll.wpliste);
		// akueller_Strom
		JavaRDD<aktueller_Strom> aktuellerStrom = sc
				.parallelize(Waermepumpen_Controll.stromliste);

//		distData2 = jssc.sparkContext().parallelize(
//				Waermepumpen_Controll.wpliste);

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
		System.out.println(aktueller_Strom);

//		wordCounts.print();
		jssc.start();

		jssc.awaitTermination();
		jssc.close();

	}

	// map : Nimmt ein individuelles Element im Input RDD und produziert ein
	// neues Output-Element
	// rdd.map(new Function<Integer, Integer> (){
	// public Integer call(Integer x) {return x+1;}
	// });

}
