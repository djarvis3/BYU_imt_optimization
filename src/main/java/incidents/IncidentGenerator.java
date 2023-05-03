package incidents;

import java.util.Random;

public class IncidentGenerator {

	// Define the possible incident numbers and their corresponding probability ranges
	private int[] incidentNumbers = {5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
	private int[] incidentRanges = {19, 36, 51, 64, 75, 84, 91, 96, 99, 100};

	// Declare the incident number variable
	private int incNum;

	public IncidentGenerator() {
		Random rand = new Random();
		// Generate a random number between 1 and 100
		int numSelector = rand.nextInt(100) + 1;

		// Check which range the randomly generated number falls into and assign the corresponding incident number
		for (int i = 0; i < incidentRanges.length; i++) {
			if (numSelector <= incidentRanges[i]) {
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


