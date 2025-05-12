package classes.devices;

import classes.SmartDevice;
import enums.DeviceStatus;
import enums.DeviceType;
import enums.RoomType;
import interfaces.Switchable;
import util.DeviceRegistry;
import util.ThermostatTemperatureGenerator;

import java.util.EnumSet;
import java.util.UUID;

public class AirConditioner extends SmartDevice implements Switchable {
    private final DeviceType type = DeviceType.AIR_CONDITIONER;
    private UUID connectedThermostatId;

    public AirConditioner(String name, DeviceStatus status, RoomType location, UUID houseId) throws Exception {
        super(name, status, location, houseId);
        this.setPossibleStatuses(EnumSet.of(
                DeviceStatus.ON,
                DeviceStatus.OFF,
                DeviceStatus.STANDBY,
                DeviceStatus.NEEDS_REPAIR,
                DeviceStatus.NEEDS_CLEANING));
    }

    @Override
    public void turnOn() throws Exception {
        this.setStatus(DeviceStatus.ON);
    }

    @Override
    public void turnOff() throws Exception {
        this.setStatus(DeviceStatus.OFF);
    }

    @Override
    public boolean isOn() {
        return getStatus().equals(DeviceStatus.ON);
    }

    public void connectToThermostat(UUID thermostatId){
        this.connectedThermostatId = thermostatId;
        SmartDevice thermostat = DeviceRegistry.getInstance().getItem(thermostatId);
        if(thermostat.getClass() == Thermostat.class){
            ((Thermostat) thermostat).connectAirConditionerOrRadiator(this);
        }else {
            DeviceType type = thermostat.getType();
            System.out.printf("AirConditioner can't be connected to the device with type %s\n", type);
        }
    }

    public void disconnectFromThermostat(){
        SmartDevice thermostat = DeviceRegistry.getInstance().getItem(this.connectedThermostatId);
        ((Thermostat) thermostat).disconnectAirConditionerOrRadiator(this);
    }

    public void startCooling(){
        if(isOn()){
            ThermostatTemperatureGenerator.getInstance().simulateCooling();
        }
    }

    @Override
    public void simulate() {

    }

    @Override
    public void stopSimulation() {

    }
}
