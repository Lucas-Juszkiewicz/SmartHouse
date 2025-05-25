package classes.devices;

import classes.SmartDevice;
import enums.DeviceStatus;
import enums.DeviceType;
import enums.RoomType;
import enums.SimulationStatus;
import interfaces.Switchable;
import util.DeviceRegistry;
import util.GroundFloorTemperature;
import util.TerminalColors;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

public class AirConditioner extends SmartDevice implements Switchable {
    private UUID connectedThermostatId;

    public AirConditioner(String name, DeviceStatus status, RoomType roomType, UUID houseId) throws Exception {
        super(name, DeviceType.AIR_CONDITIONER, roomType, houseId);
        this.setPossibleStatuses(EnumSet.of(
                DeviceStatus.ON,
                DeviceStatus.OFF,
                DeviceStatus.OK,
                DeviceStatus.STANDBY,
                DeviceStatus.NEEDS_REPAIR,
                DeviceStatus.NEEDS_CLEANING));
        setStatus(status);
    }

    @Override
    public void turnOn() {
        if (this.getStatus() != DeviceStatus.NEEDS_CLEANING
                &&
                this.getStatus() != DeviceStatus.NEEDS_REPAIR) {
            try {
                this.setStatus(DeviceStatus.ON);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {
            System.out.printf("AirConditioner (%s) can't be turned on. It %s", this.getId(), this.getStatus().toString());
        }
    }

    @Override
    public void turnOff() throws Exception {
        this.setStatus(DeviceStatus.OFF);
    }

    @Override
    public boolean isOn() {
        return getStatus().equals(DeviceStatus.ON);
    }

    public void connectToThermostat(UUID thermostatId) {
        this.connectedThermostatId = thermostatId;
        SmartDevice thermostat = DeviceRegistry.getInstance().getItem(thermostatId);
        if (thermostat.getClass() == Thermostat.class) {
            ((Thermostat) thermostat).connect(this);
        } else {
            DeviceType type = thermostat.getType();
            System.out.printf("AirConditioner can't be connected to the device with type %s\n", type);
        }
    }

    public void disconnectFromThermostat() {
        SmartDevice thermostat = DeviceRegistry.getInstance().getItem(this.connectedThermostatId);
        ((Thermostat) thermostat).disconnect(this);
    }

    public void startCooling() {
        if (isOn()) {
            GroundFloorTemperature.getInstance().simulateCooling();
        } else {
            System.out.printf("AirConditioner (%s) is off\n)", this.getId());
        }
    }

    public void stopCooling() {
        try {
            this.setStatus(DeviceStatus.STANDBY);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        GroundFloorTemperature.getInstance().setSimulationStatus(SimulationStatus.STANDBY);
    }

    @Override
    public void simulate() {
        Set<Thermostat> thermostats = DeviceRegistry.getInstance().getItemByClass(Thermostat.class);
    }

    @Override
    public void stopSimulation() {

    }

    @Override
    public String toString() {
        return TerminalColors.ANSI_YELLOW + "AirConditioner \n" + TerminalColors.ANSI_RESET +
                "Connected thermostat id: " + TerminalColors.ANSI_YELLOW + connectedThermostatId + TerminalColors.ANSI_RESET + "\n" +
                super.toString();
    }

    @Override
    public String toStringNested() {
        return TerminalColors.ANSI_YELLOW + "\tAirConditioner \n" + TerminalColors.ANSI_RESET +
                "\tConnected thermostat id: " + TerminalColors.ANSI_YELLOW + connectedThermostatId + TerminalColors.ANSI_RESET + "\n" +
                super.toStringNested();
    }
}
