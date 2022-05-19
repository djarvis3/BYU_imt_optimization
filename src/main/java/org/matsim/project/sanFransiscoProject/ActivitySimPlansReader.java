package org.matsim.project.sanFransiscoProject;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;

public class ActivitySimPlansReader {
    Scenario scenario = null;

    PopulationFactory pf = null;
    Population pop = null;
    CoordinateTransformation ct = null;

    public ActivitySimPlansReader (){
        this.scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        this.pop = this.scenario.getPopulation();
        this.pf = this.pop.getFactory();
    }

    public ActivitySimPlansReader (Scenario scenario){
        this.scenario = scenario;
        this.pop = this.scenario.getPopulation();
        this.pf = this.pop.getFactory();
    }

    public void parseCsv (String csv){
    }

    public static void main(String[] args) {
        String csv = args[0];
        ActivitySimPlansReader reader = new ActivitySimPlansReader();
        reader.parseCsv(csv);
    }
}
