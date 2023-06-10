package IMT.events.eventHanlders;

import IMT.events.CustomEventWriterXML;
import IMT.events.incidents.IncidentEvent;
import org.matsim.api.core.v01.events.Event;
import org.matsim.core.events.handler.BasicEventHandler;

public class CustomEventHandler implements BasicEventHandler {
	private final CustomEventWriterXML writer;

	public CustomEventHandler(CustomEventWriterXML writer) {
		this.writer = writer;
	}

	@Override
	public void handleEvent(Event event) {
		if(event instanceof IncidentEvent) {
			writer.handleEvent(event);
		}
	}

	@Override
	public void reset(int iteration) { }
}

