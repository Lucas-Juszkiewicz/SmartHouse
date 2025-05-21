package util;

import classes.devices.Thermostat;
import enums.DeviceStatus;
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
    private volatile Enum<SimulationStatus> simulationStatus;
    private Thread coolingThread;
    private Thread heatingThread;

    public GroundFloorTemperature(String name) {
        super();
        this.name = name;
    }

    public static GroundFloorTemperature getInstance() {
        return instance;
    }

    public Enum<SimulationStatus> getSimulationStatus() {
        return simulationStatus;
    }

    public void setSimulationStatus(Enum<SimulationStatus> simulationStatus) {
        if (possibleSimulationStatuses.contains(simulationStatus)) {
            this.simulationStatus = simulationStatus;
        }else {
            System.out.println("Temperature simulation status could not be set");
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
        for (Thermostat observer : observers) {
            try {
                observer.setStatus(DeviceStatus.COOLING);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        coolingThread = new Thread(() -> {
            while (getSimulationStatus().equals(SimulationStatus.COOLING)) {
                this.setTemperature(this.getTemperature() - 0.3);
                System.out.println("GFT " + this.getSimulationStatus() + " " + this.getTemperature());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        coolingThread.start();
    }

    public void simulateHeating() {
        this.setSimulationStatus(SimulationStatus.HEATING);
        for (Thermostat observer : observers) {
            try {
                observer.setStatus(DeviceStatus.HEATING);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        heatingThread = new Thread(() -> {
            while (getSimulationStatus().equals(SimulationStatus.HEATING)) {
                this.setTemperature(this.getTemperature() + 0.3);
                System.out.println("GFT " + this.getSimulationStatus() + " " + this.getTemperature());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        heatingThread.start();
    }
}
