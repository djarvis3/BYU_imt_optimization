package analysis.run;

import analysis.feeders.IncidentImpactProcessor;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class RunIncidentImpactEventHandler {

	private static final int MAX_FEEDERS = 2;
	private static final Logger LOGGER = Logger.getLogger(RunIncidentImpactEventHandler.class.getName());

	public static void main(String[] args) {
		validateInputArgs(args);

		Config config = ConfigUtils.createConfig();
		Network network = NetworkUtils.createNetwork();
		new MatsimNetworkReader(network).readFile(args[0]);

		IncidentImpactProcessor processor = new IncidentImpactProcessor(network);

		try (FileWriter writer = new FileWriter(args[3], true)) {
			writer.append("Seed,Incident Link,Feeder 1,Feeder 2\n");
			processChangeEvents(args[1], args[2], processor, writer);
		} catch (IOException e) {
			LOGGER.severe("Failed to write to the CSV file. " + e.getMessage());
		}
	}

	private static void validateInputArgs(String[] args) {
		if (args.length < 4) {
			LOGGER.severe("Please provide the paths for the network, events, change events folder, and the output CSV file.");
			System.exit(1);
		}
	}

	private static void processChangeEvents(String eventsFilePath, String changeEventsFolderPath, IncidentImpactProcessor processor, FileWriter writer) {
		try (Stream<Path> files = Files.list(Paths.get(changeEventsFolderPath))) {
			files.forEach(changeEventPath -> {
				String seedCode = changeEventPath.getFileName().toString().split("\\.")[0];
				processor.executeEventHandlerOnFiles(eventsFilePath, changeEventPath.toString());

				Map<String, List<String>> linkFeederMap = processor.getLinkFeederMap();
				writeToCSV(linkFeederMap, writer, seedCode);
			});
		} catch (IOException e) {
			LOGGER.severe("Failed to process change events. " + e.getMessage());
		}
	}

	private static void writeToCSV(Map<String, List<String>> linkFeederMap, FileWriter writer, String seedCode) {
		StringBuilder sb = new StringBuilder();
		linkFeederMap.forEach((impactedLink, feeders) -> {
			sb.append(seedCode).append(",").append(impactedLink);
			for (int i = 0; i < MAX_FEEDERS; i++) {
				sb.append(",").append(i < feeders.size() ? feeders.get(i) : "");
			}
			sb.append("\n");
		});
		synchronized(writer) {
			try {
				writer.write(sb.toString());
			} catch (IOException e) {
				LOGGER.severe("Failed to write data to CSV. " + e.getMessage());
			}
		}
	}
}
