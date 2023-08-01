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
import org.matsim.core.mobsim.framework.events.MobsimAfterSimStepEvent;
import org.matsim.core.mobsim.framework.listeners.MobsimAfterSimStepListener;

import java.util.*;

/**
 * The RequestCreator class creates and submits requests for incidents during the simulation.
 */
public class RequestCreator implements MobsimAfterSimStepListener {

	private static double FLOW_CAPACITY_FACTOR;
	private final VrpOptimizer optimizer;
	private final Network network;
	private final PriorityQueue<ImtRequest> requests;

	List<Incident> incidentsList;

	@Inject
	public RequestCreator(@DvrpMode(TransportMode.truck) VrpOptimizer optimizer,
						  @DvrpMode(TransportMode.truck) Network network,
						  Scenario scenario, ImtConfigGroup imtConfig) {
		this.optimizer = optimizer;
		this.network = network;

		FLOW_CAPACITY_FACTOR = scenario.getConfig().qsim().getFlowCapFactor();
		if ("incidentSeedSelection".equals(imtConfig.getIncidentSelection())) {
			this.incidentsList = readSelectedIncidentsFromCsv(imtConfig);
		} else if ("selectAllIncidents".equals(imtConfig.getIncidentSelection())) {
			this.incidentsList = readAllIncidentsFromCsv(imtConfig);
		} else {
			this.incidentsList = Collections.emptyList(); // baseline
		}

		this.requests = new PriorityQueue<>(100, Comparator.comparing(Request::getSubmissionTime));
		for (Incident incident : incidentsList) {
			requests.add(createRequest(incident));
		}
	}

	private List<Incident> readAllIncidentsFromCsv(ImtConfigGroup imtConfig) {
		IncidentReader incidents = new IncidentReader(imtConfig.getIncidentsCsvFilePath(), network);
		return incidents.getAllIncidents();
	}

	private List<Incident> readSelectedIncidentsFromCsv(ImtConfigGroup imtConfig) {
		IncidentReader incidents = new IncidentReader(imtConfig.getIncidentsCsvFilePath(), network);
		return incidents.getSeededIncidents(imtConfig.getNumIncidentsToSelect(), imtConfig.getIncidentSelectionSeed());
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
