package IMT.events;

import org.matsim.api.core.v01.events.Event;
import org.matsim.core.events.algorithms.EventWriterXML;
import org.matsim.core.utils.io.UncheckedIOException;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

public class CustomEventWriterXML extends EventWriterXML {
	private final PrintWriter writer;

	public CustomEventWriterXML(OutputStream out) {
		super(out);
		this.writer = new PrintWriter(out);
	}

	@Override
	public void handleEvent(Event event) {
		if(event instanceof ImtEvent) {
			ImtEvent imtEvent = (ImtEvent) event;
			try {
				writer.print(String.format("\t<event time=\"%s\" type=\"%s\"", imtEvent.getTime(), imtEvent.getEventType()));

				Map<String, String> attributes = imtEvent.getAttributes();
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

	@Override
	public void closeFile() {
		writer.flush();
		writer.close();
		super.closeFile();
	}
}

