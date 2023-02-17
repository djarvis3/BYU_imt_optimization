package byu.IMT.oneIMT;

import org.testng.annotations.Test;

import org.matsim.core.utils.io.IOUtils;
import org.matsim.examples.ExamplesUtils;

import java.net.URL;
	@Test
	public class RunOneImtExampleTest {
		public void testRun() {
			URL configUrl = IOUtils.extendUrl(ExamplesUtils.getTestScenarioURL("dvrp-grid"),
            "one_truck_config.xml");
			RunOneImtExample.run(configUrl, "one_truck_vehicles.xml", false, 1);
	}
}
