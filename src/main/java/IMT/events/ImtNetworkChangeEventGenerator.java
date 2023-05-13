package IMT.events;

import IMT.Request;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkUtils;

import java.util.Objects;

/**
 A class representing an event generator that adds a network change event upon the arrival of
 an incident management team (IMT).
 */
public class ImtNetworkChangeEventGenerator {

	private final Scenario scenario;
	private final Link toLink;
	private final double currLinkCapacity;
	private final Request request;
	private final double arrivalTime;

	/**
	 Constructs a new instance of the ImtNetworkChangeEventGenerator class.
	 @param scenario the scenario where the network change event occurs
	 @param toLink the link to which the network change event is applied
	 @param currLinkCapacity the current capacity of the link
	 @param request the request associated with the network change event
	 @param arrivalTime the time when the IMT arrives
	 @throws NullPointerException if any of the non-primitive arguments are null
	 */
	public ImtNetworkChangeEventGenerator(Scenario scenario, Link toLink, double currLinkCapacity, Request request,
										  double arrivalTime) {
		this.scenario = Objects.requireNonNull(scenario, "scenario must not be null");
		this.toLink = Objects.requireNonNull(toLink, "toLink must not be null");
		this.currLinkCapacity = currLinkCapacity;
		this.request = Objects.requireNonNull(request, "request must not be null");
		this.arrivalTime = arrivalTime;
	}
	/**
	 Adds a network change event to the network.
	 @param fullCapacity the full capacity of the link
	 @param reducedCapacity the reduced capacity of the link
	 */
	public void addEventToNetwork(double fullCapacity, double reducedCapacity) {

		// Generate Network Change Event
		NetworkChangeEvent restoreCapacityEvent = new NetworkChangeEvent(arrivalTime);
		restoreCapacityEvent.setFlowCapacityChange
				(new NetworkChangeEvent.ChangeValue(NetworkChangeEvent.ChangeType.
						ABSOLUTE_IN_SI_UNITS, currLinkCapacity));
		restoreCapacityEvent.addLink(toLink);
		NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), restoreCapacityEvent);

		// Log incident information
		EventHandler.handleImtNetworkChangeEvent(request, fullCapacity, reducedCapacity, currLinkCapacity, arrivalTime);
	}
}
