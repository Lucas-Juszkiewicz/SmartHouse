package util;

import classes.SmartDevice;
import interfaces.ObservableDevice;

public abstract class ValueGenerator<T extends SmartDevice> implements ObservableDevice<T> {
    private double value;
    private volatile boolean isGenerating = false;
    private Thread valueGeneratingThreat;
    private final Object valueLock = new Object();

    public ValueGenerator() {
    }
    public double getValue() {
        synchronized (valueLock) {
            return value;
        }
    }

    public void setValue(double value) {
        synchronized (valueLock) {
            this.value = value;
        }
    }

    public void startGenerating(double minValue, double maxValue, double stepMin, double stepMax) {
        isGenerating = true;
        valueGeneratingThreat = new Thread(() -> generate(minValue, maxValue, stepMin, stepMax));
        valueGeneratingThreat.start();
    }

    public void stopGenerating() {
        isGenerating = false;
        valueGeneratingThreat.interrupt();
    }

    public void generate(double minValue, double maxValue, double stepMin, double stepMax) {
        this.value = minValue + ((maxValue - minValue) / 2); // starter value
        while (isGenerating) {
            double plusMinusOrNoChange = 0.1 + Math.random() * (3.00 - 0.1);
//            double oneValueStep = 0.1 + Math.random() * (0.20 - 0.1);
            double oneValueStep = stepMin + Math.random() * (stepMax - stepMin);
            if (plusMinusOrNoChange > 1.5) {
                synchronized (valueLock) {
                    value = Math.min(value + oneValueStep, maxValue);
                }
                notifyObservers();
            } else if (plusMinusOrNoChange <= 1.2) {
                synchronized (valueLock) {
                    value = Math.max(value - oneValueStep, minValue);
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
