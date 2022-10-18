package org.matsim.codeexamples.events.taxiHandling;

import org.matsim.api.core.v01.events.PersonArrivalEvent;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.events.handler.PersonArrivalEventHandler;
import org.matsim.api.core.v01.events.handler.PersonDepartureEventHandler;

import java.util.Objects;

/**
 * This event handler prints some event information to the console.
 * @author dgrether
 *
 */

public class MyTaxiTripInformationHandler implements
        PersonDepartureEventHandler,
        PersonArrivalEventHandler { //CustomPersonDepartureEventHandler {

    @Override
    public void reset(int iteration) {
        System.out.println("reset...");
    }

    @Override
    public void handleEvent(PersonDepartureEvent event) {
        if (Objects.equals(event.getLegMode(), "taxi")) {
            System.out.println("AgentDepartureEvent");
            System.out.println("Mode Type:" + event.getLegMode());
            System.out.println("Time: " + event.getTime());
            System.out.println("LinkId: " + event.getLinkId());
            System.out.println("PersonId: " + event.getPersonId());
        }
    }

    @Override
    public void handleEvent(PersonArrivalEvent event) {
        if (Objects.equals(event.getLegMode(), "taxi")) {
            System.out.println("AgentArrivalEvent");
            System.out.println("Mode Type:" + event.getLegMode());
            System.out.println("Time: " + event.getTime());
            System.out.println("LinkId: " + event.getLinkId());
            System.out.println("PersonId: " + event.getPersonId());
        }
    }

    /*
     where the rabbit hole started. I wanted to add DVRP Id as an event characteristic.
     public void handleEvent(CustomPersonDepartureEvent event) {
     System.out.println("DVRP ID:" + event.getDVRPId());
     }
    */
}
