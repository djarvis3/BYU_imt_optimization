package byu.incidents;

public class Incident_Generator {

	Integer incNum;

	// Based on probability distribution the number of incidents in determined
	{
		double numSelector = Math.random();

		if (numSelector <= 0.022556391)
			incNum = 1;
		if (numSelector > 0.022556391 & numSelector <= 0.045112782)
			incNum = 2;
		if (numSelector > 0.045112782 & numSelector <= 0.060150376)
			incNum = 3;
		if (numSelector > 0.060150376 & numSelector <= 0.127819549)
			incNum = 4;
		if (numSelector > 0.127819549 & numSelector <= 0.180451128)
			incNum = 5;
		if (numSelector > 0.180451128 & numSelector <= 0.323308271)
			incNum = 6;
		if (numSelector > 0.323308271 & numSelector <= 0.466165414)
			incNum = 7;
		if (numSelector > 0.466165414 & numSelector <= 0.593984962)
			incNum = 8;
		if (numSelector > 0.593984962 & numSelector <= 0.654135338)
			incNum = 9;
		if (numSelector > 0.654135338 & numSelector <= 0.759398496)
			incNum = 10;
		if (numSelector > 0.759398496 & numSelector <= 0.84962406)
			incNum = 11;
		if (numSelector > 0.84962406 & numSelector <= 0.932330827)
			incNum = 12;
		if (numSelector > 0.932330827 & numSelector <= 0.969924812)
			incNum = 13;
		if (numSelector > 0.969924812 & numSelector <= 0.97593985)
			incNum = 14;
		if (numSelector > 0.97593985 & numSelector <= 0.981954887)
			incNum = 15;
		if (numSelector > 0.981954887 & numSelector <= 0.987969925)
			incNum = 16;
		if (numSelector > 0.987969925 & numSelector <= 0.993984962)
			incNum = 17;
		if (numSelector > 0.993984962) this.incNum = 18;
	}
}

