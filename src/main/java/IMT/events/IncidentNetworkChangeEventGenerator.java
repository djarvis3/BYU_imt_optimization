package IMT.events;

import IMT.Request;
import IMT.events.eventHanlders.IMT_Log;
import IMT.events.eventHanlders.Incidents_Log;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;

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
	public void addEventToLog(Link incidentLink, double reducedCapacity, double fullCapacity,
							  double startTime, double endTime, Request request) {
		Objects.requireNonNull(incidentLink, "incidentLink must not be null");

		String output = scenario.getConfig().controler().getOutputDirectory();
		if (output.endsWith("IMT")) {
			// Log incident information
			IMT_Log.handleIncidentNetworkChangeEvent(request, reducedCapacity, fullCapacity, startTime, endTime);
		}
		else if (output.endsWith("Incidents")) {
			// Log incident information
			Incidents_Log.handleIncidentNetworkChangeEvent(request, reducedCapacity, fullCapacity, startTime, endTime);
		}
	}
}
