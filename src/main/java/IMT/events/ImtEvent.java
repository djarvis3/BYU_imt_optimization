package IMT.events;

import org.matsim.api.core.v01.events.Event;

import java.util.Map;

public class ImtEvent extends Event {
	private final String currentCapacity;

	public ImtEvent(double time, String currentCapacity){
		super(time);
		this.currentCapacity = currentCapacity;
	}

	public String getCurrentCapacity(){
		return  currentCapacity;
	}

	@Override
	public String getEventType() {
		return "imt";
	}

	@Override
	public Map<String, String> getAttributes() {
		Map<String, String> attr = super.getAttributes();
		attr.put("currentCapacity", this.currentCapacity);
		return attr;
	}
}
