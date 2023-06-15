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


public final class ActionCreator implements VrpAgentLogic.DynActionCreator {
	private final MobsimTimer timer;

	@Inject
	public ActionCreator(MobsimTimer timer) {
		this.timer = timer;
	}

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
