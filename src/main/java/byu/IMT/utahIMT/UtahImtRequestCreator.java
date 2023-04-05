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

package byu.IMT.utahIMT;
import byu.incidents.Incident;
import byu.incidents.IncidentReader;
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
 * @author michalm
 */
public class UtahImtRequestCreator implements MobsimAfterSimStepListener, EventHandler {

	private final VrpOptimizer optimizer;
	private final PriorityQueue<UtahImtRequest> requests = new PriorityQueue<>(18,
			Comparator.comparing(Request::getSubmissionTime));
	private final Network network;
	private static int incidentsAddedCount = 0; // variable to count the number of times addIncidentsToRequests is called

	@Inject
	public UtahImtRequestCreator(@DvrpMode(TransportMode.truck) VrpOptimizer optimizer,
								 @DvrpMode(TransportMode.truck) Network network,
								 Scenario scenario) {

		this.optimizer = optimizer;
		this.network = network;

		// only add incidents if they have not been added before
		if (incidentsAddedCount == 0) {
			addIncidentsToRequests(scenario);
			incidentsAddedCount++;
		}
	}



	private void addIncidentsToRequests(Scenario scenario) {
		IncidentReader incidents = new IncidentReader(scenario.getNetwork());
		incidents.readIncidents("incident_excel_data/IncidentData_Daniel.csv");
		List<Incident> incidentsSelected = incidents.getIncidentsSelected();
		for (Incident incident : incidentsSelected) {
			requests.add(createRequest(incident));
		}
	}

	public UtahImtRequest createRequest(Incident incident) {
		String requestId = "incident_" + incident.getIncidentID();
		Link toLink = network.getLinks().get(Id.createLinkId(incident.getLinkId()));
		double time = incident.getStartTime();
		return new UtahImtRequest(Id.create(requestId, Request.class), toLink, time);
	}

	@Override
	public void notifyMobsimAfterSimStep(MobsimAfterSimStepEvent e) {
		while (isReadyForSubmission(requests.peek(), e.getSimulationTime())) {
			optimizer.requestSubmitted(requests.poll());
		}
	}

	private boolean isReadyForSubmission(UtahImtRequest request, double currentTime) {
		return request != null && request.getSubmissionTime() <= currentTime;
	}

	@Override
	public void reset(int iteration) {
		EventHandler.super.reset(iteration);
	}
}
