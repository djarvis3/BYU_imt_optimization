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

import IMT.ImtRequest;
import IMT.logs.IMT_Log;
import IMT.logs.Incidents_Log;

import java.util.Objects;
import java.util.Optional;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.fleet.Fleet;
import org.matsim.contrib.dvrp.optimizer.Request;
import org.matsim.contrib.dvrp.optimizer.VrpOptimizer;
import org.matsim.contrib.dvrp.router.TimeAsTravelDisutility;
import org.matsim.contrib.dvrp.run.DvrpMode;
import org.matsim.contrib.dvrp.schedule.*;
import org.matsim.core.controler.events.StartupEvent;
import org.matsim.core.controler.listener.StartupListener;
import org.matsim.core.events.handler.EventHandler;
import org.matsim.core.mobsim.framework.MobsimTimer;
import org.matsim.core.router.speedy.SpeedyDijkstraFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.trafficmonitoring.FreeSpeedTravelTime;

import com.google.inject.Inject;

/**
 * Optimizer class responsible for handling IMT requests and managing vehicle schedules.
 */
public final class Optimizer implements VrpOptimizer, StartupListener, EventHandler {

	@Override
	public void reset(int iteration) {
		EventHandler.super.reset(iteration);
	}

	/**
	 * Enum representing different types of IMT tasks.
	 */
	public enum ImtTaskType implements Task.TaskType {
		WAIT, DRIVE_TO_INCIDENT, ARRIVE, INCIDENT_MANAGEMENT, DEPART
	}

	private final Fleet fleet;
	private final RequestHandler requestHandler;
	private final TimingUpdater timingUpdater;

	/**
	 * Constructs an Optimizer object with the specified network, fleet, timer, scenario, and events manager.
	 *
	 * @param network   the network for path calculation
	 * @param fleet     the fleet of vehicles
	 * @param timer     the simulation timer
	 * @param scenario  the scenario for configuration
	 * @param events    the events manager for handling events
	 */

	@Inject
	public Optimizer(@DvrpMode(TransportMode.truck) Network network, @DvrpMode(TransportMode.truck) Fleet fleet, MobsimTimer timer, Scenario scenario, EventsManager events) {
		Objects.requireNonNull(events, "Events cannot be null");
		Objects.requireNonNull(scenario, "scenario cannot be null");

		TravelTime travelTime = new FreeSpeedTravelTime();
		LeastCostPathCalculator router = createRouter(network, travelTime);

		this.fleet = Objects.requireNonNull(fleet, "Fleet cannot be null");
		this.requestHandler = new RequestHandler(fleet, router, travelTime, timer, scenario, events);
		this.timingUpdater = new TimingUpdater(timer);

		initWaitTasks();
		initLogging(scenario);
	}

	private LeastCostPathCalculator createRouter(Network network, TravelTime travelTime) {
		return new SpeedyDijkstraFactory().createPathCalculator(network, new TimeAsTravelDisutility(travelTime), travelTime);
	}

	private void initLogging(Scenario scenario) {
		String outputDirectory = scenario.getConfig().controler().getOutputDirectory();
		if (outputDirectory.contains("Incidents")) {
			new Incidents_Log(scenario);
		} else if (outputDirectory.contains("IMT")) {
			new IMT_Log(scenario);
		}
	}

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

	@Override
	public void requestSubmitted(Request request) {
		Objects.requireNonNull(request, "Request cannot be null");
		requestHandler.handleRequest((ImtRequest) request);
	}

	@Override
	public void nextTask(DvrpVehicle vehicle) {
		Objects.requireNonNull(vehicle, "Vehicle cannot be null");
		timingUpdater.updateTimings(vehicle.getSchedule(), vehicle);
		vehicle.getSchedule().nextTask();
	}

	@Override
	public void notifyStartup(StartupEvent event) {
		// Start listening to request events
		event.getServices().getEvents().addHandler(this);
	}
}
