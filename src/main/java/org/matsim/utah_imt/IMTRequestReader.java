package org.matsim.utah_imt;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

import java.io.File;


public class IMTRequestReader {
	Scenario sc;
	Network network;
	Double time;
	Double x;
	Double y;
	Coord incidentCord;
    CoordinateTransformation ct;

    public IMTRequestReader() {
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

			// get linkId from csv file
			time = record.getDouble("start");
			x= record.getDouble("Longitude");
			y=record.getDouble("Latitude");
			incidentCord = CoordUtils.createCoord(x,y);
			incidentCord = ct.transform(incidentCord);
        }
    }

        public static void main (String[]args){
            String csv = "src/main/java/org/matsim/utah_imt/UtahIncidents_9_18_2018.csv";
            String outfile = "scenarios/utahIMT/incident_new_cord.xml";
            IMTRequestReader reader = new IMTRequestReader();
            reader.parseCsv(csv);
            reader.writeXml(outfile);
        }



        private void writeXml (String outfile){
        }
    }
