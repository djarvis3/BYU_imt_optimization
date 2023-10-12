package analysis.trucks;

import org.matsim.api.core.v01.Id;
import org.matsim.vehicles.Vehicle;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Map;

public class DataUtils {

	public static Set<Id<Vehicle>> loadTruckIdsFromCSV(Path trucksPath) throws IOException {
		List<String> lines = Files.readAllLines(trucksPath);
		Set<Id<Vehicle>> truckIds = new HashSet<>();

		for (int i = 1; i < lines.size(); i++) {
			truckIds.add(Id.create(lines.get(i), Vehicle.class));
		}

		return truckIds;
	}

	public static double computeAverage(Map<Id<Vehicle>, Double> data, String scenario) {
		double total = 0.0;
		int divisor;

		switch (scenario) {
			case "2":
				divisor = 20;
				break;
			case "3":
				divisor = 30;
				break;
			default:
				divisor = data.size();
		}

		for (double value : data.values()) {
			total += value;
		}

		return total / divisor;
	}
}
