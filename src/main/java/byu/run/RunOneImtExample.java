package byu.run;

import byu.oneIMT.OneImtModule;
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

public class RunOneImtExample {

	public static final String CONFIG_FILE = "C:/Users/djarvis3/BYU_imt_optimization/scenarios/oneImt/one_imt_config.xml";

	public static final String TRUCK_FILE = "C:/Users/djarvis3/BYU_imt_optimization/scenarios/oneImt/one_imt_vehicles.xml";

	public static void run(String configUrl, String trucksFile, boolean otfvis, int lastIteration) {
		// load config
		Config config = ConfigUtils.loadConfig(configUrl, new DvrpConfigGroup(), new OTFVisConfigGroup());
		config.controler().setLastIteration(lastIteration);

		// load scenario
		Scenario scenario = ScenarioUtils.loadScenario(config);

		// setup controler
		Controler controler = new Controler(scenario);
		controler.addOverridingModule(new DvrpModule());
		controler.addOverridingModule(new OneImtModule(ConfigGroup.getInputFileURL(config.getContext(), trucksFile)));
		controler.configureQSimComponents(DvrpQSimComponents.activateModes(TransportMode.truck));

		if (otfvis) {
			controler.addOverridingModule(new OTFVisLiveModule()); // OTFVis visualisation
		}

		// run simulation
		controler.run();
	}


	public static void main(String[] args) {run(CONFIG_FILE,TRUCK_FILE, false, 1);}
}
