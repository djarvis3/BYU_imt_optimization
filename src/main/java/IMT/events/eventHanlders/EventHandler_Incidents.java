package IMT.events.eventHanlders;

import IMT.Request;
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

/**
 * The EventHandler_Incidents class handles incidents-related events and logging.
 */
public class EventHandler_Incidents {
	private static final Logger LOGGER = Logger.getLogger(EventHandler_Incidents.class.getName());
	private static int iterationCount = 0;

	/**
	 * Constructs an EventHandler_Incidents object for handling incidents-related events and logging.
	 *
	 * @param scenario the scenario object containing the configuration information
	 */
	public EventHandler_Incidents(Scenario scenario) {
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

	/**
	 * Logs the beginning of an iteration.
	 */
	private void logIterationBegin() {
		LOGGER.severe("########################## ITERATION " + iterationCount + " BEGINS ##########################");
		iterationCount++;
	}

	/**
	 * Handles the logging of IMT log information from IncidentNetworkChangeEventGenerator for an incident network change event.
	 *
	 * @param request         the request associated with the incident
	 * @param reducedCapacity the reduced capacity of the incident link
	 * @param fullCapacity    the full capacity of the incident link
	 * @param startTime       the start time of the incident
	 * @param endTime         the end time of the incident
	 */
	public static void handleIncidentNetworkChangeEvent(Request request, double reducedCapacity,
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
				request.getId(), request.getTotalIMTs(), request.getToLink().getId(), fullCapacity, reducedCapacity,
				formattedStart, formattedEnd);

		// Use LOGGER.info instead of LOGGER.warning to match the ChangeEvent class
		LOGGER.info(String.format("%-80s",imtLog + incidentInfo));
	}
}

