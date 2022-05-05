package org.matsim.project.cedarcityProject;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.io.OsmNetworkReader;

public class RunPNetworkGenerator {

    public static void main(String[] args) {

        String osm = "./src/main/cedarcity.osm"; // input OpenStreetMap file

        // The coordinate system OpenStreetMap uses is WGS84, but for MATSim, we need a projection where distances
        // are (roughly) euclidean distances in meters.
        // UTM coordinates are one such option, EPSG coordinates are another

        CoordinateTransformation ct =
                TransformationFactory.getCoordinateTransformation(TransformationFactory.WGS84, "EPSG:26912");

        Config config = ConfigUtils.createConfig();
        Scenario scenario = ScenarioUtils.createScenario(config);

        Network network = scenario.getNetwork();  // Pick the Network from the Scenario for convenience.

        OsmNetworkReader onr = new OsmNetworkReader(network,ct);
        onr.parse(osm);

        new NetworkCleaner().run(network);  // Clean the Network

        new NetworkWriter(network).write("./scenarios/cedarcity/network.xml");  // Write the Network to a MATSim network file.

    }

}