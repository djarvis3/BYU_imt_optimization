package IMT.events;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.network.Link;

import java.util.Map;

/**
 * Represents an IMT (Incident Management Team Arrival) event.
 */
public class ImtEvent extends Event {
	private final double processTime;
	private final double arrivalTime;
	private final Id<Link> linkId;
	private final double currentCapacity;
	private final double endTime;

	/**
	 * Constructs an ImtEvent object with the specified parameters.
	 *
	 * @param processTime     the time at which the event is processed
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

	/**
	 * Returns the time at which the event is processed.
	 *
	 * @return the processing time
	 */
	public double getProcessTime() {
		return processTime;
	}

	/**
	 * Returns the arrival time of the IMT event.
	 *
	 * @return the arrival time
	 */
	public double getArrivalTime() {
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
	public double getCurrentCapacity() {
		return currentCapacity;
	}

	/**
	 * Returns the end time of the event.
	 *
	 * @return the end time
	 */
	public double getEndTime() {
		return endTime;
	}

	@Override
	public String getEventType() {
		return "imt";
	}

	@Override
	public Map<String, String> getAttributes() {
		Map<String, String> attr = super.getAttributes();
		attr.put("processTime", Double.toString(this.processTime));
		attr.put("actType", "DISPATCH");
		attr.put("linkId", this.linkId.toString());

		return attr;
	}
}
