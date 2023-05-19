/*
 * *********************************************************************** *
 * project: org.matsim.*
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2018 by the members listed in the COPYING,        *
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
 * *********************************************************************** *
 */

package IMT;

import IMT.optimizer.Optimizer;
import com.google.inject.Key;
import com.google.inject.name.Names;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.dvrp.fleet.FleetModule;
import org.matsim.contrib.dvrp.optimizer.VrpOptimizer;
import org.matsim.contrib.dvrp.router.DvrpModeRoutingNetworkModule;
import org.matsim.contrib.dvrp.run.AbstractDvrpModeModule;
import org.matsim.contrib.dvrp.run.AbstractDvrpModeQSimModule;
import org.matsim.contrib.dvrp.run.DvrpModes;
import org.matsim.contrib.dvrp.trafficmonitoring.DvrpTravelTimeModule;
import org.matsim.contrib.dvrp.vrpagent.VrpAgentLogic;
import org.matsim.contrib.dvrp.vrpagent.VrpAgentSourceQSimModule;
import org.matsim.core.router.util.TravelTime;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehicleUtils;

import java.net.URL;

/**
 * @author Michal Maciejewski (michalm)
 */

/**
 * The Module class represents a DVRP mode module for IMT vehicles.
 * This module installs routing and fleet modules for the DVRP mode, and creates a truck type for the simulation.
 */
public class ImtModule extends AbstractDvrpModeModule {
	/**
	 * The URL of the fleet specification.
	 */
	private final URL fleetSpecificationUrl;


	/**
	 * Creates a new Module object with the given fleet specification URL.
	 * @param fleetSpecificationUrl the URL of the fleet specification.
	 */
	public ImtModule(URL fleetSpecificationUrl) {
		super(TransportMode.truck);
		this.fleetSpecificationUrl = fleetSpecificationUrl;
	}

	/**
	 * Installs the DVRP mode module.
	 * This method registers the DVRP mode, installs a routing network module, binds a travel time component,
	 * installs a fleet module, and configures the QSim module for the DVRP mode.
	 */
	@Override
	public void install() {
		DvrpModes.registerDvrpMode(binder(), getMode());
		install(new DvrpModeRoutingNetworkModule(getMode(), false));
		bindModal(TravelTime.class).to(Key.get(TravelTime.class, Names.named(DvrpTravelTimeModule.DVRP_ESTIMATED)));
		install(new FleetModule(getMode(), fleetSpecificationUrl, createTruckType()));
		installQSimModule(new AbstractDvrpModeQSimModule(getMode()) {
			@Override
			protected void configureQSim() {
				install(new VrpAgentSourceQSimModule(getMode()));
				addModalComponent(RequestCreator.class);
				bindModal(VrpOptimizer.class).to(Optimizer.class).asEagerSingleton();
				bindModal(VrpAgentLogic.DynActionCreator.class).to(ActionCreator.class).asEagerSingleton();
			}
		});
	}

	/**
	 * Creates a truck type for vehicle simulation.
	 * This method creates and returns a VehicleType object representing a truck for vehicle simulation.
	 * @return the truck VehicleType object.
	 */
	private static VehicleType createTruckType() {
		VehicleType truckType = VehicleUtils.getFactory().createVehicleType(Id.create("truckType", VehicleType.class));
		truckType.setLength(15.);
		truckType.setPcuEquivalents(2.5);
		truckType.getCapacity().setSeats(1);
		return truckType;
	}
}

