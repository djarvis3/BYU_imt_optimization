package analysis.trucks;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.vehicles.Vehicle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TruckTravelAnalysisHandler implements LinkEnterEventHandler, LinkLeaveEventHandler {
	private final Set<Id<Vehicle>> truckIds;
	private final Network network;

	// Store the timestamp of when each truck enters a link
	private final Map<Id<Vehicle>, Double> truckEnterTimes = new HashMap<>();

	// Store total travel time and distance for each truck
	private final Map<Id<Vehicle>, Double> totalTravelTimes = new HashMap<>();
	private final Map<Id<Vehicle>, Double> totalDistances = new HashMap<>();

	public TruckTravelAnalysisHandler(Set<Id<Vehicle>> truckIds, Network network) {
		this.truckIds = new HashSet<>(truckIds);
		this.network = network;
	}

	@Override
	public void handleEvent(LinkEnterEvent event) {
		Id<Vehicle> vehicleId = event.getVehicleId();
		if (truckIds.contains(vehicleId)) {
			truckEnterTimes.put(vehicleId, event.getTime());
		}
	}

	@Override
	public void handleEvent(LinkLeaveEvent event) {
		Id<Vehicle> vehicleId = event.getVehicleId();
		if (truckIds.contains(vehicleId)) {
			double enterTime = truckEnterTimes.getOrDefault(vehicleId, event.getTime());
			double travelTime = event.getTime() - enterTime;

			// Update total travel time for this truck
			totalTravelTimes.put(vehicleId, totalTravelTimes.getOrDefault(vehicleId, 0.0) + travelTime);

			// Update total travel distance for this truck
			Link link = network.getLinks().get(event.getLinkId());
			totalDistances.put(vehicleId, totalDistances.getOrDefault(vehicleId, 0.0) + link.getLength());
		}
	}

	public Map<Id<Vehicle>, Double> getTotalTravelTimes() {
		return totalTravelTimes;
	}

	public Map<Id<Vehicle>, Double> getTotalDistances() {
		return totalDistances;
	}
}
