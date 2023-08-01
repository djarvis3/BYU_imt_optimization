package IMT.optimizer;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;

public class ImtArrival {
	private double arrivalTime;
	private Id<Link> linkId;
	private double currentCapacity;
	private double endTime;

	public ImtArrival(double arrivalTime, Id<Link> linkId, double currentCapacity, double endTime){
		this.arrivalTime = arrivalTime;
		this.linkId = linkId;
		this.currentCapacity = currentCapacity;
	}

	public double getArrivalTime(){
		return arrivalTime;
	}

	public void setArrivalTime(double arrivalTime){
		this.arrivalTime = arrivalTime;
	}

	public Id<Link> getLinkId(){
		return linkId;
	}

	public void setLinkId(Id<Link> linkId){
		this.linkId = linkId;
	}

	public double getCurrentCapacity() {
		return currentCapacity;
	}

	public void setCurrentCapacity(double currentCapacity){
		this.currentCapacity = currentCapacity;
	}

	public double getEndTime() {
		return endTime;
	}

	public void setEndTime(double endTime){
		this.endTime = endTime;
	}
}
