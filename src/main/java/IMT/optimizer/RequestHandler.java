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


		double flowCapacityFactor = 0.01;
		double fullLinkCapacity = request.getToLink().getCapacity() * flowCapacityFactor;
		double reducedLinkCapacity = fullLinkCapacity * (1 - request.getCapacityReduction());
		double linkCapacityGap = fullLinkCapacity - reducedLinkCapacity;

		double endTime = request.getEndTime();
		int respondingIMTs = request.getRespondingIMTs();

		incidentNetworkChangeEvents(request.getToLink(), reducedLinkCapacity, fullLinkCapacity,
				request.getSubmissionTime(), request.getEndTime(), request);

		List<DvrpVehicle> closestVehicles = closestVehicleFinder.getClosestVehicles(request.getToLink(), respondingIMTs);
		for (DvrpVehicle imtUnit : closestVehicles) {
			Schedule schedule = imtUnit.getSchedule();
			ScheduleUpdater updater = new ScheduleUpdater(router, travelTime, timer);
			double currLinkCapacity = fullLinkCapacity - (linkCapacityGap / 4);
			linkCapacityGap = fullLinkCapacity - currLinkCapacity;
			double arrivalTime = updater.updateScheduleForVehicle(schedule, request.getToLink(), endTime, request, imtUnit);

			if (arrivalTime<endTime) {
				imtNetworkChangeEvent(request.getToLink(), currLinkCapacity, request, arrivalTime);
			}
			else {
				String output = ("Arrival Time is greater than incident End Time, No Network Change Event. ");
				String incidentInfo = String.format("Request ID " + request.getId(), ", Arrival Time " + arrivalTime, ", End Time " + endTime);
				System.out.println(output + incidentInfo);
			}
		}
	}

	/**
	 * Adds a network change event for a given link based on the current capacity and other request parameters.
	 *
	 * @param incidentLink      The link at which the incident occurs.
	 * @param reducedCapacity	The capacity reduction caused by the incident.
	 * @param fullCapacity		The original "full" capacity.
	 * @param startTime         The incident startTime.
	 * @param endTime      		The incident endTime.
	 * @throws NullPointerException if toLink or request are null.
	 */
	private void incidentNetworkChangeEvents(Link incidentLink, double reducedCapacity, double fullCapacity,
											 double startTime, double endTime, Request request) {
		Objects.requireNonNull(incidentLink, "incidentLink must not be null");

		NetworkChangeEvent startEvent = new NetworkChangeEvent(startTime);
		startEvent.setFlowCapacityChange(new NetworkChangeEvent.ChangeValue
				(NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, reducedCapacity));
		startEvent.addLink(incidentLink);
		NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), startEvent);

		NetworkChangeEvent endEvent = new NetworkChangeEvent(endTime);
		endEvent.setFlowCapacityChange(new NetworkChangeEvent.ChangeValue
				(NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, fullCapacity));
		endEvent.addLink(incidentLink);
		NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), endEvent);

		String incidentInfo = String.format("Incident ID " + request.getId()+
				", Link ID " + incidentLink +
				", Full Capacity " + fullCapacity +
				", Reduced Capacity " + reducedCapacity);

		System.out.println(incidentInfo);
	}


	/**
	 * Adds a network change event for a given link based on the current capacity and other request parameters.
	 *
	 * @param toLink           The link to add the network change event for.
	 * @param currLinkCapacity The current capacity of the link.
	 * @param request          The request associated with the network change event.
	 * @param arrivalTime      The time at which the network change event should occur.
	 * @throws NullPointerException if toLink or request are null.
	 */
	private void imtNetworkChangeEvent(Link toLink, double currLinkCapacity, Request request, double arrivalTime) {
		Objects.requireNonNull(toLink, "toLink must not be null");
		Objects.requireNonNull(request, "request must not be null");

		NetworkChangeEvent restoreCapacityEvent = new NetworkChangeEvent(arrivalTime);
		restoreCapacityEvent.setFlowCapacityChange(new NetworkChangeEvent.ChangeValue(NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, currLinkCapacity));
		restoreCapacityEvent.addLink(toLink);
		NetworkUtils.addNetworkChangeEvent(scenario.getNetwork(), restoreCapacityEvent);

		String output = ("Arrival Time is less than incident End Time, Network Change Event Added Upon IMT Arrival. ");
		String incidentInfo = String.format("Request ID " + request.getId(), ", # of IMTs" + request.getRespondingIMTs(),
				", Full Capacity " + request.getToLink().getCapacity(),
				", Reduced Capacity " + (request.getToLink().getCapacity()*(1-request.getCapacityReduction())),
				", Current Capacity " + currLinkCapacity);

		System.out.println(output + incidentInfo);
	}
}
