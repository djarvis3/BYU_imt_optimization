/**

 The Incident class represents an incident that occurs on a particular link in a transportation network.
 It stores information about the link, incident, responding IMTs, start time, end time, and capacity reduction.
 */

package incidents;

public class Incident {
	private String linkId;
	private String incID;
	private Integer respondingIMTs;
	private Integer start;
	private Integer end;
	private Double reduction;

	public Incident(String linkId, String incID, Integer respondingIMTs, int start, int end, Double reduction) {
		this.linkId = linkId;
		this.incID = incID;
		this.respondingIMTs = respondingIMTs;
		this.start = start;
		this.end = end;
		this.reduction = reduction;
	}

	// getters and setters for the fields
	public String getLinkId() {
		return linkId;
	}

	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}

	public String getIncID() {
		return incID;
	}

	public void setIncID(String incID) {
		this.incID = incID;
	}

	public Integer getRespondingIMTs() {
		return respondingIMTs;
	}

	public void setRespondingIMTs(Integer respondingIMTs) {
		this.respondingIMTs = respondingIMTs;
	}

	public Integer getStartTime() {
		return start;
	}

	public void setStartTime(Integer start) {
		this.start = start;
	}

	public Integer getEndTime() {
		return end;
	}

	public void setEndTime(Integer end) {
		this.end = end;
	}

	public Double getCapacityReduction() {
		return reduction;
	}

	public void setCapacityReduction(Double reduction) {
		this.reduction = reduction;
	}
}
