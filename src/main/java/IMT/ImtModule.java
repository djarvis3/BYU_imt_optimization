package IMT;

import IMT.optimizer.Optimizer;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.dvrp.fleet.FleetModule;
import org.matsim.contrib.dvrp.optimizer.VrpOptimizer;
import org.matsim.contrib.dvrp.router.DvrpModeRoutingNetworkModule;
import org.matsim.contrib.dvrp.run.AbstractDvrpModeModule;
import org.matsim.contrib.dvrp.run.AbstractDvrpModeQSimModule;
import org.matsim.contrib.dvrp.run.DvrpModes;
import org.matsim.contrib.dvrp.trafficmonitoring.DvrpTravelTimeModule;
import org.matsim.contrib.dvrp.vrpagent.VrpAgentLogic;
import org.matsim.contrib.dvrp.vrpagent.VrpAgentSourceQSimModule;
import org.matsim.core.router.util.TravelTime;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehicleUtils;

import com.google.inject.Key;
import com.google.inject.name.Names;

import java.net.URL;

/**
 * Module for the Incident Management Team (IMT) extension.
 */
public class ImtModule extends AbstractDvrpModeModule {
	private final URL fleetSpecificationUrl;
	private final ImtConfigGroup imtConfigGroup;

	/**
	 * Constructs an ImtModule with the specified fleet specification URL and IMT configuration group.
	 *
	 * @param fleetSpecificationUrl the URL of the fleet specification
	 * @param imtConfigGroup        the IMT configuration group
	 */
	public ImtModule(URL fleetSpecificationUrl, ImtConfigGroup imtConfigGroup) {
		super(TransportMode.truck);
		this.fleetSpecificationUrl = fleetSpecificationUrl;
		this.imtConfigGroup = imtConfigGroup;
	}

	@Override
	public void install() {
		// Register the transport mode as DVRP mode
		DvrpModes.registerDvrpMode(binder(), getMode());

		// Install routing network module with dynamic re-routing disabled
		install(new DvrpModeRoutingNetworkModule(getMode(), false));

		// Bind the travel time estimator
		bindModal(TravelTime.class).to(Key.get(TravelTime.class, Names.named(DvrpTravelTimeModule.DVRP_ESTIMATED)));

		// Install fleet specification with the provided URL and truck type
		install(new FleetModule(getMode(), fleetSpecificationUrl, createTruckType()));

		// Install QSim (MATSim's simulation engine) related modules and bind components
		installQSimModule(new AbstractDvrpModeQSimModule(getMode()) {
			@Override
			protected void configureQSim() {
				install(new VrpAgentSourceQSimModule(getMode()));

				// Add request creator component
				addModalComponent(RequestCreator.class);

				// Bind the optimizer and action creator components
				bindModal(VrpOptimizer.class).to(Optimizer.class).asEagerSingleton();
				bindModal(VrpAgentLogic.DynActionCreator.class).to(ActionCreator.class).asEagerSingleton();
			}
		});
	}

	/**
	 * Creates a VehicleType object representing a truck, with predefined properties such as length, PCU equivalents, and seating capacity.
	 *
	 * @return the created VehicleType object
	 */
	private static VehicleType createTruckType() {
		VehicleType truckType = VehicleUtils.getFactory().createVehicleType(Id.create("truckType", VehicleType.class));
		truckType.setLength(15.);
		truckType.setPcuEquivalents(2.5);
		truckType.getCapacity().setSeats(1);
		return truckType;
	}
}
