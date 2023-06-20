package IMT;

import incidents.Incident;

import java.util.List;

/**
 * The IncidentManager class manages the selected incidents.
 */
public class IncidentManager {
	private static List<Incident> incidentsSelected;

	/**
	 * Get the list of selected incidents.
	 * @return The list of selected incidents.
	 */
	public static List<Incident> getIncidentsSelected() {
		return incidentsSelected;
	}

	/**
	 * Set the list of selected incidents.
	 * @param incidentsSelected The list of selected incidents.
	 */
	public static void setIncidentsSelected(List<Incident> incidentsSelected) {
		IncidentManager.incidentsSelected = incidentsSelected;
	}
}
