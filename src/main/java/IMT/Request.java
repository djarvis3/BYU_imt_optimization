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
	private final double endTime;
	private final double capacityReduction;
	private final int totalIMTs;
	private int numIMT;

	// Define a private final variable toLink of type Link
	private final Link toLink;

	// Constructor that takes the input parameters id, toLink, submissionTime, endTime, capacityReduction,
	// and totalIMTs and assigns them to the corresponding private final variables
	public Request(Id<org.matsim.contrib.dvrp.optimizer.Request> id, Link toLink, double submissionTime,
				   double endTime, double capacityReduction, int respondingIMTs) {
		this.id = id;
		this.toLink = toLink;
		this.submissionTime = submissionTime;
		this.endTime = endTime;
		this.capacityReduction = capacityReduction;
		this.totalIMTs = respondingIMTs;
	}

	// Implement the getId method defined in the Request interface and return the value of the id variable
	@Override
	public Id<org.matsim.contrib.dvrp.optimizer.Request> getId() {
		return id;
	}

	// Implement the getSubmissionTime method defined in the Request interface and
	// return the value of the submissionTime variable
	@Override
	public double getSubmissionTime() {
		return submissionTime;
	}

	// Create a getter method called getEndTime that returns the value of the endTime variable
	public double getEndTime() {
		return endTime;
	}

	// Create a getter method called getToLink that returns the value of the toLink variable
	public Link getToLink() {
		return toLink;
	}

	// Create a getter method called getCapacityReduction that returns the value of the capacityReduction variable
	public double getCapacityReduction() {
		return capacityReduction;
	}

	// Create a getter method called getTotalIMTs that returns the value of the totalIMTs variable
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
