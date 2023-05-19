/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2012 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package IMT;
import incidents.Incident;

import incidents.IncidentReader;
import com.google.inject.Inject;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.dvrp.optimizer.VrpOptimizer;
import org.matsim.contrib.dvrp.run.DvrpMode;
import org.matsim.core.events.handler.EventHandler;
import org.matsim.core.mobsim.framework.events.MobsimAfterSimStepEvent;
import org.matsim.core.mobsim.framework.listeners.MobsimAfterSimStepListener;

import java.util.*;

/**
 * @author michalm
 */
public class RequestCreator implements MobsimAfterSimStepListener, EventHandler {

	// Instance variables
	private final VrpOptimizer optimizer;
	private final PriorityQueue<Request> requests = new PriorityQueue<>(100,
			Comparator.comparing(org.matsim.contrib.dvrp.optimizer.Request::getSubmissionTime));
	private final Network network;
	List<Incident> incidentsList = IncidentManager.getIncidentsSelected();

	// Constructor
	@Inject
	public RequestCreator(@DvrpMode(TransportMode.truck) VrpOptimizer optimizer,
						  @DvrpMode(TransportMode.truck) Network network) {

		// Initialize instance variables
		this.optimizer = optimizer;
		this.network = network;

		// Read incidents from the CSV file and select only the "incidentsList" list
		if (incidentsList == null) {
			incidentsList = readIncidentsFromCsv();
			IncidentManager.setIncidentsSelected(incidentsList);
		}

		// Create a new request for each  incident and add it to the requests PriorityQueue
		for (Incident incident : incidentsList) {
			requests.add(createRequest(incident));
		}
	}

	// Reads incidents from the CSV file and selects only the "incidentsList" list
	private List<Incident> readIncidentsFromCsv() {
		if (incidentsList == null) {
			IncidentReader incidents = new IncidentReader("scenarios/equil/IncidentData_Equil.csv");
			// to select random incidents from the CSV use incidents.getRandomIncidents();
			// incidentsList = incidents.randomIncidents();

			// to select all the incidents from the CSV use
			incidentsList = incidents.getSeededIncidents(1,4);
			// incidentsList = incidents.getSeededIncidents(23,1234);
		}
		return incidentsList;
	}

	// Creates a new request from an incident and a responding unit ID
	private Request createRequest(Incident incident) {
		String requestId = "incident_" + incident.getIncidentID();
		Link toLink = network.getLinks().get(Id.createLinkId(incident.getLinkId()));
		double submissionTime = incident.getStartTime();
		double endTime = incident.getEndTime();
		double capacityReduction = incident.getCapacityReduction();
		int respondingIMTs = incident.getRespondingIMTs();
		return new Request(Id.create(requestId, org.matsim.contrib.dvrp.optimizer.Request.class),
				toLink, submissionTime, endTime, capacityReduction, respondingIMTs);
	}

	// Called at the end of each simulation step
	@Override
	public void notifyMobsimAfterSimStep(@SuppressWarnings("rawtypes") MobsimAfterSimStepEvent e) {
		// Submit all requests that are ready (i.e., whose submission time is less than or equal to the current time)
		while (canSubmitRequest(requests.peek(), e.getSimulationTime())) {
			optimizer.requestSubmitted(requests.poll());
		}
	}

	// Determines whether a request is ready to be submitted
	private boolean canSubmitRequest(Request request, double currentTime) {
		return request != null && request.getSubmissionTime() <= currentTime;
	}
}
