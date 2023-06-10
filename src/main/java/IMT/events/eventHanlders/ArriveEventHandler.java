package IMT.events.eventHanlders;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.ActivityStartEvent;
import org.matsim.api.core.v01.events.handler.ActivityStartEventHandler;

import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkChangeEvent.ChangeType;
import org.matsim.core.network.NetworkChangeEvent.ChangeValue;
import org.matsim.core.network.NetworkUtils;

public class ArriveEventHandler implements ActivityStartEventHandler {

	private final Scenario scenario;

	public ArriveEventHandler(Scenario scenario) {
		this.scenario = scenario;
	}

	@Override
	public void handleEvent(ActivityStartEvent event) {
		if (event.getEventType().equals("actstart")) {
			if (event.getActType().equals("ARRIVE")) {
				// create a NetworkChangeEvent and add it to the NetworkChangeEventsManager
				NetworkChangeEvent imtArrival = new NetworkChangeEvent(event.getTime());
				imtArrival.setFlowCapacityChange(new ChangeValue(ChangeType.ABSOLUTE_IN_SI_UNITS, 0.8)); // For example, reduce the capacity to 20%
				imtArrival.addLink(scenario.getNetwork().getLinks().get(event.getLinkId()));
				NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), imtArrival);
			}
		}
	}

	@Override
	public void reset(int iteration) {
		// This method is called at the start of each iteration.
		// Depending on your use case, you might want to reset some of the handler's internal data here.
	}
}
