package IMT.events;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.util.DvrpEventsReaders;

import java.util.Map;

/**
 * Represents an IMT (Incident Management Team  Arrival) event.
 */
public class ImtEvent extends Event {
	private final Double processTime;
	private final Double arrivalTime;
	private final Id<Link> linkId;
	private final Double currentCapacity;
	private final Double endTime;

	/**
	 * Constructs an ImtEvent object with the specified parameters.
	 *
	 * @param arrivalTime     the arrival time of the IMT vehicle
	 * @param linkId          the ID of the link associated with the event
	 * @param currentCapacity the current capacity at the time of the event
	 * @param endTime         the end time of the event
	 */
	public ImtEvent(double processTime, double arrivalTime, Id<Link> linkId, double currentCapacity, double endTime) {
		super(processTime);
		this.processTime = processTime;
		this.arrivalTime = arrivalTime;
		this.linkId = linkId;
		this.currentCapacity = currentCapacity;
		this.endTime = endTime;
	}

	public Double getProcessTime(){return processTime;}

	/**
	 * Returns the arrival time of the IMT event.
	 *
	 * @return the arrival time
	 */
	public Double getArrivalTime() {
		return arrivalTime;
	}

	/**
	 * Returns the ID of the link associated with the event.
	 *
	 * @return the link ID
	 */
	public Id<Link> getLinkId() {
		return linkId;
	}

	/**
	 * Returns the current capacity at the time of the event.
	 *
	 * @return the current capacity
	 */
	public Double getCurrentCapacity() {
		return currentCapacity;
	}

	/**
	 * Returns the end time of the event.
	 *
	 * @return the end time
	 */
	public Double getEndTime() {
		return endTime;
	}

	@Override
	public String getEventType() {
		return "imt";
	}

	@Override
	public Map<String, String> getAttributes() {
		Map<String, String> attr = super.getAttributes();
		attr.put("processTime", this.processTime.toString());
		attr.put("actType", "DISPATCH");
		attr.put("linkId", this.linkId.toString());

		return attr;
	}
}
