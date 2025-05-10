package classes;

import enums.DeviceType;
import enums.DeviceStatus;
import enums.RoomType;
import util.DeviceRegistry;
import util.HouseRegistry;
import util.TemperatureGenerator;
import util.TerminalColors;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Objects;
import java.util.UUID;

public abstract class SmartDevice {
    private final UUID id;
    private final String name;
    private DeviceType type;
    private DeviceStatus status;
    private final EnumSet<DeviceStatus> possibleStatuses;
    private RoomType location; //in which room the device is located
    private final ArrayList<Warning> warnings = new ArrayList<>();

    public SmartDevice(
            String name,
            DeviceType type,
            EnumSet<DeviceStatus> possibleStatuses,
            DeviceStatus status,
            RoomType location,
            UUID houseId
    ) throws Exception {
        this.id = DeviceRegistry.getInstance().generateId();
        this.name = name;
        this.type = type;
        this.possibleStatuses = possibleStatuses;
        setStatus(status);
        setLocation(location, houseId);
        System.out.println(DeviceRegistry.getInstance().addItem(id, this)); // it adds device to the DeviceRegistry
    }

    public SmartDevice(
            String name,
            EnumSet<DeviceStatus> possibleStatuses,
            DeviceStatus status,
            RoomType location,
            UUID houseId
    ) throws Exception {
        this.id = DeviceRegistry.getInstance().generateId();
        this.name = name;
        this.possibleStatuses = possibleStatuses;
        setStatus(status);
        setLocation(location, houseId);
        System.out.println(DeviceRegistry.getInstance().addItem(id, this)); // it adds device to the DeviceRegistry
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public DeviceType getType() {
        return type;
    }

    public DeviceStatus getStatus() {
        return status;
    }

    public void setStatus(DeviceStatus status) throws Exception {
        if (possibleStatuses.contains(status)) {
            this.status = status;
        } else {
            throw new IllegalArgumentException("Status " + status + " is not allowed for this device.");
        }
    }

    public RoomType getLocation() {
        return this.location;
    }

    public void setLocation(RoomType location, UUID houseId) {
        House house = HouseRegistry.getInstance().getItem(houseId);
        if (house.getRooms().contains(location)) {
            this.location = location;
        } else {
            throw new IllegalArgumentException("This house does not contain this kind of room.");
        }
    }

    public ArrayList<Warning> getWarnings() {
        return warnings;
    }

    public void updateTemperature() {

    }

    public abstract void simulate();
    public abstract void stopSimulation();

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
        return "\tThis SmartDevice information \n" +
                "\tid: " + TerminalColors.ANSI_YELLOW + id + TerminalColors.ANSI_RESET + "\n" +
                "\tname: " + TerminalColors.ANSI_YELLOW + name + TerminalColors.ANSI_RESET + "\n" +
                "\ttype: " + TerminalColors.ANSI_YELLOW + type + TerminalColors.ANSI_RESET + "\n" +
                "\tstatus: " + TerminalColors.ANSI_YELLOW + status + TerminalColors.ANSI_RESET + "\n\n";
    }
}

