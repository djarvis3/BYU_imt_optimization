package IMT.optimizer;

import IMT.Request;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.fleet.Fleet;
import org.matsim.contrib.dvrp.schedule.*;
import org.matsim.core.mobsim.framework.MobsimTimer;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelTime;


import java.util.List;
import java.util.Objects;

/**
 * The RequestHandler class is responsible for handling requests by updating vehicle schedules and network event changes.
 */
public class RequestHandler {

	private final LeastCostPathCalculator router;
	private final TravelTime travelTime;
	private final MobsimTimer timer;
	private final Scenario scenario;
	private final ClosestVehicleFinder closestVehicleFinder;

	/**
	 * Constructor for the RequestHandler class.
	 *
	 * @throws NullPointerException if any of the parameters are null.
	 */
	public RequestHandler(Fleet fleet, LeastCostPathCalculator router, TravelTime travelTime, MobsimTimer timer, Scenario scenario) {
		Objects.requireNonNull(fleet, "fleet must not be null");
		this.router = Objects.requireNonNull(router, "router must not be null");
		this.travelTime = Objects.requireNonNull(travelTime, "travelTime must not be null");
		this.timer = Objects.requireNonNull(timer, "timer must not be null");
		this.scenario = Objects.requireNonNull(scenario, "scenario must not be null");
		this.closestVehicleFinder = new ClosestVehicleFinder(fleet, router, travelTime);
	}

	/**
	 * Handles a given request by updating the schedules of the closest vehicles and adding a network change event.
	 *
	 * @param request The request to handle.
	 * @throws NullPointerException if request is null.
	 */
	public void handleRequest(Request request) {
		Objects.requireNonNull(request, "request must not be null");

		double fullLinkCapacity = request.getToLink().getCapacity();
		double currLinkCapacity = fullLinkCapacity * (1 - request.getCapacityReduction());
		double endTime = request.getEndTime();
		int respondingIMTs = request.getRespondingIMTs();

		List<DvrpVehicle> closestVehicles = closestVehicleFinder.getClosestVehicles(request.getToLink(), respondingIMTs);
		for (DvrpVehicle vehicle : closestVehicles) {
			Schedule schedule = vehicle.getSchedule();
			ScheduleUpdater updater = new ScheduleUpdater(closestVehicleFinder, router, travelTime, timer);
			currLinkCapacity = updater.updateScheduleForClosestVehicles(schedule, request.getToLink(), currLinkCapacity, fullLinkCapacity - currLinkCapacity, endTime, request);
			double arrivalTime = updater.getArrivalTime();
			addNetworkChangeEvent(request.getToLink(), currLinkCapacity, fullLinkCapacity, request, arrivalTime);
		}
	}

	/**
	 * Adds a network change event for a given link based on the current capacity and other request parameters.
	 *
	 * @param toLink           The link to add the network change event for.
	 * @param currLinkCapacity The current capacity of the link.
	 * @param fullLinkCapacity The full capacity of the link.
	 * @param request          The request associated with the network change event.
	 * @param arrivalTime      The time at which the network change event should occur.
	 * @throws NullPointerException if toLink or request are null.
	 */
	private void addNetworkChangeEvent(Link toLink, double currLinkCapacity, double fullLinkCapacity, Request request, double arrivalTime) {
		Objects.requireNonNull(toLink, "toLink must not be null");
		Objects.requireNonNull(request, "request must not be null");

		double reducedCapacity = fullLinkCapacity - currLinkCapacity;
		NetworkChangeEvent restoreCapacityEvent = new NetworkChangeEvent(arrivalTime);
		restoreCapacityEvent.setFlowCapacityChange(new NetworkChangeEvent.ChangeValue(NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, currLinkCapacity));
		restoreCapacityEvent.addLink(toLink);
		NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), restoreCapacityEvent);
		String output = String.format("Request IDs %s, Responding IMTs %d, Full Capacity %.2f, Reduced Capacity %.2f, Current Capacity %.2f", request.getId(), request.getRespondingIMTs(), fullLinkCapacity, reducedCapacity, currLinkCapacity);
		System.out.println(output);
	}
}

