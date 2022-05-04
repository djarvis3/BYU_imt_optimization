package org.matsim.codeexamples.population.demandGeneration;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.config.ConfigUtils;

import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

/**
 * "P" has to do with "Potsdam" and "Z" with "Zurich", but P and Z are mostly used to show which classes belong together.
 */
public class RunPPopulationGenerator implements Runnable {

    public Random r;

    private Map<String, Coord> zoneGeometries = new HashMap<>();

    private CoordinateTransformation ct = TransformationFactory.getCoordinateTransformation(TransformationFactory.WGS84, TransformationFactory.WGS84_UTM33N);

    private Scenario scenario;

    private Population population;

    public static void main(String[] args) {
        RunPPopulationGenerator potsdamPop = new RunPPopulationGenerator();
        potsdamPop.run();
    }

    @Override
    public void run() {
        scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        population = scenario.getPopulation();
        fillZoneData();
        generatePopulation();
        PopulationWriter populationWriter = new PopulationWriter(scenario.getPopulation(), scenario.getNetwork());
        populationWriter.write("./scenarios/rome/population.xml");
    }

    private void fillZoneData() {
        // Add the locations you want to use here.
        // (with proper coordinates)
        this.r=new Random();
        zoneGeometries.put("home1", randomHomeCord(r));
        zoneGeometries.put("home2", new Coord( 12.47931,  41.91842));
        zoneGeometries.put("home3", new Coord( 12.48125,  41.91854));
        zoneGeometries.put("home4", new Coord( 12.48391,  41.92105));
        zoneGeometries.put("home5", new Coord( 12.48412,  41.92180));
        zoneGeometries.put("entertainment1", new Coord( 12.47411,  41.92301));
        zoneGeometries.put("entertainment2", new Coord( 12.47495,  41.92382));
        zoneGeometries.put("entertainment3", new Coord( 12.47921,  41.92513));
        zoneGeometries.put("entertainment4", new Coord( 12.482354,  41.92624));
        zoneGeometries.put("entertainment5", new Coord( 12.483145,  41.92654));
        zoneGeometries.put("work1", new Coord( 12.486662,  41.91841));
        zoneGeometries.put("work2", new Coord( 12.487642,  41.91921));
        zoneGeometries.put("work3", new Coord( 12.49125,  41.92015));
        zoneGeometries.put("work4", new Coord( 12.49521,  41.92223));
        zoneGeometries.put("work5", new Coord( 12.49641,  41.92231));
        zoneGeometries.put("school1", new Coord( 12.486012,  41.92275));
        zoneGeometries.put("school2", new Coord( 12.486421,  41.92345));
        zoneGeometries.put("school3", new Coord( 12.49175,  41.92512));
        zoneGeometries.put("school4", new Coord( 12.49411,  41.92685));
        zoneGeometries.put("school5", new Coord( 12.49641,  41.92701));
    }

