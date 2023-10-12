package analysis.trucks;

import org.matsim.api.core.v01.Id;
import org.matsim.vehicles.Vehicle;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class AnalysisWriter implements AutoCloseable {

	private final PrintWriter writer;

	public AnalysisWriter(String outputPath) throws IOException {
		this.writer = new PrintWriter(new FileWriter(outputPath));
		this.writer.println("Scenario,Seed,Truck,Total Time (seconds),Total Distance (meters),Average Time (seconds),Average Distance (meters)");
	}

	public void writeData(String scenario, String seed, Id<Vehicle> truckId, double totalTime, double totalDistance, double averageTime, double averageDistance) {
		writer.println(scenario + "," + seed + "," + truckId + "," + totalTime + "," + totalDistance + "," + averageTime + "," + averageDistance);
	}

	@Override
	public void close() {
		writer.close();
	}
}
