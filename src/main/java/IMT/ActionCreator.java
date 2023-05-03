package IMT;

import IMT.optimizer.Optimizer;
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

// Make this class final to prevent it from being subclassed
public final class ActionCreator implements VrpAgentLogic.DynActionCreator {

	// Define a private final variable timer of type MobsimTimer
	private final MobsimTimer timer;

	// Constructor that takes a MobsimTimer object as input parameter and assigns it to the timer variable
	@Inject
	public ActionCreator(MobsimTimer timer) {
		this.timer = timer;
	}

	// Implement the createAction method defined in the VrpAgentLogic.DynActionCreator interface
	@Override
	public DynAction createAction(DynAgent dynAgent, DvrpVehicle vehicle, double now) {

		// Get the current task from the vehicle's schedule
		Task task = vehicle.getSchedule().getCurrentTask();

		// Return a DynAction object based on the type of the current task
		return switch ((Optimizer.ImtTaskType) task.getTaskType()) {

			// If the task type is DRIVE_TO_INCIDENT, return a VrpLegFactory object that creates a leg for an IMT with an offline tracker
			case DRIVE_TO_INCIDENT -> VrpLegFactory.createWithOfflineTracker(TransportMode.truck, vehicle, timer);

			// If the task type is ARRIVAL, INCIDENT_MANAGEMENT, DEPARTURE, or WAIT, return an IdleDynActivity object with the task type as its name and the task's end time as its end time
			case ARRIVE, INCIDENT_MANAGEMENT, DEPART, WAIT -> new IdleDynActivity(task.getTaskType() + "", task::getEndTime);
		};
	}
}
