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
        this.ct = TransformationFactory.getCoordinateTransformation(TransformationFactory.WGS84, "EPSG:32612");
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
            Id<Person> personId = Id.createPersonId(record.getString("personId"));
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
            if (record.getString("activityType") != null) { // this is an activity
                Double x = record.getDouble("activityLocationX");
                Double y = record.getDouble("activityLocationY");
                Coord actCoord = CoordUtils.createCoord(x, y);
                // The ct.transformation is necessary if the coordinates are not set to UTM 12N
				// actCoord = ct.transform(actCoord);

                // ActivitySim modes are not the same as BEAM / MATSim modes. Will need to translate them here.

                String actType = record.getString("activityType");
                if (actType.equals("Home") || actType.equals("home")) {actType = "home";}
                else if (actType.equals("atwork") || actType.equals("work") || actType.equals("Work")) {actType= "work";}
				else if (actType.equals("school") || actType.equals("univ")) {actType = "edu";}
				else if (actType.equals("othdiscr") || actType.equals("shopping")) {actType = "shopping";}
				else if (actType.equals("social") || actType.equals("eatout")) {actType = "leisure";}
				else if (actType.equals("escort") || actType.equals("social") ||
                		 actType.equals("eatout") || actType.equals("othmaint"))
  				{actType = "other";}
                else {actType = "ADD ACTIVITY TYPE";}

                Activity activity = pf.createActivityFromCoord(actType, actCoord);
                // if it's the last activity, then there won't be an end time

                // if there is an end time, add it to the activity
                if (record.getDouble("activityEndTime") != null) {
                    activity.setEndTime(record.getDouble("activityEndTime"));
                    plan.addActivity(activity);
                }

                // if the is no end time (last activity) just finish making the plan
                else {
                    plan.addActivity(activity);
                }
            }
            else { // this is a leg

                String mode = record.getString("legMode");
                if (mode.equals("DRIVEALONEFREE") || mode.equals("DRIVEALONEPAY") ||
                        mode.equals("SHARED2FREE") || mode.equals("SHARED2PAY") ||
                        mode.equals("SHARED3FREE") || mode.equals("SHARED3PAY") ||
						mode.equals("hov2") || mode.equals("hov3") ||
						mode.equals("hov2_teleportation") || mode.equals("hov3_teleportation") ||
						mode.equals("car") || mode.equals("ride_hail") ||
						mode.equals("ride_hail_pooled"))
						{mode = TransportMode.car;}
                else if (mode.equals("WALK_LOC") || mode.equals("WALK_LRF") ||
                        mode.equals("WALK_EXP") || mode.equals("WALK_HVY") ||
                        mode.equals("WALK_COM") || mode.equals("DRIVE_LOC") ||
                        mode.equals("DRIVE_LRF") || mode.equals("DRIVE_EXP") ||
                        mode.equals("DRIVE_HVY") || mode.equals("DRIVE_COM") ||
                        mode.equals("TAXI") || mode.equals("TNC_SINGLE") ||
                        mode.equals("TNC_SHARED") || mode.equals("drive_transit")
						|| mode.equals("walk_transit"))
						{mode = TransportMode.pt;}
                else if (mode.equals("WALK") || (mode.equals("walk")))
				{mode = TransportMode.walk;}
                else if (mode.equals("BIKE") || (mode.equals("bike")))
						{mode = TransportMode.bike;}
                else {mode = "ADD MODE TYPE";}
                    Leg leg = pf.createLeg(mode);
                    plan.addLeg(leg);
                }
        }
    }

        public static void main (String[]args){
            ActivitySimPlansReader reader = new ActivitySimPlansReader();
            reader.parseCsv("full/plans.csv");
            reader.writeXml("full/wfrc_calibrated_no_RH_plans.xml");
        }

        private void writeXml (String outfile){
            new PopulationWriter(sc.getPopulation()).write(outfile);
        }
    }
