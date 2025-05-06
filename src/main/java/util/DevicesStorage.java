package util;

import Exceptions.DeviceNotFoundException;
import classes.SmartDevice;

import java.util.HashMap;
import java.util.UUID;

public class DevicesStorage {
    private static HashMap<UUID, SmartDevice> devices = new HashMap<>();

    public static UUID generateId() {
        UUID id;
        do {
            id = UUID.randomUUID();
        } while (devices.containsKey(id));
        return id;
    }

    public static String addDevice(SmartDevice smartDevice) {
        devices.put(smartDevice.getId(), smartDevice);
        return "Device added";
    }

    public static SmartDevice getDeviceById(UUID id) {
        SmartDevice smartDevice = devices.get(id);
        if (smartDevice == null) {
            throw new DeviceNotFoundException(id);
        }
        return smartDevice;
    }

    public static String updateDevice(SmartDevice smartDevice) {
        devices.put(smartDevice.getId(), smartDevice);
        return "Device updated";
    }

    public static String removeDevice(UUID id) {
        devices.remove(id);
        return "Device removed";
    }


}
