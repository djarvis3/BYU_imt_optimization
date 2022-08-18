/* *********************************************************************** *
 * project: org.matsim.*												   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
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
package org.matsim.project;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;

/**
 * @author nagel
 *
 */
public class RunMatsimIncidents {

    public static void main(String[] args) {

        Config config;
        if (args == null || args.length == 0 || args[0] == null) {
            config = ConfigUtils.loadConfig("scenarios/sanFrancisco/config.xml");
        } else {
            config = ConfigUtils.loadConfig(args);
        }

        // configure the time variant network here:
        config.network().setTimeVariantNetwork(true);

        config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);

        // ---

        // create/load the scenario here.  The time variant network does already have to be set at this point
        // in the config, otherwise it will not work.
        Scenario scenario = ScenarioUtils.loadScenario(config);

/*
        Incidents incident = new Incidents(scenario);
        incident.makeOneIncident("1", "capacity", 0.0,6.*3600, 19.*3600);
        incident.makeOneIncident("2", "lane", 1,11.*3600,13.*3600);
        incident.makeOneIncident("3", "speed", 0.5,17.*3600,20.*3600);
*/

        IncidentsCSV incidents = new IncidentsCSV(scenario);
        incidents.parseIncidentsCSV("src/main/java/org/matsim/project/Incidents.csv");

        // ---

        Controler controler = new Controler( scenario ) ;

        controler.run() ;
    }

}
