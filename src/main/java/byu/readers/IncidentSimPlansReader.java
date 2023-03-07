package byu.readers;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

import java.io.File;


public class IncidentSimPlansReader {
    Scenario sc;
    Id<Person> id;
    PopulationFactory pf;
    Population pop;
	CoordinateTransformation ct;

    public IncidentSimPlansReader() {
        this.sc = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        this.pop = this.sc.getPopulation();
        this.pf = this.pop.getFactory();
        this.id = Id.createPersonId(id);
		this.ct = TransformationFactory.getCoordinateTransformation(TransformationFactory.WGS84, "EPSG:26912");
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
			Id<Person> personId = Id.createPersonId(record.getString("Incident ID"));
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

			Double x = record.getDouble("Longitude");
			Double y = record.getDouble("Lattitude");
			Coord actCoord = CoordUtils.createCoord(x, y);
			actCoord = ct.transform(actCoord);

			// ActivitySim modes are not the same as BEAM / MATSim modes. Will need to translate them here.

			String actType = "work";

			Activity activity1 = pf.createActivityFromCoord(actType, actCoord);
			// if it's the last activity2, then there won't be an end time

			// if there is an end time, add it to the activity2
			if (record.getDouble("Start Time (sec)") != null) {
				activity1.setEndTime(record.getDouble("Start Time (sec)"));
				plan.addActivity(activity1);
			}

			// if the is no end time (last activity2) just finish making the plan
			else {
				plan.addActivity(activity1);
			}

			{ // this is a leg

				String mode = "taxi";
				Leg leg = pf.createLeg(mode);
				plan.addLeg(leg);
			}

			Activity activity2 = pf.createActivityFromCoord(actType, actCoord);
			// if it's the last activity2, then there won't be an end time

			// if there is an end time, add it to the activity2
			if (record.getDouble("End Time (sec)") != null) {
				activity2.setEndTime(record.getDouble("End Time (sec)"));
				plan.addActivity(activity2);
			}

			// if the is no end time (last activity2) just finish making the plan
			else {
				plan.addActivity(activity2);
			}
		}
	}

        public static void main (String[]args){
            IncidentSimPlansReader reader = new IncidentSimPlansReader();
            reader.parseCsv("src/main/java/org/matsim/incidents/IncidentData_Daniel.csv");
            reader.writeXml("scenarios/incidents/plans_incident_workshop.xml");
        }

        private void writeXml (String outfile){new PopulationWriter(sc.getPopulation()).write("scenarios/utah/plans_incident_workshop.xml");
        }
    }
