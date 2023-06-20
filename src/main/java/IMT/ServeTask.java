package IMT;

import static IMT.optimizer.Optimizer.ImtTaskType;

import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.schedule.DefaultStayTask;

/**
 * The ServeTask class represents a task for serving an incident during the simulation.
 */
public class ServeTask extends DefaultStayTask {
	private final ImtRequest imtRequest;

	/**
	 * Creates a ServeTask instance.
	 * @param taskType The task type.
	 * @param beginTime The beginning time of the task.
	 * @param endTime The end time of the task.
	 * @param link The link associated with the task.
	 * @param imtRequest The IMT request associated with the task.
	 */
	public ServeTask(ImtTaskType taskType, double beginTime, double endTime, Link link, ImtRequest imtRequest) {
		super(taskType, beginTime, endTime, link);
		this.imtRequest = imtRequest;
	}

	/**
	 * Gets the IMT request associated with the task.
	 * @return The IMT request.
	 */
	public ImtRequest getRequest() {
		return imtRequest;
	}
}

