/**
 A class that generates a random incident number based on pre-defined probability ranges. The incident number
 is selected randomly using a number generator between 1 and 100. The corresponding incident number is assigned
 based on the probability ranges defined in the class.
 */

package incidents;

import java.util.Random;

public class IncidentNumber {

	private int incNum;

	public IncidentNumber() {
		Random rand = new Random();
		int numSelector = rand.nextInt(100) + 1;

		int[] incidentRanges = {19, 36, 51, 64, 75, 84, 91, 96, 99, 100};
		for (int i = 0; i < incidentRanges.length; i++) {
			if (numSelector <= incidentRanges[i]) {
				int[] incidentNumbers = {5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
				incNum = incidentNumbers[i];
				break;
			}
		}
	}

	public int getIncNum() {
		return incNum;
	}
}


