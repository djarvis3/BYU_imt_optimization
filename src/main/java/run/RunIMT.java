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

import IMT.ImtModule;
import decongestion.DecongestionConfigGroup;
import decongestion.DecongestionModule;
import decongestion.DecongestionRunExampleFromConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.dvrp.run.DvrpModule;
import org.matsim.contrib.dvrp.run.DvrpQSimComponents;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;

import java.io.IOException;

/**
 * The RunIMT class is responsible for running the MATSim simulation for Incident Management Teams (IMTs) and incidents.
 * It loads the MATSim configuration and scenario, sets up the controler, and executes the simulation.
 * The class provides a main method to start the simulation with default configuration values.
 * To customize the simulation, different configuration files and options can be specified.
 */
public class RunIMT {

	public static final String CONFIG_FILE = "scenarios/berlin/config_withinday.xml";
	public static final String TRUCK_FILE = "ImtVehicles_35.xml";
	private static final Logger log = LogManager.getLogger(DecongestionRunExampleFromConfig.class);

	/**
	 * Runs the MATSim simulation.
	 *
	 * @param configFile Path to the MATSim configuration file.
	 * @param trucksFile Path to the file containing information about trucks.
	 * @throws IOException if there is an error loading the configuration or scenario.
	 */
	public static void run(String configFile, String trucksFile) throws IOException {
		// load config
		Config config = ConfigUtils.loadConfig(configFile, new DvrpConfigGroup(), new DecongestionConfigGroup());

		// Set outputDirectory filepath
		config.controler().setOutputDirectory(config.controler().getOutputDirectory()+"_IMT");

		// load scenario
		Scenario scenario = ScenarioUtils.loadScenario(config);

		// setup controler
		Controler controler = new Controler(scenario);

		// #############################################################

		// congestion toll computation

		controler.addOverridingModule(new DecongestionModule(scenario));

		// add modules for handling incidents and IMTs (Incident Management Teams)
		// comment out these three lines to run a "Baseline" MATSim Run with no incidents or IMTs
		controler.addOverridingModule(new DvrpModule());
		controler.addOverridingModule(new ImtModule(ConfigGroup.getInputFileURL(config.getContext(), trucksFile)));
		controler.configureQSimComponents(DvrpQSimComponents.activateModes(TransportMode.truck));

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
		log.info("Starting simulation run with the following arguments:");
		log.info("config file: "+ CONFIG_FILE);
		RunIncidents.runIncidents(CONFIG_FILE, TRUCK_FILE);

		log.info("Starting simulation run with the following arguments:");
		log.info("config file: "+ CONFIG_FILE);
		run(CONFIG_FILE, TRUCK_FILE);
	}
}
