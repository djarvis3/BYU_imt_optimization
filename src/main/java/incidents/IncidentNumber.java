/**
 A class that generates a random incident number based on pre-defined probability ranges. The incident number
 is selected randomly using a number generator between 1 and 100. The corresponding incident number is assigned
 based on the probability ranges defined in the class. This class provides a convenient way to generate a random
 incident number for use in various applications.
 */

package incidents;

import java.util.Random;

public class IncidentNumber {

	// Declare the incident number variable
	private int incNum;

	public IncidentNumber() {
		Random rand = new Random();
		// Generate a random number between 1 and 100
		int numSelector = rand.nextInt(100) + 1;

		// Check which range the randomly generated number falls into and assign the corresponding incident number
		int[] incidentRanges = {19, 36, 51, 64, 75, 84, 91, 96, 99, 100};
		for (int i = 0; i < incidentRanges.length; i++) {
			if (numSelector <= incidentRanges[i]) {
				// Define the possible incident numbers and their corresponding probability ranges
				int[] incidentNumbers = {5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
				incNum = incidentNumbers[i];
				break;
			}
		}
	}

	// Get the incident number
	public int getIncNum() {
		return incNum;
	}
}


