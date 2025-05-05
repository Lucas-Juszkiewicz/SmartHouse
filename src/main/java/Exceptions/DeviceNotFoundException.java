package Exceptions;

import classes.Device;

import java.util.UUID;

public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(UUID id) {
        super("Device with ID '" + id + "' not found.");
    }
}
