package classes.devices;

import classes.SmartDevice;
import enums.DeviceStatus;
import enums.DeviceType;
import enums.RoomType;
import interfaces.DeviceObserver;
import interfaces.SensorDevice;
import util.GroundFloorTemperature;
import util.OutsideTemperature;
import util.TerminalColors;

import java.util.EnumSet;
import java.util.UUID;

public class TemperatureSensor extends SmartDevice implements SensorDevice<String>, DeviceObserver {
    private double temperature;
    private final Object lock = new Object();
    public TemperatureSensor(String name, DeviceType type, RoomType roomType, UUID houseId) throws Exception {
        super(name, type, roomType, houseId);
        setPossibleStatuses(
                EnumSet.of(
                        DeviceStatus.NEEDS_REPAIR,
                        DeviceStatus.NEEDS_UPDATE,
                        DeviceStatus.OK
                )
        );
        setStatus(DeviceStatus.OK);
    }

    public double getTemperature() {
        synchronized (lock) {
            return temperature;
        }
    }

    public void setTemperature(double temperature) {
        synchronized (lock) {
            this.temperature = temperature;
        }
    }

    @Override
    public void simulate() {
        OutsideTemperature.getInstance().startGenerating(-40.00, 40.00, 0.1, 0.5);
        OutsideTemperature.getInstance().addObserver(this);
    }

    @Override
    public void stopSimulation() {
        OutsideTemperature.getInstance().removeObserver(this);
        OutsideTemperature.getInstance().stopGenerating();
    }

    @Override
    public String readValue() {
        synchronized (lock) {
           return this.temperature+getUnit();
        }
    }

    @Override
    public String getUnit() {
        return "'C";
    }

    @Override
    public void updateObservedValue() {
        setTemperature(OutsideTemperature.getInstance().getValue());
    }

    @Override
    public String toString() {
        return "\n" + TerminalColors.ANSI_YELLOW + "Temperature sensor \n" + TerminalColors.ANSI_RESET +
                "Temperature: " + TerminalColors.ANSI_YELLOW + readValue() + TerminalColors.ANSI_RESET + "\n" +
                super.toString();
    }

    public String toStringNested() {
        return TerminalColors.ANSI_YELLOW + "\tTemperature sensor \n" + TerminalColors.ANSI_RESET +
                "\tTemperature: " + TerminalColors.ANSI_YELLOW + readValue() + TerminalColors.ANSI_RESET + "\n" +
                super.toStringNested();
    }
}
