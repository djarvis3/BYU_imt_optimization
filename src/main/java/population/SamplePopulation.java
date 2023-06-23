package population;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.misc.Counter;

import java.io.IOException;

public class SamplePopulation {

	public static void run(String inputPlansFile, String outputPlansFile) throws IOException {
		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);
		new PopulationReader(scenario).readFile(inputPlansFile);

		Population sampledPopulation = PopulationUtils.createPopulation(config);

		Counter counter = new Counter("population # ");
		scenario.getPopulation().getPersons().values().forEach(person -> {
			if (Math.random() <= 0.1) {  // Set the desired sampling rate here
				sampledPopulation.addPerson(person);
			}
			counter.incCounter();
		});
		counter.printCounter();

		new PopulationWriter(sampledPopulation).write(outputPlansFile);
	}

	public static void main(String[] args) throws IOException {
		if(args.length != 2) {
			System.err.println("Usage: java SamplePopulation <inputPlansFile> <outputPlansFile>");
			System.exit(1);
		}

		String inputPlansFile = args[0];
		String outputPlansFile = args[1];

		run(inputPlansFile, outputPlansFile);
	}
}
