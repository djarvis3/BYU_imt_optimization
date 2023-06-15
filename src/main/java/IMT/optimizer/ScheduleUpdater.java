package IMT.optimizer;

import IMT.ImtRequest;
import IMT.ServeTask;
import IMT.events.IncidentEvent;
import IMT.logs.IMT_Log;

import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.path.VrpPathWithTravelData;
import org.matsim.contrib.dvrp.path.VrpPaths;
import org.matsim.contrib.dvrp.schedule.*;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.mobsim.framework.MobsimTimer;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelTime;

import java.util.Objects;

public class ScheduleUpdater {
	private final LeastCostPathCalculator router;
	private final TravelTime travelTime;
	private final MobsimTimer timer;
	private final EventsManager events;
	private double arrivalTime;

	public ScheduleUpdater(LeastCostPathCalculator router, TravelTime travelTime, MobsimTimer timer, EventsManager events) {
		this.router = Objects.requireNonNull(router, "router must not be null");
		this.travelTime = Objects.requireNonNull(travelTime, "travelTime must not be null");
		this.timer = Objects.requireNonNull(timer, "timer must not be null");
		this.events = Objects.requireNonNull(events, "events must not be null");  // add this

	}

	public void updateScheduleForVehicle(Schedule schedule, ImtRequest req, DvrpVehicle imtUnit) {

		Objects.requireNonNull(schedule, "schedule must not be null");
		Objects.requireNonNull(req, "req must not be null");
		Objects.requireNonNull(imtUnit, "imtUnit must not be null");

		StayTask lastTask = (StayTask) Schedules.getLastTask(schedule);

		Link incLink = req.getIncLink();
		double currentTime = timer.getTimeOfDay();
		double reducedCapacity = req.getLinkCap_Reduced();
		double endTime = req.getEndTime();
		double fullCapacity = req.getLinkCap_Full();

		switch (lastTask.getStatus()) {
			case PLANNED -> schedule.removeLastTask();
			case STARTED -> lastTask.setEndTime(currentTime);
			default -> throw new IllegalArgumentException("Unexpected last task status: " + lastTask.getStatus());
		}

		double t0 = schedule.getStatus() == Schedule.ScheduleStatus.UNPLANNED ?
				Math.max(imtUnit.getServiceBeginTime(), currentTime) :
				Schedules.getLastTask(schedule).getEndTime();

		IncidentEvent incidentEvent = new IncidentEvent(req);
		events.processEvent(incidentEvent);

		VrpPathWithTravelData pathToIncident = VrpPaths.calcAndCreatePath(lastTask.getLink(), incLink, t0, router, travelTime);
		this.arrivalTime = pathToIncident.getArrivalTime();

		schedule.addTask(new DefaultDriveTask(Optimizer.ImtTaskType.DRIVE_TO_INCIDENT, pathToIncident));

		schedule.addTask(new ServeTask(Optimizer.ImtTaskType.ARRIVE, arrivalTime, arrivalTime, incLink, req));

		IMT_Log.logImtArrival(req,fullCapacity,reducedCapacity,req.getLinkCap_Current(),arrivalTime,imtUnit);

		if (arrivalTime < endTime) {
			schedule.addTask(new ServeTask(Optimizer.ImtTaskType.INCIDENT_MANAGEMENT, arrivalTime, endTime, incLink, req));
			schedule.addTask(new DefaultStayTask(Optimizer.ImtTaskType.WAIT, endTime, imtUnit.getServiceEndTime(), incLink));
		}
		else {
			schedule.addTask(new ServeTask(Optimizer.ImtTaskType.INCIDENT_MANAGEMENT, arrivalTime, arrivalTime, incLink, req));
			schedule.addTask(new DefaultStayTask(Optimizer.ImtTaskType.WAIT, arrivalTime, imtUnit.getServiceEndTime(), incLink));
		}
	}

	public double getArrivalTime() {
		return arrivalTime;
	}
}
