package util;

import classes.devices.Outlet;

import java.util.ArrayList;

public class PowerConsumption extends ValueGenerator<Outlet> {
    private static final PowerConsumption instance = new PowerConsumption("Outlet power consumption");
    private final String name;
    private final ArrayList<Outlet> observers = new ArrayList<>();
    private final Object statusLock = new Object();
    public PowerConsumption(String name) {
        super();
        this.name = name;
    }

    public static PowerConsumption getInstance() {
        return instance;
    }

    @Override
    public void addObserver(Outlet observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Outlet observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Outlet observer : observers) {
            observer.updateObservedValue();
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
