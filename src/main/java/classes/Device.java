package classes;

import enums.DeviceType;

import java.util.Objects;

public abstract class Device {
    private Long id;
    private String name;
    private DeviceType type;
    private boolean isOn;

    public Device(Long id, String name, DeviceType type, boolean isOn) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.isOn = isOn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public void setOn(boolean on) {
        isOn = on;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Device device)) return false;
        return Objects.equals(id, device.id) && Objects.equals(name, device.name) && type == device.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type);
    }
}

