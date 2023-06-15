package IMT.optimizer;

import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.fleet.Fleet;
import org.matsim.contrib.dvrp.path.VrpPathWithTravelData;
import org.matsim.contrib.dvrp.path.VrpPaths;
import org.matsim.contrib.dvrp.schedule.Schedules;
import org.matsim.contrib.dvrp.schedule.Task;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelTime;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class ClosestVehicleFinder {

	private final Fleet fleet;
	private final LeastCostPathCalculator router;
	private final TravelTime travelTime;

	public ClosestVehicleFinder(Fleet fleet, LeastCostPathCalculator router, TravelTime travelTime) {
		this.fleet = Objects.requireNonNull(fleet, "fleet must not be null");
		this.router = Objects.requireNonNull(router, "router must not be null");
		this.travelTime = Objects.requireNonNull(travelTime, "travelTime must not be null");
	}

	private double calculateArrivalTime(DvrpVehicle vehicle, Link toLink) {
		Task currentTask = vehicle.getSchedule().getCurrentTask();
		if (currentTask.getTaskType() != Optimizer.ImtTaskType.WAIT) {return Double.POSITIVE_INFINITY;}
		Link fromLink = Schedules.getLastLinkInSchedule(vehicle);
		double time_zero = 0;
		VrpPathWithTravelData pathToIncident = VrpPaths.calcAndCreatePath(fromLink, toLink, time_zero, router, travelTime);
		return pathToIncident.getArrivalTime();
	}

	public List<DvrpVehicle> getClosestVehicles(Link toLink, int respondingIMTs) {
		Objects.requireNonNull(toLink, "toLink must not be null");
		if (respondingIMTs <= 0) {
			throw new IllegalArgumentException("respondingVehicles must be greater than zero");
		}
		PriorityQueue<DvrpVehicle> closestVehicles = new PriorityQueue<>(Comparator.comparingDouble(vehicle -> calculateArrivalTime(vehicle, toLink)));
		closestVehicles.addAll(fleet.getVehicles().values());
		return closestVehicles.stream()
				.limit(Math.min(respondingIMTs, closestVehicles.size()))
				.collect(Collectors.toList());
	}
}
