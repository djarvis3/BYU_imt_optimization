package IMT.optimizer;

import org.matsim.contrib.dvrp.schedule.Schedule;
import org.matsim.contrib.dvrp.schedule.Task;
import org.matsim.core.mobsim.framework.MobsimTimer;

import java.util.List;
import java.util.Objects;

/**
 * A class that updates the timings of a schedule based on the current time.
 */
public class TimingUpdater {

	private final MobsimTimer timer;

	/**
	 * Constructor for the TimingUpdater class.
	 *
	 * @param timer The timer used to retrieve the current time.
	 * @throws NullPointerException if timer is null.
	 */
	public TimingUpdater(MobsimTimer timer) {
		this.timer = Objects.requireNonNull(timer, "Timer cannot be null");
	}

	/**
	 * Updates the timings of a given schedule based on the current time.
	 *
	 * @param schedule The schedule whose timings are to be updated.
	 * @throws NullPointerException if schedule is null.
	 */
	public void updateTimings(Schedule schedule) {
		Objects.requireNonNull(schedule, "Schedule cannot be null");

		// If the schedule has not started yet, there is no need to update the timings.
		if (schedule.getStatus() != Schedule.ScheduleStatus.STARTED) {
			return;
		}

		// Retrieve the current time from the timer.
		double now = timer.getTimeOfDay();

		// Update the timings of each task in the schedule.
		List<? extends Task> tasks = schedule.getTasks();
		for (Task task : tasks) {
			switch (task.getStatus()) {
				case PLANNED:
					// If the task is planned, update its start time based on the time difference between the
					// current time and the task's end time.
					task.setBeginTime(task.getBeginTime() + (now - task.getEndTime()));
					break;
				case STARTED:
					// If the task is started, update its end time based on the time difference between the
					// current time and the task's end time.
					task.setEndTime(task.getEndTime() + (now - task.getEndTime()));
					break;
				case PERFORMED:
					// If the task is already performed, do nothing.
					break;
			}
		}
	}
}

