package byu.IMT;

import com.google.inject.Inject;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.schedule.Task;
import org.matsim.contrib.dvrp.vrpagent.VrpAgentLogic;
import org.matsim.contrib.dvrp.vrpagent.VrpLegFactory;
import org.matsim.contrib.dynagent.DynAction;
import org.matsim.contrib.dynagent.DynAgent;
import org.matsim.contrib.dynagent.IdleDynActivity;
import org.matsim.core.mobsim.framework.MobsimTimer;

final class ImtActionCreator implements VrpAgentLogic.DynActionCreator {

	// Define a private final variable timer of type MobsimTimer
	private final MobsimTimer timer;

	@Inject
	public ImtActionCreator(MobsimTimer timer) {
		this.timer = timer;
	}

	@Override
	public DynAction createAction(DynAgent dynAgent, DvrpVehicle vehicle, double now) {

		// Get the current task from the vehicle's schedule
		Task task = vehicle.getSchedule().getCurrentTask();

		// Return a DynAction object based on the type of the current task
		return switch ((ImtOptimizer.UtahImtTaskType) task.getTaskType()) {

			// If the task type is DRIVE_TO_INCIDENT, return a VrpLegFactory object that creates a leg for an IMT with an offline tracker
			case DRIVE_TO_INCIDENT -> VrpLegFactory.createWithOfflineTracker(TransportMode.truck, vehicle, timer);

			// If the task type is ARRIVAL, INCIDENT_MANAGEMENT, DEPARTURE, or WAIT, return an IdleDynActivity object with the task type as its name and the task's end time as its end time
			case ARRIVAL, INCIDENT_MANAGEMENT, DEPARTURE, WAIT -> new IdleDynActivity(task.getTaskType() + "", task::getEndTime);
		};
	}
}
