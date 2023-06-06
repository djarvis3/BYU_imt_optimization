package incidents;

import java.util.*;

public class IncidentSelector {

	/**
	 * Selects a random subset of incidents from the given list based on the specified incidentNumber.
	 *
	 * @param incidents       the list of incidents to select from
	 * @param uniqueIncidents the list of unique incidents to ensure that incident aren't picked from the same link
	 * @param incidentNumber  the number of incidents to select
	 * @return a randomly selected subset of incidents
	 * @throws IllegalArgumentException if incidentNumber is greater than the size of total incidents
	 *                                  or greater than the size of unique incidents
	 */
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

	/**
	 * Selects a seeded subset of incidents from the given list based on the specified incidentNumber and seed.
	 *
	 * @param incidents       the list of incidents to select from
	 * @param uniqueIncidents the list of unique incidents to ensure that incident aren't picked from the same link
	 * @param incidentNumber  the number of incidents to select
	 * @param seed            the seed value to use for randomization
	 * @return a seeded subset of incidents
	 * @throws IllegalArgumentException if incidentNumber is greater than the size of total incidents
	 *                                  or greater than the size of unique incidents
	 */
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



	/**
	 * Selects all unique incidents from the given list.
	 *
	 * @param incidents the list of incidents to select from
	 * @return a list of all unique incidents
	 */
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
