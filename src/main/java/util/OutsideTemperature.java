package util;

import classes.SmartDevice;
import classes.devices.TemperatureSensor;
import classes.devices.Thermostat;
import enums.SimulationStatus;

import java.util.ArrayList;
import java.util.EnumSet;

public class OutsideTemperature extends ValueGenerator<TemperatureSensor> {
    private static final OutsideTemperature instance = new OutsideTemperature("Outside temperature");
    private final String name;
    private final ArrayList<TemperatureSensor> observers = new ArrayList<>();

    public OutsideTemperature(String name) {
        super();
        this.name = name;
    }

    public static OutsideTemperature getInstance() {
        return instance;
    }

    @Override
    public void addObserver(TemperatureSensor observer) {
        observers.add(observer);
        System.out.printf("%s\n (%s) has been added to the %ss observers list", observer.getName(), observer.getId(), this.name);
    }

    @Override
    public void removeObserver(TemperatureSensor observer) {
        observers.remove(observer);
        System.out.printf("%s\n (%s) has been removed from the %ss observers list.", observer.getName(), observer.getId(), name);
    }

    @Override
    public void notifyObservers() {
        for (TemperatureSensor observer : observers) {
            observer.updateObservedValue();
        }
    }
}
