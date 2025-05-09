package classes;

import enums.DeviceStatus;
import enums.DeviceType;
import enums.RoomType;
import util.ThermostatTemperatureGenerator;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.UUID;

public class Thermostat extends SmartDevice {
    private double airTemperature;
    private double desiredTemperature;
    private ArrayList<Double> temperatureHistory = new ArrayList<>();
    private final ArrayList<AirConditioner> airConditioners = new ArrayList<>();
    private final ArrayList<Radiator> radiators = new ArrayList<>();

    public Thermostat(String name,
                      DeviceType type,
                      EnumSet<DeviceStatus> possibleStatuses,
                      DeviceStatus status,
                      RoomType location,
                      UUID houseId, double desiredTemperature) throws Exception {
        super(name, type, possibleStatuses, status, location, houseId);
        this.desiredTemperature = desiredTemperature;
    }

    public void updateTemperature() {
        this.airTemperature = ThermostatTemperatureGenerator.getInstance().getTemperature();
        temperatureHistory.add(airTemperature);
    }

    public void showTemperatureHistory() {

    }

    public void startTemperatureControl(){

    }

    public void stopTemperatureControl(){

    }

    @Override
    public void simulate() {
        ThermostatTemperatureGenerator.getInstance().startGenerating(15.00, 40.00);
        ThermostatTemperatureGenerator.getInstance().addObserver(this);
    }

    @Override
    public void stopSimulation() {
        ThermostatTemperatureGenerator.getInstance().removeObserver(this);
        ThermostatTemperatureGenerator.getInstance().stopGenerating();
    }

    @Override
    public String toString() {
        return "Thermostat \n" +
                "airTemperature: " + airTemperature + "\n" +
                "desiredTemperature: " + desiredTemperature + "\n" + "\n" + super.toString();
    }
}
