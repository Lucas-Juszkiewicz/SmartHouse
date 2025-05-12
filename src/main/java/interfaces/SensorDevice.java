package interfaces;

public interface SensorDevice<T> {
    T readValue();
    String getUnit();
}
