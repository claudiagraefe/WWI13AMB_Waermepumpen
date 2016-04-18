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

import com.google.common.collect.Lists;

import scala.Tuple2;




public class ApacheSpark {
	 

	private static final String host = "localhost";
	 private static final int port = 9999;

	private JavaSparkContext sc;
	private SQLContext sqlContext;
	private JavaStreamingContext jssc;

	public ApacheSpark() {	
		
		   // Listen on a server socket and on connection send some \n-delimited text to the client
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(port);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                    for (; true;) {
                    //	out.println(new Date().toString());
                        out.println("Das ist ein Test");
                        out.flush();
                        Thread.sleep(100);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
  



		
		
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("Waermepumpenregelung");
		//sc = new JavaSparkContext(conf);
		//sqlContext = new SQLContext(sc);
		jssc = new JavaStreamingContext(conf, Durations.seconds(5));
		
		//JavaReceiverInputDStream<String> logDataDStream =
		 //       jssc.socketTextStream("localhost", 9999);		
		
		
	}

	private static Function<Row, String> function = new Function<Row, String>() {
		public String call(Row row) {
			return "id: " + row.getInt(0) + ", leistung: " + row.getInt(1) + ", offtime: " + row.getInt(2);
		}
	};
	
	public void testKwhMonotonSteigend() throws Exception {

		
        // Create a JavaReceiverInputDStream on target ip:port and count the words in input stream of \n delimited text
      
		
		JavaReceiverInputDStream<String> lines = jssc.socketTextStream(host, port, StorageLevels.MEMORY_AND_DISK_SER);
		//JavaReceiverInputDStream<String> lines = jssc.sparkContext().parallelize(Waermepumpen_Controll.wpliste);
		
        JavaDStream<String> words = lines.flatMap(x -> Lists.newArrayList(x.split(" ")));

        JavaPairDStream<String, Integer> wordCounts = words.mapToPair(s -> new Tuple2<String, Integer>(s, 1)).reduceByKey(
                (i1, i2) -> i1 + i2);

        wordCounts.print();
        jssc.start();

        jssc.awaitTermination();
        jssc.close();
		
		
		
		
		
		//WP Liste als RDD laden. 
		//JavaRDD<Waermepumpe> distData = sc.parallelize(Waermepumpen_Controll.wpliste);
		
        //JavaRDD<Waermepumpe> distData = jssc.sparkContext().parallelize(Waermepumpen_Controll.wpliste);

		// Table mit dem Namen "waermepumpen" erstellen
		//DataFrame schemaWaermepumpe = sqlContext.createDataFrame(distData, Waermepumpe.class);
		//schemaWaermepumpe.registerTempTable("waermepumpen");
		
		//neu
		//sqlContext.cacheTable("waermepumpen");

		// SQL can be run over RDDs that have been registered as tables.
		// Select Statement Offtime, ID, Leistung aus Waermepumpe.java 
		// Aufsteigend nach offtime sortieren
		
		//Average berechnen direkt in SQL mit dazugeben 
		//DataFrame aktiveWaermepumpenResult = sqlContext.sql("SELECT id, leistung, offtime FROM waermepumpen ORDER BY offtime ASC");

		// The results of SQL queries are DataFrames and support all the normal RDD operations.
		// The columns of a row in the result can be accessed by ordinal.
	//	List<String> aktiveWaermepumpen = aktiveWaermepumpenResult.javaRDD().map(function).collect();

	//	System.out.println(aktiveWaermepumpen);

	}
	
	//map : Nimmt ein individuelles Element im Input RDD und produziert ein neues Output-Element
	//rdd.map(new Function<Integer, Integer> (){
	//	public Integer call(Integer x) {return x+1;}
	//});
	
	
	
	


}
