package IMT.events.eventHanlders;

import IMT.events.IncidentEvent;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.Event;
import org.matsim.core.events.handler.BasicEventHandler;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkChangeEvent.ChangeType;
import org.matsim.core.network.NetworkChangeEvent.ChangeValue;
import org.matsim.core.network.NetworkUtils;

/**
 * Handles incident events and generates network change events accordingly.
 */
public class IncidentEventHandler implements BasicEventHandler {

	private final Scenario scenario;
	private boolean isFirstIteration = true;

	/**
	 * Constructs an IncidentEventHandler object with the given scenario.
	 *
	 * @param scenario the scenario to be used for event handling
	 */
	public IncidentEventHandler(Scenario scenario) {
		this.scenario = scenario;
	}

	/**
	 * Handles the incident event and generates network change events for incident start and end.
	 *
	 * @param event the event to be handled
	 */
	@Override
	public void handleEvent(Event event) {
		if (isFirstIteration && event instanceof IncidentEvent incidentEvent) {
			// Incident start network change event
			NetworkChangeEvent incidentStart = new NetworkChangeEvent(incidentEvent.getTime());
			incidentStart.setFlowCapacityChange(new ChangeValue(ChangeType.ABSOLUTE_IN_SI_UNITS, incidentEvent.getReducedCapacity()));
			incidentStart.addLink(scenario.getNetwork().getLinks().get(incidentEvent.getLinkId()));
			NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), incidentStart);

			// Incident end network change event
			NetworkChangeEvent incidentEnd = new NetworkChangeEvent(incidentEvent.getEndTime());
			incidentEnd.setFlowCapacityChange(new ChangeValue(ChangeType.ABSOLUTE_IN_SI_UNITS, incidentEvent.getFullCapacity()));
			incidentEnd.addLink(scenario.getNetwork().getLinks().get(incidentEvent.getLinkId()));
			NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), incidentEnd);
		}
	}

	/**
	 * Resets the state of the event handler for the first iteration.
	 *
	 * @param iteration the iteration number
	 */
	@Override
	public void reset(int iteration) {
		if (iteration != 0) {
			this.isFirstIteration = false;
		}
	}
}
