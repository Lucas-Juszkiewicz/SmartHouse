package classes.devices;

import classes.SmartDevice;
import enums.DeviceStatus;
import enums.DeviceType;
import enums.RoomType;
import interfaces.DeviceObserver;
import interfaces.Switchable;
import util.Rule;
import util.ThermostatTemperatureGenerator;

import java.util.*;
import java.util.stream.Collectors;

public class Thermostat extends SmartDevice implements DeviceObserver {
    private final DeviceType type = DeviceType.THERMOSTAT;
    private double airTemperature;
    private double desiredTemperature;
    private Deque<Double> temperatureHistory = new ArrayDeque<>();
    private final ArrayList<AirConditioner> airConditioners = new ArrayList<>();
    private final ArrayList<Radiator> radiators = new ArrayList<>();

    public Thermostat(String name, EnumSet<DeviceStatus> possibleStatuses, DeviceStatus status, RoomType location, UUID houseId, Double desiredTemperature) throws Exception {
        super(name, possibleStatuses, status, location, houseId);
        this.desiredTemperature = desiredTemperature;
    }

    @Override
    public DeviceType getType() {
        return type;
    }

    public void showTemperatureHistory() {

    }

    public void startTemperatureControl() {

    }

    public void stopTemperatureControl() {

    }

    public void connectAirConditionerOrRadiator(SmartDevice device) {
        String type = null;

        if (device instanceof AirConditioner airConditioner) {
            airConditioners.add(airConditioner);
            type = "AirConditioner";
        } else if (device instanceof Radiator radiator) {
            radiators.add(radiator);
            type = "Radiator";
        }

        if (type != null) {
            System.out.printf("%s in %s(%s) has been connected to the thermostat.\n",
                    type, device.getLocation(), device.getId());
        } else {
            System.out.printf("You have passed %s instead of AirConditioner or Radiator\n",
                    device.getType());
        }
    }


    public void disconnectAirConditionerOrRadiator(SmartDevice device) {
        String type = null;

        if (device instanceof AirConditioner airConditioner) {
            airConditioners.remove(airConditioner);
            type = "AirConditioner";
        } else if (device instanceof Radiator radiator) {
            radiators.remove(radiator);
            type = "Radiator";
        }

        if (type != null) {
            System.out.printf("%s in %s(%s) has been disconnected.\n",
                    type, device.getLocation(), device.getId());
        } else {
            System.out.printf("You have passed %s instead of AirConditioner or Radiator\n",
                    device.getType());
        }
    }


    public void disconnectDevicesByLocation(RoomType location, DeviceType type) {
        List<? extends SmartDevice> toRemove = List.of();
        String typeName = type.toString();

        switch (type) {
            case AIR_CONDITIONER -> {
                toRemove = airConditioners.stream()
                        .filter(ac -> ac.getLocation().equals(location))
                        .toList();
                airConditioners.removeAll(toRemove);
            }
            case RADIATOR -> {
                toRemove = radiators.stream()
                        .filter(rad -> rad.getLocation().equals(location))
                        .toList();
                radiators.removeAll(toRemove);
            }
            default -> {
                System.out.printf("Unsupported device type: %s\n", type);
                return;
            }
        }

        int count = toRemove.size();

        if (count > 0) {
            System.out.printf("%ss in %s (count %d) %s been disconnected.\n",
                    typeName, location, count, (count > 1 ? "had" : "has"));
        } else {
            System.out.printf("There is no %s in %s.\n", typeName, location);
        }
    }


    @Override
    public void simulate() {
        ThermostatTemperatureGenerator.getInstance().startGenerating(0.00, 40.00);
        ThermostatTemperatureGenerator.getInstance().addObserver(this);
    }

    @Override
    public void stopSimulation() {
        ThermostatTemperatureGenerator.getInstance().removeObserver(this);
        ThermostatTemperatureGenerator.getInstance().stopGenerating();
    }

    @Override
    public void updateObservedValue() {
        this.airTemperature = ThermostatTemperatureGenerator.getInstance().getTemperature();
        if (temperatureHistory.size() == 10) {
            temperatureHistory.removeFirst();
        }
        temperatureHistory.addLast(this.airTemperature);

        if (this.airTemperature + 1.00 > desiredTemperature) {
            airConditioners.forEach(AirConditioner::startCooling);
        }else if (this.airTemperature - 1.00 < desiredTemperature) {
            radiators.forEach(Radiator::startHeating);
        }else {
            airConditioners.forEach(AirConditioner::stopCooling);
            radiators.forEach(Radiator::stopHeating);
        }
    }

    @Override
    public String toString() {
        return "Thermostat \n" +
                "airTemperature: " + airTemperature + "\n" +
                "desiredTemperature: " + desiredTemperature + "\n" + "\n" + super.toString();
    }
}
