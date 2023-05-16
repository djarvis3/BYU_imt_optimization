/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2016 by the members listed in the COPYING,        *
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
package run;

import IMT.Module;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.dvrp.run.DvrpModule;
import org.matsim.contrib.dvrp.run.DvrpQSimComponents;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vis.otfvis.OTFVisConfigGroup;

import java.io.IOException;

/**
 * The RunIMT class is responsible for running the MATSim simulation for Incident Management Teams (IMTs) and incidents.
 * It loads the MATSim configuration and scenario, sets up the controler, and executes the simulation.
 * The class provides a main method to start the simulation with default configuration values.
 * To customize the simulation, different configuration files and options can be specified.
 */
public class RunIMT {

	public static final String CONFIG_FILE = "scenarios/utah/config.xml";
	public static final String TRUCK_FILE = "ImtVehicles_25.xml";
	// use the no vehicles TRUCK_FILE to create an "Incidents Only" MATSim Run
	// public static final String TRUCK_FILE = "NoVehicles.xml";

	/**
	 * Runs the MATSim simulation.
	 *
	 * @param configFile Path to the MATSim configuration file.
	 * @param trucksFile Path to the file containing information about trucks.
	 * @param otfvis     Flag indicating whether to enable OTFVis visualization.
	 * @throws IOException if there is an error loading the configuration or scenario.
	 */
	public static void run(String configFile, String trucksFile, boolean otfvis) throws IOException {
		// load config
		Config config = ConfigUtils.loadConfig(configFile, new DvrpConfigGroup(), new OTFVisConfigGroup());

		// load scenario
		Scenario scenario = ScenarioUtils.loadScenario(config);

		// setup controler
		Controler controler = new Controler(scenario);

		// add modules for handling incidents and IMTs (Incident Management Teams)
		// comment out these three lines to run a "Baseline" MATSim Run with no incidents or IMTs
		controler.addOverridingModule(new DvrpModule());
		controler.addOverridingModule(new Module(ConfigGroup.getInputFileURL(config.getContext(), trucksFile)));
		controler.configureQSimComponents(DvrpQSimComponents.activateModes(TransportMode.truck));

		// enable OTFVis visualization if specified
		if (otfvis) {
			controler.addOverridingModule(new OTFVisLiveModule());
		}

		// run simulation
		controler.run();
	}

	/**
	 * Main method to start the MATSim simulation.
	 *
	 * @param args Command line arguments (not used).
	 * @throws IOException if there is an error running the simulation.
	 */
	public static void main(String[] args) throws IOException {
		// Run the MATSim simulation
		run(CONFIG_FILE, TRUCK_FILE, false);
	}
}

