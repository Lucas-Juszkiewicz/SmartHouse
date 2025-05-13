package classes.devices;

import classes.SmartDevice;
import enums.DeviceStatus;
import enums.DeviceType;
import enums.RoomType;
import enums.SimulationStatus;
import interfaces.Switchable;
import util.DeviceRegistry;
import util.ThermostatTemperatureGenerator;

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
        if(isOn()){
            ThermostatTemperatureGenerator.getInstance().simulateHeating();
        }
    }

    public void stopHeating(){
        ThermostatTemperatureGenerator.getInstance().setSimulationStatus(SimulationStatus.STANDBY);
    }

    @Override
    public void simulate() {

    }

    @Override
    public void stopSimulation() {

    }
}
