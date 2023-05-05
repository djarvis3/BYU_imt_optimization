/**

 The Incident class represents an incident that occurs on a particular link in a transportation network.
 It stores information about the link, incident, responding IMTs, start time, end time, and capacity reduction.
 */

package incidents;

public class Incident {
	private String linkId;
	private Integer incidentID;
	private Integer respondingIMTs;
	private Double start;
	private Double end;
	private Double reduction;

	public Incident(String linkId, Integer incidentID, Integer respondingIMTs, Double start, Double end, Double reduction) {
		this.linkId = linkId;
		this.incidentID = incidentID;
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

	public Integer getIncidentID() {
		return incidentID;
	}

	public void setIncidentID(Integer incidentID) {
		this.incidentID = incidentID;
	}

	public Integer getRespondingIMTs() {
		return respondingIMTs;
	}

	public void setRespondingIMTs(Integer respondingIMTs) {
		this.respondingIMTs = respondingIMTs;
	}

	public Double getStartTime() {
		return start;
	}

	public void setStartTime(Double start) {
		this.start = start;
	}

	public Double getEndTime() {
		return end;
	}

	public void setEndTime(Double end) {
		this.end = end;
	}

	public Double getCapacityReduction() {
		return reduction;
	}

	public void setCapacityReduction(Double reduction) {
		this.reduction = reduction;
	}
}
