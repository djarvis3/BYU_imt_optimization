package org.matsim.codeexamples.events.taxiHandling;

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
public class MyTaxiResponseTimeHandler implements PersonEntersVehicleEventHandler {

    private double[] volumeTaxi_3_1;

    public MyTaxiResponseTimeHandler() {
        reset(0);
    }

    public double getTravelTime(int slot) {
        return this.volumeTaxi_3_1[slot];
    }

    private int getSlot(double time) {
        return (int) time / 3600;
    }

    @Override
    public void reset(int iteration) {
        this.volumeTaxi_3_1 = new double[24];
    }

    @Override
    public void handleEvent(PersonEntersVehicleEvent event) {
        String eventType = String.valueOf(event.getEventType());
        String vehicleId = String.valueOf(event.getVehicleId());
        if ((eventType.equals("PersonEntersVehicle")) && (vehicleId.equals("taxi_3_1"))) {
            this.volumeTaxi_3_1[getSlot(event.getTime())]++;
        }
    }

    public void writeChart(String filename) {
        double[] hours = new double[24];
        for (double i = 0.0; i < 24.0; i++) {
            hours[(int) i] = i;
        }
        XYLineChart chart = new XYLineChart("Taxi 3_1 Passenger", "hour", "entrances");
        chart.addSeries("times", hours, this.volumeTaxi_3_1);
        chart.saveAsPng(filename, 800, 600);
    }
}
