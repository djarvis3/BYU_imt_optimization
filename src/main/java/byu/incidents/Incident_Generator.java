package byu.incidents;

import java.util.Random;

public class Incident_Generator {

	private final int[] PROBABILITIES = {
			23, 45, 60, 128, 181, 323, 466, 594, 654, 759,
			850, 932, 970, 976, 982, 988, 994
	};

	private int incNum;

	public Incident_Generator() {
		Random rand = new Random();
		int numSelector = rand.nextInt(1000) + 1; // generate a random number between 1 and 1000

		for (int i = 1; i < PROBABILITIES.length; i++) {
			if (numSelector <= PROBABILITIES[i]) {
				incNum = i + 0;
				break;
			}
		}
	}

	public int getIncNum() {
		return incNum;
	}
}

