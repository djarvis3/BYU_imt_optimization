package byu.imt.event_Handlers.taxiHandling;

import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.handler.PersonEntersVehicleEventHandler;
import org.matsim.core.utils.charts.XYLineChart;

/**
 * This EventHandler implementation counts the
 * traffic volume on the link with id number 6 and
 * provides a method to write the hourly volumes
 * to a chart png.
 * @author dgrether
 *
 */
public class MyTaxiVolumeHandler implements PersonEntersVehicleEventHandler {

    private double[] volumeImt_1;

    public MyTaxiVolumeHandler() {
        reset(0);
    }

    public double getTravelTime(int slot) {
        return this.volumeImt_1[slot];
    }

    private int getSlot(double time) {
        return (int) time / 3600;
    }

    @Override
    public void reset(int iteration) {
        this.volumeImt_1 = new double[26];
    }

    @Override
    public void handleEvent(PersonEntersVehicleEvent event) {
        String eventType = String.valueOf(event.getEventType());
        String vehicleId = String.valueOf(event.getVehicleId());
        if ((eventType.equals("PersonEntersVehicle")) && (vehicleId.equals("imt_1"))) {
            this.volumeImt_1[getSlot(event.getTime())]++;
        }
    }

    public void writeChart(String filename) {
        double[] hours = new double[26];
        for (double i = 0.0; i < 24.0; i++) {
            hours[(int) i] = i;
        }
        XYLineChart chart = new XYLineChart("imt_1", "hour", "entrances");
        chart.addSeries("times", hours, this.volumeImt_1);
        chart.saveAsPng(filename, 800, 600);
    }
}
