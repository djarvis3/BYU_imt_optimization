package IMT.optimizer;

import IMT.Request;
import IMT.events.ChangeEvent;
import IMT.events.EventHandler_IMT;
import IMT.events.ImtNetworkChangeEventGenerator;
import IMT.events.IncidentNetworkChangeEventGenerator;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.fleet.Fleet;
import org.matsim.contrib.dvrp.schedule.*;
import org.matsim.core.mobsim.framework.MobsimTimer;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelTime;

import java.util.List;
import java.util.Objects;

/**
 The RequestHandler class is responsible for handling incident requests. It updates the schedules of the closest
 vehicles and adds a network change event. This class uses the Fleet, LeastCostPathCalculator, TravelTime,
 MobsimTimer, and Scenario classes.
 */
public class RequestHandler {

	private static double FLOW_CAPACITY_FACTOR;

	/*
	 This value represents the percentage of capacity restored upon IMT arrival.
	 For example, if LINK_CAPACITY_RESTORE_INTERVAL is 0.25, 25% of the "lost" capacity is restored
	 upon the vehicle's arrival.
	 */
	private static final double LINK_CAPACITY_RESTORE_INTERVAL = 0.25;

	// These fields store references to the objects used by this class.
	private final LeastCostPathCalculator router;
	private final TravelTime travelTime;
	private final MobsimTimer timer;
	private final Scenario scenario;
	private final ClosestVehicleFinder closestVehicleFinder;
	private final IncidentNetworkChangeEventGenerator incidentNCE;
	private final ChangeEvent changeEvent;


	/**
	 * This constructor initializes the RequestHandler class.
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
		this.incidentNCE = new IncidentNetworkChangeEventGenerator(scenario);
		this.changeEvent = new ChangeEvent();


		FLOW_CAPACITY_FACTOR = scenario.getConfig().qsim().getFlowCapFactor();
	}

	/**
	 This method handles a request for an incident. It calculates the link capacities for the incident,
	 generates an incident network change event, updates the schedules of the closest vehicles, and adds an IMT
	 network change event. If the arrival time of a vehicle is greater than the incident end time, no IMT Network
	 Change Event is added and a log message is generated.
	 @param request the incident request to handle
	 @throws NullPointerException if the request is null
	 */
	public void handleRequest(Request request) {
		Objects.requireNonNull(request, "request must not be null");

		// Calculate the link capacities for the incident.
		double fullLinkCapacity = request.getToLink().getCapacity() * FLOW_CAPACITY_FACTOR;
		double reducedLinkCapacity = fullLinkCapacity * (1 - request.getCapacityReduction());
		double initialReducedCapacity = reducedLinkCapacity;
		double linkCapacityGap = fullLinkCapacity - reducedLinkCapacity;


		changeEvent.addNetworkChangeEvent(String.valueOf(request.getSubmissionTime()), String.valueOf(request.getToLink().getId()), String.valueOf(initialReducedCapacity));
		changeEvent.addNetworkChangeEvent(String.valueOf(request.getEndTime()), String.valueOf(request.getToLink().getId()), String.valueOf(fullLinkCapacity));

		changeEvent.saveToFile(scenario);


		// Add incident network change event to LOG.
		incidentNCE.addEventToLog(request.getToLink(), reducedLinkCapacity,
				fullLinkCapacity, request.getSubmissionTime(), request.getEndTime(), request);

		// Update the schedules of the closest vehicles and add an IMT network change event.
		double endTime = request.getEndTime();
		int respondingIMTs = request.getTotalIMTs();
		List<DvrpVehicle> closestVehicles = closestVehicleFinder.getClosestVehicles(request.getToLink(), respondingIMTs);
		int numIMT = 0; // Initialize the counter variable to 1
		for (DvrpVehicle imtUnit : closestVehicles) {
			numIMT+= 1; // Increase numIMT by 1
			Schedule schedule = imtUnit.getSchedule();
			ScheduleUpdater updater = new ScheduleUpdater(router, travelTime, timer);
			double currLinkCapacity = reducedLinkCapacity + (linkCapacityGap * LINK_CAPACITY_RESTORE_INTERVAL);
			linkCapacityGap = fullLinkCapacity - currLinkCapacity;
			reducedLinkCapacity = fullLinkCapacity - linkCapacityGap;
			double arrivalTime = updater.updateScheduleForVehicle(schedule, request.getToLink(), endTime, request, imtUnit);

			if (arrivalTime < endTime) {
				ImtNetworkChangeEventGenerator event = new ImtNetworkChangeEventGenerator(scenario,
						currLinkCapacity, request, arrivalTime);
				event.addEventToLog(fullLinkCapacity, initialReducedCapacity, imtUnit);


				changeEvent.addNetworkChangeEvent(String.valueOf(arrivalTime), String.valueOf(request.getToLink().getId()), String.valueOf(currLinkCapacity));

				changeEvent.saveToFile(scenario);

			} else {
				// Log IMT information
				EventHandler_IMT.handleLateImtArrival(request, arrivalTime, imtUnit);
			}
			request.setNumIMT(numIMT);
		}
	}
}
