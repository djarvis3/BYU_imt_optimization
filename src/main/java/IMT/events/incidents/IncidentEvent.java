package IMT.events.incidents;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.network.Link;

import java.util.Map;

public class IncidentEvent extends Event {
	private final Id<Link> linkId;
	private final Double reducedCapacity;
	private final Double endTime;
	private final Double fullCapacity;

	public IncidentEvent(double startTime, Id<Link> linkId, double reducedCapacity, double endTime, double fullCapacity)
	{
		super(startTime);
		this.linkId = linkId;
		this.reducedCapacity = reducedCapacity;
		this.endTime = endTime;
		this.fullCapacity = fullCapacity;
	}

	public Id<Link> getLinkId(){
		return  linkId;
	}
	public Double getReducedCapacity(){return  reducedCapacity;}
	public Double getEndTime(){return endTime;}
	public Double getFullCapacity(){
		return fullCapacity;
	}



	@Override
	public String getEventType() {
		return "incident";
	}

	@Override
	public Map<String, String> getAttributes() {
		Map<String, String> attr = super.getAttributes();
		attr.put("linkId", this.linkId.toString());
		attr.put("reducedCapacity", this.reducedCapacity.toString());
		attr.put("endTime", this.endTime.toString());
		attr.put("fullCapacity", this.fullCapacity.toString());

		return attr;
	}
}
