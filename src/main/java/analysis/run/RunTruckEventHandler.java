package analysis.run;

import analysis.trucks.TruckEventsHandler;
import org.matsim.api.core.v01.Id;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.vehicles.Vehicle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class RunTruckEventHandler {

	private static final Logger LOGGER = Logger.getLogger(RunTruckEventHandler.class.getName());

	public static void main(String[] args) {
		if (args.length < 3) {
			LOGGER.severe("Please provide the paths for the trucks, events, and truck events output files.");
			return;
		}

		Path trucksPath = Paths.get(args[0]);
		Path eventsPath = Paths.get(args[1]);
		String outputFilePath = args[2];

		// Ensure the output file has a .xml extension
		if (!outputFilePath.endsWith(".xml")) {
			outputFilePath += ".xml";
		}

		if (!Files.exists(trucksPath) || !Files.exists(eventsPath)) {
			LOGGER.severe("One or more provided paths do not exist.");
			return;
		}

		TruckEventsHandler handler = null;
		try {
			Set<Id<Vehicle>> truckIds = loadTruckIdsFromCSV(trucksPath);
			handler = new TruckEventsHandler(truckIds, outputFilePath);
			processEvents(eventsPath.toString(), handler);
		} catch (IOException e) {
			LOGGER.severe("Error reading the truck IDs from the CSV file: " + e.getMessage());
		} finally {
			if (handler != null) {
				handler.close();
			}
		}
	}

	private static Set<Id<Vehicle>> loadTruckIdsFromCSV(Path trucksPath) throws IOException {
		List<String> lines = Files.readAllLines(trucksPath);
		Set<Id<Vehicle>> truckIds = new HashSet<>();

		// Skip the header line and iterate over the rest
		for (int i = 1; i < lines.size(); i++) {
			truckIds.add(Id.create(lines.get(i), Vehicle.class));
		}

		return truckIds;
	}

	private static void processEvents(String eventsFilePath, TruckEventsHandler handler) {
		EventsManager eventsManager = EventsUtils.createEventsManager();
		eventsManager.addHandler(handler);
		new MatsimEventsReader(eventsManager).readFile(eventsFilePath);
	}
}
