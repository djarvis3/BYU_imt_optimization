/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
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

import com.google.inject.Inject;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.fleet.Fleet;
import org.matsim.contrib.dvrp.optimizer.Request;
import org.matsim.contrib.dvrp.optimizer.VrpOptimizer;
import org.matsim.contrib.dvrp.path.VrpPathWithTravelData;
import org.matsim.contrib.dvrp.path.VrpPaths;
import org.matsim.contrib.dvrp.router.TimeAsTravelDisutility;
import org.matsim.contrib.dvrp.run.DvrpMode;
import org.matsim.contrib.dvrp.schedule.*;
import org.matsim.contrib.dvrp.schedule.Schedule.ScheduleStatus;
import org.matsim.core.mobsim.framework.MobsimTimer;
import org.matsim.core.router.speedy.SpeedyDijkstraFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.trafficmonitoring.FreeSpeedTravelTime;

import java.util.List;

/**
 * @author michalm
 */
final class OneImtOptimizer implements VrpOptimizer {
	enum OneImtTaskType implements Task.TaskType {
		DRIVE_TO_INCIDENT, ARRIVAL, INCIDENT_MANAGEMENT, DEPARTURE, WAIT
	}

	private final MobsimTimer timer;

	private final TravelTime travelTime;
	private final LeastCostPathCalculator router;

	private final DvrpVehicle vehicle;// we have only one vehicle

	private static final double INCIDENT_MANAGEMENT_DURATION = 120; // This time should be variable, but for now, it is going to be two minutes.


	@Inject
	public OneImtOptimizer(@DvrpMode(TransportMode.truck) Network network, @DvrpMode(TransportMode.truck) Fleet fleet,
						   MobsimTimer timer) {
		this.timer = timer;
		travelTime = new FreeSpeedTravelTime();
		router = new SpeedyDijkstraFactory().createPathCalculator(network, new TimeAsTravelDisutility(travelTime),
				travelTime);

		vehicle = fleet.getVehicles().values().iterator().next();
		vehicle.getSchedule()
				.addTask(new DefaultStayTask(OneImtTaskType.WAIT, vehicle.getServiceBeginTime(), vehicle.getServiceEndTime(),
						vehicle.getStartLink()));
	}

	@Override
	public void requestSubmitted(Request request) {
		Schedule schedule = vehicle.getSchedule();
		StayTask lastTask = (StayTask)Schedules.getLastTask(schedule);// only WaitTask possible here
		double currentTime = timer.getTimeOfDay();

		switch (lastTask.getStatus()) {
			case PLANNED:
				schedule.removeLastTask();// remove wait task
				break;

			case STARTED:
				lastTask.setEndTime(currentTime);// shorten wait task
				break;

			case PERFORMED:
			default:
				throw new IllegalStateException();
		}

		OneImtRequest req = (OneImtRequest)request;
		Link toLink = req.getToLink();

		double t0 = schedule.getStatus() == ScheduleStatus.UNPLANNED ?
				Math.max(vehicle.getServiceBeginTime(), currentTime) :
				Schedules.getLastTask(schedule).getEndTime();

		VrpPathWithTravelData pathToIncident = VrpPaths.calcAndCreatePath(lastTask.getLink(), toLink, t0, router, travelTime);
		schedule.addTask(new DefaultDriveTask(OneImtTaskType.DRIVE_TO_INCIDENT, pathToIncident));

		double t1 = pathToIncident.getArrivalTime();
		double t2 = t1 + INCIDENT_MANAGEMENT_DURATION;// 2 minutes for the incident management.
		schedule.addTask(new OneImtServeTask(OneImtTaskType.INCIDENT_MANAGEMENT, t1, t2, toLink, req));

		// just wait (and be ready) till the end of the vehicle's time window (T1)
		double tEnd = Math.max(t2, vehicle.getServiceEndTime());
		schedule.addTask(new DefaultStayTask(OneImtTaskType.WAIT, t2, tEnd, toLink));
	}

	@Override
	public void nextTask(DvrpVehicle vehicle1) {
		updateTimings();
		vehicle1.getSchedule().nextTask();
	}

	/**
	 * Simplified version. For something more advanced, see
	 * {@link org.matsim.contrib.taxi.scheduler.TaxiScheduler#updateBeforeNextTask(DvrpVehicle)} in the taxi contrib
	 */
	private void updateTimings() {
		Schedule schedule = vehicle.getSchedule();
		if (schedule.getStatus() != ScheduleStatus.STARTED) {
			return;
		}

		double now = timer.getTimeOfDay();
		Task currentTask = schedule.getCurrentTask();
		double diff = now - currentTask.getEndTime();

		if (diff == 0) {
			return;
		}

		currentTask.setEndTime(now);

		List<? extends Task> tasks = schedule.getTasks();
		int nextTaskIdx = currentTask.getTaskIdx() + 1;

		// all except the last task (waiting)
		for (int i = nextTaskIdx; i < tasks.size() - 1; i++) {
			Task task = tasks.get(i);
			task.setBeginTime(task.getBeginTime() + diff);
			task.setEndTime(task.getEndTime() + diff);
		}

		// wait task
		if (nextTaskIdx != tasks.size()) {
			Task waitTask = tasks.get(tasks.size() - 1);
			waitTask.setBeginTime(waitTask.getBeginTime() + diff);

			double tEnd = Math.max(waitTask.getBeginTime(), vehicle.getServiceEndTime());
			waitTask.setEndTime(tEnd);
		}
	}
}
