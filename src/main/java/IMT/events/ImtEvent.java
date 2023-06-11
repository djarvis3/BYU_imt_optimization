package IMT.events;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.network.Link;

import java.util.Map;

public class ImtEvent extends Event {
	private final Double arrivalTime;
	private final Id<Link> linkId;
	private final Double currentCapacity;
	private final Double endTime;

	public ImtEvent(double arrivalTime, Id<Link> linkId, double currentCapacity, double endTime)
	{
		super(arrivalTime);
		this.arrivalTime = arrivalTime;
		this.linkId = linkId;
		this.currentCapacity = currentCapacity;
		this.endTime = endTime;
	}

	public Double getArrivalTime(){ return  arrivalTime;}
	public Id<Link> getLinkId(){return  linkId;}
	public Double getCurrentCapacity(){return  currentCapacity;}
	public Double getEndTime(){return endTime;}

	@Override
	public String getEventType() {
		return "imt";
	}

	@Override
	public Map<String, String> getAttributes() {
		Map<String, String> attr = super.getAttributes();
		if(arrivalTime>endTime) {attr.put("actType", "LATE_ARRIVAL");}
		else {attr.put("actType", "ARRIVE");}
		attr.put("linkId", this.linkId.toString());
		attr.put("currentCapacity", this.currentCapacity.toString());

		return attr;
	}
}