    private void generatePopulation() {
        generateHomeWorkHomeTrips("home1", "work1", 20); // create 20 trips from zone 'home1' to 'work1'
        generateHomeWorkHomeTrips("home1", "work2", 20); // create 20 trips from zone 'home1' to 'work2'
        generateHomeWorkHomeTrips("home1", "work3", 20); // create 20 trips from zone 'home1' to 'work3'
        generateHomeWorkHomeTrips("home1", "work4", 20); // create 20 trips from zone 'home1' to 'work4'
        generateHomeWorkHomeTrips("home1", "work5", 20); // create 20 trips from zone 'home1' to 'work5'
        generateHomeWorkHomeTrips("home2", "work1", 20); // create 20 trips from zone 'home2' to 'work1'
        generateHomeWorkHomeTrips("home2", "work2", 20); // create 20 trips from zone 'home2' to 'work2'
        generateHomeWorkHomeTrips("home2", "work3", 20); // create 20 trips from zone 'home2' to 'work3'
        generateHomeWorkHomeTrips("home2", "work4", 20); // create 20 trips from zone 'home2' to 'work4'
        generateHomeWorkHomeTrips("home2", "work5", 20); // create 20 trips from zone 'home2' to 'work5'
        generateHomeWorkHomeTrips("home3", "work1", 20); // create 20 trips from zone 'home3' to 'work1'
        generateHomeWorkHomeTrips("home3", "work2", 20); // create 20 trips from zone 'home3' to 'work2'
        generateHomeWorkHomeTrips("home3", "work3", 20); // create 20 trips from zone 'home3' to 'work3'
        generateHomeWorkHomeTrips("home3", "work4", 20); // create 20 trips from zone 'home3' to 'work4'
        generateHomeWorkHomeTrips("home3", "work5", 20); // create 20 trips from zone 'home3' to 'work5'
        generateHomeWorkHomeTrips("home4", "work1", 20); // create 20 trips from zone 'home4' to 'work1'
        generateHomeWorkHomeTrips("home4", "work2", 20); // create 20 trips from zone 'home4' to 'work2'
        generateHomeWorkHomeTrips("home4", "work3", 20); // create 20 trips from zone 'home4' to 'work3'
        generateHomeWorkHomeTrips("home4", "work4", 20); // create 20 trips from zone 'home4' to 'work4'
        generateHomeWorkHomeTrips("home4", "work5", 20); // create 20 trips from zone 'home4' to 'work5'
        generateHomeWorkHomeTrips("home5", "work1", 20); // create 20 trips from zone 'home5' to 'work1'
        generateHomeWorkHomeTrips("home5", "work2", 20); // create 20 trips from zone 'home5' to 'work2'
        generateHomeWorkHomeTrips("home5", "work3", 20); // create 20 trips from zone 'home5' to 'work3'
        generateHomeWorkHomeTrips("home5", "work4", 20); // create 20 trips from zone 'home5' to 'work4'
        generateHomeWorkHomeTrips("home5", "work5", 20); // create 20 trips from zone 'home5' to 'work5'
        generateHomeWorkHomeTrips("home1", "entertainment1", 20); // create 20 trips from zone 'home1' to 'entertainment1'
        generateHomeWorkHomeTrips("home1", "entertainment2", 20); // create 20 trips from zone 'home1' to 'entertainment2'
        generateHomeWorkHomeTrips("home1", "entertainment3", 20); // create 20 trips from zone 'home1' to 'entertainment3'
        generateHomeWorkHomeTrips("home1", "entertainment4", 20); // create 20 trips from zone 'home1' to 'entertainment4'
        generateHomeWorkHomeTrips("home1", "entertainment5", 20); // create 20 trips from zone 'home1' to 'entertainment5'
        generateHomeWorkHomeTrips("home2", "entertainment1", 20); // create 20 trips from zone 'home2' to 'entertainment1'
        generateHomeWorkHomeTrips("home2", "entertainment2", 20); // create 20 trips from zone 'home2' to 'entertainment2'
        generateHomeWorkHomeTrips("home2", "entertainment3", 20); // create 20 trips from zone 'home2' to 'entertainment3'
        generateHomeWorkHomeTrips("home2", "entertainment4", 20); // create 20 trips from zone 'home2' to 'entertainment4'
        generateHomeWorkHomeTrips("home2", "entertainment5", 20); // create 20 trips from zone 'home2' to 'entertainment5'
        generateHomeWorkHomeTrips("home3", "entertainment1", 20); // create 20 trips from zone 'home3' to 'entertainment1'
        generateHomeWorkHomeTrips("home3", "entertainment2", 20); // create 20 trips from zone 'home3' to 'entertainment2'
        generateHomeWorkHomeTrips("home3", "entertainment3", 20); // create 20 trips from zone 'home3' to 'entertainment3'
        generateHomeWorkHomeTrips("home3", "entertainment4", 20); // create 20 trips from zone 'home3' to 'entertainment4'
        generateHomeWorkHomeTrips("home3", "entertainment5", 20); // create 20 trips from zone 'home3' to 'entertainment5'
        generateHomeWorkHomeTrips("home4", "entertainment1", 20); // create 20 trips from zone 'home4' to 'entertainment1'
        generateHomeWorkHomeTrips("home4", "entertainment2", 20); // create 20 trips from zone 'home4' to 'entertainment2'
        generateHomeWorkHomeTrips("home4", "entertainment3", 20); // create 20 trips from zone 'home4' to 'entertainment3'
        generateHomeWorkHomeTrips("home4", "entertainment4", 20); // create 20 trips from zone 'home4' to 'entertainment4'
        generateHomeWorkHomeTrips("home4", "entertainment5", 20); // create 20 trips from zone 'home4' to 'entertainment5'
        generateHomeWorkHomeTrips("home5", "entertainment1", 20); // create 20 trips from zone 'home5' to 'entertainment1'
        generateHomeWorkHomeTrips("home5", "entertainment2", 20); // create 20 trips from zone 'home5' to 'entertainment2'
        generateHomeWorkHomeTrips("home5", "entertainment3", 20); // create 20 trips from zone 'home5' to 'entertainment3'
        generateHomeWorkHomeTrips("home5", "entertainment4", 20); // create 20 trips from zone 'home5' to 'entertainment4'
        generateHomeWorkHomeTrips("home5", "entertainment5", 20); // create 20 trips from zone 'home5' to 'entertainment5'
        generateHomeWorkHomeTrips("home1", "school1", 20); // create 20 trips from zone 'home1' to 'school1'
        generateHomeWorkHomeTrips("home1", "school2", 20); // create 20 trips from zone 'home1' to 'school2'
        generateHomeWorkHomeTrips("home1", "school3", 20); // create 20 trips from zone 'home1' to 'school3'
        generateHomeWorkHomeTrips("home1", "school4", 20); // create 20 trips from zone 'home1' to 'school4'
        generateHomeWorkHomeTrips("home1", "school5", 20); // create 20 trips from zone 'home1' to 'school5'
        generateHomeWorkHomeTrips("home2", "school1", 20); // create 20 trips from zone 'home2' to 'school1'
        generateHomeWorkHomeTrips("home2", "school2", 20); // create 20 trips from zone 'home2' to 'school2'
        generateHomeWorkHomeTrips("home2", "school3", 20); // create 20 trips from zone 'home2' to 'school3'
        generateHomeWorkHomeTrips("home2", "school4", 20); // create 20 trips from zone 'home2' to 'school4'
        generateHomeWorkHomeTrips("home2", "school5", 20); // create 20 trips from zone 'home2' to 'school5'
        generateHomeWorkHomeTrips("home3", "school1", 20); // create 20 trips from zone 'home3' to 'school1'
        generateHomeWorkHomeTrips("home3", "school2", 20); // create 20 trips from zone 'home3' to 'school2'
        generateHomeWorkHomeTrips("home3", "school3", 20); // create 20 trips from zone 'home3' to 'school3'
        generateHomeWorkHomeTrips("home3", "school4", 20); // create 20 trips from zone 'home3' to 'school4'
        generateHomeWorkHomeTrips("home3", "school5", 20); // create 20 trips from zone 'home3' to 'school5'
        generateHomeWorkHomeTrips("home4", "school1", 20); // create 20 trips from zone 'home4' to 'school1'
        generateHomeWorkHomeTrips("home4", "school2", 20); // create 20 trips from zone 'home4' to 'school2'
        generateHomeWorkHomeTrips("home4", "school3", 20); // create 20 trips from zone 'home4' to 'school3'
        generateHomeWorkHomeTrips("home4", "school4", 20); // create 20 trips from zone 'home4' to 'school4'
        generateHomeWorkHomeTrips("home4", "school5", 20); // create 20 trips from zone 'home4' to 'school5'
        generateHomeWorkHomeTrips("home5", "school1", 20); // create 20 trips from zone 'home5' to 'school1'
        generateHomeWorkHomeTrips("home5", "school2", 20); // create 20 trips from zone 'home5' to 'school2'
        generateHomeWorkHomeTrips("home5", "school3", 20); // create 20 trips from zone 'home5' to 'school3'
        generateHomeWorkHomeTrips("home5", "school4", 20); // create 20 trips from zone 'home5' to 'school4'
        generateHomeWorkHomeTrips("home5", "school5", 20); // create 20 trips from zone 'home5' to 'school5'

        //... generate more trips here
    }

