package analysis.volume;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.events.handler.BasicEventHandler;
import org.matsim.api.core.v01.network.Network;

import java.util.HashMap;
import java.util.Map;

public class VolumeEventHandler implements BasicEventHandler {
	private final Map<Id<Link>, int[]> linkVolumes = new HashMap<>();
	private static final int NUMBER_OF_INTERVALS = 120; // 30 hours * 4 intervals per hour
	private static final int SECONDS_PER_INTERVAL = 15 * 60; // 15 minutes in seconds
	private final Network network;

	public VolumeEventHandler(Network network) {
		this.network = network;
	}

	@Override
	public void handleEvent(Event event) {
		if (event instanceof LinkEnterEvent linkEnterEvent) {
			Id<Link> linkId = linkEnterEvent.getLinkId();
			int intervalIndex = getTimeIntervalIndex(linkEnterEvent.getTime());
			linkVolumes.computeIfAbsent(linkId, k -> new int[NUMBER_OF_INTERVALS])[intervalIndex]++;
		}
	}

	private int getTimeIntervalIndex(double timeInSeconds) {
		int index = (int) (timeInSeconds / SECONDS_PER_INTERVAL);
		return index >= NUMBER_OF_INTERVALS ? NUMBER_OF_INTERVALS - 1 : index;
	}

	public Map<Id<Link>, int[]> getLinkVolumes() {
		// Ensure that the volumes are initialized for all links in the network
		for (Link link : network.getLinks().values()) {
			linkVolumes.putIfAbsent(link.getId(), new int[NUMBER_OF_INTERVALS]);
		}
		return linkVolumes;
	}

	@Override
	public void reset(int iteration) {
		linkVolumes.clear();
	}
}
