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
import java.util.PriorityQueue;
import java.util.stream.Collectors;

/**
 * ClosestVehicleFinder is a class that provides a method to find the closest vehicles from a given incident based on their estimated arrival time. It uses a priority queue to store vehicles and their estimated arrival time to the given link.
 */
public class ClosestVehicleFinder {

	private final Fleet fleet;
	private final LeastCostPathCalculator router;
	private final TravelTime travelTime;

	/**
	 * Constructs a new ClosestVehicleFinder instance with the specified Fleet, LeastCostPathCalculator and TravelTime.
	 */
	public ClosestVehicleFinder(Fleet fleet, LeastCostPathCalculator router, TravelTime travelTime) {
		this.fleet = fleet;
		this.router = router;
		this.travelTime = travelTime;
	}

	/**
	 * Returns a list of the closest vehicles to a given link based on the estimated arrival time.
	 * @return a list of the closest vehicles.
	 */
	public List<DvrpVehicle> getClosestVehicles(Link toLink, int respondingIMTs) {
		PriorityQueue<DvrpVehicle> closestVehicles = new PriorityQueue<>(Comparator.comparingDouble(vehicle -> calculateArrivalTime(vehicle, toLink)));
		closestVehicles.addAll(fleet.getVehicles().values());
		return closestVehicles.stream()
				.limit(Math.min(respondingIMTs, closestVehicles.size()))
				.collect(Collectors.toList());
	}

	/**
	 * Calculates the estimated arrival time of a given vehicle to a given link.
	 * @return the estimated arrival time of the vehicle to the given link.
	 */
	private double calculateArrivalTime(DvrpVehicle vehicle, Link toLink) {
		Task lastTask = Schedules.getLastTask(vehicle.getSchedule());
		if (lastTask.getTaskType() != Optimizer.ImtTaskType.WAIT) {
			return Double.POSITIVE_INFINITY;
		}
		Link fromLink = Schedules.getLastLinkInSchedule(vehicle);
		double time_zero = 0;
		VrpPathWithTravelData pathToIncident = VrpPaths.calcAndCreatePath(fromLink, toLink, time_zero, router, travelTime);
		return pathToIncident.getArrivalTime();
	}
}