    private void generateHomeWorkHomeTrips(String from, String to, int quantity) {
        for (int i=0; i<quantity; ++i) {
            Coord source = zoneGeometries.get(from);
            Coord sink = zoneGeometries.get(to);
            Person person = population.getFactory().createPerson(createId(from, to, i, TransportMode.car));
            Plan plan = population.getFactory().createPlan();
            Coord homeLocation = shoot(ct.transform(source));
            Coord workLocation = shoot(ct.transform(sink));
            plan.addActivity(createHome(homeLocation));
            plan.addLeg(createDriveLeg());
            plan.addActivity(createWork(workLocation));
            plan.addLeg(createDriveLeg());
            plan.addActivity(createHome(homeLocation));
            person.addPlan(plan);
            population.addPerson(person);
        }
    }

    private Leg createDriveLeg() {
        Leg leg = population.getFactory().createLeg(TransportMode.car);
        return leg;
    }

    private Coord randomHomeCord(Random r) {
      float rangeEW = r.nextInt(11855);
        float pointEW=(1247370+rangeEW)/100000;
      float rangeNS = r.nextInt(470);
        float pointNS=(4191781+rangeNS)/100000;
      Coord homeCord=new Coord(rangeEW, rangeNS);
        return homeCord;
    }


    private Coord shoot(Coord source) {
        // Insert code here to blur the input coordinate.
        // For example, add a random number to the x and y coordinates.
        return source;
    }

    private Activity createWork(Coord workLocation) {
        Activity activity = population.getFactory().createActivityFromCoord("work", workLocation);
        activity.setEndTime(17*60*60);
        return activity;
    }

    private Activity createHome(Coord homeLocation) {
        Activity activity = population.getFactory().createActivityFromCoord("home", homeLocation);
        activity.setEndTime(9*60*60);
        return activity;
    }

    private Id<Person> createId(String source, String sink, int i, String transportMode) {
        return Id.create(transportMode + "_" + source + "_" + sink + "_" + i, Person.class);
    }

}