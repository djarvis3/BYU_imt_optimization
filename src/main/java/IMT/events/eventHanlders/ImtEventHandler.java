package IMT.events.eventHanlders;

import IMT.events.ImtEvent;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.Event;
import org.matsim.core.events.handler.BasicEventHandler;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkUtils;

/**
 * Handles IMT events and generates network change events accordingly.
 */
public class ImtEventHandler implements BasicEventHandler {

	private final Scenario scenario;
	private boolean isFirstIteration = true;

	/**
	 * Constructs an ImtEventHandler object with the given scenario.
	 *
	 * @param scenario the scenario to be used for event handling
	 */
	public ImtEventHandler(Scenario scenario) {
		this.scenario = scenario;
	}

	/**
	 * Handles the specified event and generates network change events for IMT arrivals.
	 *
	 * @param event the event to be handled
	 */
	@Override
	public void handleEvent(Event event) {
		if (isFirstIteration && event instanceof ImtEvent imtEvent) {
			if (imtEvent.getArrivalTime() < imtEvent.getEndTime()) {
				// IMT arrival NetworkChangeEvent
				NetworkChangeEvent imtArrival = new NetworkChangeEvent(imtEvent.getArrivalTime());
				imtArrival.setFlowCapacityChange(new NetworkChangeEvent.ChangeValue(NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, imtEvent.getCurrentCapacity()));
				imtArrival.addLink(scenario.getNetwork().getLinks().get(imtEvent.getLinkId()));
				NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), imtArrival);
			}
		}
	}

	/**
	 * Resets the state of the event handler for the first iteration.
	 *
	 * @param iteration the iteration number
	 */
	@Override
	public void reset(int iteration) {
		if (iteration == 1) {
			this.isFirstIteration = false;
		}
	}
}
