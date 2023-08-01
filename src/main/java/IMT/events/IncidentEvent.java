package IMT.events;

import IMT.ImtRequest;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.network.Link;

import java.util.Map;

/**
 * Represents an incident event.
 */
public class IncidentEvent extends Event {
	private final Id<Link> linkId;
	private final double reducedCapacity;
	private final double endTime;
	private final double fullCapacity;

	/**
	 * Constructs an IncidentEvent object from the given IMT request.
	 *
	 * @param req the IMT request associated with the incident event
	 */
	public IncidentEvent(ImtRequest req) {
		super(req.getSubmissionTime());
		this.linkId = req.getIncLink().getId();
		this.reducedCapacity = req.getLinkCap_Reduced();
		this.endTime = req.getEndTime();
		this.fullCapacity = req.getLinkCap_Full();
	}

	/**
	 * Returns the ID of the link associated with the incident event.
	 *
	 * @return the link ID
	 */
	public Id<Link> getLinkId() {
		return linkId;
	}

	/**
	 * Returns the reduced capacity of the link during the incident event.
	 *
	 * @return the reduced capacity
	 */
	public double getReducedCapacity() {
		return reducedCapacity;
	}

	/**
	 * Returns the end time of the incident event.
	 *
	 * @return the end time
	 */
	public double getEndTime() {
		return endTime;
	}

	/**
	 * Returns the full capacity of the link after the incident event.
	 *
	 * @return the full capacity
	 */
	public double getFullCapacity() {
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
		attr.put("reducedCapacity", Double.toString(this.reducedCapacity));
		attr.put("endTime", Double.toString(this.endTime));
		attr.put("fullCapacity", Double.toString(this.fullCapacity));

		return attr;
	}
}
