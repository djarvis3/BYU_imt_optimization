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
import org.matsim.contrib.dvrp.optimizer.Request;

public final class ImtRequest implements org.matsim.contrib.dvrp.optimizer.Request {
	private final Id<org.matsim.contrib.dvrp.optimizer.Request> id;
	private final double startTime;
	private final Link incLink;
	private final double capReducPercent;
	private final double linkCap_Reduced;
	private final double endTime;
	private final double linkCap_Full;
	private final int totalIMTs;
	private int numIMT;
	private double linkCap_Current;

	public ImtRequest(Id<org.matsim.contrib.dvrp.optimizer.Request> id, double startTime, Link incLink, double capacityReduction_percentage, double linkCap_Reduced, double endTime, double linkCap_Full, int totalIMTs) {
		this.id = id;
		this.startTime = startTime;
		this.incLink = incLink;
		this.capReducPercent = capacityReduction_percentage;
		this.linkCap_Reduced = linkCap_Reduced;
		this.endTime = endTime;
		this.linkCap_Full = linkCap_Full;
		this.totalIMTs = totalIMTs;
	}

	@Override
	public Id<Request> getId() {
		return id;
	}

	@Override
	public double getSubmissionTime() {
		return startTime;
	}

	public Link getIncLink() {
		return incLink;
	}

	public double getCapReducPercent() {
		return capReducPercent;
	}

	public double getLinkCap_Reduced(){return linkCap_Reduced;}

	public double getEndTime() {
		return endTime;
	}

	public double getLinkCap_Full(){ return linkCap_Full;}

	public int getTotalIMTs() {
		return totalIMTs;
	}

	public int getNumIMT() {
		return numIMT;
	}

	public void setNumIMT(int numIMT) {
		this.numIMT = numIMT;
	}

	public double getLinkCap_Current(){
		return linkCap_Current;
	}

	public void setLinkCap_Current(double linkCap_Current) {
		this.linkCap_Current = linkCap_Current;
	}
}
