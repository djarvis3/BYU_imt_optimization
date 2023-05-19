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

package IMT.optimizer;

import com.google.inject.Inject;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.fleet.Fleet;
import org.matsim.contrib.dvrp.optimizer.Request;
import org.matsim.contrib.dvrp.optimizer.VrpOptimizer;
import org.matsim.contrib.dvrp.router.TimeAsTravelDisutility;
import org.matsim.contrib.dvrp.run.DvrpMode;
import org.matsim.contrib.dvrp.schedule.*;
import org.matsim.core.controler.Controler;
import org.matsim.core.mobsim.framework.MobsimTimer;
import org.matsim.core.router.speedy.SpeedyDijkstraFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.trafficmonitoring.FreeSpeedTravelTime;

import java.util.Objects;
import java.util.Optional;

/**
 * @author michalm
 * VrpOptimizer implementation that optimizes vehicle routing problems.
 */
public final class Optimizer implements VrpOptimizer {

	// Define task types that can be used by the optimizer
	public enum ImtTaskType implements Task.TaskType {
		WAIT, DRIVE_TO_INCIDENT, ARRIVE, INCIDENT_MANAGEMENT, DEPART
	}

	private final Fleet fleet;
	private final RequestHandler requestHandler;
	private final TimingUpdater timingUpdater;

	/**
	 * Constructs a new Optimizer instance.
	 */
	@Inject
	public Optimizer(@DvrpMode(TransportMode.truck) Network network, @DvrpMode(TransportMode.truck) Fleet fleet,
					 MobsimTimer timer, Scenario scenario) {
		this.fleet = Objects.requireNonNull(fleet, "Fleet cannot be null");
		Objects.requireNonNull(scenario, "scenario cannot be null");
		TravelTime travelTime = new FreeSpeedTravelTime();
		LeastCostPathCalculator router = new SpeedyDijkstraFactory().createPathCalculator(network,
				new TimeAsTravelDisutility(travelTime), travelTime);
		initWaitTasks();
		this.requestHandler = new RequestHandler(fleet, router, travelTime, timer, scenario);
		this.timingUpdater = new TimingUpdater(timer);
	}

	/**
	 * Initializes the wait tasks for the vehicles in the fleet.
	 */
	private void initWaitTasks() {
		for (DvrpVehicle vehicle : fleet.getVehicles().values()) {
			Optional.ofNullable(vehicle.getSchedule())
					.ifPresent(schedule -> schedule.addTask(
							new DefaultStayTask(
									ImtTaskType.WAIT,
									vehicle.getServiceBeginTime(),
									vehicle.getServiceEndTime(),
									vehicle.getStartLink())
					));
		}
	}

	/**
	 * Handles every new request submitted to the optimizer.
	 * @throws NullPointerException if the request is null.
	 */
	@Override
	public void requestSubmitted(Request request) {
		Objects.requireNonNull(request, "Request cannot be null");
		requestHandler.handleRequest((IMT.Request) request);
	}

	/**
	 * Updates the timings for the next task of the specified vehicle.
	 *
	 * @param vehicle the vehicle to update.
	 * @throws NullPointerException if the vehicle is null.
	 */
	@Override
	public void nextTask(DvrpVehicle vehicle) {
		Objects.requireNonNull(vehicle, "Vehicle cannot be null");
		timingUpdater.updateTimings(vehicle.getSchedule());
		vehicle.getSchedule().nextTask();
	}
}
