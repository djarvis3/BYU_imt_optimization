package IMT.events;

import java.util.ArrayList;
import java.util.List;

public class ImtEventList {

	private List<ImtEvent> eventList;

	public ImtEventList() {
		eventList = new ArrayList<>();
	}

	public List<ImtEvent> getEventList() {
		return eventList;
	}

	public void addEvent(ImtEvent event) {
		eventList.add(event);
	}
}
