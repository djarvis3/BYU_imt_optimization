package org.matsim.codeexamples.events.eventsCopies;

import org.matsim.api.core.v01.Id;
import org.matsim.facilities.ActivityFacility;

public interface HasFacilityId{
        String ATTRIBUTE_FACILITY = "facility";
        Id<ActivityFacility> getFacilityId();
}
