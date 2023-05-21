package IMT.events;

import org.matsim.api.core.v01.Scenario;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;



public class ChangeEvent {
	private Document document;

	public ChangeEvent() {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			document = docBuilder.newDocument();

			// Create the root element for the XML document and set the namespaces and schema location
			Element rootElement = document.createElementNS("http://www.matsim.org/files/dtd", "networkChangeEvents");
			rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			rootElement.setAttribute("xsi:schemaLocation", "http://www.matsim.org/files/dtd http://www.matsim.org/files/dtd/networkChangeEvents.xsd");
			document.appendChild(rootElement);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addNetworkChangeEvent(String startTime, String linkRefId, String flowCapacityValue) {
		// Insert a blank line element
		insertBlankLine();

		Element networkChangeEvent = document.createElement("networkChangeEvent");
		networkChangeEvent.setAttribute("startTime", formatTime(startTime));

		Element link = document.createElement("link");
		link.setAttribute("refId", linkRefId);

		Element flowCapacity = document.createElement("flowCapacity");
		flowCapacity.setAttribute("type", "absolute");
		flowCapacity.setAttribute("value", flowCapacityValue);

		networkChangeEvent.appendChild(link);
		networkChangeEvent.appendChild(flowCapacity);

		document.getDocumentElement().appendChild(networkChangeEvent);
	}

	private void insertBlankLine() {
		Text blankLine = document.createTextNode("\n");
		document.getDocumentElement().appendChild(blankLine);
	}

	private String formatTime(String timeInSeconds) {
		int seconds = (int) Double.parseDouble(timeInSeconds);
		int hours = seconds / 3600;
		int minutes = (seconds % 3600) / 60;
		int remainingSeconds = seconds % 60;

		return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
	}

	public void saveToFile(Scenario scenario) {
		String configFilePath = scenario.getConfig().getContext().getPath();
		String scenarioFolder = "";
		try {
			// Extract the directory path by removing the file name
			int lastSeparatorIndex = configFilePath.lastIndexOf("/");
			if (lastSeparatorIndex != -1) {
				scenarioFolder = configFilePath.substring(0, lastSeparatorIndex);
			}

			String fileName = scenarioFolder + "/ChangeFile.xml";
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(new File(fileName));

			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

}
