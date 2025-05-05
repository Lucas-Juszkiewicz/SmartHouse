package util;

import Exceptions.DeviceNotFoundException;
import classes.Device;

import java.util.HashMap;
import java.util.UUID;

public class DevicesStorage {
    private static HashMap<UUID, Device> devices = new HashMap<>();

    public static UUID generateId() {
        UUID id;
        do {
            id = UUID.randomUUID();
        } while (devices.containsKey(id));
        return id;
    }

    public static String addDevice(Device device) {
        devices.put(device.getId(), device);
        return "Device added";
    }

    public static Device getDeviceById(UUID id) {
        Device device = devices.get(id);
        if (device == null) {
            throw new DeviceNotFoundException(id);
        }
        return device;
    }

    public static String updateDevice(Device device) {
        devices.put(device.getId(), device);
        return "Device updated";
    }

    public static String removeDevice(UUID id) {
        devices.remove(id);
        return "Device removed";
    }


}
