/* *********************************************************************** *
 * project: org.matsim.*
 * AgentDepartureEventHandler.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package byu.imt.event_Handlers.customEvents.customHandlers;

import byu.imt.event_Handlers.customEvents.CustomPersonDepartureEvent;
import org.matsim.core.events.handler.EventHandler;

public interface CustomPersonDepartureEventHandler extends EventHandler {
	public void handleEvent (CustomPersonDepartureEvent event);
}
