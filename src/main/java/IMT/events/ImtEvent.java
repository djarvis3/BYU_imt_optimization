package IMT.events;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.network.Link;

import java.util.Map;

public class ImtEvent extends Event {
	private final String currentCapacity;
	private final Id<Link> linkId;

	public ImtEvent(double time, String currentCapacity, Id<Link> linkId){
		super(time);
		this.currentCapacity = currentCapacity;
		this.linkId = linkId;
	}

	public String getCurrentCapacity(){
		return  currentCapacity;
	}
	public Id<Link> getLinkId(){
		return  linkId;
	}


	@Override
	public String getEventType() {
		return "imt";
	}

	@Override
	public Map<String, String> getAttributes() {
		Map<String, String> attr = super.getAttributes();
		attr.put("currentCapacity", this.currentCapacity);
		attr.put("linkId", this.linkId.toString());  // added this line
		return attr;
	}
}
