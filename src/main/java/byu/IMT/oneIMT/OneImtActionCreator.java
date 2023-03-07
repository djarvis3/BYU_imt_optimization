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

package byu.IMT.oneIMT;

import byu.IMT.oneIMT.OneImtOptimizer.OneImtTaskType;
import com.google.inject.Inject;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.schedule.Task;
import org.matsim.contrib.dvrp.vrpagent.VrpAgentLogic;
import org.matsim.contrib.dvrp.vrpagent.VrpLegFactory;
import org.matsim.contrib.dynagent.DynAction;
import org.matsim.contrib.dynagent.DynAgent;
import org.matsim.contrib.dynagent.IdleDynActivity;
import org.matsim.core.mobsim.framework.MobsimTimer;

/**
 * @author michalm
 */
final class OneImtActionCreator implements VrpAgentLogic.DynActionCreator {
	private final MobsimTimer timer;

	@Inject
	public OneImtActionCreator(MobsimTimer timer) {
		this.timer = timer;
	}

	@Override
	public DynAction createAction(DynAgent dynAgent, DvrpVehicle vehicle, double now) {
		Task task = vehicle.getSchedule().getCurrentTask();
		return switch ((OneImtTaskType) task.getTaskType()) {
			case DRIVE_TO_INCIDENT -> VrpLegFactory.createWithOfflineTracker(TransportMode.truck, vehicle, timer);
			case ARRIVAL, INCIDENT_MANAGEMENT, DEPARTURE, WAIT -> new IdleDynActivity(task.getTaskType() + "", task::getEndTime);
		};
	}
}
