package analysis.trucks;

import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.Id;
import org.matsim.vehicles.Vehicle;
import org.matsim.core.events.handler.BasicEventHandler;
import org.matsim.core.events.algorithms.EventWriterXML;

import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

public class TruckEventsHandler implements BasicEventHandler {

	private final Set<Id<Vehicle>> truckIds;
	private static final Logger LOGGER = Logger.getLogger(TruckEventsHandler.class.getName());
	private final EventWriterXML eventWriter;

	public TruckEventsHandler(Set<Id<Vehicle>> truckIds, String outputPath) {
		this.truckIds = truckIds;
		this.eventWriter = new EventWriterXML(outputPath);
	}

	@Override
	public void handleEvent(Event event) {
		Optional.ofNullable(event.getAttributes().get("vehicle"))
				.map(vid -> Id.create(vid, Vehicle.class))
				.ifPresent(vid -> {
					if (truckIds.contains(vid)) {
						eventWriter.handleEvent(event);
					}
				});
	}

	@Override
	public void reset(int iteration) {
		LOGGER.info("Reset called for iteration: " + iteration);
	}

	public void close() {
		eventWriter.closeFile();
	}
}
