package classes.devices;

import classes.SmartDevice;
import enums.DeviceStatus;
import enums.DeviceType;
import enums.RoomType;
import util.DeviceRegistry;

import java.util.EnumSet;
import java.util.UUID;

public class Radiator extends SmartDevice {
    private final DeviceType type = DeviceType.RADIATOR;

    public Radiator(String name, EnumSet<DeviceStatus> possibleStatuses, DeviceStatus status, RoomType location, UUID houseId) throws Exception {
        super(name, possibleStatuses, status, location, houseId);
    }

    public void connectToThermostat(UUID thermostatId){
        SmartDevice thermostat = DeviceRegistry.getInstance().getItem(thermostatId);
        if(thermostat.getClass() == Thermostat.class){
            ((Thermostat) thermostat).addAirConditionerOrRadiator(this);
        }else {
            DeviceType type = thermostat.getType();
            System.out.printf("Radiator can't be connected to the device with type %s\n", type);
        }
    }

    @Override
    public void simulate() {

    }

    @Override
    public void stopSimulation() {

    }
}
