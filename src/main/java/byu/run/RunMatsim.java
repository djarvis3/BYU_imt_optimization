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
package byu.run;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkChangeEvent.ChangeType;
import org.matsim.core.network.NetworkChangeEvent.ChangeValue;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.scenario.ScenarioUtils;

/**
 * @author nagel
 *
 */
public class RunMatsim{

	public static void main(String[] args) {
		String configurl = ("scenarios/berlin/config.xml");

		Config config = ConfigUtils.loadConfig( configurl ) ;

		// configure the time variant network here:
		config.network().setTimeVariantNetwork(true);

		config.controler().setOverwriteFileSetting( OverwriteFileSetting.deleteDirectoryIfExists );

		// ---

		// create/load the scenario here.  The time variant network does already have to be set at this point
		// in the config, otherwise it will not work.
		Scenario scenario = ScenarioUtils.loadScenario(config) ;

		// Read_Incident incidents = new Read_Incident(scenario);
		// incidents.Incident_Generator("src/main/java/org/matsim/reader/IncidentData_Daniel.csv");


		for ( Link link : scenario.getNetwork().getLinks().values() ) {
			double speed = link.getFreespeed() ;
			final double threshold = 34;
			if ( speed > threshold ) {
				{
					NetworkChangeEvent event = new NetworkChangeEvent(7.*3600.) ;
					event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  threshold*0.006 ));
					event.addLink(link);
					NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
				}
				{
					NetworkChangeEvent event = new NetworkChangeEvent(11.5*3600.) ;
					event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  speed ));
					event.addLink(link);
					NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
				}
			}
		}

		// ---

		Controler controler = new Controler( scenario ) ;

		controler.run();
	}

}
