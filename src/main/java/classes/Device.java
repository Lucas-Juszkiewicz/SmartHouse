package classes;

import enums.DeviceType;
import util.DevicesStorage;
import util.Warning;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public abstract class Device {
    private UUID id;
    private String name;
    private DeviceType type;
    private boolean isOn = false;
    private ArrayList<Warning> warnings;

    public Device(String name, DeviceType type) {
        this.id = DevicesStorage.generateId();
        this.name = name;
        this.type = type;
        System.out.println(DevicesStorage.addDevice(this));
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setIsOn(boolean on) {
        isOn = on;
    }

    public ArrayList<Warning> getWarnings() {
        return warnings;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return Objects.equals(id, device.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

