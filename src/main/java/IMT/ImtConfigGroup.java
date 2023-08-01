package IMT;

import org.matsim.core.config.ReflectiveConfigGroup;

public class ImtConfigGroup extends ReflectiveConfigGroup {

	public static final String GROUP_NAME = "IMT";

	private static final String INCIDENT_SELECTION = "incidentSelection";
	private static final String INCIDENTS_CSV_FILE_PATH = "incidentsCsvFilePath";
	private static final String INCIDENT_SELECTION_SEED = "incidentSelectionSeed";
	private static final String NUM_INCIDENTS_TO_SELECT = "numIncidentsToSelect";
	private static final String TRUCKS_FILE = "trucksFile";
	private static final String LINK_CAPACITY_RESTORE_INTERVAL = "linkCapacityRestoreInterval";


	private String incidentsCsvFilePath;
	private long incidentSelectionSeed;
	private int numIncidentsToSelect;
	private String incidentSelection = "incidentSeedSelection"; // Default to incidentSeedSelection
	private String trucksFile;
	private double linkCapacityRestoreInterval = 0.25; // Default value


	public ImtConfigGroup() {
		super(GROUP_NAME);
	}

	public ImtConfigGroup(String name) {
		super(name);
	}

	@StringGetter(INCIDENT_SELECTION)
	public String getIncidentSelection() {
		return incidentSelection;
	}

	@StringSetter(INCIDENT_SELECTION)
	public void setIncidentSelection(String incidentSelection) {
		this.incidentSelection = incidentSelection;
	}

	@StringGetter(INCIDENTS_CSV_FILE_PATH)
	public String getIncidentsCsvFilePath() {
		return incidentsCsvFilePath;
	}

	@StringSetter(INCIDENTS_CSV_FILE_PATH)
	public void setIncidentsCsvFilePath(String incidentsCsvFilePath) {
		this.incidentsCsvFilePath = incidentsCsvFilePath;
	}

	@StringGetter(INCIDENT_SELECTION_SEED)
	public long getIncidentSelectionSeed() {
		return incidentSelectionSeed;
	}

	@StringSetter(INCIDENT_SELECTION_SEED)
	public void setIncidentSelectionSeed(long incidentSelectionSeed) {
		this.incidentSelectionSeed = incidentSelectionSeed;
	}

	@StringGetter(NUM_INCIDENTS_TO_SELECT)
	public int getNumIncidentsToSelect() {
		return numIncidentsToSelect;
	}

	@StringSetter(NUM_INCIDENTS_TO_SELECT)
	public void setNumIncidentsToSelect(int numIncidentsToSelect) {
		this.numIncidentsToSelect = numIncidentsToSelect;
	}

	@StringGetter(TRUCKS_FILE)
	public String getTrucksFile() {
		return trucksFile;
	}

	@StringSetter(TRUCKS_FILE)
	public void setTrucksFile(String trucksFile) {
		this.trucksFile = trucksFile;
	}

	@StringGetter(LINK_CAPACITY_RESTORE_INTERVAL)
	public double getLinkCapacityRestoreInterval() {
		return linkCapacityRestoreInterval;
	}

	@StringSetter(LINK_CAPACITY_RESTORE_INTERVAL)
	public void setLinkCapacityRestoreInterval(double linkCapacityRestoreInterval) {
		this.linkCapacityRestoreInterval = linkCapacityRestoreInterval;
	}
}


