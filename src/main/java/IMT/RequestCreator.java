package IMT;

import incidents.Incident;
import incidents.IncidentReader;

import com.google.inject.Inject;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.dvrp.optimizer.Request;
import org.matsim.contrib.dvrp.optimizer.VrpOptimizer;
import org.matsim.contrib.dvrp.run.DvrpMode;
import org.matsim.core.events.handler.EventHandler;
import org.matsim.core.mobsim.framework.events.MobsimAfterSimStepEvent;
import org.matsim.core.mobsim.framework.listeners.MobsimAfterSimStepListener;

import java.util.*;

/**
 * The RequestCreator class creates and submits requests for incidents during the simulation.
 */
public class RequestCreator implements MobsimAfterSimStepListener, EventHandler {

	private static double FLOW_CAPACITY_FACTOR;
	private final VrpOptimizer optimizer;
	private final Network network;
	private final PriorityQueue<ImtRequest> requests = new PriorityQueue<>(100,
			Comparator.comparing(org.matsim.contrib.dvrp.optimizer.Request::getSubmissionTime));

	List<Incident> incidentsList = IncidentManager.getIncidentsSelected();

	/**
	 * Creates a RequestCreator instance.
	 * @param optimizer The VrpOptimizer used for request submission.
	 * @param network The network used for incident information.
	 * @param scenario The scenario containing the simulation configuration.
	 */
	@Inject
	public RequestCreator(@DvrpMode(TransportMode.truck) VrpOptimizer optimizer,
						  @DvrpMode(TransportMode.truck) Network network,
						  Scenario scenario) {
		this.optimizer = optimizer;
		this.network = network;

		FLOW_CAPACITY_FACTOR = scenario.getConfig().qsim().getFlowCapFactor();

		if (incidentsList == null) {
			incidentsList = readIncidentsFromCsv();
			IncidentManager.setIncidentsSelected(incidentsList);
		}

		for (Incident incident : incidentsList) {
			requests.add(createRequest(incident));
		}
	}

	private List<Incident> readIncidentsFromCsv() {
		if (incidentsList == null) {
			IncidentReader incidents = new IncidentReader("utah/incidents/UtahIncidents_MATSim.csv", network);
			incidentsList = incidents.getSeededIncidents(0, 3093);
		}
		return incidentsList;
	}

	private ImtRequest createRequest(Incident inc) {
		String requestId = "incident_" + inc.getIncID();
		Link incLink = network.getLinks().get(Id.createLinkId(inc.getLinkId()));
		double submissionTime = inc.getStartTime();
		double capacityReduction_percentage = inc.getCapacityReduction();
		double reducedCapacity_link = ((incLink.getCapacity() * FLOW_CAPACITY_FACTOR) - (incLink.getCapacity() * capacityReduction_percentage * FLOW_CAPACITY_FACTOR));
		double endTime = inc.getEndTime();
		double fullCapacity_link = (incLink.getCapacity() * FLOW_CAPACITY_FACTOR);
		int respondingIMTs = inc.getRespondingIMTs();

		return new ImtRequest(Id.create(requestId, Request.class), submissionTime, incLink, capacityReduction_percentage, reducedCapacity_link, endTime, fullCapacity_link, respondingIMTs);
	}

	@Override
	public void notifyMobsimAfterSimStep(@SuppressWarnings("rawtypes") MobsimAfterSimStepEvent e) {
		while (canSubmitRequest(requests.peek(), e.getSimulationTime())) {
			optimizer.requestSubmitted(requests.poll());
		}
	}

	private boolean canSubmitRequest(Request request, double currentTime) {
		return request != null && request.getSubmissionTime() <= currentTime;
	}
}
