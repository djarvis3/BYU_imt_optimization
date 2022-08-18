package org.matsim.project;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkUtils;

public class Incidents {

    Network network;
    Id<Link> id;
    String linkString;
    Double speed;
    Double capacity;
    Double lanes;
    Double newCapacity;
    Double newLanes;
    Double newSpeed;

    public Incidents(Scenario scenario) {
        this.network = scenario.getNetwork();
    }

    public void makeOneIncident(String linkId, String type, double effect, double start, double end) {
        for (Link link : network.getLinks().values() ) {
            this.id = link.getId();
            this.linkString = id.toString();
            this.capacity = link.getCapacity();
            this.newCapacity = (link.getCapacity() * effect);
            this.lanes = link.getNumberOfLanes();
            this.newLanes = (link.getNumberOfLanes() - effect);
            this.speed = link.getFreespeed();
            this.newSpeed = (link.getFreespeed() * effect);

            switch (type) {
                case "capacity":
                    if (linkId.equals(linkString)) {
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
                    }
                    break;
                case "lanes":
                    if (linkId.equals(linkString)) {
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
                case "speed":
                    if (linkId.equals(linkString)) {
                        {
                            NetworkChangeEvent event = new NetworkChangeEvent(start);
                            event.setFreespeedChange(new NetworkChangeEvent.ChangeValue(NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, newSpeed));
                            event.addLink(link);
                            NetworkUtils.addNetworkChangeEvent(network, event);
                        }
                        {
                            NetworkChangeEvent event = new NetworkChangeEvent(end);
                            event.setFlowCapacityChange(new NetworkChangeEvent.ChangeValue(NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, speed));
                            event.addLink(link);
                            NetworkUtils.addNetworkChangeEvent(network, event);
                        }
                    }
                    break;
            }
        }
    }
}