package IMT.optimizer;

import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.schedule.Schedule;
import org.matsim.contrib.dvrp.schedule.Task;
import org.matsim.core.mobsim.framework.MobsimTimer;

import java.util.List;
import java.util.Objects;

/**
 * Updates the timings of a schedule based on the current time.
 */
public class TimingUpdater {

	private final MobsimTimer timer;

	/**
	 * Constructs a TimingUpdater object with the specified timer.
	 *
	 * @param timer the simulation timer
	 */
	public TimingUpdater(MobsimTimer timer) {
		this.timer = Objects.requireNonNull(timer, "Timer cannot be null");
	}

	/**
	 * Updates the timings of a schedule based on the current time.
	 *
	 * @param schedule the schedule to update
	 * @param vehicle  the vehicle associated with the schedule
	 * @throws NullPointerException if the schedule is null
	 */
	public void updateTimings(Schedule schedule, DvrpVehicle vehicle) {
		Objects.requireNonNull(schedule, "Schedule cannot be null");

		if (schedule.getStatus() != Schedule.ScheduleStatus.STARTED) {
			return;
		}

		double now = timer.getTimeOfDay();
		Task currentTask = schedule.getCurrentTask();
		double diff = now - currentTask.getEndTime();

		if (diff == 0) {
			return;
		}

		currentTask.setEndTime(now);

		List<? extends Task> tasks = schedule.getTasks();
		int nextTaskIdx = currentTask.getTaskIdx() + 1;

		// All tasks except the last waiting task
		for (int i = nextTaskIdx; i < tasks.size() - 1; i++) {
			Task task = tasks.get(i);
			task.setBeginTime(task.getBeginTime() + diff);
			task.setEndTime(task.getEndTime() + diff);
		}

		// Waiting task
		if (nextTaskIdx != tasks.size()) {
			Task waitTask = tasks.get(tasks.size() - 1);
			waitTask.setBeginTime(waitTask.getBeginTime() + diff);

			double tEnd = Math.max(waitTask.getBeginTime(), vehicle.getServiceEndTime());
			waitTask.setEndTime(tEnd);
		}
	}
}
