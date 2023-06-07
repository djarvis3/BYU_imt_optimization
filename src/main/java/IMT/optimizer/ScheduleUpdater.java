package IMT.optimizer;

import IMT.Request;
import IMT.ServeTask;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.path.VrpPathWithTravelData;
import org.matsim.contrib.dvrp.path.VrpPaths;
import org.matsim.contrib.dvrp.schedule.*;
import org.matsim.core.mobsim.framework.MobsimTimer;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelTime;




import java.util.Objects;

/**
 * This class updates the schedule for the closest vehicles to an incident by adding tasks for driving to the incident,
 * serving the incident, waiting, and updating the link capacity. It uses the closest vehicle finder, the least cost path
 * calculator, travel time, and a mobsim timer to calculate the tasks and their timings.
 */
public class ScheduleUpdater {
	private final LeastCostPathCalculator router;
	private final TravelTime travelTime;
	private final MobsimTimer timer;

	/**
	 * Constructs a new ScheduleUpdater object with the given the closest vehicle finder, the least cost path calculator,
	 * travel time, and mobsim timer.
	 *
	 * @throws NullPointerException if any of the arguments are null
	 */
	public ScheduleUpdater(LeastCostPathCalculator router, TravelTime travelTime, MobsimTimer timer) {
		this.router = Objects.requireNonNull(router, "router must not be null");
		this.travelTime = Objects.requireNonNull(travelTime, "travelTime must not be null");
		this.timer = Objects.requireNonNull(timer, "timer must not be null");

	}


	/**
	 * Updates the schedule for every responding vehicle by adding tasks for driving to the incident, serving the
	 * incident, and waiting.
	 *
	 * @throws NullPointerException if any of the arguments are null
	 * @throws IllegalArgumentException if the last task status is unexpected
	 */
	public double updateScheduleForVehicle(Schedule schedule, Link toLink, double endTime, Request request, DvrpVehicle imtUnit) {
		Objects.requireNonNull(schedule, "schedule must not be null");
		Objects.requireNonNull(request, "request must not be null");
		Objects.requireNonNull(imtUnit, "imtUnit must not be null");

		StayTask lastTask = (StayTask) Schedules.getLastTask(schedule);
		double currentTime = timer.getTimeOfDay();

		switch (lastTask.getStatus()) {
			case PLANNED -> schedule.removeLastTask();
			case STARTED -> lastTask.setEndTime(currentTime);
			default -> throw new IllegalArgumentException("Unexpected last task status: " + lastTask.getStatus());
		}

		double startTime = schedule.getStatus() == Schedule.ScheduleStatus.UNPLANNED ?
				Math.max(imtUnit.getServiceBeginTime(), currentTime) :
				Schedules.getLastTask(schedule).getEndTime();

		VrpPathWithTravelData pathToIncident = VrpPaths.calcAndCreatePath(lastTask.getLink(), toLink, startTime,
				router, travelTime);
		double arrivalTime = pathToIncident.getArrivalTime();
		schedule.addTask(new DefaultDriveTask(Optimizer.ImtTaskType.DRIVE_TO_INCIDENT, pathToIncident));
		schedule.addTask(new ServeTask(Optimizer.ImtTaskType.ARRIVE, arrivalTime, arrivalTime, toLink, request));

		if (arrivalTime < endTime) {
			schedule.addTask(new ServeTask(Optimizer.ImtTaskType.INCIDENT_MANAGEMENT, arrivalTime, endTime,
					toLink, request));
			schedule.addTask(new DefaultStayTask(Optimizer.ImtTaskType.WAIT, endTime,
					Double.POSITIVE_INFINITY, toLink));
		}
		else {
			schedule.addTask(new ServeTask(Optimizer.ImtTaskType.INCIDENT_MANAGEMENT, arrivalTime, arrivalTime,
					toLink, request));
			schedule.addTask(new DefaultStayTask(Optimizer.ImtTaskType.WAIT, arrivalTime,
					Double.POSITIVE_INFINITY, toLink));

		}

		return arrivalTime;
	}

}
