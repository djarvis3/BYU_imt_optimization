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

import decongestion.DecongestionConfigGroup;
import decongestion.DecongestionModule;
import incidents.IncidentApplicator;
import incidents.IncidentReader;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
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


public class RunIncidents {

	/**
	 * Runs the MATSim simulation.
	 *
	 * @param configFile Path to the MATSim configuration file.
	 */
	public static void runIncidents(String configFile) throws IOException {
		// load config
		Config config = ConfigUtils.loadConfig(configFile, new DecongestionConfigGroup());

		// Set outputDirectory filepath
		config.controler().setOutputDirectory(config.controler().getOutputDirectory()+"_Incidents");

		// load scenario
		Scenario scenario = ScenarioUtils.loadScenario(config);

		// add incident to the scenario
		IncidentReader incidents = new IncidentReader("scenarios/utah/incidents/UtahIncidents_MATSim.csv", scenario.getNetwork());
		IncidentApplicator applyIncidents = new IncidentApplicator(scenario, incidents.getSeededIncidents(10,6725));
		applyIncidents.apply();

		// setup controler
		Controler controler = new Controler(scenario);

		// congestion toll computation

		controler.addOverridingModule(new DecongestionModule(scenario));

		// run simulation
		controler.run();
	}

	/**
	 * Main method to start the MATSim simulation.
	 *
	 * @param args Command line arguments (not used).
	 *
	 */


	public static void main(String[] args) throws IOException {
		if(args.length != 1) {
			System.err.println("Usage: java RunIncidents <configFile>");
			System.exit(1);
		}

		String configFile = args[0];

		// Run the MATSim simulation
		runIncidents(configFile);
	}
}
