package classes.devices;

import classes.SmartDevice;
import enums.DeviceStatus;
import enums.DeviceType;
import enums.RoomType;
import interfaces.Switchable;
import util.DeviceRegistry;

import java.util.EnumSet;
import java.util.UUID;

public class Radiator extends SmartDevice implements Switchable {
    private final DeviceType type = DeviceType.RADIATOR;
    private UUID connectedThermostatId;

    public Radiator(String name, DeviceStatus status, RoomType location, UUID houseId) throws Exception {
        super(name, status, location, houseId);
        this.setPossibleStatuses(EnumSet.of(
                DeviceStatus.ON,
                DeviceStatus.OFF,
                DeviceStatus.STANDBY,
                DeviceStatus.NEEDS_REPAIR));
    }

    @Override
    public void turnOn() throws Exception {
        this.setStatus(DeviceStatus.ON);
    }

    @Override
    public void turnOff() throws Exception{
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
            ((Thermostat) thermostat).connectAirConditionerOrRadiator(this);
        }else {
            DeviceType type = thermostat.getType();
            System.out.printf("Radiator can't be connected to the device with type %s\n", type);
        }
    }

    public void disconnectFromThermostat(){
        SmartDevice thermostat = DeviceRegistry.getInstance().getItem(this.connectedThermostatId);
        ((Thermostat) thermostat).disconnectAirConditionerOrRadiator(this);
    }

    public void startHeating(){

    }

    @Override
    public void simulate() {

    }

    @Override
    public void stopSimulation() {

    }
}
