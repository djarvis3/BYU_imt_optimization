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

import incidents.IncidentApplicator;
import incidents.IncidentReader;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vis.otfvis.OTFVisConfigGroup;

import java.io.IOException;

/**
 * An example class to Run a taxi scenario based on a config file.
 * Note that several optimizers may be set directly within the config file.
 */


public class RunOnlyIncidents {

	public static final String CONFIG_FILE = "scenarios/berlin/config_withinday.xml";

	public static void run(String configFile, boolean otfvis) throws IOException {
		// load config
		Config config = ConfigUtils.loadConfig(configFile, new DvrpConfigGroup(), new OTFVisConfigGroup());

		// load scenario
		Scenario scenario = ScenarioUtils.loadScenario(config);

		// read the incidentCSV
		IncidentReader incidents = new IncidentReader("scenarios/berlin/IncidentData_Berlin.csv");
		IncidentApplicator applyIncidents =
				// to apply random incidents from the CSV to the network use incidents.getRandomIncidents


				/*
				new IncidentApplicator(scenario.getNetwork(), incidents.getRandomIncidents());
				applyIncidents.apply();
				*/


				// to apply all the incidents from the CSV to the network use incidents.getAllIncidents

				new IncidentApplicator(scenario, incidents.getAllIncidents());
				applyIncidents.apply();


				// setup controler
		Controler controler = new Controler(scenario);

		if (otfvis) {
			controler.addOverridingModule(new OTFVisLiveModule()); // OTFVis visualisation
		}

		// run simulation
		controler.run();
	}


	public static void main(String[] args) throws IOException {run(CONFIG_FILE, false);}
}
