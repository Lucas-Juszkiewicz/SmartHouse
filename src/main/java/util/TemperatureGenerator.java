package util;

import classes.SmartDevice;
import classes.devices.Thermostat;
import interfaces.ObservableDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

public abstract class TemperatureGenerator<T extends SmartDevice> implements ObservableDevice<T> {
    private double temperature;
    private volatile boolean isGenerating = false;
    private Thread TemperatureGeneratingThreat;
    private final Object temperatureLock = new Object();

    public TemperatureGenerator() {
    }
    public double getTemperature() {
        synchronized (temperatureLock) {
            return temperature;
        }
    }

    public void setTemperature(double temperature) {
        synchronized (temperatureLock) {
            this.temperature = temperature;
        }
    }

    public void startGenerating(double minTemperature, double maxTemperature) {
        isGenerating = true;
        TemperatureGeneratingThreat = new Thread(() -> generate(minTemperature, maxTemperature));
        TemperatureGeneratingThreat.start();
    }

    public void stopGenerating() {
        isGenerating = false;
        TemperatureGeneratingThreat.interrupt();
    }

    public void generate(double minTemperature, double maxTemperature) {
        this.temperature = minTemperature + ((maxTemperature - minTemperature) / 2); // starter value
        while (isGenerating) {
            double plusMinusOrNoChange = 0.1 + Math.random() * (3.00 - 0.1);
            double oneTempStep = 0.1 + Math.random() * (0.20 - 0.1);
            if (plusMinusOrNoChange > 1.5) {
                synchronized (temperatureLock) {
                    temperature = Math.min(temperature + oneTempStep, maxTemperature);
                }
                notifyObservers();
            } else if (plusMinusOrNoChange <= 1.2) {
                synchronized (temperatureLock) {
                    temperature = Math.max(temperature - oneTempStep, minTemperature);
                }
                notifyObservers();
            }
            try {
                Thread.sleep(5000);
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
