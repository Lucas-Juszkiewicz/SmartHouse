package classes;

import enums.DeviceType;
import enums.DeviceStatus;
import enums.RoomType;
import util.DeviceRegistry;
import util.HouseRegistry;
import util.TerminalColors;

import java.util.*;

public abstract class SmartDevice {
    private final UUID id;
    private final String name;
    private DeviceType type;
    private DeviceStatus status;
    private EnumSet<DeviceStatus> possibleStatuses;
    private final UUID houseId;
    private String houseName;
    private RoomType roomType; //in which room the device is located
    private String location;
    private ArrayList<Warning> warnings = new ArrayList<>();

    public SmartDevice(
            String name,
            DeviceType type,
//            DeviceStatus status,
            RoomType roomType,
            UUID houseId
    ) throws Exception {
        this.id = DeviceRegistry.getInstance().generateId();
        this.name = name;
        this.type = type;
//        setStatus(status);
        this.houseId = houseId;
        this.roomType = roomType;
        setLocation(roomType, houseId);
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

    public UUID getHouseId() {
        return houseId;
    }

    public String getLocation() {
        return this.location;
    }

    public void setStatus(DeviceStatus status) {
        if (possibleStatuses.contains(status)) {
            this.status = status;
        } else {
            throw new IllegalArgumentException("Status " + status + " is not allowed for this device.");
        }
    }

    public EnumSet<DeviceStatus> getPossibleStatuses() {
        return possibleStatuses;
    }

    public void setPossibleStatuses(EnumSet<DeviceStatus> possibleStatuses) {
        this.possibleStatuses = possibleStatuses;
    }

    private void setLocation(RoomType roomType, UUID houseId) {
        House house = HouseRegistry.getInstance().getItem(houseId);
        if (house.getRooms().stream().anyMatch(r -> r.getType() == roomType)) {
            this.houseName = house.getName();
            String roomTypeStr =
                    roomType.name()
                            .toLowerCase(Locale.ROOT)
                            .replace("_", " ");
            String roomTypeStrFirstUppercase =
                    Character.toUpperCase(roomTypeStr.charAt(0)) + roomTypeStr.substring(1);
            this.location = roomTypeStrFirstUppercase + " in '" + this.houseName + "'";
        } else {
            throw new IllegalArgumentException("This house does not contain this kind of room.");
        }
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public ArrayList<Warning> getWarnings() {
        return warnings;
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
        return "id: " + TerminalColors.ANSI_YELLOW + id + TerminalColors.ANSI_RESET + "\n" +
                "name: " + TerminalColors.ANSI_YELLOW + name + TerminalColors.ANSI_RESET + "\n" +
                "type: " + TerminalColors.ANSI_YELLOW + type + TerminalColors.ANSI_RESET + "\n" +
                "status: " + TerminalColors.ANSI_YELLOW + status + TerminalColors.ANSI_RESET + "\n" +
                "location: " + TerminalColors.ANSI_YELLOW + location + TerminalColors.ANSI_RESET + "\n";
    }

    public String toStringNested() {
        return "\tid: " + TerminalColors.ANSI_YELLOW + id + TerminalColors.ANSI_RESET + "\n" +
                "\tname: " + TerminalColors.ANSI_YELLOW + name + TerminalColors.ANSI_RESET + "\n" +
                "\ttype: " + TerminalColors.ANSI_YELLOW + type + TerminalColors.ANSI_RESET + "\n" +
                "\tstatus: " + TerminalColors.ANSI_YELLOW + status + TerminalColors.ANSI_RESET + "\n";
    }
}

