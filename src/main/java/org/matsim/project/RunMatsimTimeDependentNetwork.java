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

import org.matsim.api.core.v01.Id;
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
public class RunMatsimTimeDependentNetwork {

	public static void main(String[] args) {
		Config config;
		if ( args==null || args.length==0 || args[0]==null ){
			config = ConfigUtils.loadConfig( "scenarios/sanFrancisco/config.xml" );
		} else {
			config = ConfigUtils.loadConfig( args );
		}

		//configure the time variant network here:
		config.network().setTimeVariantNetwork(true);

		config.controler().setOverwriteFileSetting( OverwriteFileSetting.deleteDirectoryIfExists );

		// ---

		// create/load the scenario here.  The time variant network does already have to be set at this point
		// in the config, otherwise it will not work.
		Scenario scenario = ScenarioUtils.loadScenario(config) ;

		// ---

		for (Link link: scenario.getNetwork().getLinks().values() ) {
			Id<Link> linkGet = link.getId() ;
			String linkId = linkGet.toString() ;
			double capacity = link.getCapacity() ;
			double lanes = link.getNumberOfLanes() ;
			double speed = link.getFreespeed() ;
			double arrivalTime = 9.*3600;
			double departureTime = arrivalTime + (0.25*3600);
			final double threshold = 33;


			// ** Alterations to all Links of a particular type
			// ** Alterations based on the berlin scenario
			// network change of FreeSpeed
			if ( speed > threshold) {
				{
					NetworkChangeEvent event = new NetworkChangeEvent(arrivalTime-(0.5*3600)) ;
					event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  threshold/100 ));
					event.addLink(link);
					NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
				}
				{
					NetworkChangeEvent event = new NetworkChangeEvent(departureTime+(0.5*3600)) ;
					event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  speed ));
					event.addLink(link);
					NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
				}
			}
			// network change of Lanes
			if ( lanes > 4.0) {
				{
				NetworkChangeEvent event = new NetworkChangeEvent(13*3600) ;
				event.setLanesChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, 1.0));
				event.addLink(link);
				NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
				}
				{
				NetworkChangeEvent event = new NetworkChangeEvent(15*3600) ;
				event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  lanes));
				event.addLink(link);
				NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
				}
			}
			// network change of Capacity
			if ( capacity > 30000.0) {
				{
					NetworkChangeEvent event = new NetworkChangeEvent(19. * 3600);
					event.setFlowCapacityChange(new ChangeValue(ChangeType.ABSOLUTE_IN_SI_UNITS, capacity / 5000));
					event.addLink(link);
					NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), event);
				}
				{
					NetworkChangeEvent event = new NetworkChangeEvent(21.5 * 3600);
					event.setFlowCapacityChange(new ChangeValue(ChangeType.ABSOLUTE_IN_SI_UNITS, capacity));
					event.addLink(link);
					NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), event);
				}
			}

			// ** Alterations of Specific Links Based on LinkId for sanFrancisco scenario
			// network change of FreeSpeed
			if ( linkId.equals("32") || linkId.equals("108") || linkId.equals("153") || linkId.equals("333") || linkId.equals("338")) {
				{
					NetworkChangeEvent event = new NetworkChangeEvent(arrivalTime-(0.5*3600)) ;
					event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed/2000));
					event.addLink(link);
					NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
				}
				{
					NetworkChangeEvent event = new NetworkChangeEvent(departureTime+(0.5*3600)) ;
					event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  speed));
					event.addLink(link);
					NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
				}
			}
			// network change of Lanes
			if ( linkId.equals("21") || linkId.equals("179") || linkId.equals("182") || linkId.equals("312") || linkId.equals("327")) {
				{
					NetworkChangeEvent event = new NetworkChangeEvent(15*3600) ;
					event.setLanesChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, 0.0));
					event.addLink(link);
					NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
				}
				{
					NetworkChangeEvent event = new NetworkChangeEvent(18*3600) ;
					event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  lanes));
					event.addLink(link);
					NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
				}
			}
			// network change of Capacity
			if ( linkId.equals("34") || linkId.equals("83") || linkId.equals("239") || linkId.equals("304") || linkId.equals("324")) {
				{
					NetworkChangeEvent event = new NetworkChangeEvent(7.*3600) ;
					event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/5000));
					event.addLink(link);
					NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
				}
				{
					NetworkChangeEvent event = new NetworkChangeEvent(12.5*3600) ;
					event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  capacity));
					event.addLink(link);
					NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
				}
			}

			// ** Alterations of Specific Links Based on LinkId for siouxFalls
			if ( linkId.equals("3_1") || linkId.equals("32_1") || linkId.equals("45_2") || linkId.equals("62_1") || linkId.equals("62_3")) {
				{
					NetworkChangeEvent event = new NetworkChangeEvent(arrivalTime-(0.5*3600)) ;
					event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, speed/1000));
					event.addLink(link);
					NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
				}
				{
					NetworkChangeEvent event = new NetworkChangeEvent(departureTime+(0.5*3600)) ;
					event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  speed));
					event.addLink(link);
					NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
				}
			}
			// network change of Lanes
			if ( linkId.equals("6_2") || linkId.equals("33_4") || linkId.equals("47_3") || linkId.equals("55_1") || linkId.equals("59_4")) {
				{
					NetworkChangeEvent event = new NetworkChangeEvent(15*3600) ;
					event.setLanesChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, 0.0));
					event.addLink(link);
					NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
				}
				{
					NetworkChangeEvent event = new NetworkChangeEvent(18*3600) ;
					event.setFreespeedChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  lanes));
					event.addLink(link);
					NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
				}
			}
			// network change of Capacity
			if ( linkId.equals("7_2") || linkId.equals("34_4") || linkId.equals("51_1") || linkId.equals("60_1") || linkId.equals("59_1")) {
				{
					NetworkChangeEvent event = new NetworkChangeEvent(7.*3600) ;
					event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS, capacity/5000));
					event.addLink(link);
					NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(),event);
				}
				{
					NetworkChangeEvent event = new NetworkChangeEvent(12.5*3600) ;
					event.setFlowCapacityChange(new ChangeValue( ChangeType.ABSOLUTE_IN_SI_UNITS,  capacity));
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
