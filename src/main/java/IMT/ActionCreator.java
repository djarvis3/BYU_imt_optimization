package IMT;

import IMT.optimizer.Optimizer;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.schedule.Task;
import org.matsim.contrib.dvrp.vrpagent.VrpAgentLogic;
import org.matsim.contrib.dvrp.vrpagent.VrpLegFactory;
import org.matsim.contrib.dynagent.DynAction;
import org.matsim.contrib.dynagent.DynAgent;
import org.matsim.contrib.dynagent.IdleDynActivity;
import org.matsim.core.mobsim.framework.MobsimTimer;

import com.google.inject.Inject;


/**
 * Creates dynamic actions for VRP agents based on their current tasks.
 */
public final class ActionCreator implements VrpAgentLogic.DynActionCreator {
	private final MobsimTimer timer;

	/**
	 * Constructs an ActionCreator object with the specified timer.
	 *
	 * @param timer the simulation timer
	 */
	@Inject
	public ActionCreator(MobsimTimer timer) {
		this.timer = timer;
	}

	/**
	 * Creates a dynamic action based on the current task of the VRP agent.
	 *
	 * @param dynAgent the dynamic agent
	 * @param vehicle  the vehicle associated with the agent
	 * @param now      the current simulation time
	 * @return the created dynamic action
	 */
	@Override
	public DynAction createAction(DynAgent dynAgent, DvrpVehicle vehicle, double now) {
		Task task = vehicle.getSchedule().getCurrentTask();
		return switch ((Optimizer.ImtTaskType) task.getTaskType()) {
			case DRIVE_TO_INCIDENT -> VrpLegFactory.createWithOfflineTracker(TransportMode.truck, vehicle, timer);

			case ARRIVE, INCIDENT_MANAGEMENT, DEPART, WAIT ->
					new IdleDynActivity(task.getTaskType() + "", task::getEndTime);
		};
	}
}
