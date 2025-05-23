package util;

import classes.devices.Thermostat;
import enums.SimulationStatus;

import java.util.ArrayList;
import java.util.EnumSet;

public class GroundFloorTemperature extends TemperatureGenerator<Thermostat> {
    private static final GroundFloorTemperature instance = new GroundFloorTemperature("Thermostat temperature");
    private final String name;
    private final ArrayList<Thermostat> observers = new ArrayList<>();
    private final EnumSet<SimulationStatus> possibleSimulationStatuses = EnumSet.of(
            SimulationStatus.HEATING,
            SimulationStatus.COOLING,
            SimulationStatus.STANDBY);
    private Enum<SimulationStatus> simulationStatus;
    private final Object statusLock = new Object();

    public GroundFloorTemperature(String name) {
        super();
        this.name = name;
    }

    public static GroundFloorTemperature getInstance() {
        return instance;
    }

    public Enum<SimulationStatus> getSimulationStatus() {
        synchronized (statusLock) {
            return simulationStatus;
        }
    }

    public void setSimulationStatus(Enum<SimulationStatus> simulationStatus) {
        synchronized (statusLock) {
        this.simulationStatus = simulationStatus;
        }
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
        new Thread(() -> {
            while (getSimulationStatus().equals(SimulationStatus.COOLING)) {
                this.setTemperature(this.getTemperature() - 0.5);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    public void simulateHeating() {
        this.setSimulationStatus(SimulationStatus.HEATING);
        new Thread(() -> {
            while (getSimulationStatus().equals(SimulationStatus.HEATING)) {
                this.setTemperature(this.getTemperature() + 0.5);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
}
