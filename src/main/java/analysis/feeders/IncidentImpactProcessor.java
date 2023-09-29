package analysis.feeders;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsReaderXMLv1;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.io.NetworkChangeEventsParser;

import java.util.*;
import java.util.stream.Collectors;

public final class IncidentImpactProcessor {

	private final Network network;
	private final IncidentImpactEventHandler eventHandler;

	public IncidentImpactProcessor(Network network) {
		this.network = Objects.requireNonNull(network, "Network must not be null");
		this.eventHandler = new IncidentImpactEventHandler(network);
	}

	public void executeEventHandlerOnFiles(String eventsFilePath, String changeEventsFilePath) {
		validateFilePaths(eventsFilePath, changeEventsFilePath);

		Set<String> impactedLinksSet = extractImpactedLinks(changeEventsFilePath);
		eventHandler.setImpactedLinks(impactedLinksSet);

		processEvents(eventsFilePath);
	}

	private void validateFilePaths(String eventsFilePath, String changeEventsFilePath) {
		if (eventsFilePath == null || changeEventsFilePath == null) {
			throw new IllegalArgumentException("File paths must not be null.");
		}
	}

	private void processEvents(String eventsFilePath) {
		EventsManager eventsManager = EventsUtils.createEventsManager();
		eventsManager.addHandler(eventHandler);

		new EventsReaderXMLv1(eventsManager).readFile(eventsFilePath);
	}

	private Set<String> extractImpactedLinks(String changeEventsFilePath) {
		List<NetworkChangeEvent> changeEvents = new ArrayList<>();

		NetworkChangeEventsParser parser = new NetworkChangeEventsParser(network, changeEvents);
		parser.readFile(changeEventsFilePath);

		return changeEvents.stream()
				.map(event -> event.getLinks().iterator().next().getId().toString())
				.collect(Collectors.toSet());
	}

	public Map<String, List<String>> getLinkFeederMap() {
		return eventHandler.getLinkFeederMap();
	}
}
