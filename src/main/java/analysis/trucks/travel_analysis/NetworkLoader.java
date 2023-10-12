package analysis.trucks.travel_analysis;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;

public class NetworkLoader {
	public Network loadNetwork(String path) {
		ConfigUtils.createConfig();
		Network network = NetworkUtils.createNetwork();
		new MatsimNetworkReader(network).readFile(path);
		return network;
	}
}
