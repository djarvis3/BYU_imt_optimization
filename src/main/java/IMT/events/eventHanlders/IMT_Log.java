package IMT.events.eventHanlders;
import IMT.Request;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;

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
 * Handles logging for network change events associated with incidents or IMT arrival events.
 */
public class EventHandler_IMT_Log {
	private static final Logger LOGGER = Logger.getLogger(EventHandler_IMT_Log.class.getName());
	private static int iterationCount = 0;

	/**
	 * Constructs an EventHandler object.
	 *
	 * @param scenario the scenario object to retrieve the output directory from
	 */
	public EventHandler_IMT_Log(Scenario scenario) {
		String outputDirectory = scenario.getConfig().controler().getOutputDirectory();

		try {
			// Create the output directory if it doesn't exist
			Path outputDirPath = Paths.get(outputDirectory);
			if (!Files.exists(outputDirPath)) {
				Files.createDirectories(outputDirPath);
			}

			// Create the imtITERS directory if it doesn't exist
			Path imtItersDirPath = Paths.get(outputDirectory, "imtITERS");
			if (!Files.exists(imtItersDirPath)) {
				Files.createDirectories(imtItersDirPath);
			}

			FileHandler imtHandler = new FileHandler(outputDirectory + "/imtITERS/logIMT.log", true);

			// Set the log level to INFO
			imtHandler.setLevel(Level.INFO);

			// Use a custom formatter to format the log messages with the desired date format
			imtHandler.setFormatter(new Formatter() {
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
			});

			// Add the file handler to the logger
			LOGGER.addHandler(imtHandler);

			logIterationBegin();

		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Failed to create file handler for logIMT.log", e);
		}
	}

	// Add Iteration Counter and separated lines
	private void logIterationBegin() {
		LOGGER.severe("########################## ITERATION " + iterationCount + " BEGINS ##########################");
		iterationCount++;
	}

	/**
	 * Handle IMT log information from ImtNetworkChangeEventGenerator.
	 *
	 * @param request          the request associated with the network change event
	 * @param fullCapacity     the full capacity of the link
	 * @param reducedCapacity  the reduced capacity of the link
	 * @param currLinkCapacity the current capacity of the link
	 * @param arrivalTime      the time when the IMT arrives
	 * @param imtUnit          the vehicle sent to the incident
	 */
	public static void handleImtNetworkChangeEvent(Request request, double fullCapacity, double reducedCapacity, double currLinkCapacity, double arrivalTime, DvrpVehicle imtUnit) {
		String imtLog = ("IMT: ");
		Duration arrival = Duration.ofSeconds((long) arrivalTime);
		LocalTime localArrival = LocalTime.MIDNIGHT.plus(arrival);
		String formattedArrival = String.format("%02d:%02d:%02d", localArrival.getHour(), localArrival.getMinute(), localArrival.getSecond());
		String incidentInfo = String.format("Request ID %s, IMT %s of %s, " +
						"Vehicle ID %s, Link ID %s, Full Capacity %.2f, Reduced Capacity %.2f, " +
						"Current Capacity %.2f, Arrival Time %s",
				request.getId(), (request.getNumIMT() + 1), request.getTotalIMTs(), imtUnit.getId(),
				request.getToLink().getId(), fullCapacity, reducedCapacity, currLinkCapacity, formattedArrival);
		String logMsg = imtLog + incidentInfo;

		// Use LOGGER.info instead of LOGGER.warning to match the ChangeEvent class
		LOGGER.info(String.format("%-80s", logMsg));
	}

	/**
	 * Handles the logging of IMT log information from ImtGenerator for late IMT arrival.
	 *
	 * @param request     the request associated with the IMT
	 * @param arrivalTime the time when the IMT arrives
	 * @param imtUnit     the vehicle sent to the incident
	 */
	public static void handleLateImtArrival(Request request, double arrivalTime, DvrpVehicle imtUnit) {
		String imtLog = ("IMT: ");
		Duration arrival = Duration.ofSeconds((long) arrivalTime);
		Duration endTime = Duration.ofSeconds((long) request.getEndTime());
		LocalTime localArrival = LocalTime.MIDNIGHT.plus(arrival);
		LocalTime localEndTime = LocalTime.MIDNIGHT.plus(endTime);
		String formattedArrival = String.format("%02d:%02d:%02d", localArrival.getHour(), localArrival.getMinute(), localArrival.getSecond());
		String formattedEndTime = String.format("%02d:%02d:%02d", localEndTime.getHour(), localEndTime.getMinute(), localEndTime.getSecond());

		String incidentInfo = String.format("Request ID %s, IMT %s of %s, " +
						"Vehicle ID %s, Arrival Time %s, End Time %s. ",
				request.getId(), (request.getNumIMT() + 1), request.getTotalIMTs(), imtUnit.getId(), formattedArrival, formattedEndTime);
		String output = ("Late Arrival");
		String logMsg = imtLog + incidentInfo + output;

		// Use LOGGER.info instead of LOGGER.warning to match the ChangeEvent class
		LOGGER.info(String.format("%-80s", logMsg));
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
		LOGGER.info(String.format("%-80s", incidentInfo));
	}
}

