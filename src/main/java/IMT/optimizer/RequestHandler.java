package IMT.optimizer;

import IMT.ImtRequest;
import IMT.logs.IMT_Log;
import IMT.logs.ChangeEvents_Log;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.fleet.Fleet;
import org.matsim.contrib.dvrp.schedule.*;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.mobsim.framework.MobsimTimer;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelTime;

import java.util.List;
import java.util.Objects;

public class RequestHandler {

	private static final double LINK_CAPACITY_RESTORE_INTERVAL = 0.25;

	private final LeastCostPathCalculator router;
	private final TravelTime travelTime;
	private final MobsimTimer timer;
	private final ClosestVehicleFinder closestVehicleFinder;
	private final ChangeEvents_Log incLOG;
	private final EventsManager events;

	public RequestHandler(Fleet fleet, LeastCostPathCalculator router, TravelTime travelTime, MobsimTimer timer, Scenario scenario, EventsManager events) {
		Objects.requireNonNull(fleet, "fleet must not be null");
		Objects.requireNonNull(scenario, "scenario must not be null");

		this.router = Objects.requireNonNull(router, "router must not be null");
		this.travelTime = Objects.requireNonNull(travelTime, "travelTime must not be null");
		this.timer = Objects.requireNonNull(timer, "timer must not be null");
		this.events = Objects.requireNonNull(events, "events must not be null");
		this.closestVehicleFinder = new ClosestVehicleFinder(fleet, router, travelTime);
		this.incLOG = new ChangeEvents_Log(scenario);
	}

	public void handleRequest(ImtRequest req) {

		Objects.requireNonNull(req, "req must not be null");

		double fullLinkCapacity = req.getLinkCap_Full();
		double reducedLinkCapacity = req.getLinkCap_Reduced();
		double linkCapacityGap = fullLinkCapacity - reducedLinkCapacity;
		double currLinkCapacity;
		double endTime = req.getEndTime();
		int respondingIMTs = req.getTotalIMTs();
		int numIMT = 0;

		incLOG.addEventToLog(req.getIncLink(), reducedLinkCapacity, fullLinkCapacity, req.getSubmissionTime(), req.getEndTime(), req);

		List<DvrpVehicle> closestVehicles = closestVehicleFinder.getClosestVehicles(req.getIncLink(), respondingIMTs);

		for (DvrpVehicle imtUnit : closestVehicles) {
			Schedule schedule = imtUnit.getSchedule();
			ScheduleUpdater updater = new ScheduleUpdater(router, travelTime, timer, events);
			currLinkCapacity = reducedLinkCapacity + (linkCapacityGap * LINK_CAPACITY_RESTORE_INTERVAL);
			linkCapacityGap = fullLinkCapacity - currLinkCapacity;
			reducedLinkCapacity = fullLinkCapacity - linkCapacityGap;
			req.setLinkCap_Current(currLinkCapacity);

			updater.updateScheduleForVehicle(schedule, req, imtUnit);
			double arrivalTime = updater.getArrivalTime();

			numIMT += 1;

			if (arrivalTime > endTime) {IMT_Log.handleLateImtArrival(req, arrivalTime, imtUnit);}
			req.setNumIMT(numIMT);
		}
	}
}
