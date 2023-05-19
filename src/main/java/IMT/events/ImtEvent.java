package IMT.events;

import org.matsim.api.core.v01.network.Link;

public class ImtEvent {
	private Link toLink;
	private Double arrivalTime;
	private Double restoredCapacity;

	public ImtEvent(Link toLink, Double arrivalTime, Double restoredCapacity){
		this.toLink = toLink;
		this.arrivalTime = arrivalTime;
		this.restoredCapacity = restoredCapacity;
	}

	// getters and setters for the fields
	public Link getToLink() {
		return toLink;
	}

	public void setLinkId(Link toLink) {
		this.toLink = toLink;
	}

	public Double getArriveTime() {return arrivalTime;}

	public void setArriveTime(Double arrivalTime) {this.arrivalTime = arrivalTime;}


	public Double getRestoredCapacity() {
		return restoredCapacity;
	}

	public void setRestoredCapacity(Double restoredCapacity) {this.restoredCapacity = restoredCapacity;}

}
