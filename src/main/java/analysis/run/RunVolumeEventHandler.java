package analysis.run;

import analysis.volume.VolumeEventHandler;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.EventsReaderXMLv1;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class RunVolumeEventHandler {

	private static final Logger LOGGER = Logger.getLogger(RunVolumeEventHandler.class.getName());

	public static void main(String[] args) {
		if (args.length < 3) {
			LOGGER.severe("Please provide the paths for the network, events directory, and the CSV output directory.");
			return;
		}

		Path eventsPath = Paths.get(args[1]);
		if (!Files.isDirectory(eventsPath)) {
			LOGGER.severe("Provided events path is not a directory!");
			return;
		}

		Path outputDir = Paths.get(args[2], "outputs");
		if (!Files.exists(outputDir)) {
			try {
				Files.createDirectories(outputDir);
			} catch (IOException e) {
				LOGGER.severe("Failed to create output directory: " + e.getMessage());
				return;
			}
		}

		try {
			List<Path> eventFiles = findEventFiles(eventsPath);
			for (Path eventFile : eventFiles) {
				VolumeEventHandler handler = new VolumeEventHandler();
				processEvents(eventFile.toString(), handler);

				String outputFileName = eventFile.getFileName().toString().split("\\.")[0] + ".volume.csv";
				Path outputFile = outputDir.resolve(outputFileName);
				try (FileWriter writer = new FileWriter(outputFile.toFile())) {
					writeVolumesToCSV(handler.getLinkVolumes(), writer);
				} catch (IOException e) {
					LOGGER.severe("Failed to write to the CSV file: " + outputFileName + ". Error: " + e.getMessage());
				}
			}
		} catch (IOException e) {
			LOGGER.severe("Failed to list files from the events directory: " + e.getMessage());
		}
	}

	private static List<Path> findEventFiles(Path directory) throws IOException {
		List<Path> eventFiles = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					eventFiles.addAll(findEventFiles(entry));  // Recursively add files from subdirectories.
				} else if (entry.toString().endsWith(".xml.gz") || entry.toString().endsWith(".xml")) {
					eventFiles.add(entry);
				}
			}
		}
		return eventFiles;
	}

	private static void processEvents(String eventsFilePath, VolumeEventHandler handler) {
		EventsManager eventsManager = EventsUtils.createEventsManager();
		eventsManager.addHandler(handler);
		new EventsReaderXMLv1(eventsManager).readFile(eventsFilePath);
	}

	private static void writeVolumesToCSV(Map<Id<Link>, int[]> linkVolumes, FileWriter writer) throws IOException {
		List<String> headers = new ArrayList<>();
		headers.add("Link Id");
		for (int i = 1; i <= 120; i++) {
			headers.add(String.format("%d:%02d:00", (i - 1) / 4, (i % 4) * 15));
		}
		writer.write(String.join(",", headers) + "\n");

		List<Id<Link>> sortedLinkIds = new ArrayList<>(linkVolumes.keySet());
		Collections.sort(sortedLinkIds);

		for (Id<Link> linkId : sortedLinkIds) {
			List<String> values = new ArrayList<>();
			values.add(linkId.toString());
			for (int volume : linkVolumes.get(linkId)) {
				values.add(String.valueOf(volume));
			}
			writer.write(String.join(",", values) + "\n");
		}
	}
}
