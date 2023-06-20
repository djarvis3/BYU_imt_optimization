package IMT;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.optimizer.Request;

/**
 * Represents an IMT (Incident Management Team) request.
 */
public final class ImtRequest implements Request {
	private final Id<Request> id;
	private final double startTime;
	private final Link incLink;
	private final double capReducPercent;
	private final double linkCap_Reduced;
	private final double endTime;
	private final double linkCap_Full;
	private final int totalIMTs;
	private int numIMT;
	private double linkCap_Current;

	/**
	 * Creates a new IMT request.
	 *
	 * @param id                 The request ID.
	 * @param startTime          The start time of the request.
	 * @param incLink            The link associated with the incident.
	 * @param capacityReduction  The percentage of capacity reduction.
	 * @param linkCap_Reduced    The reduced link capacity.
	 * @param endTime            The end time of the request.
	 * @param linkCap_Full       The full link capacity.
	 * @param totalIMTs          The total number of IMTs required.
	 */
	public ImtRequest(Id<Request> id, double startTime, Link incLink, double capacityReduction, double linkCap_Reduced, double endTime, double linkCap_Full, int totalIMTs) {
		this.id = id;
		this.startTime = startTime;
		this.incLink = incLink;
		this.capReducPercent = capacityReduction;
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

	/**
	 * Gets the link associated with the incident.
	 *
	 * @return The incident link.
	 */
	public Link getIncLink() {
		return incLink;
	}

	/**
	 * Gets the capacity reduction percentage.
	 *
	 * @return The capacity reduction percentage.
	 */
	public double getCapReducPercent() {
		return capReducPercent;
	}

	/**
	 * Gets the reduced link capacity.
	 *
	 * @return The reduced link capacity.
	 */
	public double getLinkCap_Reduced() {
		return linkCap_Reduced;
	}

	/**
	 * Gets the end time of the request.
	 *
	 * @return The end time of the request.
	 */
	public double getEndTime() {
		return endTime;
	}

	/**
	 * Gets the full link capacity.
	 *
	 * @return The full link capacity.
	 */
	public double getLinkCap_Full() {
		return linkCap_Full;
	}

	/**
	 * Gets the total number of IMTs required.
	 *
	 * @return The total number of IMTs.
	 */
	public int getTotalIMTs() {
		return totalIMTs;
	}

	/**
	 * Gets the number of IMTs assigned to the request.
	 *
	 * @return The number of assigned IMTs.
	 */
	public int getNumIMT() {
		return numIMT;
	}

	/**
	 * Sets the number of IMTs assigned to the request.
	 *
	 * @param numIMT The number of assigned IMTs.
	 */
	public void setNumIMT(int numIMT) {
		this.numIMT = numIMT;
	}

	/**
	 * Gets the current link capacity.
	 *
	 * @return The current link capacity.
	 */
	public double getLinkCap_Current() {
		return linkCap_Current;
	}

	/**
	 * Sets the current link capacity.
	 *
	 * @param linkCap_Current The current link capacity.
	 */
	public void setLinkCap_Current(double linkCap_Current) {
		this.linkCap_Current = linkCap_Current;
	}
}
