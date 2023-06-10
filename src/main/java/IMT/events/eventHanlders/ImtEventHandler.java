package IMT.events.eventHanlders;

import IMT.events.incidents.IncidentEvent;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.Event;
import org.matsim.core.events.handler.BasicEventHandler;
import org.matsim.core.network.NetworkChangeEvent;

public class ImtEventHandler implements BasicEventHandler {

	private final Scenario scenario;

	public ImtEventHandler(Scenario scenario){
		this.scenario = scenario;
	}

	@Override
	public void reset(int iteration) {
		// reset state if necessary
	}

	@Override
	public void handleEvent(Event event) {
		if (event instanceof IncidentEvent incidentEvent) {

			NetworkChangeEvent imtArrival = new NetworkChangeEvent(incidentEvent.getTime());
			imtArrival.setFlowCapacityChange(new NetworkChangeEvent.ChangeValue(NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, incidentEvent.getFullCapacity()));
			imtArrival.addLink(scenario.getNetwork().getLinks().get(incidentEvent.getLinkId()));

			// do something with imtEvent
		}
	}
}



