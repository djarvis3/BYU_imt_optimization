package IMT.events.incidents;

import org.matsim.api.core.v01.Scenario;

import org.matsim.api.core.v01.events.Event;
import org.matsim.core.events.handler.BasicEventHandler;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkChangeEvent.ChangeType;
import org.matsim.core.network.NetworkChangeEvent.ChangeValue;
import org.matsim.core.network.NetworkUtils;

public class IncidentEventHandler implements BasicEventHandler {

	private final Scenario scenario;

	public IncidentEventHandler(Scenario scenario) {
		this.scenario = scenario;
	}

	@Override
	public void handleEvent(Event event) {
		if (event instanceof IncidentEvent incidentEvent) {
				// incident starts
				NetworkChangeEvent incidentStart = new NetworkChangeEvent(incidentEvent.getTime());
				incidentStart.setFlowCapacityChange(new ChangeValue(ChangeType.ABSOLUTE_IN_SI_UNITS, incidentEvent.getReducedCapacity()));
				incidentStart.addLink(scenario.getNetwork().getLinks().get(incidentEvent.getLinkId()));
				NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), incidentStart);

				// incident ends
				NetworkChangeEvent incidentEnd = new NetworkChangeEvent(incidentEvent.getEndTime());
				incidentEnd.setFlowCapacityChange(new ChangeValue(ChangeType.ABSOLUTE_IN_SI_UNITS, incidentEvent.getFullCapacity()));
				incidentEnd.addLink(scenario.getNetwork().getLinks().get(incidentEvent.getLinkId()));
				NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), incidentEnd);
			}
		}

		@Override
	public void reset(int iteration) {
		// This method is called at the start of each iteration.
		// Depending on your use case, you might want to reset some of the handler's internal data here.
	}
}
