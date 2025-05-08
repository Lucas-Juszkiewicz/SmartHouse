package util;

import classes.SmartDevice;

public class DeviceRegistry extends Registry<SmartDevice> {
    private static final DeviceRegistry instance = new DeviceRegistry();

    private DeviceRegistry() {
        super(SmartDevice.class);
    }

    public static DeviceRegistry getInstance() {
        return instance;
    }
}
