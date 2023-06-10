/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package IMT;


import IMT.optimizer.Optimizer;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.schedule.DefaultStayTask;

/**
 * The ServeTask class represents a task for serving a request.
 *
 * This class extends the DefaultStayTask class and adds a reference to the Request object being served.
 */
public class ServeTask extends DefaultStayTask {
	/**
	 * The request being served by this task.
	 */
	private final Request request;

	/**
	 * Creates a new ServeTask object with the given task type, time bounds, link, and request.
	 *
	 * @param taskType  the type of task.
	 * @param beginTime the begin time of the task.
	 * @param endTime   the end time of the task.
	 * @param link      the link associated with the task.
	 * @param request   the request being served by the task.
	 */
	public ServeTask(Optimizer.ImtTaskType taskType, double beginTime, double endTime, Link link,
					 Request request) {
		super(taskType, beginTime, endTime, link);
		this.request = request;
	}

	/**
	 * Returns the request being served by this task.
	 *
	 * @return the request being served by the task.
	 */
	public Request getRequest() {
		return request;
	}
}
