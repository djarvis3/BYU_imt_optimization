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

package byu.IMT;

import byu.incidents.Incident;
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
public final class ImtOptimizer implements VrpOptimizer {

	// Define task types that can be used by the optimizer
	public enum UtahImtTaskType implements Task.TaskType {
		DRIVE_TO_INCIDENT, ARRIVAL, INCIDENT_MANAGEMENT, DEPARTURE, WAIT
	}

	// Define some constants
	private static final double INCIDENT_MANAGEMENT_DURATION = 120; // 2 minutes

	// This submissionTime should be variable, but for now, it is going to be two minutes.

	private final MobsimTimer timer;
	private final TravelTime travelTime;
	private final LeastCostPathCalculator router;
	private final DvrpVehicle vehicle;
	private final Fleet fleet;
	List<Incident> incidentsSelected = IncidentManager.getIncidentsSelected();


	// Constructor for the optimizer
	@Inject
	public ImtOptimizer(@DvrpMode(TransportMode.truck) Network network, @DvrpMode(TransportMode.truck) Fleet fleet, MobsimTimer timer) {
		this.timer = timer;
		this.fleet = fleet;

		// Set up travel-related objects
		travelTime = new FreeSpeedTravelTime();
		router = new SpeedyDijkstraFactory().createPathCalculator(network, new TimeAsTravelDisutility(travelTime), travelTime);

		// Set up the vehicles that will be used by the optimizer
		this.vehicle = fleet.getVehicles().values().iterator().next();

		// Loop over all vehicles and add a waiting task to their schedules
		for (DvrpVehicle vehicle : fleet.getVehicles().values()) {
			vehicle.getSchedule().addTask(new DefaultStayTask(UtahImtTaskType.WAIT, vehicle.getServiceBeginTime(), vehicle.getServiceEndTime(), vehicle.getStartLink()));
		}
	}

	// Method called when a new request is submitted to the optimizer
	@Override
	public void requestSubmitted(Request request) {
		// Cast the request to the appropriate type
		ImtRequest req = (ImtRequest) request;
		Link toLink = req.getToLink();

		// Find the vehicle with the best path to the incident
		double bestTravelTime = Double.POSITIVE_INFINITY;
		DvrpVehicle bestVehicle = null;
		for (DvrpVehicle vehicle : fleet.getVehicles().values()) {
			// Get the last link in the vehicle's schedule
			Link fromLink = Schedules.getLastLinkInSchedule(vehicle);

			// Calculate the travel time from the current location of the vehicle to the incident location
			double time_zero = 0;
			VrpPathWithTravelData pathToIncident = VrpPaths.calcAndCreatePath(fromLink, toLink, time_zero, router, travelTime);

			// Calculate the time it would take to complete the request (travel time + incident management time)
			double travelTimeWithIncident = pathToIncident.getArrivalTime();

			// Check if this is the best vehicle so far
			if (travelTimeWithIncident < bestTravelTime) {
				bestTravelTime = travelTimeWithIncident;
				bestVehicle = vehicle;
			}
		}

		// Add tasks to the schedule of the best vehicle
		Schedule schedule = bestVehicle.getSchedule();
		StayTask lastTask = (StayTask) Schedules.getLastTask(schedule); // Only WaitTask possible here
		double currentTime = timer.getTimeOfDay();

		switch (lastTask.getStatus()) {
			case PLANNED -> schedule.removeLastTask(); // Remove wait task
			case STARTED -> lastTask.setEndTime(currentTime); // Shorten wait task
			case PERFORMED -> throw new IllegalStateException();
		}

		double t0 = schedule.getStatus() == ScheduleStatus.UNPLANNED ?
				Math.max(bestVehicle.getServiceBeginTime(), currentTime) :
				Schedules.getLastTask(schedule).getEndTime();

		VrpPathWithTravelData pathToIncident = VrpPaths.calcAndCreatePath(lastTask.getLink(), toLink, t0, router, travelTime);
		schedule.addTask(new DefaultDriveTask(UtahImtTaskType.DRIVE_TO_INCIDENT, pathToIncident));

		double t1 = pathToIncident.getArrivalTime();
		double t2 = t1 + INCIDENT_MANAGEMENT_DURATION;// 2 minutes for the incident management.
		schedule.addTask(new ImtServeTask(UtahImtTaskType.INCIDENT_MANAGEMENT, t1, t2, toLink, req));

		// just wait (and be ready) till the end of the vehicle's submissionTime window (T1)
		double tEnd = Math.max(t2, vehicle.getServiceEndTime());
		schedule.addTask(new DefaultStayTask(UtahImtTaskType.WAIT, t2, tEnd, toLink));
	}

	@Override
	public void nextTask(DvrpVehicle vehicle) {
		updateTimings(vehicle.getSchedule());
		vehicle.getSchedule().nextTask();
	}

	private void updateTimings(Schedule schedule) {
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

		// last task (waiting)
		Task lastTask = tasks.get(tasks.size() - 1);
		lastTask.setBeginTime(Math.max(currentTask.getEndTime(), lastTask.getBeginTime()));
		lastTask.setEndTime(Math.max(currentTask.getEndTime(), lastTask.getEndTime()));
	}
}
