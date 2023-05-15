package IMT.events;
import IMT.Request;
import org.matsim.api.core.v01.Scenario;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;

import java.util.logging.*;

/**
 * Handles logging for network change events associated with incidents or IMT arrival events.
 */

public class EventHandler {
	private static final Logger LOGGER = Logger.getLogger(EventHandler.class.getName());

	public EventHandler(Scenario scenario) {
		String outputDirectory = scenario.getConfig().controler().getOutputDirectory();
		LOGGER.info(outputDirectory);

		try {
			// Create a new file handler that writes log messages to a file named "ImtEvents.log"
			FileHandler fileHandler = new FileHandler(outputDirectory + "/logIMT.log", true);

			// Set the log level to INFO
			fileHandler.setLevel(Level.INFO);

			// Use a custom formatter to format the log messages with the desired date format
			fileHandler.setFormatter(new Formatter() {
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
			LOGGER.addHandler(fileHandler);

		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Failed to create file handler for ImtEvents.log", e);
		}
	}


	/**
	 * Handle IMT log information from ImtNetworkChangeEventGenerator.
	 * @param request the request associated with the network change event
	 * @param fullCapacity the full capacity of the link
	 * @param reducedCapacity the reduced capacity of the link
	 * @param currLinkCapacity the current capacity of the link
	 * @param arrivalTime the time when the IMT arrives
	 */
	public static void handleImtNetworkChangeEvent(Request request, double fullCapacity, double reducedCapacity, double currLinkCapacity, double arrivalTime) {
		String imtLog = ("IMT: ");
		Duration arrival = Duration.ofSeconds((long) arrivalTime);
		LocalTime localArrival = LocalTime.MIDNIGHT.plus(arrival);
		String formattedArrival = String.format("%02d:%02d:%02d", localArrival.getHour(), localArrival.getMinute(), localArrival.getSecond());
		String incidentInfo = String.format("Request ID %s, IMT %s of %s, " +
						"Full Capacity %.2f, Reduced Capacity %.2f, " +
						"Current Capacity %.2f, Arrival Time %s",
				request.getId(), (request.getNumIMT()+1), request.getTotalIMTs(), fullCapacity,
				reducedCapacity, currLinkCapacity, formattedArrival);
		String logMsg = imtLog + incidentInfo;

		LOGGER.info(String.format("%-80s", logMsg));
	}

	/**
	 * Handle IMT log information from ImtGenerator.
	 * @param request the request associated with the IMT
	 * @param arrivalTime the time when the IMT arrives
	 */
	public static void handleImtGenerator(Request request, double arrivalTime) {
		String imtLog = ("IMT: ");
		Duration arrival = Duration.ofSeconds((long) arrivalTime);
		Duration endTime = Duration.ofSeconds((long) request.getEndTime());
		LocalTime localArrival = LocalTime.MIDNIGHT.plus(arrival);
		LocalTime localEndTime = LocalTime.MIDNIGHT.plus(endTime);
		String formattedArrival = String.format("%02d:%02d:%02d", localArrival.getHour(), localArrival.getMinute(), localArrival.getSecond());
		String formattedEndTime = String.format("%02d:%02d:%02d", localEndTime.getHour(), localEndTime.getMinute(), localEndTime.getSecond());

		String incidentInfo = String.format("Request ID %s, IMT %s of %s, " +
						"Arrival Time %s, End Time %s. ",
				request.getId(), (request.getNumIMT()+1), request.getTotalIMTs(), formattedArrival, formattedEndTime);
		String output = ("Late Arrival");
		String logMsg = imtLog + incidentInfo + output;

		LOGGER.info(String.format("%-80s", logMsg));
	}

	/**
	 * Handle IMT log information from IncidentNetworkChangeEventGenerator.
	 * @param request the request associated with the incident
	 * @param fullCapacity the full capacity of the incident link
	 * @param reducedCapacity the reduced capacity of the incident link
	 * @param startTime the start time of the incident
	 * @param endTime the end time of the incident
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
						"Full Capacity %.2f, Reduced Capacity %.2f, " +
						"Start Time %s, End Time %s",
				request.getId(), request.getTotalIMTs(), fullCapacity, reducedCapacity,
				formattedStart, formattedEnd);

		String logMsg = imtLog + incidentInfo;

		LOGGER.warning(String.format("%-80s", logMsg));
	}
}
