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
import incidents.IncidentApplicator;
import incidents.IncidentReader;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.dvrp.run.DvrpModule;
import org.matsim.contrib.dvrp.run.DvrpQSimComponents;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vis.otfvis.OTFVisConfigGroup;

/**
 * The RunIMT class is responsible for running the MATSim simulation for Incident Management Teams (IMTs) and incidents.
 * It loads the MATSim configuration and scenario, sets up the controler, and executes the simulation.
 * The class provides a main method to start the simulation with default configuration values.
 * To customize the simulation, different configuration files and options can be specified.
 */
public class RunIncidents {

	public static final String CONFIG_FILE = "scenarios/equil/config_withinday.xml";
	public static final String TRUCK_FILE = "ImtVehicles_1.xml";

	/**
	 * Runs the MATSim simulation.
	 *
	 * @param configFile Path to the MATSim configuration file.
	 * @param trucksFile Path to the file containing information about trucks.
	 */
	public static void runIncidents(String configFile, String trucksFile) {
		// load config
		Config config = ConfigUtils.loadConfig(configFile, new DvrpConfigGroup(), new OTFVisConfigGroup());

		// Set inputChangeEventsFile parameter to null
		config.network().setChangeEventsInputFile(null);

		// Set lastIteration parameter
		config.controler().setLastIteration(5);

		// Set outputDirectory filepath
		config.controler().setOutputDirectory(config.controler().getOutputDirectory()+"_IncidentsOnly");

		// load scenario
		MutableScenario scenario = (MutableScenario) ScenarioUtils.loadScenario(config);

		// add incident to the scenario
		IncidentReader incidents = new IncidentReader("scenarios/equil/IncidentData_Equil.csv");
		IncidentApplicator applyIncidents = new IncidentApplicator(scenario, incidents.getSeededIncidents(1,4));
		applyIncidents.apply();

		// setup controler
		Controler controler = new Controler(scenario);

		// add modules for handling incidents and IMTs (Incident Management Teams)
		// comment out these three lines to run a "Baseline" MATSim Run with no incidents or IMTs
		controler.addOverridingModule(new DvrpModule());
		controler.addOverridingModule(new ImtModule(ConfigGroup.getInputFileURL(config.getContext(), trucksFile), config, scenario));
		controler.configureQSimComponents(DvrpQSimComponents.activateModes(TransportMode.truck));

		// run simulation
		controler.run();
	}

	/**
	 * Main method to start the MATSim simulation.
	 *
	 * @param args Command line arguments (not used).
	 *
	 */
	public static void main(String[] args) {
		// Run the MATSim simulation
		runIncidents(CONFIG_FILE, TRUCK_FILE);
	}
}

