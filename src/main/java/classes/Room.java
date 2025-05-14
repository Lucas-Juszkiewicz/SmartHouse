package classes;

import enums.RoomType;
import util.DeviceRegistry;
import util.HouseRegistry;
import util.TerminalColors;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Room {
    private final String name;
    private final RoomType type;
    private final Double area;
    private final UUID houseId;
    private String houseName;

    public Room(String name, RoomType type, Double area, UUID houseId) {
        this.name = name;
        this.type = type;
        this.area = area;
        this.houseId = houseId;
        associateWithHouse();
    }

    public String getName() {
        return name;
    }

    public RoomType getType() {
        return type;
    }

    public Double getArea() {
        return area;
    }

    public UUID getHouseId() {
        return houseId;
    }

    public List<SmartDevice> getSmartDevices() {
        return DeviceRegistry.getInstance().getDeviceByRoom(this.type, this.houseId);
    }

    private void associateWithHouse(){
        House house = HouseRegistry.getInstance().getItems().get(houseId);
        house.addRoom(this);
        this.houseName = house.getName();

    }

    @Override
    public String toString() {
        return TerminalColors.ANSI_BRIGHT_YELLOW + "Room\n" + TerminalColors.ANSI_RESET +
                "in '" + houseName + "'\n" +
                "name: " + name + "\n" +
                "type: " + type + "\n" +
                "area: " + area + "m^2";
    }

    public String toStringNested() {
        return TerminalColors.ANSI_BRIGHT_YELLOW + "\tRoom\n" + TerminalColors.ANSI_RESET +
                "\tname: " + name + "\n" +
                "\ttype: " + type + "\n" +
                "\tarea: " + area + "m^2";
    }
}
