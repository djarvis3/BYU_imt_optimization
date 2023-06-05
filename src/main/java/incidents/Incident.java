/**

 The Incident class represents an incident that occurs on a particular link in a transportation network.
 It stores information about the link, incident, responding IMTs, start time, end time, and capacity reduction.
 */

package incidents;

public class Incident {
	private Integer linkId;
	private String incidentID;
	private Integer respondingIMTs;
	private Integer start;
	private Integer end;
	private Double reduction;

	public Incident(int linkId, String incidentID, Integer respondingIMTs, int start, int end, Double reduction) {
		this.linkId = linkId;
		this.incidentID = incidentID;
		this.respondingIMTs = respondingIMTs;
		this.start = start;
		this.end = end;
		this.reduction = reduction;
	}

	// getters and setters for the fields
	public int getLinkId() {
		return linkId;
	}

	public void setLinkId(Integer linkId) {
		this.linkId = linkId;
	}

	public String getIncidentID() {
		return incidentID;
	}

	public void setIncidentID(String incidentID) {
		this.incidentID = incidentID;
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
