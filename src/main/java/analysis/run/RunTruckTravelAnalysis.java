package analysis.run;

import analysis.trucks.travel_analysis.AnalysisWriter;
import analysis.trucks.travel_analysis.DataUtils;
import analysis.trucks.travel_analysis.FileProcessor;
import analysis.trucks.travel_analysis.NetworkLoader;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.Id;
import org.matsim.vehicles.Vehicle;

import java.nio.file.Paths;
import java.util.Set;
import java.util.logging.Logger;
import java.io.IOException;

public class RunTruckTravelAnalysis {

	private static final Logger LOGGER = Logger.getLogger(RunTruckTravelAnalysis.class.getName());

	public static void main(String[] args) throws IOException {
		if (args.length < 4) {
			LOGGER.severe("Please provide the paths for the truck IDs CSV, truck folder, MATSim network file, and output CSV file.");
			return;
		}

		Network network = new NetworkLoader().loadNetwork(args[2]);
		Set<Id<Vehicle>> truckIds = DataUtils.loadTruckIdsFromCSV(Paths.get(args[0]));

		try (AnalysisWriter writer = new AnalysisWriter(args[3])) {
			FileProcessor processor = new FileProcessor(network, truckIds, writer);
			processor.processFiles(Paths.get(args[1]));
		} catch (IOException e) {
			LOGGER.severe("Error reading/writing files: " + e.getMessage());
		}
	}
}
