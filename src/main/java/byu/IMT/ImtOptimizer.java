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

import com.google.inject.Inject;
import org.matsim.api.core.v01.Scenario;
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
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.router.speedy.SpeedyDijkstraFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.trafficmonitoring.FreeSpeedTravelTime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author michalm
 */
public final class ImtOptimizer implements VrpOptimizer {

	// Define task types that can be used by the optimizer
	public enum ImtTaskType implements Task.TaskType {
		WAIT, DRIVE_TO_INCIDENT, ARRIVE, INCIDENT_MANAGEMENT, DEPART
	}

	// Define some constants
	// private static final double INCIDENT_MANAGEMENT_DURATION = 120; // 2 minutes

	// This submissionTime should be variable, but for now, it is going to be two minutes.

	private final MobsimTimer timer;
	private final TravelTime travelTime;
	private final LeastCostPathCalculator router;
	private final Fleet fleet;
	private final Scenario scenario;

	// Constructor for the optimizer
	@Inject
	public ImtOptimizer(@DvrpMode(TransportMode.truck) Network network, @DvrpMode(TransportMode.truck) Fleet fleet, MobsimTimer timer, Scenario scenario) {
		this.timer = timer;
		this.fleet = fleet;
		this.scenario = scenario;

		// Set up travel-related objects
		travelTime = new FreeSpeedTravelTime();
		router = new SpeedyDijkstraFactory().createPathCalculator(network, new TimeAsTravelDisutility(travelTime), travelTime);

		// Loop over all vehicles and add a waiting task to their schedules
		for (DvrpVehicle vehicle : fleet.getVehicles().values()) {
			vehicle.getSchedule().addTask(new DefaultStayTask(ImtTaskType.WAIT, vehicle.getServiceBeginTime(), vehicle.getServiceEndTime(), vehicle.getStartLink()));
		}
	}

	// Method called when a new request is submitted to the optimizer
	@Override
	public void requestSubmitted(Request request) {
		// Cast the request to the appropriate type
		ImtRequest req = (ImtRequest) request;
		Link toLink = req.getToLink();
		double full_linkCapacity = toLink.getCapacity();
		double curr_linkCapacity = full_linkCapacity - (full_linkCapacity * req.getCapacityReduction());

		// The difference between the links full capacity, and it's reduced capacity;
		double gap = full_linkCapacity - curr_linkCapacity;

		// An error was occurring because sometimes the vehicle didn't arrive before the incident was supposed to end. I think that's a problem with where I placed the vehicles and perhaps the FFS. I need to look into that further and fix the error, but for now adding two hours to the incident end time will work.
		double endTime = (req.getEndTime() + 10000);
		int respondingIMTs = req.getRespondingIMTs();


		// Find the 'x' (respondingIMTs) number of vehicles the closest vehicles to the incident
		List<DvrpVehicle> closestVehicles = new ArrayList<>(fleet.getVehicles().values());
		closestVehicles.sort(Comparator.comparingDouble(vehicle -> {
			// Get the last task in the vehicle's schedule
			Task lastTask = Schedules.getLastTask(vehicle.getSchedule());
			if (lastTask.getTaskType() != ImtTaskType.WAIT) {
				// skip the vehicle if its last task is not planned and not WAIT
				return Double.POSITIVE_INFINITY;
			}
			// Get the last link in the vehicle's schedule
			Link fromLink = Schedules.getLastLinkInSchedule(vehicle);

			// Find the path from the current vehicle to the incident
			double time_zero = 0;
			VrpPathWithTravelData pathToIncident = VrpPaths.calcAndCreatePath(fromLink, toLink, time_zero, router, travelTime);

			// Calculate the time it would take for that vehicle to get to the incident
			return pathToIncident.getArrivalTime();
		}));
		closestVehicles = closestVehicles.subList(0, Math.min(respondingIMTs, closestVehicles.size()));

		// Add tasks to the schedules of the 'x' (respondingIMTs) number of vehicles the closest vehicles
		for (DvrpVehicle vehicle : closestVehicles) {
			Schedule schedule = vehicle.getSchedule();
			StayTask lastTask = (StayTask) Schedules.getLastTask(schedule); // Only WaitTask possible here
			double currentTime = timer.getTimeOfDay();

			switch (lastTask.getStatus()) {
				case PLANNED -> schedule.removeLastTask(); // Remove wait task
				case STARTED -> lastTask.setEndTime(currentTime); // Shorten wait task
				case PERFORMED -> throw new IllegalStateException();
			}


			double t0 = schedule.getStatus() == ScheduleStatus.UNPLANNED ?
					Math.max(vehicle.getServiceBeginTime(), currentTime) :
					Schedules.getLastTask(schedule).getEndTime();

			VrpPathWithTravelData pathToIncident = VrpPaths.calcAndCreatePath(lastTask.getLink(), toLink, t0, router, travelTime);
			double t1 = pathToIncident.getArrivalTime();
			schedule.addTask(new DefaultDriveTask(ImtTaskType.DRIVE_TO_INCIDENT, pathToIncident));
			schedule.addTask(new ImtServeTask(ImtTaskType.ARRIVE, t1, t1, toLink, req));
			schedule.addTask(new ImtServeTask(ImtTaskType.INCIDENT_MANAGEMENT, t1, endTime, toLink, req));
			schedule.addTask(new DefaultStayTask(ImtTaskType.WAIT, endTime, Double.POSITIVE_INFINITY, toLink));
			// update the current link capacity to be improved by 25%
			curr_linkCapacity = curr_linkCapacity + gap/4;
			// update gap value for next vehicle
			gap = full_linkCapacity - curr_linkCapacity;
			{
				NetworkChangeEvent restoreCapacityEvent = new NetworkChangeEvent(t1);
				restoreCapacityEvent.setFlowCapacityChange(new NetworkChangeEvent.ChangeValue(NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, (curr_linkCapacity)));
				restoreCapacityEvent.addLink(toLink);
				NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), restoreCapacityEvent);
				System.out.println("Request IDs " + req.getId() + ", Responding IMTs " + req.getRespondingIMTs() +
						", Full Capacity " + toLink.getCapacity() + ",  Reduced Capacity " + (full_linkCapacity-(full_linkCapacity*req.getCapacityReduction())) + ", Current Capacity " + curr_linkCapacity);
			}
		}
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
			switch (task.getStatus()) {
				case PLANNED -> task.setBeginTime(task.getBeginTime() + diff);
				case STARTED -> task.setEndTime(task.getEndTime() + diff);
				case PERFORMED -> { /* do nothing */ }
			}
		}
	}
}
