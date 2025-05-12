package interfaces;

public interface Switchable {
    void turnOn() throws Exception;
    void turnOff() throws Exception;
    boolean isOn();
}
