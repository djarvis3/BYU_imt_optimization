package IMT.logs;

import IMT.ImtRequest;
import org.matsim.api.core.v01.Scenario;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;
import java.util.logging.*;

public class Incidents_Log {
	private static final Logger LOGGER = Logger.getLogger(Incidents_Log.class.getName());
	private static int iterationCount = 0;

	public Incidents_Log(Scenario scenario) {
		String outputDirectory = scenario.getConfig().controler().getOutputDirectory();
		try {
			Path outputDirPath = Paths.get(outputDirectory);
			if (!Files.exists(outputDirPath)) {
				Files.createDirectories(outputDirPath);
			}

			// Create incidentITERS directory if it doesn't exist
			Path incidentDirPath = outputDirPath.resolve("incidentITERS");
			if (!Files.exists(incidentDirPath)) {
				Files.createDirectories(incidentDirPath);
			}

			String logFilePath = incidentDirPath.resolve("logINCIDENT.log").toString();

			FileHandler fileHandler = new FileHandler(logFilePath, 0, 1, true); // Disable rotation
			fileHandler.setLevel(Level.INFO);

			Formatter formatter = new Formatter() {
				private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

				@Override
				public String format(LogRecord record) {
					StringBuilder sb = new StringBuilder();
					sb.append(dateFormat.format(new Date(record.getMillis())));
					sb.append(" ");
					sb.append(record.getLevel());
					sb.append(" - ");
					sb.append(formatMessage(record));
					sb.append(System.lineSeparator());
					return sb.toString();
				}
			};

			fileHandler.setFormatter(formatter);

			LOGGER.addHandler(fileHandler);

			logIterationBegin();

		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Failed to create file handler for logIMT.log", e);
		}
	}

	private void logIterationBegin() {
		LOGGER.severe("########################## ITERATION " + iterationCount + " BEGINS ##########################");
		iterationCount++;
	}

	public static void handleIncidentNetworkChangeEvent(ImtRequest imtRequest, double reducedCapacity,
														double fullCapacity, double startTime, double endTime) {
		String imtLog = ("Incident: ");
		Duration start = Duration.ofSeconds((long) startTime);
		Duration end = Duration.ofSeconds((long) endTime);
		LocalTime localArrival = LocalTime.MIDNIGHT.plus(start);
		LocalTime localEndTime = LocalTime.MIDNIGHT.plus(end);
		String formattedStart = String.format("%02d:%02d:%02d", localArrival.getHour(), localArrival.getMinute(), localArrival.getSecond());
		String formattedEnd = String.format("%02d:%02d:%02d", localEndTime.getHour(), localEndTime.getMinute(), localEndTime.getSecond());
		String incidentInfo = String.format("Request ID %s, %s IMT(s), " +
						"Link ID %s, Full Capacity %.2f, Reduced Capacity %.2f, " +
						"Start Time %s, End Time %s",
				imtRequest.getId(), imtRequest.getTotalIMTs(), imtRequest.getIncLink().getId(), fullCapacity, reducedCapacity,
				formattedStart, formattedEnd);

		// Use LOGGER.info instead of LOGGER.warning to match the ChangeEvent class
		LOGGER.info(String.format("%-80s",imtLog + incidentInfo));
	}
}

