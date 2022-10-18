package org.matsim.reader;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import java.io.File;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkUtils;

public class IncidentsCSV {

    // used for IncidentsCSV
    Network network;

    // used for CSV parsing
    String linkCSV;
    String type;
    Double effect;
    Double start;
    Double end;

    // used for Network parsing
    Id<Link> id;
    String linkNetwork;
    Double speed;
    Double capacity;
    Double lanes;
    Double newCapacity;
    Double newLanes;
    Double newSpeed;

    public IncidentsCSV(Scenario scenario) {
        this.network = scenario.getNetwork();
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
            linkCSV = record.getString("link");
            type = record.getString("type");
            effect = record.getDouble("effect");
            start = record.getDouble("start");
            end = record.getDouble("end");

            for (Link link : network.getLinks().values()) {
                this.id = link.getId();
                this.linkNetwork = id.toString();
                this.capacity = link.getCapacity();
                this.newCapacity = (link.getCapacity() * effect);
                this.lanes = link.getNumberOfLanes();
                this.newLanes = (link.getNumberOfLanes() - effect);
                this.speed = link.getFreespeed();
                this.newSpeed = (link.getFreespeed() * effect);

                        if (linkCSV.equals(linkNetwork)) {
                            {
                                NetworkChangeEvent event = new NetworkChangeEvent(start);
                                event.setLanesChange(new NetworkChangeEvent.ChangeValue(NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, newLanes));
                                event.addLink(link);
                                NetworkUtils.addNetworkChangeEvent(network, event);
                            }
                            {
                                NetworkChangeEvent event = new NetworkChangeEvent(end);
                                event.setLanesChange(new NetworkChangeEvent.ChangeValue(NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, lanes));
                                event.addLink(link);
                                NetworkUtils.addNetworkChangeEvent(network, event);
                            }
                        }
                        break;
            }
        }
    }
}

