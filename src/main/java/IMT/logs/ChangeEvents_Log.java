package IMT.logs;

import IMT.ImtRequest;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;

import java.util.Objects;

/**
 * The IncidentNetworkChangeEventGenerator class generates network change events for a given incident upon request
 * during the scenario.
 */
public class ChangeEvents_Log {

	private final Scenario scenario;

	/**
	 * Constructs an IncidentNetworkChangeEventGenerator with the given scenario.
	 *
	 * @param scenario the scenario to generate events for
	 * @throws NullPointerException if scenario is null
	 */
	public ChangeEvents_Log(Scenario scenario) {
		this.scenario = Objects.requireNonNull(scenario, "scenario must not be null");
	}

	public void addEventToLog(Link incidentLink, double reducedCapacity, double fullCapacity,
							  double startTime, double endTime, ImtRequest imtRequest) {
		Objects.requireNonNull(incidentLink, "incidentLink must not be null");

		String output = scenario.getConfig().controler().getOutputDirectory();
		if (output.endsWith("IMT")) {
			// Log incident information
			IMT_Log.handleIncidentNetworkChangeEvent(imtRequest, reducedCapacity, fullCapacity, startTime, endTime);
		}
		else if (output.endsWith("Incidents")) {
			// Log incident information
			Incidents_Log.handleIncidentNetworkChangeEvent(imtRequest, reducedCapacity, fullCapacity, startTime, endTime);
		}
	}
}
