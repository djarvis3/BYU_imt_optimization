package run;

import IMT.ImtConfigGroup;
import IMT.ImtModule;
import IMT.events.eventHanlders.ImtEventHandler;
import IMT.events.eventHanlders.IncidentEventHandler;
import decongestion.DecongestionConfigGroup;
import decongestion.DecongestionModule;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.dvrp.run.DvrpModule;
import org.matsim.contrib.dvrp.run.DvrpQSimComponents;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;

import java.io.IOException;

public class RunIMT {

	public static void run(String configFile) throws IOException {
		// Load config
		Config config = ConfigUtils.loadConfig(configFile, new DvrpConfigGroup(), new DecongestionConfigGroup(), new ImtConfigGroup());

		// Set outputDirectory filepath
		config.controler().setOutputDirectory(config.controler().getOutputDirectory() + "_IMT_3-2-1-21-312");

		// Load scenario
		Scenario scenario = ScenarioUtils.loadScenario(config);

		// Setup controller
		Controler controler = new Controler(scenario);

		// Add event handlers
		IncidentEventHandler incidentEventHandler = new IncidentEventHandler(scenario);
		ImtEventHandler imtEventHandler = new ImtEventHandler(scenario);
		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				this.addEventHandlerBinding().toInstance(incidentEventHandler);
			}
		});
		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				this.addEventHandlerBinding().toInstance(imtEventHandler);
			}
		});

		// Add modules, run simulation, etc
		controler.addOverridingModule(new DecongestionModule(scenario));
		controler.addOverridingModule(new DvrpModule());
		String trucksFilePath = ConfigUtils.addOrGetModule(config, ImtConfigGroup.GROUP_NAME, ImtConfigGroup.class).getTrucksFile();
		controler.addOverridingModule(new ImtModule(ConfigGroup.getInputFileURL(config.getContext(), trucksFilePath),
				ConfigUtils.addOrGetModule(config, ImtConfigGroup.GROUP_NAME, ImtConfigGroup.class)));
		controler.configureQSimComponents(DvrpQSimComponents.activateModes(TransportMode.truck));

		// Run simulation
		controler.run();
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Usage: java RunIncidents <configFile> <trucksFile>");
			System.exit(1);
		}

		String configFile = args[0];

		run(configFile);
	}
}
