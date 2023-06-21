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

	/**
	 * Constructs an ImtModule with the specified fleet specification URL.
	 *
	 * @param fleetSpecificationUrl the URL of the fleet specification
	 */
	public ImtModule(URL fleetSpecificationUrl) {
		super(TransportMode.truck);
		this.fleetSpecificationUrl = fleetSpecificationUrl;
	}

	@Override
	public void install() {
		DvrpModes.registerDvrpMode(binder(), getMode());
		install(new DvrpModeRoutingNetworkModule(getMode(), false));
		bindModal(TravelTime.class).to(Key.get(TravelTime.class, Names.named(DvrpTravelTimeModule.DVRP_ESTIMATED)));
		install(new FleetModule(getMode(), fleetSpecificationUrl, createTruckType()));
		installQSimModule(new AbstractDvrpModeQSimModule(getMode()) {
			@Override
			protected void configureQSim() {
				install(new VrpAgentSourceQSimModule(getMode()));

				addModalComponent(RequestCreator.class);
				bindModal(VrpOptimizer.class).to(Optimizer.class).asEagerSingleton();
				bindModal(VrpAgentLogic.DynActionCreator.class).to(ActionCreator.class).asEagerSingleton();
			}
		});
	}

	private static VehicleType createTruckType() {
		VehicleType truckType = VehicleUtils.getFactory().createVehicleType(Id.create("truckType", VehicleType.class));
		truckType.setLength(15.);
		truckType.setPcuEquivalents(2.5);
		truckType.getCapacity().setSeats(1);
		return truckType;
	}
}
