
/**
 * 
 * @author Daniyal Adzhiyev
 * Version: 1.0.0
 * Date: 1/25/2019
 * CS320
 * 
 * This application uses Regular Expressions to grab Bus and Route
 * information from the Community Transit Web Site and display corresponding 
 * routes with their stop.
 * 
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This class will generate a Routes object which can display
 * the cities, bus routes, and specific route stops for Community Transit
 * passengers.  It gathers its information using Regular Expressions from
 * The Community Transit web site.
 * 
 */
public class Routes {
	// string array to hold the Cities and bus routes
	private String busRoutes[] = new String[300];

	/*
	 * This method generates the list of cities and bus routes and stores them into
	 * an array.
	 */
	public void generateRoutes() throws Exception {
		// Creates a connection the Community Transit city and route information
		URLConnection comTrans = new URL("https://www.communitytransit.org/busservice/schedules/").openConnection();
		comTrans.setRequestProperty("user-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

		// reads the contents of the URL page source
		BufferedReader in = new BufferedReader(
				new InputStreamReader(comTrans.getInputStream(), Charset.forName("UTF-8")));

		String inputLine = ""; // String to hold line text of the web site source text
		String text = ""; // String to hold the entire web site page source text

		// Reads the lines from the web site and stores it in the inputLine string
		// then looping through and adding all inputLines to the text string
		// until their is nothing left to read.
		while ((inputLine = in.readLine()) != null) {
			text += inputLine + "\n";
		}
		in.close();

		// regular expression to find all cities and their bus routes
		String input = " <hr id=.*\\s*<h3>(.*)</h3>[\\s*.*]*|<strong><a href=\"/schedules/route/.*>(.*)</a></strong>";
		Pattern p = Pattern.compile(input); // compiles the Regular Expression that will be used
		Matcher m = p.matcher(text); // Matches the Regular Expression against the entire text string.

		int index = 0; // index to keep track of busRoutes array

		// loops through the Matcher m and stores group1 and group2 into busRoutes array
		while (m.find()) {

			busRoutes[index] = m.group(1); // Stores the city names
			if (m.group(2) != null) {
				busRoutes[index] = m.group(2); // Stores the corresponding bus routes for each city
			}

			index++;
		}

		// Loops the array to search for the special case in which there
		// are "*" characters at the end of the strings for bus routes
		// and trims that portion off the string to allow it to be used in the
		// URL string later.
		for (int i = 0; i < busRoutes.length; i++) {
			if (busRoutes[i] != null && busRoutes[i].contains("*")) {
				busRoutes[i] = busRoutes[i].substring(0, busRoutes[i].length() - 2);
			}
		}
	}

	/*
	 * Gets the input from the user for the letter that their city starts with and
	 * lists all cities with that letter and their bus routes.
	 */
	public void getLetterRoutes() {

		String cityCharacters = ""; // String to hold all the city characters
		Scanner userInput = new Scanner(System.in);
		String userChar = "";
		// loops through the busRoutes array and checks that the character entered is
		// equal
		// to the start of one of the cities in the array.
		for (int i = 0; i < busRoutes.length; i++) {

			if (busRoutes[i] != null && busRoutes[i].substring(0, 1).matches("[A-Z]+")) {

				cityCharacters += busRoutes[i].substring(0, 1);

			}
		}

		boolean flag = false; // boolean flag used to check if userInput is valid String

		// Stores user entry for the letter of the city, and if the letter isn't in the
		// cityCharacters String it loops through and asks user to re-enter a valid
		// input
		while (flag == false) {

			System.out.print("Please enter a letter that your destinations start with: ");
			userChar = userInput.next().toUpperCase(); // allows upper and lower case entries
			if (cityCharacters.contains(userChar)) {
				flag = true; // allows to exit loop since the input is in the String

			} else {
				System.out.println("Error: No City found with that "
						+ "letter, please type only the first letter of the city you are searching for");
				System.out.println();
			}
		}

		// Loops through the busRoutes array printing all the city destinations along
		// with
		// their bus routes
		for (int j = 0; j < busRoutes.length; j++) {

			// prints out the destinations
			if ((busRoutes[j] != null) && busRoutes[j].substring(0, 1).equals(userChar)
					&& !busRoutes[j].contentEquals("Swift")) {
				System.out.println("Destination: " + busRoutes[j]);

				// nested loop to print out all bus routes/numbers for the corresponding city
				while (busRoutes[j + 1] != null && busRoutes[j + 1].charAt(0) >= '0'
						&& busRoutes[j + 1].charAt(0) <= '9'
						|| (busRoutes[j + 1] != null && busRoutes[j + 1].contentEquals("Swift"))) {
					System.out.println("Bus Number: " + busRoutes[j + 1]);
					j++;
				}
				System.out.println("-------------------------------"); // line break text

			}
		}

	}

	/*
	 * This method gets the routeID from the user and generates the routeURL needed
	 * to get the information for the specific route's stop information.
	 *
	 * @return routeURL the generated routeURL used to search for specific route and
	 * stop information
	 */
	public String getRouteID() {
		String routeID = ""; // routeID to be stored
		boolean flag = false;
		Scanner userRouteInput = new Scanner(System.in);

		// Asks the user for a routeID and searches through the busRoute array
		// to make sure that it is a valid input
		while (flag == false) {
			System.out.print("Please enter a route ID as a string: ");
			routeID = userRouteInput.next();

			for (int i = 0; i < busRoutes.length; i++) {
				if (busRoutes[i] != null && busRoutes[i].equals(routeID)) {
					flag = true;

				}

			}
			if (flag == false) {
				System.out.println("Please enter a route number Exactly as it appears on screen");
			}
		}

		routeID = routeID.replaceAll("/", "-"); // replaces all "/" characters with "-" to match the URL equivalent
		// concatenates the web site URL and routeID to create the specific routeURL
		String routeURL = "https://www.communitytransit.org/busservice/schedules/route/" + routeID;
		System.out.println("The link for your route is: " + routeURL);
		System.out.println();
		return routeURL;

	}

	/*
	 * This method searches through the specific route URL and generates the the
	 * Destinations and corresponding stop number and stop location using Regular
	 * Expression.
	 * 
	 * @param url the URL of the specific route
	 */
	public void displayStops(String url) throws Exception {
		String[] specificRoutes = new String[50];
		int index = 0;
		// Creates a connection the Community Transit specific route web site
		URLConnection comTrans = new URL(url).openConnection();
		comTrans.setRequestProperty("user-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

		// reads the contents of the URL page source
		BufferedReader in = new BufferedReader(
				new InputStreamReader(comTrans.getInputStream(), Charset.forName("UTF-8")));
		String inputLine = "";
		String text = "";
		while ((inputLine = in.readLine()) != null) {
			text += inputLine + "\n";
		}
		in.close();

		// regular expression to search through whole web page
		String inputBlock = "<h2>Weekday([\\s\\S]*?</thead>[\\s\\S]*?)</thead>";

		// regular expression to search through the inputBlock
		String inputRoutes = "<small>(.*)</small></h2>|fa-stack-1x\">(.*)</strong>[\\s*.*]*|<p>(.*)</p>";
		Pattern pBlock = Pattern.compile(inputBlock); // compiles the Regular Expression inputBlock
		Matcher mBlock = pBlock.matcher(text); // matches text with inputBlock RE
		Pattern pRoutes = Pattern.compile(inputRoutes); // compiles the Regular Expression inputRoutes
		String croppedText = "";

		// loops through the match mBlock and stores the contents of mBlock.group(1)
		// into
		// the cropped text
		while (mBlock.find()) {
			croppedText += mBlock.group(1);

		}

		// creates the matcher that uses the cropped text to search
		// through
		Matcher mRoutes = pRoutes.matcher(croppedText);

		// loops through the mRoutes storing the contents of
		// group(1) the destination, group(2) the stop number
		// and group(3) the stop location into the
		// specificRoutes array
		while (mRoutes.find()) {

			specificRoutes[index] = mRoutes.group(1);

			if (mRoutes.group(2) != null) {
				specificRoutes[index] = mRoutes.group(2);
			}
			if (mRoutes.group(3) != null) {
				specificRoutes[index] = mRoutes.group(3);
			}
			index++;

		}

		// Loops through the specific routes array to fix the text
		// where there is a substring of "&amp:" and replaces with an
		// "&"
		for (int i = 0; i < specificRoutes.length; i++) {

			if (specificRoutes[i] != null) {
				specificRoutes[i] = specificRoutes[i].replaceAll("&amp;", "&");
			}
		}

		// Loops through the specificRoutes array printing out the Destination, stop
		// number, and
		// stop location
		for (int k = 0; k < specificRoutes.length; k++) {
			if (specificRoutes[k] != null && specificRoutes[k].substring(0, 2).equals("To")) {
				System.out.println("Destination: " + specificRoutes[k]);
				// nested loop to print out the stop numbers and stop locations for their
				// corresponding "destination"
				while (specificRoutes[k + 1] != null && !specificRoutes[k + 1].substring(0, 1).equals("T")) {
					System.out.println("Stop Number: " + specificRoutes[k + 1] + " " + specificRoutes[k + 2]);

					k = k + 2; // adds 2 since each stop number is followed by a stop location in the array
				}
				System.out.println("-----------------------------------------"); // line break text

			}
		}

	}

}
