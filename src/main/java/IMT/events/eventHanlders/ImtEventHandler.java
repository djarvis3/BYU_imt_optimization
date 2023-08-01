package IMT.events.eventHanlders;

import IMT.events.ImtEvent;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.Event;
import org.matsim.core.events.handler.BasicEventHandler;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkUtils;

/**
 * Implementation of a BasicEventHandler that handles IMT events and generates network change events accordingly.
 * This handler is designed to operate only during the first iteration of a simulation scenario.
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
	 * Handles the specified event and generates network change events for IMT arrivals if the event is an instance of ImtEvent.
	 * This method will only process events during the first iteration of the simulation.
	 *
	 * @param event the event to be handled
	 */
	@Override
	public void handleEvent(Event event) {
		if (!isFirstIteration) return;
		if (event instanceof ImtEvent imtEvent) {
			handleImtEvent(imtEvent);
		}
	}

	/**
	 * Handles an ImtEvent by creating and adding a corresponding NetworkChangeEvent to the scenario's network.
	 * Only called if the event's arrival time is less than its end time.
	 *
	 * @param imtEvent the ImtEvent to be handled
	 */
	private void handleImtEvent(ImtEvent imtEvent) {
		if (imtEvent.getArrivalTime() < imtEvent.getEndTime()) {
			NetworkChangeEvent imtArrival = new NetworkChangeEvent(imtEvent.getArrivalTime());
			imtArrival.setFlowCapacityChange(new NetworkChangeEvent.ChangeValue(NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, imtEvent.getCurrentCapacity()));
			imtArrival.addLink(scenario.getNetwork().getLinks().get(imtEvent.getLinkId()));
			NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), imtArrival);
		}
	}

	/**
	 * Resets the state of the event handler for the first iteration.
	 * After the first iteration, the handler will no longer process events.
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
