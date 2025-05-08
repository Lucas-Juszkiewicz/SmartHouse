package util;

import classes.SmartDevice;

import java.util.ArrayList;
import java.util.Map;

public class TemperatureGenerator {
    private double temperature;
    private boolean isGenerating = false;
    private ArrayList<SmartDevice> observers = new ArrayList<>();

    public TemperatureGenerator() {
    }

    public void addObserver(SmartDevice observer) {
        observers.add(observer);
        System.out.printf("Observer %s\n (%s) has been added.", observer.getName(), observer.getId());
    }

    public void removeObserver(SmartDevice observer) {
        observers.remove(observer);
        System.out.printf("Observer %s\n (%s) has been removed.", observer.getName(), observer.getId());
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getTemperature() {
        return temperature;
    }

    public void startGenerating() {
        isGenerating = true;
    }

    public void stopGenerating() {
        isGenerating = false;
    }

    public double generate(double minTemperature, double maxTemperature) {
        isGenerating = true;
        do {
            this.temperature = minTemperature + Math.random() * (maxTemperature - minTemperature);
            for (SmartDevice observer : observers) {
                observer.updateTemperature();
            }
        } while (isGenerating);
    }
}
