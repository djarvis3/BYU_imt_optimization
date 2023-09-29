package analysis.feeders;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.events.handler.BasicEventHandler;

import java.util.*;
import java.util.stream.Collectors;

public class IncidentImpactEventHandler implements BasicEventHandler {

	private final Set<Id<Link>> impactedLinks = new HashSet<>();
	private final Map<String, Deque<Id<Link>>> vehicleLinkSequence = new HashMap<>();
	private final Map<Id<Link>, Link> allLinks;
	private final Map<String, Map<Id<Link>, Integer>> linkFeederCountMap = new HashMap<>();

	public IncidentImpactEventHandler(Network network) {
		this.allLinks = new HashMap<>(network.getLinks());
	}

	@Override
	public void handleEvent(Event event) {
		if (event instanceof LinkEnterEvent linkEnterEvent) {
			String vehicleId = linkEnterEvent.getVehicleId().toString();
			Id<Link> linkId = linkEnterEvent.getLinkId();
			vehicleLinkSequence.computeIfAbsent(vehicleId, v -> new LinkedList<>()).add(linkId);

			if (impactedLinks.contains(linkId)) {
				List<Id<Link>> previousLinks = getLastTwoLinksWithoutRemoval(vehicleId);
				if (previousLinks != null && previousLinks.size() >= 2) {
					Id<Link> feeder1 = previousLinks.get(0);
					Id<Link> feeder2 = previousLinks.get(1);

					if (isMotorway(allLinks.get(feeder1))) {
						incrementFeederCount(linkId.toString(), feeder1);

						if (isMotorway(allLinks.get(feeder2))) {
							incrementFeederCount(feeder1.toString(), feeder2);
						}
					}
				}
			}
		}
	}

	private void incrementFeederCount(String targetLink, Id<Link> feederLink) {
		linkFeederCountMap.computeIfAbsent(targetLink, k -> new HashMap<>()).merge(feederLink, 1, Integer::sum);
	}

	private List<Id<Link>> getLastTwoLinksWithoutRemoval(String vehicleId) {
		Deque<Id<Link>> sequence = vehicleLinkSequence.get(vehicleId);
		if (sequence.size() < 2) return null;
		Iterator<Id<Link>> iter = sequence.descendingIterator();
		return List.of(iter.next(), iter.next());
	}

	private boolean isMotorway(Link link) {
		return "motorway".equals(link.getAttributes().getAttribute("type"));
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
						entry -> entry.getValue().entrySet().stream()
								.filter(linkEntry -> !linkEntry.getKey().toString().equals(entry.getKey())) // Filter out the incident link
								.sorted(Map.Entry.<Id<Link>, Integer>comparingByValue().reversed())
								.limit(2)
								.map(Map.Entry::getKey)
								.map(Id::toString)
								.collect(Collectors.toList())
				));
	}
}
