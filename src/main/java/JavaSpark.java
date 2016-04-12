import static spark.Spark.*;
import spark.Request;
import spark.Response;
import spark.Route;

public class JavaSpark {
	public JavaSpark(final Waermepumpen_Controll waermepumpen_controll) {

		get("/users", new Route() {
			@Override
			public Object handle(Request request, Response response) {
				// process request
				return Waermepumpen_Controll.wpliste;
			}
		});
		// more routes
		get("/users", new Route() {
			@Override
			public Object handle(Request request, Response response) {
				// process request
				return Waermepumpen_Controll.stromliste;
			}
		});
	}
}
