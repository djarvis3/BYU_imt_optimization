package IMT.optimizer;

import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.fleet.Fleet;
import org.matsim.contrib.dvrp.path.VrpPathWithTravelData;
import org.matsim.contrib.dvrp.path.VrpPaths;
import org.matsim.contrib.dvrp.schedule.Schedule;
import org.matsim.contrib.dvrp.schedule.Schedules;
import org.matsim.contrib.dvrp.schedule.Task;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelTime;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Finds the closest vehicles to a specific link for IMT response.
 */
public class ClosestVehicleFinder {

	private final Fleet fleet;
	private final LeastCostPathCalculator router;
	private final TravelTime travelTime;

	/**
	 * Constructs a ClosestVehicleFinder object with the specified fleet, router, and travel time.
	 *
	 * @param fleet      the fleet of vehicles to search from
	 * @param router     the least cost path calculator
	 * @param travelTime the travel time estimator
	 */
	public ClosestVehicleFinder(Fleet fleet, LeastCostPathCalculator router, TravelTime travelTime) {
		this.fleet = Objects.requireNonNull(fleet, "fleet must not be null");
		this.router = Objects.requireNonNull(router, "router must not be null");
		this.travelTime = Objects.requireNonNull(travelTime, "travelTime must not be null");
	}

	private double calculateArrivalTime(DvrpVehicle vehicle, Link toLink) {
		// Check if the vehicle's schedule has started
		if (vehicle.getSchedule().getStatus() != Schedule.ScheduleStatus.STARTED) {
			return Double.POSITIVE_INFINITY;
		}

		Task currentTask = vehicle.getSchedule().getCurrentTask();
		if (currentTask.getTaskType() != Optimizer.ImtTaskType.WAIT) {
			return Double.POSITIVE_INFINITY;
		}
		Link fromLink = Schedules.getLastLinkInSchedule(vehicle);
		VrpPathWithTravelData pathToIncident = VrpPaths.calcAndCreatePath(fromLink, toLink, 0, router, travelTime);
		return pathToIncident.getArrivalTime();
	}

	/**
	 * Returns a list of the closest vehicles to the specified link for IMT response.
	 *
	 * @param toLink           the link to which vehicles should be closest
	 * @param respondingIMTs   the number of the closest vehicles to find
	 * @param requestTime      the time of the request
	 * @return a list of the closest vehicles
	 * @throws IllegalArgumentException if respondingIMTs is less than or equal to zero
	 */
	public List<DvrpVehicle> getClosestVehicles(Link toLink, int respondingIMTs, double requestTime) {
		Objects.requireNonNull(toLink, "toLink must not be null");
		if (respondingIMTs <= 0) {
			throw new IllegalArgumentException("respondingVehicles must be greater than zero");
		}

		return fleet.getVehicles().values().stream()
				.filter(vehicle -> vehicle.getServiceBeginTime() < requestTime)
				.filter(vehicle -> vehicle.getServiceEndTime() > requestTime)
				.sorted(Comparator.comparingDouble(vehicle -> calculateArrivalTime(vehicle, toLink)))
				.limit(respondingIMTs)
				.collect(Collectors.toList());
	}

}
