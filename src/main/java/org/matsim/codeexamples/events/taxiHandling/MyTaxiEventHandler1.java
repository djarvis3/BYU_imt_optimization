package org.matsim.codeexamples.events.taxiHandling;

import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.PersonArrivalEvent;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.events.handler.PersonArrivalEventHandler;
import org.matsim.api.core.v01.events.handler.PersonDepartureEventHandler;
import org.matsim.codeexamples.events.eventsCopies.LinkEnterEvent_WithMode;
import org.matsim.codeexamples.events.eventsCopies.LinkLeaveEvent_WithMode;
import org.matsim.codeexamples.events.eventsCopies.PersonArrivalEvent_WithMode;
import org.matsim.codeexamples.events.eventsCopies.PersonDepartureEvent_WithMode;
import org.matsim.codeexamples.events.eventsCopies.handler.LinkEnterEventHandler_WithMode;
import org.matsim.codeexamples.events.eventsCopies.handler.LinkLeaveEventHandler_WithMode;
import org.matsim.codeexamples.events.eventsCopies.handler.PersonArrivalEventHandler_WithMode;
import org.matsim.codeexamples.events.eventsCopies.handler.PersonDepartureEventHandler_WithMode;

/**
 * This event handler prints some event information to the console.
 * @author dgrether
 *
 */
public class MyTaxiEventHandler1 implements
        LinkEnterEventHandler,
        LinkLeaveEventHandler, PersonArrivalEventHandler, PersonDepartureEventHandler
{

    @Override
    public void reset(int iteration) {
        System.out.println("reset...");
    }

    @Override
    public void handleEvent(LinkEnterEvent event) {
        System.out.println("LinkEnterEvent_WithMode");
        System.out.println("Time: " + event.getTime());
        System.out.println("LinkId: " + event.getLinkId());
    }

    @Override
    public void handleEvent(LinkLeaveEvent event) {
        System.out.println("LinkLeaveEvent_WithMode");
        System.out.println("Time: " + event.getTime());
        System.out.println("LinkId: " + event.getLinkId());
    }

    @Override
    public void handleEvent(PersonArrivalEvent event) {
        System.out.println("AgentArrivalEvent_WithMode");
        System.out.println("Time: " + event.getTime());
        System.out.println("LinkId: " + event.getLinkId());
        System.out.println("PersonId: " + event.getPersonId());
    }

    @Override
    public void handleEvent(PersonDepartureEvent event) {
        System.out.println("AgentDepartureEvent_WithMode");
        System.out.println("Time: " + event.getTime());
        System.out.println("LinkId: " + event.getLinkId());
        System.out.println("PersonId: " + event.getPersonId());
    }
}
