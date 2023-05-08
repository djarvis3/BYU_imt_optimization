package IMT.networkChangesEvents;

import IMT.Request;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkUtils;

import java.util.Objects;
import java.util.logging.Logger;

public class ImtNetworkChangeEventGenerator {

	private static final Logger LOGGER = Logger.getLogger(ImtNetworkChangeEventGenerator.class.getName());

	private final Scenario scenario;
	private final Link toLink;
	private final double currLinkCapacity;
	private final Request request;
	private final double arrivalTime;

	public ImtNetworkChangeEventGenerator(Scenario scenario, Link toLink, double currLinkCapacity, Request request, double arrivalTime) {
		this.scenario = Objects.requireNonNull(scenario, "scenario must not be null");
		this.toLink = Objects.requireNonNull(toLink, "toLink must not be null");
		this.currLinkCapacity = currLinkCapacity;
		this.request = Objects.requireNonNull(request, "request must not be null");
		this.arrivalTime = arrivalTime;
	}

	public void addEventToNetwork(double fullCapacity, double reducedCapacity) {
		NetworkChangeEvent restoreCapacityEvent = new NetworkChangeEvent(arrivalTime);
		restoreCapacityEvent.setFlowCapacityChange(new NetworkChangeEvent.ChangeValue(NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, currLinkCapacity));
		restoreCapacityEvent.addLink(toLink);
		NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), restoreCapacityEvent);

		String output = ("Arrival Time is less than incident End Time, Network Change Event Added Upon IMT Arrival. ");
		String incidentInfo = String.format("Request ID %s, # of IMTs %s, Full Capacity %.2f, Reduced Capacity %.2f, " +
						"Arrival Time %s",
				request.getId(), request.getRespondingIMTs(), fullCapacity, reducedCapacity, arrivalTime);

		LOGGER.info(output + incidentInfo);
	}
}
