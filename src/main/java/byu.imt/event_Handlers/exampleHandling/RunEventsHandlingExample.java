/* *********************************************************************** *
 * project: org.matsim.*
 * EventsReader
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2009 by the members listed in the COPYING,        *
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
package byu.imt.event_Handlers.exampleHandling;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.dvrp.run.DvrpModule;
import org.matsim.contrib.dvrp.run.DvrpQSimComponents;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.contrib.taxi.run.MultiModeTaxiConfigGroup;
import org.matsim.contrib.taxi.run.MultiModeTaxiModule;
import org.matsim.contrib.taxi.run.TaxiConfigGroup;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vis.otfvis.OTFVisConfigGroup;


/**
 * This class contains a main method to call the
 * example event handlers MyEventHandler1-3.
 *
 * @author dgrether
 */
public class RunEventsHandlingExample {

    public static final String CONFIG_FILE_ASSIGNMENT = "scenarios/sanFrancisco/config_taxi.xml";

    public static void run(String configFile, boolean otfvis, int lastIteration) {
        // load config
        Config config = ConfigUtils.loadConfig(configFile, new MultiModeTaxiConfigGroup(), new DvrpConfigGroup(),
                new OTFVisConfigGroup());
        config.controler().setLastIteration(lastIteration);

        // load scenario
        Scenario scenario = ScenarioUtils.loadScenario(config);

        // setup controler
        Controler controler = new Controler(scenario);

        String mode = TaxiConfigGroup.getSingleModeTaxiConfig(config).getMode();
        controler.addOverridingModule(new DvrpModule());
        controler.addOverridingModule(new MultiModeTaxiModule());
        controler.configureQSimComponents(DvrpQSimComponents.activateModes(mode));

        if (otfvis) {
            controler.addOverridingModule(new OTFVisLiveModule());
        }

        // run simulation
        controler.run();

        // add event handlers for the output events
        String inputFile = "output/sf_Outputs/sfOutput_RunMatsimTaxi/output_events.xml.gz";

        //create an event object
        EventsManager events = EventsUtils.createEventsManager();

        //create the handler and add it
        MyEventHandler1 handler1 = new MyEventHandler1();
        MyEventHandler2 handler2 = new MyEventHandler2();
        MyEventHandler3 handler3 = new MyEventHandler3();
        events.addHandler(handler1);
        events.addHandler(handler2);
        events.addHandler(handler3);

        //create the reader and read the file
        events.initProcessing();
        MatsimEventsReader reader = new MatsimEventsReader(events);
        reader.readFile(inputFile);
        events.finishProcessing();

        System.out.println("average travel time: " + handler2.getTotalTravelTime());
        handler3.writeChart("output/sf_Outputs/departuresPerHour.png");

        System.out.println("Events file read!");
    }

    public static void main(String[] args) {
        run(CONFIG_FILE_ASSIGNMENT, false, 20);
    }

}
