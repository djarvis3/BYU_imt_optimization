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
package org.matsim.utah_imt;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.incidents.Read_Incident;

/**
 * @author nagel
 *
 */

public class UtahRunMatsimIncidents {

	public static void main(String[] args) {
		if ( args.length==0 ) {
			args = new String [] { "scenarios/utah/config.xml" } ;
		} else {
			Gbl.assertIf( args[0] != null && !args[0].equals( "" ) );
		}

		Config config = ConfigUtils.loadConfig( args ) ;

		config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.overwriteExistingFiles);

		config.network().setTimeVariantNetwork(true);

		// possibly modify config here

		Scenario scenario = ScenarioUtils.loadScenario(config) ;

		Read_Incident incidents = new Read_Incident(scenario);
		incidents.Incident_Generator("src/main/java/org/matsim/reader/IncidentData_Daniel.csv");

		// possibly modify scenario here

		Controler controler = new Controler( scenario ) ;

		// possibly modify controler here

		//		controler.addOverridingModule( new OTFVisLiveModule() ) ;

		controler.run();
	}
}
