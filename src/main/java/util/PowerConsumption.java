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
//        System.out.printf("%s\n (%s) has been added to the %ss observers list", observer.getName(), observer.getId(), this.name);
    }

    @Override
    public void removeObserver(Outlet observer) {
        observers.remove(observer);
//        System.out.printf("%s\n (%s) has been removed from the %ss observers list.", observer.getName(), observer.getId(), name);
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
