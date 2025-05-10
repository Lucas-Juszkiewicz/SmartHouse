package util;

import classes.SmartDevice;

import java.util.ArrayList;

public abstract class TemperatureGenerator {
    private final String name;
    private double temperature;
    private final ArrayList<Double> temperatureHistory = new ArrayList<>();
    private boolean isGenerating = false;
    private final ArrayList<SmartDevice> observers = new ArrayList<>();

    public TemperatureGenerator(String name) {
        this.name = name;
    }

    public double getTemperature() {
        return temperature;
    }

    public ArrayList<Double> getTemperatureHistory() {
        return temperatureHistory;
    }

    public void addObserver(SmartDevice observer) {
        observers.add(observer);
        System.out.printf("%s\n (%s) has been added to the %ss observers list", observer.getName(), observer.getId(), name);
    }

    public void removeObserver(SmartDevice observer) {
        observers.remove(observer);
        System.out.printf("%s\n (%s) has been removed from the %ss observers list.", observer.getName(), observer.getId(), name);
    }

    public void startGenerating(double minTemperature, double maxTemperature) {
        isGenerating = true;
        new Thread(() -> generate(minTemperature, maxTemperature)).start();
    }

    public void stopGenerating() {
        isGenerating = false;
    }

    public void generate(double minTemperature, double maxTemperature) {
        this.temperature = minTemperature + ((maxTemperature - minTemperature) / 2); // starter value
        while (isGenerating) {
            double plusMinusOrNoChange = 0.1 + Math.random() * (3.00 - 0.1);
            double oneTempStep = 0.1 + Math.random() * (2.00 - 0.1);
            if (plusMinusOrNoChange > 1.6) {
                temperature = Math.min(temperature + oneTempStep, maxTemperature);
            } else if (plusMinusOrNoChange < 1.3) {
                temperature = Math.max(temperature - oneTempStep, minTemperature);
            }
            temperatureHistory.add(temperature);
            for (SmartDevice observer : observers) {
                observer.updateTemperature();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
