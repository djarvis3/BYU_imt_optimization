package IMT.events;

import IMT.ImtRequest;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.network.Link;

import java.util.Map;

public class IncidentEvent extends Event {
	private final Id<Link> linkId;
	private final Double reducedCapacity;
	private final Double endTime;
	private final Double fullCapacity;

	public IncidentEvent(ImtRequest req)
	{
		super(req.getSubmissionTime());
		this.linkId = req.getIncLink().getId();
		this.reducedCapacity = req.getLinkCap_Reduced();
		this.endTime = req.getEndTime();
		this.fullCapacity = req.getLinkCap_Full();
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
