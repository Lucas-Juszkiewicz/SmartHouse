package util;

public class ThermostatTemperatureGenerator extends TemperatureGenerator {
    private static final ThermostatTemperatureGenerator instance = new ThermostatTemperatureGenerator("Thermostat temperature");
    public ThermostatTemperatureGenerator(String name) {
        super(name);
    }

    public static ThermostatTemperatureGenerator getInstance() {
        return instance;
    }
}
