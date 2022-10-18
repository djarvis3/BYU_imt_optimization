package org.matsim.codeexamples.events.taxiHandling;

import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;

public class TaxiEventHandlerReader {

    public TaxiEventHandlerReader (){
    }

    public static void main (String[]args) {

        String eventsXML = "output/utah_Outputs/output_events.xml.gz";
        //create an event object
        EventsManager events = EventsUtils.createEventsManager();

        //create the handler and add it
        MyTaxiTripInformationHandler infoHandler = new MyTaxiTripInformationHandler();
        MyTaxiCountingHandler countingHandler = new MyTaxiCountingHandler();
        MyTaxiVolumeHandler volumeHandler = new MyTaxiVolumeHandler();
        events.addHandler(infoHandler);
        events.addHandler(countingHandler);
        events.addHandler(volumeHandler);

        events.initProcessing();
        MatsimEventsReader reader = new MatsimEventsReader(events);
        reader.readFile(eventsXML);
        events.finishProcessing();

        System.out.println("total taxi trips: " + (countingHandler.getTotalTaxiTrips())/2);
        volumeHandler.writeChart("output/utah_Outputs/UtahTaxiTripsPerHour.png");

        System.out.println("Events file read!");
    }
}

