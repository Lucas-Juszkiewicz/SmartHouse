package classes;

import enums.RoomType;
import util.DeviceRegistry;
import util.HouseRegistry;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Room {
    private final String name;
    private final RoomType type;
    private final Double area;
    private final UUID houseId;

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
        HouseRegistry.getInstance().getItems().get(houseId).addRoom(this);
    }
}
