package analysis.feeders;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.events.handler.BasicEventHandler;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class IncidentImpactEventHandler implements BasicEventHandler {

	private final Set<Id<Link>> impactedLinks = new HashSet<>();
	private final Map<String, Deque<Id<Link>>> vehicleLinkSequence = new HashMap<>();
	private final Map<String, Set<String>> linkFeederMap = new HashMap<>();
	private final Network network;
	private final Map<Id<Link>, Link> allLinks;

	private static final Logger LOGGER = Logger.getLogger(IncidentImpactEventHandler.class.getName());

	public IncidentImpactEventHandler(Network network) {
		this.network = network;
		this.allLinks = (Map<Id<Link>, Link>) network.getLinks();
	}

	@Override
	public void handleEvent(Event event) {
		if (event instanceof LinkEnterEvent linkEnterEvent) {
			String vehicleId = linkEnterEvent.getVehicleId().toString();
			Id<Link> linkId = linkEnterEvent.getLinkId();

			vehicleLinkSequence.computeIfAbsent(vehicleId, v -> new LinkedList<>()).add(linkId);

			if (impactedLinks.contains(linkId) && isImpactedLinkPrecededByMotorways(vehicleId)) {
				linkFeederMap.computeIfAbsent(linkId.toString(), k -> new HashSet<>())
						.addAll(getRecentFeeders(vehicleId).stream().map(Id::toString).collect(Collectors.toSet()));
			}
		}
	}

	private boolean isImpactedLinkPrecededByMotorways(String vehicleId) {
		Deque<Id<Link>> sequence = vehicleLinkSequence.get(vehicleId);
		List<Id<Link>> validFeeders = new ArrayList<>();

		for (Id<Link> linkId : sequence) {
			if (isMotorway(allLinks.get(linkId)) && isLinkLengthValid(linkId)) {
				validFeeders.add(linkId);
			}
			if (validFeeders.size() == 2) {
				break;
			}
		}

		if (validFeeders.size() < 2) {
			return false;
		}

		Id<Link> impactedLinkId = sequence.getLast();
		Id<Link> feeder1Id = validFeeders.get(0);
		Id<Link> feeder2Id = validFeeders.get(1);

		return areLinksConnected(feeder2Id, feeder1Id) && areLinksConnected(feeder1Id, impactedLinkId);
	}

	private boolean areLinksConnected(Id<Link> upstream, Id<Link> downstream) {
		Link upstreamLink = allLinks.get(upstream);
		Link downstreamLink = allLinks.get(downstream);

		return upstreamLink.getToNode().equals(downstreamLink.getFromNode());
	}

	private boolean isMotorway(Link link) {
		return "motorway".equals(link.getAttributes().getAttribute("type"));
	}

	private boolean isLinkLengthValid(Id<Link> linkId) {
		Link link = allLinks.get(linkId);
		return link.getLength() > 100;
	}

	private List<Id<Link>> getRecentFeeders(String vehicleId) {
		Deque<Id<Link>> sequence = vehicleLinkSequence.get(vehicleId);

		List<Id<Link>> validFeeders = new ArrayList<>();
		for (Id<Link> linkId : sequence) {
			if (isMotorway(allLinks.get(linkId)) && isLinkLengthValid(linkId)) {
				validFeeders.add(linkId);
			}
			if (validFeeders.size() == 2) {
				break;
			}
		}

		return validFeeders;
	}

	@Override
	public void reset(int iteration) {
		vehicleLinkSequence.clear();
	}

	public Map<String, List<String>> getLinkFeederMap() {
		return linkFeederMap.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey,
						entry -> new ArrayList<>(entry.getValue())));
	}

	public void setImpactedLinks(Set<String> impactedLinks) {
		this.impactedLinks.clear();
		this.impactedLinks.addAll(impactedLinks.stream().map(Id::createLinkId).collect(Collectors.toSet()));
	}
}
