package util;

import classes.SmartDevice;
import classes.devices.Thermostat;
import interfaces.ObservableDevice;

import java.util.ArrayList;

public abstract class TemperatureGenerator<T extends SmartDevice> implements ObservableDevice<T> {
    private double temperature;
    private boolean isGenerating = false;

    public TemperatureGenerator() {
    }
    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void startGenerating(double minTemperature, double maxTemperature) {
        isGenerating = true;
        new Thread(() -> generate(minTemperature, maxTemperature)).start();
        //I should name this Threat 'TemperatureGeneratingThreat'
    }

    public void stopGenerating() {
        isGenerating = false; // I should stop 'TemperatureGeneratingThreat'
    }

    public void generate(double minTemperature, double maxTemperature) {
        this.temperature = minTemperature + ((maxTemperature - minTemperature) / 2); // starter value
        while (isGenerating) {
            double plusMinusOrNoChange = 0.1 + Math.random() * (3.00 - 0.1);
            double oneTempStep = 0.1 + Math.random() * (2.00 - 0.1);
            if (plusMinusOrNoChange > 1.6) {
                temperature = Math.min(temperature + oneTempStep, maxTemperature);
                notifyObservers();
            } else if (plusMinusOrNoChange < 1.3) {
                temperature = Math.max(temperature - oneTempStep, minTemperature);
                notifyObservers();
            }
            try {
                Thread.sleep(5000);
                // I should try to make the loop 'sleep' not the Threat
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void addObserver(SmartDevice observer) {

    }

    @Override
    public void removeObserver(SmartDevice observer) {

    }

    @Override
    public void notifyObservers() {
    }
}
