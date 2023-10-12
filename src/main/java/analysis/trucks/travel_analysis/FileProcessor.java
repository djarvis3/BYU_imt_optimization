package analysis.trucks;

import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.Id;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.vehicles.Vehicle;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileProcessor {
	private static final Pattern SCENARIO_ID_PATTERN = Pattern.compile("(\\d+)-(\\d+)-(\\d+)_trucks.*\\.xml\\.gz");

	private final Network network;
	private final Set<Id<Vehicle>> truckIds;
	private final AnalysisWriter writer;

	public FileProcessor(Network network, Set<Id<Vehicle>> truckIds, AnalysisWriter writer) {
		this.network = network;
		this.truckIds = truckIds;
		this.writer = writer;
	}

	public void processFiles(Path rootPath) {
		try {
			FileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path xmlFile, BasicFileAttributes attrs) throws IOException {
					Matcher matcher = SCENARIO_ID_PATTERN.matcher(xmlFile.getFileName().toString());
					if (matcher.matches()) {
						String scenario = matcher.group(1);
						String seed = matcher.group(3);  // Extract the scenario ID from the filename using the matched pattern.

						TruckTravelAnalysisHandler handler = new TruckTravelAnalysisHandler(truckIds, network);
						EventsManager eventsManager = EventsUtils.createEventsManager();
						eventsManager.addHandler(handler);
						new MatsimEventsReader(eventsManager).readFile(xmlFile.toString());

						Map<Id<Vehicle>, Double> totalTravelTimes = handler.getTotalTravelTimes();
						Map<Id<Vehicle>, Double> totalDistances = handler.getTotalDistances();

						double averageTravelTime = DataUtils.computeAverage(totalTravelTimes, scenario);
						double averageDistance = DataUtils.computeAverage(totalDistances, scenario);

						for (Id<Vehicle> truckId : truckIds) {
							double totalTime = totalTravelTimes.getOrDefault(truckId, 0.0);
							double totalDistance = totalDistances.getOrDefault(truckId, 0.0);
							writer.writeData(scenario, seed, truckId, totalTime, totalDistance, averageTravelTime, averageDistance);
						}
					}
					return FileVisitResult.CONTINUE;
				}
			};
			Files.walkFileTree(rootPath, EnumSet.noneOf(FileVisitOption.class), 3, fileVisitor);
		} catch (IOException e) {
			throw new RuntimeException("Error processing files under path: " + rootPath, e);
		}
	}
}
