package IMT;

import incidents.Incident;

import java.util.List;

/**
 * The IncidentManager class manages a list of selected Incidents.
 */
public class IncidentManager {
	/**
	 * The list of selected Incidents.
	 */
	private static List<Incident> incidentsSelected;

	/**
	 * Returns the list of selected Incidents.
	 *
	 * @return the list of selected Incidents.
	 */
	public static List<Incident> getIncidentsSelected() {
		return incidentsSelected;
	}

	/**
	 * Sets the list of selected Incidents.
	 *
	 * @param incidentsSelected the new list of selected Incidents.
	 */
	public static void setIncidentsSelected(List<Incident> incidentsSelected) {
		IncidentManager.incidentsSelected = incidentsSelected;
	}
}
