package byu.imt.event_Handlers.taxiHandling;

import org.matsim.api.core.v01.events.PersonArrivalEvent;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.events.handler.PersonArrivalEventHandler;
import org.matsim.api.core.v01.events.handler.PersonDepartureEventHandler;

import java.util.Objects;


/**
 * This EventHandler implementation counts the travel time of
 * all agents and provides the average travel time per
 * agent.
 * Actually, handling Departures and Arrivals should be sufficient for this (may 2014)
 * @author dgrether
 *
 */
public class MyTaxiCountingHandler implements
        PersonDepartureEventHandler,
        PersonArrivalEventHandler {

    private double totalTaxiTrips = 0.0;

    public MyTaxiCountingHandler() {
    }

    public double getTotalTaxiTrips() {
        return this.totalTaxiTrips;
    }

    @Override
    public void reset(int iteration) {
        this.totalTaxiTrips = 0.0;
    }

    @Override
    public void handleEvent(PersonArrivalEvent event) {
        if (Objects.equals(event.getLegMode(), "taxi")) {
            this.totalTaxiTrips = totalTaxiTrips + 1;
        }
    }

    @Override
    public void handleEvent(PersonDepartureEvent event) {
        if (Objects.equals(event.getLegMode(), "taxi")) {
            this.totalTaxiTrips = totalTaxiTrips + 1;
        }
    }
}
