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

package byu.IMT;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.optimizer.Request;

/**
 * @author michalm
 */
public final class ImtRequest implements Request {
	private final Id<Request> id;
	private final double submissionTime; // Submission time = Incident Start Time
	private final double endTime;
	private final double capacityReduction;
	private final int respondingIMTs;

	private final Link toLink;

	public ImtRequest(Id<Request> id, Link toLink, double submissionTime, double endTime, double capacityReduction, int respondingIMTs) {
		this.id = id;
		this.toLink = toLink;
		this.submissionTime = submissionTime;
		this.endTime = endTime;
		this.capacityReduction = capacityReduction;
		this.respondingIMTs = respondingIMTs;

	}

	@Override
	public Id<Request> getId() {
		return id;
	}

	@Override
	public double getSubmissionTime() {
		return submissionTime;
	}

	public double getEndTime() {
		return endTime;
	}

	public Link getToLink() {
		return toLink;
	}

	public double getCapacityReduction(){return capacityReduction;}

	public int getRespondingIMTs() {
		return respondingIMTs;
	}
}
