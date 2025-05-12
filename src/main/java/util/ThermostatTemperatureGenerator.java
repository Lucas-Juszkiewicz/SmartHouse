package util;

import classes.devices.Thermostat;
import enums.SimulationStatus;
import interfaces.ObservableDevice;

import java.util.ArrayList;
import java.util.EnumSet;

public class ThermostatTemperatureGenerator extends TemperatureGenerator  implements ObservableDevice<Thermostat> {
    private static final ThermostatTemperatureGenerator instance = new ThermostatTemperatureGenerator("Thermostat temperature");
    private final String name;
    private final ArrayList<Thermostat> observers = new ArrayList<>();
    private final EnumSet<SimulationStatus> possibleSimulationStatuses = EnumSet.of(SimulationStatus.HEATING, SimulationStatus.COOLING);
    private Enum<SimulationStatus> simulationStatus;

    public ThermostatTemperatureGenerator(String name) {
        super();
        this.name = name;
    }

    public static ThermostatTemperatureGenerator getInstance() {
        return instance;
    }

    public Enum<SimulationStatus> getSimulationStatus() {
        return simulationStatus;
    }

    public void setSimulationStatus(Enum<SimulationStatus> simulationStatus) {
        this.simulationStatus = simulationStatus;
    }

    @Override
    public void addObserver(Thermostat observer) {
        observers.add(observer);
        System.out.printf("%s\n (%s) has been added to the %ss observers list", observer.getName(), observer.getId(), this.name);
    }

    @Override
    public void removeObserver(Thermostat observer) {
        observers.remove(observer);
        System.out.printf("%s\n (%s) has been removed from the %ss observers list.", observer.getName(), observer.getId(), name);
    }

    @Override
    public void notifyObservers() {
        for (Thermostat observer : observers) {
            observer.updateObservedValue();
        }
    }

    public void simulateCooling() {
        this.setSimulationStatus(SimulationStatus.COOLING);
        while(getSimulationStatus().equals(SimulationStatus.COOLING)) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }
}
