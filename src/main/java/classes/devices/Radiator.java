package classes.devices;

import classes.SmartDevice;
import enums.DeviceStatus;
import enums.DeviceType;
import enums.RoomType;
import enums.SimulationStatus;
import interfaces.DeviceObserver;
import interfaces.Switchable;
import util.DeviceRegistry;
import util.GroundFloorTemperature;
import util.TerminalColors;

import java.util.EnumSet;
import java.util.UUID;

public class Radiator extends SmartDevice implements Switchable, DeviceObserver {
//    private final DeviceType type = DeviceType.RADIATOR;
    private UUID connectedThermostatId;

    public Radiator(String name, RoomType roomType, UUID houseId) throws Exception {
        super(name, DeviceType.RADIATOR, roomType, houseId);
        this.setPossibleStatuses(EnumSet.of(
                DeviceStatus.ON,
                DeviceStatus.OFF,
                DeviceStatus.STANDBY,
                DeviceStatus.NEEDS_REPAIR));
        this.setStatus(DeviceStatus.ON);
    }

    @Override
    public void turnOn() {
        if (this.getStatus() != DeviceStatus.NEEDS_REPAIR) {
            try {
                this.setStatus(DeviceStatus.ON);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {
            System.out.printf("Radiator (%s) can't be turned on. It %s", this.getId(), this.getStatus().toString());
        }
    }

    @Override
    public void turnOff() throws Exception{
        stopHeating();
        this.setStatus(DeviceStatus.OFF);
    }

    @Override
    public boolean isOn() {
        return this.getStatus().equals(DeviceStatus.ON);
    }

    public void connectToThermostat(UUID thermostatId){
        this.connectedThermostatId = thermostatId;
        SmartDevice thermostat = DeviceRegistry.getInstance().getItem(thermostatId);
        if(thermostat.getClass() == Thermostat.class){
            ((Thermostat) thermostat).connect(this);
        }else {
            DeviceType type = thermostat.getType();
            System.out.printf("Radiator can't be connected to the device with type %s\n", type);
        }
    }

    public void disconnectFromThermostat(){
        SmartDevice thermostat = DeviceRegistry.getInstance().getItem(this.connectedThermostatId);
        ((Thermostat) thermostat).disconnect(this);
    }

    public void startHeating(){
        if(isOn()){
            GroundFloorTemperature.getInstance().simulateHeating();
        }
    }

    public void stopHeating(){
        GroundFloorTemperature.getInstance().setSimulationStatus(SimulationStatus.STANDBY);
    }

    @Override
    public void simulate() {

    }

    @Override
    public void stopSimulation() {

    }

    @Override
    public String toString() {
        return TerminalColors.ANSI_YELLOW + "Radiator \n" + TerminalColors.ANSI_RESET +
                "Connected thermostat id: " + TerminalColors.ANSI_YELLOW + connectedThermostatId + TerminalColors.ANSI_RESET + "\n" +
                super.toString();
    }

    @Override
    public String toStringNested() {
        return TerminalColors.ANSI_YELLOW + "\tRadiator \n" + TerminalColors.ANSI_RESET +
                "\tConnected thermostat id: " + TerminalColors.ANSI_YELLOW + connectedThermostatId + TerminalColors.ANSI_RESET + "\n" +
                super.toStringNested();
    }

    @Override
    public void updateObservedValue() {

    }
}
