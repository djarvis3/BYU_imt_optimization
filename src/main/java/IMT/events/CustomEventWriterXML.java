package IMT.events;

import IMT.events.incidents.IncidentEvent;
import org.matsim.api.core.v01.events.Event;
import org.matsim.core.events.algorithms.EventWriterXML;
import org.matsim.core.utils.io.UncheckedIOException;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;

public class CustomEventWriterXML extends EventWriterXML {
	private final PrintWriter writer;
	private PriorityQueue<Event> eventQueue;

	public CustomEventWriterXML(OutputStream out) {
		super(out);
		this.writer = new PrintWriter(out);
		// Initialize event queue with a comparator that sorts by event time
		this.eventQueue = new PriorityQueue<>(Comparator.comparingDouble(Event::getTime));
	}

	@Override
	public void handleEvent(Event event) {
		// Instead of immediately writing the event, add it to the queue
		eventQueue.add(event);
	}

	@Override
	public void closeFile() {
		// When closing the file, process all events in the queue in order of occurrence
		while (!eventQueue.isEmpty()) {
			Event event = eventQueue.poll();
			// Your existing event writing code here
			// ...

			if(event instanceof IncidentEvent) {
				IncidentEvent incidentEvent = (IncidentEvent) event;
				try {
					// Convert time to nearest second before printing
					String roundedTime = String.valueOf(Math.round(incidentEvent.getTime()));
					writer.print(String.format("\t<event time=\"%s\" type=\"%s\"", roundedTime, incidentEvent.getEventType()));

					Map<String, String> attributes = incidentEvent.getAttributes();
					for(Map.Entry<String, String> entry : attributes.entrySet()) {
						writer.print(String.format(" %s=\"%s\"", entry.getKey(), entry.getValue()));
					}

					writer.println(" />");
				} catch (Exception e) {
					throw new UncheckedIOException(e);
				}
			} else {
				super.handleEvent(event);
			}
		}

		writer.flush();
		super.closeFile();
	}
}


