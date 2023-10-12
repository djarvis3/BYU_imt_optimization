package analysis.run;

import analysis.trucks.TruckEventsHandler;
import org.matsim.api.core.v01.Id;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.vehicles.Vehicle;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class RunTruckEventHandler {

	private static final Logger LOGGER = Logger.getLogger(RunTruckEventHandler.class.getName());

	public static void main(String[] args) {
		if (args.length < 3) {
			LOGGER.severe("Please provide the paths for the trucks CSV, the root directory for the events files, and the output directory for truck events.");
			return;
		}

		Path trucksPath = Paths.get(args[0]);
		Path rootEventsDir = Paths.get(args[1]);
		Path outputDir = Paths.get(args[2]);

		if (!Files.exists(trucksPath) || !Files.isDirectory(rootEventsDir)) {
			LOGGER.severe("Invalid paths provided.");
			return;
		}

		// Check if output directory exists, if not create it
		if (!Files.exists(outputDir)) {
			try {
				Files.createDirectories(outputDir);
			} catch (IOException e) {
				LOGGER.severe("Error creating the output directory: " + e.getMessage());
				return;
			}
		}

		try {
			Set<Id<Vehicle>> truckIds = loadTruckIdsFromCSV(trucksPath);

			// Get list of event files starting with '2' or '3'
			List<Path> eventFiles = Files.walk(rootEventsDir)
					.filter(path -> Files.isRegularFile(path) &&
							(path.getFileName().toString().startsWith("2") || path.getFileName().toString().startsWith("3")) &&
							path.toString().endsWith(".xml.gz"))
					.collect(Collectors.toList());

			for (Path eventFile : eventFiles) {
				String outputFileName = eventFile.getFileName().toString().replace(".events.xml.gz", "_trucks.events.xml.gz");
				Path outputFilePath = outputDir.resolve(outputFileName);

				TruckEventsHandler handler = new TruckEventsHandler(truckIds, outputFilePath.toString());
				processEvents(eventFile.toString(), handler);
				handler.close();
			}
		} catch (IOException e) {
			LOGGER.severe("Error: " + e.getMessage());
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
