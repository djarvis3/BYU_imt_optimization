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

		double currentTime = timer.getTimeOfDay();
		Task currentTask = schedule.getCurrentTask();
		double timeDifference = currentTime - currentTask.getEndTime();

		if (timeDifference == 0) {
			return;
		}

		currentTask.setEndTime(currentTime);

		List<? extends Task> tasks = schedule.getTasks();
		int nextTaskIndex = currentTask.getTaskIdx() + 1;

		// Update the timing for all tasks except the last waiting task
		for (int i = nextTaskIndex; i < tasks.size() - 1; i++) {
			Task task = tasks.get(i);
			task.setBeginTime(task.getBeginTime() + timeDifference);
			task.setEndTime(task.getEndTime() + timeDifference);
		}

		// Update the timing for the waiting task, considering the vehicle's service end time
		if (nextTaskIndex != tasks.size()) {
			Task waitTask = tasks.get(tasks.size() - 1);
			waitTask.setBeginTime(waitTask.getBeginTime() + timeDifference);

			double serviceEndTime = Math.max(waitTask.getBeginTime(), vehicle.getServiceEndTime());
			waitTask.setEndTime(serviceEndTime);
		}
	}
}
