/**
 * 
 * @author Daniyal Adzhiyev Version: 1.0.0 Date: 1/25/2019 CS320
 * 
 *         This application uses Regular Expressions to grab Bus and Route
 *         information from the Community Transit Web Site and display
 *         corresponding routes with their stop.
 * 
 */

/*
 * The main class which creates the Route object and calls its methods to get
 * route information
 * 
 */
public class App {
	public static void main(String[] args) throws Exception {

		Routes route = new Routes();
		route.generateRoutes();
		route.getLetterRoutes();
		String routeURL = route.getRouteID();
		route.displayStops(routeURL);

	}
}
