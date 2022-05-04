package org.matsim.project;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

public class LinkTablesReader {
    private static final Logger log = Logger.getLogger(LinkTablesReader.class);
    private final Scenario scenario;
    private CoordinateTransformation ct;
    private Network network;
    private NetworkFactory networkFactory;
    Integer linkCounter = 1;

    private final File nodesFile;
    private final File linksFile;

    /**
     * Initialize a network reader.
     * @param scenario A MATSim scenario
     * @param nodesFile A CSV file with node IDS.
     * @param linksFile A CSV file with link attributes
     */
    public LinkTablesReader (Scenario scenario, File nodesFile, File linksFile) {
        this.scenario = scenario;
        this.network = this.scenario.getNetwork();
        this.networkFactory = network.getFactory();
        this.ct = TransformationFactory.getCoordinateTransformation(
                TransformationFactory.WGS84,
                this.scenario.getConfig().global().getCoordinateSystem());
        this.nodesFile = nodesFile;
        this.linksFile = linksFile;
    }

    public void makeNetwork() throws IOException {
        readNodes(nodesFile);
        readLinks(linksFile);
        //addTurnaroundLinks();
        //new NetworkCleaner().run(network);
    }

    /**
     * Read a Nodes CSV file into the network.
     * @param nodesFile A CSV file with the following fields:
     *                  - id
     *                  - x (lon)
     *                  - y (lat)
     */
    private void readNodes(File nodesFile) throws IOException {

        try{
            CSVFormat csvFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase();
            Path path = Paths.get(String.valueOf(nodesFile));
            CSVParser csvParser = CSVParser.parse(path, StandardCharsets.UTF_8, csvFormat);

            for(CSVRecord csvRecord : csvParser) {
                Id<Node> nodeId = Id.createNodeId(csvRecord.get("id"));
                Double lon = Double.valueOf(csvRecord.get("x"));
                Double lat = Double.valueOf(csvRecord.get("y"));
                Coord coordLatLon = CoordUtils.createCoord(lon, lat);
                Coord coord = ct.transform(coordLatLon);
                Node node = networkFactory.createNode(nodeId, coord);
                network.addNode(node);

            }

        } catch (IOException e){
            e.printStackTrace();
        }


    }


    /**
     * Read the network links information
     * @param linksFile A csv file containing the following fields:
     *                  - link_id,
     *                  - Oneway,
     *                  - Speed (free flow),
     *                  - DriveTime (minutes)
     *                  - Length_Miles (miles)
     *                  - RoadClass (text)
     *                  - AADT (count)
     *                  - a
     *                  - b
     *                  - ft (hcm definition)
     *                  - lanes
     *                  - sl (miles per hour)
     *                  - med median treatment
     *                  - terrain
     *                  - capacity (vehicles / hr)
     */
    private void readLinks(File linksFile) throws IOException {
        // Start a reader and read the header row. `col` is an index between the column names and numbers
        try{
            CSVFormat csvFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase();
            Path path = Paths.get(String.valueOf(linksFile));
            CSVParser csvParser = CSVParser.parse(path, StandardCharsets.UTF_8, csvFormat);



            for(CSVRecord csvRecord : csvParser) {
                // set up link ID with from and to nodes
                Id<Node> fromNodeId = Id.createNodeId(csvRecord.get("a"));
                Id<Node> toNodeId   = Id.createNodeId(csvRecord.get("b"));
                Node fromNode = network.getNodes().get(fromNodeId);
                Node toNode   = network.getNodes().get(toNodeId);
                Id<Link> linkId = Id.createLinkId(linkCounter);
                Link l =  networkFactory.createLink(linkId, fromNode, toNode);

                // get link attributes from csv
                Double speed  = Double.valueOf(csvRecord.get("speed"));
                Double lengthMiles = Double.valueOf(csvRecord.get("length"));
                Double capacity    = Double.valueOf(csvRecord.get("capacity"));
                Integer lanes      = Integer.valueOf(csvRecord.get("lanes"));
                String type = csvRecord.get("type");
                Integer oneWay = 0;
                try {
                    oneWay = Integer.valueOf(csvRecord.get("oneway"));
                } catch (IllegalArgumentException e){
                }

                Double length = lengthMiles * 1609.34; // convert miles to meters
                Double freeSpeed = speed * 0.44704; // convert meters per minute to meters per second

                // put link attributes on link
                l.setLength(length);
                l.setFreespeed(freeSpeed);
                l.setNumberOfLanes(lanes);
                l.setCapacity(capacity);
                l.getAttributes().putAttribute("type", type);

                network.addLink(l);
                linkCounter++;

                // create reverse direction link if it exists
                if(oneWay != 1) {
                    Id<Link> rLinkId = Id.createLinkId(linkCounter);
                    Link rl = networkFactory.createLink(rLinkId, toNode, fromNode);

                    rl.setLength(length);
                    rl.setFreespeed(freeSpeed);
                    rl.setNumberOfLanes(lanes);
                    rl.setCapacity(capacity);
                    rl.getAttributes().putAttribute("type", type);
                    network.addLink(rl);
                    linkCounter++;
                }

            }

        } catch (IOException e){
            e.printStackTrace();
        }


    }



