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
import IMT.events.CustomEventWriterXML;
import IMT.events.eventHanlders.ArriveEventHandler;
import IMT.events.eventHanlders.CustomEventHandler;
import decongestion.DecongestionConfigGroup;
import decongestion.DecongestionModule;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.dvrp.run.DvrpModule;
import org.matsim.contrib.dvrp.run.DvrpQSimComponents;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.mobsim.qsim.AbstractQSimModule;
import org.matsim.core.scenario.ScenarioUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The RunIMT class is responsible for running the MATSim simulation for Incident Management Teams (IMTs) and incidents.
 * It loads the MATSim configuration and scenario, sets up the controler, and executes the simulation.
 * The class provides a main method to start the simulation with default configuration values.
 * To customize the simulation, different configuration files and options can be specified.
 */
public class RunIMT {

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
		config.controler().setOutputDirectory(config.controler().getOutputDirectory() + "_IMT");

// load scenario
		Scenario scenario = ScenarioUtils.loadScenario(config);

// setup controler
		Controler controler = new Controler(scenario);


// add event handler
		ArriveEventHandler arriveEventHandler = new ArriveEventHandler(scenario);
		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				this.addEventHandlerBinding().toInstance(arriveEventHandler);
			}
		});

// initialize EventWriter
		EventsManager events = controler.getEvents();
		CustomEventWriterXML writer = null;
		try {
			OutputStream out = Files.newOutputStream(Paths.get("output_events.xml.gz"));
			writer = new CustomEventWriterXML(out);

			// initialize CustomEventHandler with writer and register it to the events manager
			CustomEventHandler handler = new CustomEventHandler(writer);
			events.addHandler(handler);

			controler.addOverridingModule(new AbstractModule() {
				@Override
				public void install() {
					this.addEventHandlerBinding().toInstance(handler);
				}
			});

			// add modules, run simulation, etc
			controler.addOverridingModule(new DecongestionModule(scenario));
			controler.addOverridingModule(new DvrpModule());
			controler.addOverridingModule(new ImtModule(ConfigGroup.getInputFileURL(config.getContext(), trucksFile)));
			controler.configureQSimComponents(DvrpQSimComponents.activateModes(TransportMode.truck));

			// run simulation
			controler.run();
		} finally {
			// ensure all events are written and the file is properly closed
			if (writer != null) {
				writer.closeFile();
			}
		}
	}


		/**
		 * Main method to start the MATSim simulation.
		 *
		 * @param args Command line arguments (not used).
		 * @throws IOException if there is an error running the simulation.
		 */
	public static void main(String[] args) throws IOException {
		if(args.length != 2) {
			System.err.println("Usage: java RunIncidents <configFile> <trucksFile>");
			System.exit(1);
		}

		String configFile = args[0];
		String trucksFile = args[1];


		run(configFile, trucksFile);
	}
}
