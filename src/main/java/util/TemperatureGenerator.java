package util;

import interfaces.ObservableDevice;

import java.util.ArrayList;

public abstract class TemperatureGenerator {
    private double temperature;
    private boolean isGenerating = false;


    public TemperatureGenerator() {
    }
    public double getTemperature() {
        return temperature;
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
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
