package incidents;

import java.util.*;

public class IncidentSelector {

	public static List<Incident> selectRandomSubset(List<Incident> incidents, List<Incident> uniqueIncidents,
											  int incidentNumber) {
		if (incidentNumber > incidents.size()) {
			throw new IllegalArgumentException("incidentNumber is greater than the size of incidents");
		}
		if (incidentNumber > uniqueIncidents.size()) {
			throw new IllegalArgumentException("incidentNumber is greater than the size of unique incidents");
		}
		Random random = new Random();
		List<Incident> selectedIncidents = new ArrayList<>();
		Set<String> selectedLinkIds = new HashSet<>(); // Track selected linkIds

		while (selectedIncidents.size() < incidentNumber) {
			int randomIndex = random.nextInt(incidents.size());
			Incident randomIncident = incidents.get(randomIndex);
			if (!selectedLinkIds.contains(randomIncident.getLinkId())) {
				selectedIncidents.add(randomIncident);
				selectedLinkIds.add(randomIncident.getLinkId()); // Add the selected linkId
			}
		}
		return selectedIncidents;
	}

	public static List<Incident> selectSeededSubset(List<Incident> incidents, List<Incident> uniqueIncidents,
											  int incidentNumber, long seed) {
		if (incidentNumber > incidents.size()) {
			throw new IllegalArgumentException("incidentNumber is greater than the size of incidents");
		}
		if (incidentNumber > uniqueIncidents.size()) {
			throw new IllegalArgumentException("incidentNumber is greater than the size of unique incidents");
		}
		Random random = new Random(seed);
		List<Incident> selectedIncidents = new ArrayList<>();
		Set<String> selectedLinkIds = new HashSet<>(); // Track selected linkIds

		while (selectedIncidents.size() < incidentNumber) {
			int randomIndex = random.nextInt(incidents.size());
			Incident randomIncident = incidents.get(randomIndex);
			if (!selectedLinkIds.contains(randomIncident.getLinkId())) {
				selectedIncidents.add(randomIncident);
				selectedLinkIds.add(randomIncident.getLinkId()); // Add the selected linkId
			}
		}
		return selectedIncidents;
	}

	public static List<Incident> selectAllUniqueIncidents(List<Incident> incidents) {
		List<Incident> selectedIncidents = new ArrayList<>();
		Set<String> selectedLinkIds = new HashSet<>(); // Track selected linkIds

		for (Incident incident : incidents) {
			if (!selectedLinkIds.contains(incident.getLinkId())) {
				selectedIncidents.add(incident);
				selectedLinkIds.add(incident.getLinkId()); // Add the selected linkId
			}
		}
		return selectedIncidents;
	}

}
