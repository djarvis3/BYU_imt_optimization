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
package byu.run;

import byu.IMT.utahIMT.UtahImtModule;
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
 * An example class to Run a taxi scenario based on a config file.
 * Note that several optimizers may be set directly within the config file.
 */


public class RunUtahIMT {

	public static final String CONFIG_FILE = "C:/Users/djarvis3/BYU_imt_optimization/scenarios/utahIMT/utah.xml";

	public static final String TRUCK_FILE = "C:/Users/djarvis3/BYU_imt_optimization/scenarios/utahIMT/ImtVehicles.xml";

	public static void run(String configFile, String trucksFile, boolean otfvis) throws IOException {
		// load config
		Config config = ConfigUtils.loadConfig(configFile, new DvrpConfigGroup(), new OTFVisConfigGroup());

		// load scenario
		Scenario scenario = ScenarioUtils.loadScenario(config);


		// setup controler
		Controler controler = new Controler(scenario);
		controler.addOverridingModule(new DvrpModule());
		controler.addOverridingModule(new UtahImtModule(ConfigGroup.getInputFileURL(config.getContext(), trucksFile)));
		controler.configureQSimComponents(DvrpQSimComponents.activateModes(TransportMode.truck));

		if (otfvis) {
			controler.addOverridingModule(new OTFVisLiveModule()); // OTFVis visualisation
		}

		// run simulation
		controler.run();
	}


	public static void main(String[] args) throws IOException {run(CONFIG_FILE,TRUCK_FILE, false);}
}
