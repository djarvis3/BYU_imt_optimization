package readers;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

import java.io.File;


public class ActivitySimPlansReader {
    Scenario sc;
    Id<Person> id;
    PopulationFactory pf;
    Population pop;
    CoordinateTransformation ct;

    public ActivitySimPlansReader() {
        this.sc = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        this.pop = this.sc.getPopulation();
        this.pf = this.pop.getFactory();
        this.id = Id.createPersonId(id);
        this.ct = TransformationFactory.getCoordinateTransformation(TransformationFactory.WGS84, "EPSG:7131");
    }

    public void parseCsv(String csv) {
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.getFormat().setLineSeparator("\n");
        parserSettings.setHeaderExtractionEnabled(true);

        CsvParser parser = new CsvParser(parserSettings);
        parser.beginParsing(new File(csv));

        Record record;
        parser.getRecordMetadata();

        while ((record = parser.parseNextRecord()) != null) {

            // get person id from csv file
            Id<Person> personId = Id.createPersonId(record.getString("person_id"));
            Person person;
            Plan plan;
            // check to see if we have seen this person before
            if (sc.getPopulation().getPersons().containsKey(personId)) { // this person already exists
                person = sc.getPopulation().getPersons().get(personId);
                plan = person.getSelectedPlan();
            } else { // new person needs to be created
                person = pf.createPerson(personId);
                sc.getPopulation().addPerson(person);
                plan = pf.createPlan();
                person.addPlan(plan);
            }

            // let's figure out if this is an activity or a leg
            if (record.getString("ActivityType") != null) { // this is an activity
                Double x = record.getDouble("x");
                Double y = record.getDouble("y");
                Coord actCoord = CoordUtils.createCoord(x, y);
                actCoord = ct.transform(actCoord);

                // ActivitySim modes are not the same as BEAM / MATSim modes. Will need to translate them here.

                String actType = record.getString("ActivityType");
                if (actType.equals("Home") || actType.equals("home")) {actType = "home";}
                else if (actType.equals("atwork") || actType.equals("work")) {actType= "work";}
                else if (actType.equals("escort") || actType.equals("social")) {actType= "leisure";}
                else if (actType.equals("school") || actType.equals("univ")) {actType= "education";}
                else if (actType.equals("eatout") || actType.equals("othdiscr") ||
                         actType.equals("othmaint") || actType.equals("shopping"))  {actType = "other";}
                else {actType = "ADD ACTIVITY TYPE";}

                Activity activity = pf.createActivityFromCoord(actType, actCoord);
                // if it's the last activity, then there won't be an end time

                // if there is an end time, add it to the activity
                if (record.getDouble("departure_time") != null) {
                    activity.setEndTime(record.getDouble("departure_time") * 3600);
                    plan.addActivity(activity);
                }

                // if the is no end time (last activity) just finish making the plan
                else {
                    plan.addActivity(activity);
                }
            }
            else { // this is a leg

                String mode = record.getString("trip_mode");
                if (mode.equals("DRIVEALONEFREE") || mode.equals("DRIVEALONEPAY") ||
                        mode.equals("SHARED2FREE") || mode.equals("SHARED2PAY") ||
                        mode.equals("SHARED3FREE") || mode.equals("SHARED3PAY"))
                         {mode = TransportMode.car;}
                else if (mode.equals("WALK_LOC") || mode.equals("WALK_LRF") ||
                        mode.equals("WALK_EXP") || mode.equals("WALK_HVY") ||
                        mode.equals("WALK_COM") || mode.equals("DRIVE_LOC") ||
                        mode.equals("DRIVE_LRF") || mode.equals("DRIVE_EXP") ||
                        mode.equals("DRIVE_HVY") || mode.equals("DRIVE_COM") ||
                        mode.equals("TAXI") || mode.equals("TNC_SINGLE") ||
                        mode.equals("TNC_SHARED")) {mode = TransportMode.pt;}
                else if (mode.equals("WALK")){mode = TransportMode.walk;}
                else if (mode.equals("BIKE")){mode = TransportMode.bike;}
                else {mode = "ADD MODE TYPE";}
                    Leg leg = pf.createLeg(mode);
                    plan.addLeg(leg);
                }
        }
    }

        public static void main (String[]args){
            String csv = args[0];
            String outfile = args[1];
            ActivitySimPlansReader reader = new ActivitySimPlansReader();
            reader.parseCsv(csv);
            reader.writeXml(outfile);
        }

        private void writeXml (String outfile){
            new PopulationWriter(sc.getPopulation()).write(outfile);
        }
    }
