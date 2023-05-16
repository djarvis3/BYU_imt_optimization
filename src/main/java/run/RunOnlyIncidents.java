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

	public static final String CONFIG_FILE = "scenarios/utah/config.xml";

	public static void run(String configFile, boolean otfvis) throws IOException {
		// load config
		Config config = ConfigUtils.loadConfig(configFile, new DvrpConfigGroup(), new OTFVisConfigGroup());

		// load scenario
		Scenario scenario = ScenarioUtils.loadScenario(config);




		// setup controler
		Controler controler = new Controler(scenario);
		IncidentReader incidents = new IncidentReader("scenarios/utah/IncidentData_Utah.csv");
		IncidentApplicator applyIncidents =
				// to apply random incidents from the CSV to the network use incidents.getRandomIncidents
				// new IncidentApplicator(scenario, incidents.getRandomIncidents());

				// to apply all the incidents from the CSV to the network use incidents.getAllIncidents
				// new IncidentApplicator(scenario, incidents.getAllIncidents());

				// to apply seeded incidents from the CSV to the network use incidents.getSeededIncidents
				new IncidentApplicator(scenario, incidents.getSeededIncidents(10,1234));
		applyIncidents.apply();

		if (otfvis) {
			controler.addOverridingModule(new OTFVisLiveModule()); // OTFVis visualisation
		}

		// run simulation
		controler.run();

		// read the incidentCSV

	}

	public static void main(String[] args) throws IOException {run(CONFIG_FILE, false);}
}
