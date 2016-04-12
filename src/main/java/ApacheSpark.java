import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;


public class ApacheSpark {
	private JavaSparkContext sc;
	private SQLContext sqlContext;

	public ApacheSpark() {
		SparkConf conf = new SparkConf().setAppName("egal").setMaster("local[2]");
		sc = new JavaSparkContext(conf);
		sqlContext = new SQLContext(sc);
	}

	private static Function<Row, String> function = new Function<Row, String>() {
		public String call(Row row) {
			return "id: " + row.getInt(0) + ", leistung: " + row.getInt(1) + ", offtime: " + row.getInt(2);
		}
	};
	
	public void testKwhMonotonSteigend() throws Exception {

		//WP Liste als RDD laden. 
		for (Waermepumpe p : Waermepumpen_Controll.wpliste) {
			System.out.println(p.getId() + " XY Leistung: " + p.getLeistung()
					+ " kW" + ", " + p.getLocation());
		}
		
		JavaRDD<Waermepumpe> distData = sc.parallelize(Waermepumpen_Controll.wpliste);

		// Table mit dem Namen "waermepumpen" erstellen
		DataFrame schemaWaermepumpe = sqlContext.createDataFrame(distData, Waermepumpe.class);
		schemaWaermepumpe.registerTempTable("waermepumpen");

		// SQL can be run over RDDs that have been registered as tables.
		// Select Statement Offtime, ID, Leistung aus Waermepumpe.java 
		// Aufsteigend nach offtime sortieren
		DataFrame aktiveWaermepumpenResult = sqlContext.sql("SELECT id, leistung, offtime FROM waermepumpen ORDER BY offtime ASC");

		// The results of SQL queries are DataFrames and support all the normal RDD operations.
		// The columns of a row in the result can be accessed by ordinal.
		List<String> aktiveWaermepumpen = aktiveWaermepumpenResult.javaRDD().map(function).collect();

		System.out.println(aktiveWaermepumpen);

	}
	
	


}
