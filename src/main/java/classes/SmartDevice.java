package classes;

import enums.DeviceType;
import enums.DeviceStatus;
import util.DevicesStorage;
import util.Warning;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Objects;
import java.util.UUID;

public abstract class SmartDevice {
    private UUID id;
    private String name;
    private DeviceType type;
    private DeviceStatus status;
    private final EnumSet<DeviceStatus> possibleStatuses;
    private ArrayList<Warning> warnings;

    public SmartDevice(String name, DeviceType type, EnumSet<DeviceStatus> possibleStatuses) {
        this.id = DevicesStorage.generateId();
        this.name = name;
        this.type = type;
        this.possibleStatuses = possibleStatuses;
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

    public DeviceStatus getStatus() {
        return status;
    }

    public void setStatus(DeviceStatus status) throws Exception {
        if (possibleStatuses.contains(status)) {
            this.status = status;
        }else {
            throw new IllegalArgumentException("Status " + status + " is not allowed for this device.");
        }
    }


    public ArrayList<Warning> getWarnings() {
        return warnings;
    }

    public abstract void simulate();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SmartDevice smartDevice = (SmartDevice) o;
        return Objects.equals(id, smartDevice.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "SmartDevice\n" +
                "id: " + id + "\n" +
                "name: " + name + "\n" +
                "type: " + type + "\n" +
                "status: " + "\n\n";
    }
}

