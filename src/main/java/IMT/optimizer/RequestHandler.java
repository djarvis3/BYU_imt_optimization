package IMT.optimizer;

import IMT.ImtConfigGroup;
import IMT.ImtRequest;
import IMT.events.IncidentEvent;
import IMT.logs.ChangeEvents_Log;
import IMT.logs.IMT_Log;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.fleet.Fleet;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.mobsim.framework.MobsimTimer;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelTime;

import java.util.List;
import java.util.Objects;

/**
 * Handles IMT requests and updates vehicle schedules accordingly.
 * Utilizes the closest vehicles to an incident and schedules them accordingly
 * to handle the incident, managing their capacities and arrival times.
 */
public class RequestHandler {
	private final double linkCapacityRestoreInterval;

	private final LeastCostPathCalculator router;
	private final TravelTime travelTime;
	private final MobsimTimer timer;
	private final ClosestVehicleFinder closestVehicleFinder;
	private final ChangeEvents_Log incLOG;
	private final EventsManager events;
	private final ScheduleUpdater scheduleUpdater;


	/**
	 * Constructs a RequestHandler object with the specified fleet, router, travel time, timer, scenario, and events manager.
	 *
	 * @param fleet     the fleet of vehicles
	 * @param router    the least cost path calculator
	 * @param travelTime the travel time estimator
	 * @param timer     the simulation timer
	 * @param scenario  the scenario for configuration
	 * @param events    the events manager for handling events
	 */
	public RequestHandler(Fleet fleet, LeastCostPathCalculator router, TravelTime travelTime, MobsimTimer timer, Scenario scenario, EventsManager events) {
		Objects.requireNonNull(fleet, "fleet must not be null");
		Objects.requireNonNull(scenario, "scenario must not be null");
		ImtConfigGroup imtConfig = (ImtConfigGroup) scenario.getConfig().getModules().get(ImtConfigGroup.GROUP_NAME);


		this.router = Objects.requireNonNull(router, "router must not be null");
		this.travelTime = Objects.requireNonNull(travelTime, "travelTime must not be null");
		this.timer = Objects.requireNonNull(timer, "timer must not be null");
		this.events = Objects.requireNonNull(events, "events must not be null");
		this.closestVehicleFinder = new ClosestVehicleFinder(fleet, router, travelTime);
		this.incLOG = new ChangeEvents_Log(scenario);
		this.scheduleUpdater = new ScheduleUpdater();
		this.linkCapacityRestoreInterval = imtConfig.getLinkCapacityRestoreInterval();

	}


	/**
	 * Handles an IMT request and updates vehicle schedules accordingly.
	 *
	 * @param req the IMT request to be handled
	 * @throws NullPointerException if the req is null
	 */
	public void handleRequest(ImtRequest req) {
		Objects.requireNonNull(req, "req must not be null");

		double fullLinkCapacity = req.getLinkCap_Full();
		double reducedLinkCapacity = req.getLinkCap_Reduced();
		double linkCapacityGap = fullLinkCapacity - reducedLinkCapacity;

		processIncidentEvent(req);
		incLOG.addEventToLog(req.getIncLink(), reducedLinkCapacity, fullLinkCapacity, req.getSubmissionTime(), req.getEndTime(), req);

		List<DvrpVehicle> closestVehicles = closestVehicleFinder.getClosestVehicles(req.getIncLink(), req.getTotalIMTs(), req.getSubmissionTime());

		int numIMT = 0;
		for (DvrpVehicle imtUnit : closestVehicles) {
			double currLinkCapacity = updateLinkCapacity(reducedLinkCapacity, linkCapacityGap);
			linkCapacityGap = fullLinkCapacity - currLinkCapacity;
			reducedLinkCapacity = fullLinkCapacity - linkCapacityGap;
			req.setLinkCap_Current(currLinkCapacity);

			scheduleUpdater.updateScheduleForVehicle(imtUnit.getSchedule(), req, imtUnit, numIMT, router, travelTime, timer, events);
			double arrivalTime = scheduleUpdater.getArrivalTime();

			numIMT += 1;
			handleLateArrival(req, arrivalTime, imtUnit, numIMT);
		}
	}

	private void processIncidentEvent(ImtRequest req) {
		IncidentEvent incidentEvent = new IncidentEvent(req);
		events.processEvent(incidentEvent);
	}

	private double updateLinkCapacity(double reducedLinkCapacity, double linkCapacityGap) {
		return reducedLinkCapacity + (linkCapacityGap * linkCapacityRestoreInterval);
	}

	private void handleLateArrival(ImtRequest req, double arrivalTime, DvrpVehicle imtUnit, int numIMT) {
		if (arrivalTime > req.getEndTime()) {
			IMT_Log.handleLateImtArrival(req, arrivalTime, imtUnit);
		}
		req.setNumIMT(numIMT);
	}
}
