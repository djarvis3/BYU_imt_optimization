package IMT.optimizer;

import IMT.ImtRequest;
import IMT.ServeTask;
import IMT.events.ImtEvent;
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


/**
 * Updates the schedule for a vehicle based on an IMT request.
 */

public class ScheduleUpdater {
	private double arrivalTime;
	private Link incLink;

	public ScheduleUpdater() {

	}

	public void updateScheduleForVehicle(Schedule schedule, ImtRequest req, DvrpVehicle imtUnit, Integer numImt, LeastCostPathCalculator router, TravelTime travelTime, MobsimTimer timer, EventsManager events) {
		Objects.requireNonNull(schedule, "schedule must not be null");
		Objects.requireNonNull(req, "req must not be null");
		Objects.requireNonNull(imtUnit, "imtUnit must not be null");
		Objects.requireNonNull(numImt, "num imt ust not be null");
		Objects.requireNonNull(router, "router must not be null");
		Objects.requireNonNull(travelTime, "travelTime must not be null");
		Objects.requireNonNull(timer, "MobsimTimer must not be null");
		Objects.requireNonNull(events, "Events must not be null");

		handleLastTask(schedule, timer);
		updateScheduleBasedOnRequest(schedule, req, imtUnit, router, travelTime, timer, events, numImt);
	}

	private void handleLastTask(Schedule schedule, MobsimTimer timer) {
		StayTask lastTask = (StayTask) Schedules.getLastTask(schedule);
		double currentTime = timer.getTimeOfDay();
		switch (lastTask.getStatus()) {
			case PLANNED -> schedule.removeLastTask();
			case STARTED -> lastTask.setEndTime(currentTime);
			default -> throw new IllegalArgumentException("Unexpected last task status: " + lastTask.getStatus());
		}
	}

	private void updateScheduleBasedOnRequest(Schedule schedule, ImtRequest req, DvrpVehicle imtUnit, LeastCostPathCalculator router, TravelTime travelTime, MobsimTimer timer, EventsManager events, Integer numImt) {
		double t0 = calculateInitialTime(schedule, imtUnit, timer);
		createDriveAndArrivalTasks(schedule, req, imtUnit, t0, router, travelTime, events, numImt);
		createIncidentManagementAndStayTasks(schedule, req, imtUnit);
	}

	private double calculateInitialTime(Schedule schedule, DvrpVehicle imtUnit, MobsimTimer timer) {
		return schedule.getStatus() == Schedule.ScheduleStatus.UNPLANNED ?
				Math.max(imtUnit.getServiceBeginTime(), timer.getTimeOfDay()) :
				Schedules.getLastTask(schedule).getEndTime();
	}

	private void createDriveAndArrivalTasks(Schedule schedule, ImtRequest req, DvrpVehicle imtUnit, double t0, LeastCostPathCalculator router, TravelTime travelTime, EventsManager events, Integer numImt) {
		StayTask lastTask = (StayTask) Schedules.getLastTask(schedule);
		this.incLink = req.getIncLink();
		double currentCapacity = req.getLinkCap_Current();
		double endTime = req.getEndTime();

		VrpPathWithTravelData pathToIncident = VrpPaths.calcAndCreatePath(lastTask.getLink(), incLink, t0, router, travelTime);
		this.arrivalTime = pathToIncident.getArrivalTime();
		ImtEvent arrivalEvent = new ImtEvent(req.getSubmissionTime(), (arrivalTime+numImt), incLink.getId(), currentCapacity, endTime);

		schedule.addTask(new DefaultDriveTask(Optimizer.ImtTaskType.DRIVE_TO_INCIDENT, pathToIncident));
		schedule.addTask(new ServeTask(Optimizer.ImtTaskType.ARRIVE, arrivalTime, arrivalTime, incLink, req));

		events.processEvent(arrivalEvent);
		IMT_Log.logImtArrival(req, req.getLinkCap_Full(), req.getLinkCap_Reduced(), req.getLinkCap_Current(), arrivalTime, imtUnit);
	}

	private void createIncidentManagementAndStayTasks(Schedule schedule, ImtRequest req, DvrpVehicle imtUnit) {
		double endTime = req.getEndTime();
		if (arrivalTime < endTime) {
			schedule.addTask(new ServeTask(Optimizer.ImtTaskType.INCIDENT_MANAGEMENT, arrivalTime, endTime, incLink, req));
			addPostIncidentStayTask(schedule, imtUnit, endTime, incLink);
		} else {
			schedule.addTask(new ServeTask(Optimizer.ImtTaskType.INCIDENT_MANAGEMENT, arrivalTime, arrivalTime, incLink, req));
			if (arrivalTime < imtUnit.getServiceEndTime()) {
				schedule.addTask(new DefaultStayTask(Optimizer.ImtTaskType.WAIT, arrivalTime, imtUnit.getServiceEndTime(), incLink));
			} else {
				schedule.addTask(new DefaultStayTask(Optimizer.ImtTaskType.WAIT, arrivalTime, arrivalTime, incLink));
			}
		}
	}

	private void addPostIncidentStayTask(Schedule schedule, DvrpVehicle imtUnit, double endTime, Link incLink) {
		if (endTime < imtUnit.getServiceEndTime()) {
			schedule.addTask(new DefaultStayTask(Optimizer.ImtTaskType.WAIT, endTime, imtUnit.getServiceEndTime(), incLink));
		} else if (endTime >= imtUnit.getServiceEndTime()) {
			schedule.addTask(new DefaultStayTask(Optimizer.ImtTaskType.WAIT, endTime, endTime, incLink));
		}
	}

	/**
	 * Gets the arrival time calculated during the schedule update.
	 *
	 * @return the arrival time
	 */
	public double getArrivalTime() {
		return arrivalTime;
	}
}
