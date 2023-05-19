package IMT.events;

import IMT.Request;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;

import java.util.Objects;

/**
 * The IncidentNetworkChangeEventGenerator class generates network change events for a given incident upon request
 * during the scenario.
 */
public class IncidentNetworkChangeEventGenerator {

	/**
	 * Constructs an IncidentNetworkChangeEventGenerator with the given scenario.
	 *
	 * @param scenario the scenario to generate events for
	 * @throws NullPointerException if scenario is null
	 */
	public IncidentNetworkChangeEventGenerator(Scenario scenario) {
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
	public void generateIncidentEvent(Link incidentLink, double reducedCapacity, double fullCapacity,
													double startTime, double endTime, Request request) {
		Objects.requireNonNull(incidentLink, "incidentLink must not be null");

		// Log incident information
		EventHandler.handleIncidentNetworkChangeEvent(request, reducedCapacity, fullCapacity, startTime, endTime);
	}
}
