package IMT.events.eventHanlders;

import IMT.events.ImtEvent;
import IMT.events.IncidentEvent;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.Event;
import org.matsim.core.events.handler.BasicEventHandler;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkUtils;

public class ImtEventHandler implements BasicEventHandler {

	private final Scenario scenario;

	public ImtEventHandler(Scenario scenario){
		this.scenario = scenario;
	}


	@Override
	public void handleEvent(Event event) {
		if (event instanceof ImtEvent imtEvent) {
			if (imtEvent.getArrivalTime() < imtEvent.getEndTime()) {

				// imt arrival NetworkChangeEvent
				NetworkChangeEvent imtArrival = new NetworkChangeEvent(imtEvent.getArrivalTime());
				imtArrival.setFlowCapacityChange(new NetworkChangeEvent.ChangeValue(NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, imtEvent.getCurrentCapacity()));
				imtArrival.addLink(scenario.getNetwork().getLinks().get(imtEvent.getLinkId()));
				NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), imtArrival);
			}
		}
	}

	@Override
	public void reset(int iteration) {
		// reset state if necessary
	}
}



