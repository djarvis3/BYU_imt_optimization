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

import byu.incidents.Read_Incident;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.dvrp.optimizer.Request;
import org.matsim.contrib.dvrp.optimizer.VrpOptimizer;
import org.matsim.contrib.dvrp.run.DvrpMode;
import org.matsim.core.mobsim.framework.events.MobsimAfterSimStepEvent;
import org.matsim.core.mobsim.framework.listeners.MobsimAfterSimStepListener;

import javax.inject.Inject;
import java.util.*;

/**
 * @author michalm
 */
public final class UtahImtRequestCreator implements MobsimAfterSimStepListener {
	private final VrpOptimizer optimizer;
	private final PriorityQueue<UtahImtRequest> requests = new PriorityQueue<>(10,
			Comparator.comparing(Request::getSubmissionTime));


	@Inject
	public UtahImtRequestCreator(@DvrpMode(TransportMode.truck) VrpOptimizer optimizer,
								 @DvrpMode(TransportMode.truck) Network network) {
		this.optimizer = optimizer;
		}


	public UtahImtRequest createRequest(String requestId, String toLinkId, double time, Network network) {
		return new UtahImtRequest(Id.create(requestId, Request.class),
				network.getLinks().get(Id.createLinkId(toLinkId)), time);
	}

	@Override
	public void notifyMobsimAfterSimStep(@SuppressWarnings("rawtypes") MobsimAfterSimStepEvent e) {
		while (isReadyForSubmission(requests.peek(), e.getSimulationTime())) {
			optimizer.requestSubmitted(requests.poll());
		}
	}

	private boolean isReadyForSubmission(UtahImtRequest request, double currentTime) {
		return request != null && request.getSubmissionTime() <= currentTime;
	}
}
