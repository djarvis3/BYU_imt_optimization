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

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;

/**
 * @author michalm
 */
public final class Request implements org.matsim.contrib.dvrp.optimizer.Request {
	// Define private final variables id, submissionTime, endTime, capacityReduction, totalIMTs, and numIMT
	private final Id<org.matsim.contrib.dvrp.optimizer.Request> id;
	private final double submissionTime;
	private final Link toLink;
	private final double capacityReduction_percentage;
	private final double reducedCapacity_link;
	private final double endTime;
	private final double fullCapacity_link;
	private final int totalIMTs;
	private int numIMT;


	// Constructor
	public Request(Id<org.matsim.contrib.dvrp.optimizer.Request> id, double submissionTime, Link toLink, double capacityReduction_percentage, double reducedCapacity_link, double endTime, double fullCapacity_link, int totalIMTs) {
		this.id = id;
		this.submissionTime = submissionTime;
		this.toLink = toLink;
		this.capacityReduction_percentage = capacityReduction_percentage;
		this.reducedCapacity_link = reducedCapacity_link;
		this.endTime = endTime;
		this.fullCapacity_link = fullCapacity_link;
		this.totalIMTs = totalIMTs;
	}

	// Implement the getId method defined in the Request interface and return the value of the id variable
	@Override
	public Id<org.matsim.contrib.dvrp.optimizer.Request> getId() {
		return id;
	}

	@Override
	public double getSubmissionTime() {
		return submissionTime;
	}

	public Link getToLink() {
		return toLink;
	}
	public double getCapacityReduction_percentage() {
		return capacityReduction_percentage;
	}

	public double getReducedCapacity_link(){return reducedCapacity_link;}

	public double getEndTime() {
		return endTime;
	}

	public double getFullCapacity_link(){ return fullCapacity_link;}

	public int getTotalIMTs() {
		return totalIMTs;
	}

	public int getNumIMT() {
		return numIMT;
	}

	public void setNumIMT(int numIMT) {
		this.numIMT = numIMT;
	}
}
