package IMT.events;

import IMT.Request;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;


import java.util.Objects;

/**
 A class representing an event generator that adds a network change event upon the arrival of
 an incident management team (IMT).
 */
public class ImtNetworkChangeEventGenerator {

	private final Scenario scenario;
	private final double currLinkCapacity;
	private final Request request;
	private final double arrivalTime;

	/**
	 Constructs a new instance of the ImtNetworkChangeEventGenerator class.
	 @throws NullPointerException if any of the non-primitive arguments are null
	  * @param scenario the scenario where the network change event occurs
	 * @param currLinkCapacity the current capacity of the link
	 * @param request the request associated with the network change event
	 * @param arrivalTime the time when the IMT arrives
	 */
	public ImtNetworkChangeEventGenerator(Scenario scenario, double currLinkCapacity, Request request, double arrivalTime) {
		this.scenario = Objects.requireNonNull(scenario, "scenario must not be null");
		this.currLinkCapacity = currLinkCapacity;
		this.request = Objects.requireNonNull(request, "request must not be null");
		this.arrivalTime = arrivalTime;
	}
	/**
	 Adds a network change event to the network.
	 @param fullCapacity the full capacity of the link
	 @param reducedCapacity the reduced capacity of the link
	 */
	public void addEventToLog(double fullCapacity, double reducedCapacity, DvrpVehicle imtUnit) {

/*		// Generate Network Change Event
		NetworkChangeEvent restoreCapacityEvent = new NetworkChangeEvent(arrivalTime);
		restoreCapacityEvent.setFlowCapacityChange
				(new NetworkChangeEvent.ChangeValue(NetworkChangeEvent.ChangeType.
						ABSOLUTE_IN_SI_UNITS, currLinkCapacity));
		restoreCapacityEvent.addLink(toLink);
		NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), restoreCapacityEvent);*/

		String output = scenario.getConfig().controler().getOutputDirectory();
		if (output.endsWith("IMT")) {
			// Log incident information
			EventHandler_IMT.handleImtNetworkChangeEvent(request, fullCapacity, reducedCapacity, currLinkCapacity, arrivalTime, imtUnit);
		}
	}
}
