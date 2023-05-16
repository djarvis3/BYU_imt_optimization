package IMT.events;

import IMT.Request;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkUtils;

import java.util.Objects;

/**
 * The IncidentNetworkChangeEventGenerator class generates network change events for a given incident upon request
 * during the scenario.
 */
public class IncidentNetworkChangeEventGenerator {

	private final Scenario scenario;

	/**
	 * Constructs an IncidentNetworkChangeEventGenerator with the given scenario.
	 *
	 * @param scenario the scenario to generate events for
	 * @throws NullPointerException if scenario is null
	 */
	public IncidentNetworkChangeEventGenerator(Scenario scenario) {
		this.scenario = Objects.requireNonNull(scenario, "scenario must not be null");
		new EventHandler(scenario);
	}

	/**
	 * Generates network change events from the given incident link, reduced capacity, full capacity, start time,
	 * and end time.
	 *
	 * @param incidentLink the incident link to generate events for
	 * @param reducedCapacity the reduced capacity of the incident link
	 * @param fullCapacity the full capacity of the incident link
	 * @param startTime the start time of the incident
	 * @param endTime the end time of the incident
	 * @param request the request associated with the incident
	 * @throws NullPointerException if incidentLink is null
	 */
	public void generateIncidentNetworkChangeEvents(Link incidentLink, double reducedCapacity, double fullCapacity,
													double startTime, double endTime, Request request) {
		Objects.requireNonNull(incidentLink, "incidentLink must not be null");

		// Generate start event
		NetworkChangeEvent startEvent = new NetworkChangeEvent(startTime);
		startEvent.setFlowCapacityChange(new NetworkChangeEvent.ChangeValue(
				NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, reducedCapacity));
		startEvent.addLink(incidentLink);
		NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), startEvent);

		// Generate end event
		NetworkChangeEvent endEvent = new NetworkChangeEvent(endTime);
		endEvent.setFlowCapacityChange(new NetworkChangeEvent.ChangeValue(
				NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, fullCapacity));
		endEvent.addLink(incidentLink);
		NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), endEvent);

		// Log incident information
		EventHandler.handleIncidentNetworkChangeEvent(request,  fullCapacity, reducedCapacity, startTime, endTime);
	}
}
