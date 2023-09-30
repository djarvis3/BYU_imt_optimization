package analysis.feeders;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.events.handler.BasicEventHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class IncidentImpactEventHandler implements BasicEventHandler {

	private final Set<Id<Link>> impactedLinks = ConcurrentHashMap.newKeySet();
	private final Map<String, Deque<Id<Link>>> vehicleLinkSequence = new ConcurrentHashMap<>();
	private final Map<Id<Link>, Link> allLinks;
	private final Map<String, Map<Id<Link>, Integer>> linkFeederCountMap = new HashMap<>();
	private final Map<String, Map<Id<Link>, Integer>> linkBeforeFeederCountMap = new HashMap<>();

	public IncidentImpactEventHandler(Network network) {
		this.allLinks = new HashMap<>(network.getLinks());
	}

	@Override
	public void handleEvent(Event event) {
		if (event instanceof LinkEnterEvent linkEnterEvent) {
			String vehicleId = linkEnterEvent.getVehicleId().toString();
			Id<Link> linkId = linkEnterEvent.getLinkId();

			if (impactedLinks.contains(linkId)) {
				List<Id<Link>> previousLinks = getLastTwoLinksWithoutRemoval(vehicleId);
				if (previousLinks != null && previousLinks.size() == 2) {
					Id<Link> feeder2 = previousLinks.get(1); // Link two places before the impacted link
					Id<Link> feeder1 = previousLinks.get(0); // Link immediately before the impacted link

					linkFeederCountMap.computeIfAbsent(linkId.toString(), k -> new HashMap<>()).merge(feeder1, 1, Integer::sum);
					linkBeforeFeederCountMap.computeIfAbsent(linkId.toString(), k -> new HashMap<>()).merge(feeder2, 1, Integer::sum);
				}
			}

			// Now, after processing, update the vehicleLinkSequence
			vehicleLinkSequence.computeIfAbsent(vehicleId, v -> new LinkedList<>()).add(linkId);
		}
	}

	private List<Id<Link>> getLastTwoLinksWithoutRemoval(String vehicleId) {
		Deque<Id<Link>> sequence = vehicleLinkSequence.get(vehicleId);

		// Check if sequence is null and return null if it is.
		if (sequence == null) {
			return null;
		}

		if (sequence.size() < 2) {
			return null;
		}

		Iterator<Id<Link>> iter = sequence.descendingIterator();
		return List.of(iter.next(), iter.next());
	}

	@Override
	public void reset(int iteration) {
		vehicleLinkSequence.clear();
	}

	public void setImpactedLinks(Set<String> impactedLinks) {
		if (impactedLinks == null) {
			throw new IllegalArgumentException("Impacted links set must not be null.");
		}
		this.impactedLinks.clear();
		this.impactedLinks.addAll(impactedLinks.stream().map(Id::createLinkId).collect(Collectors.toSet()));
	}

	public Map<String, List<String>> getLinkFeederMap() {
		return linkFeederCountMap.entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						entry -> {
							List<String> feeders = new ArrayList<>();

							// feeder1 is the most common link immediately before the impacted link
							String feeder1 = entry.getValue().entrySet().stream()
									.sorted(Map.Entry.<Id<Link>, Integer>comparingByValue().reversed())
									.map(Map.Entry::getKey)
									.map(Id::toString)
									.findFirst().orElse(null);
							feeders.add(feeder1);

							// feeder2 is the most common link before feeder1
							String feeder2 = linkBeforeFeederCountMap.getOrDefault(entry.getKey(), new HashMap<>()).entrySet().stream()
									.sorted(Map.Entry.<Id<Link>, Integer>comparingByValue().reversed())
									.map(Map.Entry::getKey)
									.map(Id::toString)
									.findFirst().orElse("");
							feeders.add(feeder2);

							return feeders;
						}
				));
	}
}
