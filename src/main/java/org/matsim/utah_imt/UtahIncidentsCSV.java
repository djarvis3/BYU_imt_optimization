package org.matsim.utah_imt;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

import java.io.File;

public class UtahIncidentsCSV {
    // used for IncidentsCSV
    Network network;
    // used for CSV parsing
    Double x;
    Double y;
    Coord incidentCord;
    Link linkInfo;
    String linkCSV;
    Double effect;
    Double start;
    Double end;
    CoordinateTransformation ct;

    // used for Network parsing
    Id<Link> id;
    String linkNetwork;
    Double capacity;
    Double newCapacity;

    public UtahIncidentsCSV(Scenario scenario) {
        this.network = scenario.getNetwork();
        this.ct = TransformationFactory.getCoordinateTransformation(TransformationFactory.WGS84, "EPSG:26912");
    }

    public void parseIncidentsCSV(String csv) {
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.getFormat().setLineSeparator("\n");
        parserSettings.setHeaderExtractionEnabled(true);

        CsvParser parser = new CsvParser(parserSettings);
        parser.beginParsing(new File(csv));

        Record record;
        parser.getRecordMetadata();

        while ((record = parser.parseNextRecord()) != null) {

            // get linkId from csv file

            x= record.getDouble("Longitude");
            y=record.getDouble("Latitude");
            incidentCord = CoordUtils.createCoord(x,y);
            incidentCord = ct.transform(incidentCord);
			linkInfo = NetworkUtils.getNearestLink(network, incidentCord);
			linkCSV = String.valueOf(linkInfo.getId());
            effect = record.getDouble("effect");
            start = record.getDouble("start");
            end = record.getDouble("end");

            for (Link link : network.getLinks().values()) {
                this.id = link.getId();
                this.linkNetwork = id.toString();
                this.capacity = link.getCapacity();
                this.newCapacity = link.getCapacity() - (this.capacity * effect);

                        if (linkCSV.equals(linkNetwork)) {
                            {
                                NetworkChangeEvent event = new NetworkChangeEvent(start);
                                event.setFlowCapacityChange(new NetworkChangeEvent.ChangeValue(NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, newCapacity));
                                event.addLink(link);
                                NetworkUtils.addNetworkChangeEvent(network, event);
                            }
                            {
                                NetworkChangeEvent event = new NetworkChangeEvent(end);
                                event.setFlowCapacityChange(new NetworkChangeEvent.ChangeValue(NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, capacity));
                                event.addLink(link);
                                NetworkUtils.addNetworkChangeEvent(network, event);
                            }
                            System.out.println("This Link Had A Capacity Change " + ", Link Id=" + linkCSV + ", Original Capacity " + capacity + ", Accident Capacity " + newCapacity + ", Time Effected (minutes) " + ((end-start)/60));
							System.out.println("Incident Coordinates " + incidentCord);
                        }
            }
        }
    }
}

