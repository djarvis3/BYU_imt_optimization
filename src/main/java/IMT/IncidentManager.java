package IMT;

import incidents.Incident;

import java.util.List;

public class IncidentManager {
	private static List<Incident> incidentsSelected;
	public static List<Incident> getIncidentsSelected() {
		return incidentsSelected;
	}
	public static void setIncidentsSelected(List<Incident> incidentsSelected) {
		IncidentManager.incidentsSelected = incidentsSelected;
	}
}