    /**
     * Interstates at the edge of the boundary need to have additional turnaround links added or paths
     * going in that direction end up breaking.
     */
    private void addTurnaroundLinks() {

        // create map of nodes that only have one link entering or exiting.
        ArrayList<Node> inOnlyNodes = new ArrayList<>();
        ArrayList<Node> outOnlyNodes = new ArrayList<>();

        // Loop through all nodes in the network, and populate the lists we just created
        Iterator<? extends Node> iter = network.getNodes().values().iterator();
        while (iter.hasNext()) {
            Node myNode = iter.next();
            if(myNode.getOutLinks().isEmpty()) inOnlyNodes.add(myNode); // no outbound links
            if(myNode.getInLinks().isEmpty()) outOnlyNodes.add(myNode); // no inbound links
        }

        // loop through all the outOnlyNodes
        for(Node outNode : outOnlyNodes){
            // Loop through the inOnlyNodes and see if there are any outOnlyNodes within 50m. Finds the nearest outOnlyNode
            Node matchInNode = null;
            Coord outCoord = outNode.getCoord();
            Double inDistance = Double.POSITIVE_INFINITY; // starting distance is infinite
            for (Node inNode : inOnlyNodes) {
                Coord inCoord = inNode.getCoord();
                Double thisDistance = NetworkUtils.getEuclideanDistance(outCoord, inCoord);
                if(thisDistance < inDistance & thisDistance < 50){
                    matchInNode = inNode; // update the selected companion node
                    inDistance = thisDistance; // update the comparison distance
                }
            }

            // if there is a matched inOnlyNode, we will build a new link with default stupid attributes
            if(matchInNode != null){
                Id<Link> lid = Id.createLinkId(outNode.getId() + "_" + matchInNode.getId());
                Link l =  networkFactory.createLink(lid, matchInNode, outNode);
                l.setCapacity(2000);
                l.setNumberOfLanes(1);
                l.setFreespeed(20);
                network.addLink(l);
                log.trace("Added turnaround link " + lid);
            }
        }


    }



    private void writeNetwork(String outFile){
        log.info("Writing network to " + outFile);
        log.info("--- Links: " + network.getLinks().values().size());
        log.info("--- Nodes: " + network.getNodes().values().size());
        new NetworkWriter(network).write(outFile);
    }



    public static void main(String[] args) {
        File nodesFile = new File(args[0]);
        File linksFile = new File(args[1]);
        String outFile = args[2];
        String crs = args[3];

        Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        scenario.getConfig().global().setCoordinateSystem(crs);
        LinkTablesReader reader = new LinkTablesReader(scenario, nodesFile, linksFile);

        try {
            reader.makeNetwork();
            reader.writeNetwork(outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}