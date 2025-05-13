package util;

import classes.SmartDevice;
import enums.RoomType;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DeviceRegistry extends Registry<SmartDevice> {
    private static final DeviceRegistry instance = new DeviceRegistry();

    private DeviceRegistry() {
        super(SmartDevice.class);
    }

    public static DeviceRegistry getInstance() {
        return instance;
    }

    public List<SmartDevice> getDevicesByHouse(UUID houseId) {
        return this.getItems().values().stream()
                .filter(device -> device.getHouseId().equals(houseId))
                .toList();
    }

    public List<SmartDevice> getDeviceByRoom(RoomType roomType, UUID houseId) {
        return getDevicesByHouse(houseId).stream()
                .filter(device -> device.getLocation().equals(roomType))
                .toList();
    }
}
