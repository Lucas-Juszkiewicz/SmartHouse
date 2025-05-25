package util;

import classes.House;
import classes.Room;
import classes.SmartDevice;
import classes.devices.*;
import enums.DeviceStatus;
import enums.DeviceType;
import enums.RoomType;
import exceptions.NotFoundInRegistryException;

import java.util.*;

public class CliController {
    private boolean close = false;
    private Scanner scanner;

    public CliController(Scanner scanner) {
        this.scanner = scanner;
    }

    public boolean getClose() {
        return close;
    }

    public void setClose(boolean close) {
        this.close = close;
    }

    public Thread cliThread = new Thread(() -> {
        while (!close) {
            System.out.println(TerminalColors.ANSI_GREEN + "__________________________________" + TerminalColors.ANSI_RESET + "\nEnter command: ");
            String input = scanner.nextLine().toUpperCase().trim();
            switch (input) {
                case "HOUSE_CREATE":
                    houseCreate();
                    break;
                case "HOUSE_DELETE":
                    break;
                case "HOUSE_SHOW":
                    showHouseDetails();
                    break;
                case "ROOM_CREATE":
                    createRoom();
                    break;
                case "ROOM_DELETE":
                    break;
                case "DEVICE_CREATE":
                    createDevice();
                    break;
                case "DEVICE_FIND":
                    findDevice();
                    break;
                case "DEVICE_DELETE":
                    break;
                case "SHOW":
                    listAll();
                    break;
                case "CONNECT_THERMOSTAT":
                    connectToThermostat();
                    break;
                case "DEMO":
                    demo();
                    break;
                case "SIMULATE":
                    simulateDevice();
                    break;
                case "SIMULATE_STOP":
                    stopSimulationForDevice();
                    break;
                case "DEVICE_STATUS":
                    deviceStatus();
                    break;
                case "ON":
                    on();
                    break;
                case "OFF":
                    off();
                    break;
                case "HELP":
                    break;
                case "CLOSE":
                    close = true;
                    break;
                default:
                    break;
            }
        }
        Thread.currentThread().interrupt();
    });

    private void houseCreate() {
        System.out.print("Enter house name: ");
        String name = scanner.nextLine().trim();

        double x = 0;
        double y = 0;
        boolean validInput = false;

        while (!validInput) {
            try {
                System.out.print("Enter latitude coordinate: ");
                x = Double.parseDouble(scanner.nextLine().trim());

                System.out.print("Enter longitude coordinate: ");
                y = Double.parseDouble(scanner.nextLine().trim());

                validInput = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Coordinates must be numbers. Please try again.");
            }
        }

        Coordinates coordinates = new Coordinates(x, y);
        House house = new House(name, coordinates);

        System.out.println("House created successfully:");
        System.out.println(house);
    }

    private void createRoom() {
        try {
            System.out.print("Enter room name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter house UUID: ");
            String houseIdStr = scanner.nextLine().trim();
            UUID houseId;
            try {
                houseId = UUID.fromString(houseIdStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid UUID format.");
                return;
            }

            System.out.print("Choose the room type from a list:");
            RoomType type;
            for (RoomType rt : RoomType.values()) {
                System.out.println(" - " + rt);
            }

            System.out.print("Enter room type (e.g., KITCHEN, BEDROOM, BATHROOM): ");
            String typeStr = scanner.nextLine().trim().toUpperCase();
            try {
                type = RoomType.valueOf(typeStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid room type. Allowed types: " + Arrays.toString(RoomType.values()));
                return;
            }

            System.out.print("Enter room area in m²: ");
            double area = Double.parseDouble(scanner.nextLine().trim());

            if (!HouseRegistry.getInstance().getItems().containsKey(houseId)) {
                System.out.println("House with the given UUID does not exist.");
                return;
            }

            Room room = new Room(name, type, area, houseId);
            System.out.println("Room created successfully:");
            System.out.println(room);

        } catch (Exception e) {
            System.out.println("Error while creating room: " + e.getMessage());
        }
    }

    private void createDevice() {
        try {
            System.out.print("Enter device name: ");
            String name = scanner.nextLine().trim();
            System.out.println(TerminalColors.ANSI_GRAY + "__________________________________" + TerminalColors.ANSI_RESET);
            System.out.println("Available device types:");
            for (DeviceType dt : DeviceType.values()) {
                System.out.println(" - " + dt);
            }
            System.out.println(TerminalColors.ANSI_GRAY + "__________________________________" + TerminalColors.ANSI_RESET);
            System.out.print("Enter device type: ");
            DeviceType type;
            try {
                type = DeviceType.valueOf(scanner.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid device type.");
                return;
            }
            System.out.println(TerminalColors.ANSI_GRAY + "__________________________________" + TerminalColors.ANSI_RESET);
            System.out.print("Enter house UUID: ");
            UUID houseId;
            try {
                houseId = UUID.fromString(scanner.nextLine().trim());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid UUID format.");
                return;
            }
            System.out.println(TerminalColors.ANSI_GRAY + "__________________________________" + TerminalColors.ANSI_RESET);
            if (!HouseRegistry.getInstance().getItems().containsKey(houseId)) {
                System.out.println("House with the given UUID does not exist.");
                return;
            }

            House house;
            try {
                house = HouseRegistry.getInstance().getItem(houseId);
            } catch (NotFoundInRegistryException e) {
                System.out.println("House with this UUID does not exist.");
                return;
            }

            if (house.getRooms().isEmpty()) {
                System.out.println("There are no rooms in this house. You need to create a room first.");
                return;
            }
            System.out.println("Rooms in this house:");
            house.getRooms().forEach(room ->
                    System.out.println(" - " + room.getName() + " (" + room.getType() + ")")
            );

            System.out.print("Enter room name where device will be installed: ");
            String roomName = scanner.nextLine().trim();

            Room chosenRoom = house.getRooms().stream()
                    .filter(r -> r.getName().equalsIgnoreCase(roomName))
                    .findFirst()
                    .orElse(null);

            if (chosenRoom == null) {
                System.out.println("Room not found in this house.");
                return;
            }

            SmartDevice device = null;
            switch (type) {
                case THERMOSTAT -> {
                    System.out.print("Enter desired temperature: ");
                    double temp = Double.parseDouble(scanner.nextLine().trim());

                    DeviceStatus thermostatStatus = askForStatus(EnumSet.of(
                            DeviceStatus.NEEDS_REPAIR,
                            DeviceStatus.NEEDS_UPDATE,
                            DeviceStatus.OK));

                    device = new Thermostat(name, thermostatStatus, chosenRoom.getType(), houseId, temp, scanner);
                }
                case AIR_CONDITIONER -> {
                    DeviceStatus acStatus = askForStatus(EnumSet.of(
                            DeviceStatus.ON,
                            DeviceStatus.OFF,
                            DeviceStatus.STANDBY,
                            DeviceStatus.NEEDS_REPAIR,
                            DeviceStatus.NEEDS_CLEANING));

                    device = new AirConditioner(name, acStatus, chosenRoom.getType(), houseId);
                }
                case PLUG, OUTLET -> {
                    device = new Outlet(name, type, chosenRoom.getType(), houseId, scanner);
                }
                case RADIATOR -> {
                    device = new Radiator(name, chosenRoom.getType(), houseId);
                }
                case LIGHT -> {
                    device = new Lightbulb(name, type, chosenRoom.getType(), houseId);
                }
                case TEMPERATURE_SENSOR, THERMOMETER -> {
                    device = new TemperatureSensor(name, type, chosenRoom.getType(), houseId);
                }
            }

            System.out.println("Device created successfully:");
            if (device != null) {
                System.out.println(device);
            }

        } catch (Exception e) {
            System.out.println("Error while creating device: " + e.getMessage());
        }
    }

    public void findDevice() {
        System.out.println("How would you like to search for the device?");
        System.out.println("1 - By Device ID");
        System.out.println("2 - By House ID");
        System.out.println("3 - By Room in a House");
        System.out.print("Enter choice (1/2/3): ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> {
                System.out.print("Enter device UUID: ");
                String idStr = scanner.nextLine().trim();
                try {
                    UUID id = UUID.fromString(idStr);
                    SmartDevice device = DeviceRegistry.getInstance().getItem(id);
                    System.out.println("Device found:");
                    System.out.println(device);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid UUID format.");
                } catch (NotFoundInRegistryException e) {
                    System.out.println("Device not found.");
                }
            }

            case "2" -> {
                System.out.print("Enter house UUID: ");
                String houseIdStr = scanner.nextLine().trim();
                try {
                    UUID houseId = UUID.fromString(houseIdStr);
                    List<SmartDevice> devices = DeviceRegistry.getInstance().getDevicesByHouse(houseId);
                    if (devices.isEmpty()) {
                        System.out.println("No devices found in this house.");
                    } else {
                        System.out.println("Devices found:");
                        devices.forEach(System.out::println);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid UUID format.");
                }
            }

            case "3" -> {
                System.out.print("Enter house UUID: ");
                String houseIdStr = scanner.nextLine().trim();
                UUID houseId;
                try {
                    houseId = UUID.fromString(houseIdStr);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid UUID format.");
                    return;
                }

                System.out.println("Choose room type:");
                for (RoomType rt : RoomType.values()) {
                    System.out.println(" - " + rt);
                }

                RoomType roomType = null;
                while (roomType == null) {
                    System.out.print("Enter room type: ");
                    String rtStr = scanner.nextLine().trim().toUpperCase();
                    try {
                        roomType = RoomType.valueOf(rtStr);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid room type.");
                    }
                }

                List<SmartDevice> devices = DeviceRegistry.getInstance().getDeviceByRoom(roomType, houseId);
                if (devices.isEmpty()) {
                    System.out.println("No devices found in this room.");
                } else {
                    System.out.println("Devices found:");
                    devices.forEach(System.out::println);
                }
            }

            default -> System.out.println("Invalid choice.");
        }
    }

    public void listAll() {
        System.out.println("What do you want to list?");
        System.out.println("1. Houses");
        System.out.println("2. Rooms");
        System.out.println("3. Devices");
        System.out.print("Enter your choice (1/2/3): ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> listHouses();
            case "2" -> listRooms();
            case "3" -> listDevices();
            default -> System.out.println("Invalid choice.");
        }
    }

    private void listHouses() {
        var houses = HouseRegistry.getInstance().getItems().values();
        if (houses.isEmpty()) {
            System.out.println("No houses found.");
            return;
        }
        houses.forEach(System.out::println);
    }

    private void listRooms() {
        var houses = HouseRegistry.getInstance().getItems();
        if (houses.isEmpty()) {
            System.out.println("No houses found.");
            return;
        }

        for (House house : houses.values()) {
            System.out.println("House: " + house.getName() + " (UUID: " + house.getId() + ")");
            if (house.getRooms().isEmpty()) {
                System.out.println("  No rooms.");
            } else {
                house.getRooms().forEach(room -> System.out.println("  " + room));
            }
        }
    }

    private void listDevices() {
        var devices = DeviceRegistry.getInstance().getItems().values();
        if (devices.isEmpty()) {
            System.out.println("No devices found.");
            return;
        }
        devices.forEach(System.out::println);
    }

    private void showHouseDetails() {
        System.out.println("How would you like to search for a house?");
        System.out.println("1. By UUID");
        System.out.println("2. By name");
        System.out.print("Choose (1/2): ");
        String choice = scanner.nextLine().trim();

        House house = null;

        switch (choice) {
            case "1" -> {
                System.out.print("Enter the UUID of the house: ");
                String idStr = scanner.nextLine().trim();
                try {
                    UUID houseId = UUID.fromString(idStr);
                    house = HouseRegistry.getInstance().getItem(houseId);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid UUID format.");
                    return;
                } catch (Exception e) {
                    System.out.println("No house found with the given UUID.");
                    return;
                }
            }
            case "2" -> {
                System.out.print("Enter the name of the house: ");
                String name = scanner.nextLine().trim().toLowerCase();
                for (Map.Entry<UUID, House> entry : HouseRegistry.getInstance().getItems().entrySet()) {
                    if (entry.getValue().getName().toLowerCase().equals(name)) {
                        house = entry.getValue();
                        break;
                    }
                }
                if (house == null) {
                    System.out.println("No house found with the given name.");
                    return;
                }
            }
            default -> {
                System.out.println("Invalid choice.");
                return;
            }
        }
        System.out.println(house.toString());
    }

    private void demo() {
        DemoDataGenerator demoDataGenerator = new DemoDataGenerator(scanner);
        try {
            demoDataGenerator.demoSetUp();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void connectToThermostat() {
        System.out.println("How would you like to find the thermostat?");
        System.out.println("1. By UUID");
        System.out.println("2. By name");
        System.out.print("Choose (1/2): ");
        String thermostatChoice = scanner.nextLine().trim();

        Thermostat thermostat = null;

        switch (thermostatChoice) {
            case "1" -> {
                System.out.print("Enter thermostat UUID: ");
                try {
                    UUID id = UUID.fromString(scanner.nextLine().trim());
                    SmartDevice device = DeviceRegistry.getInstance().getItem(id);
                    if (device instanceof Thermostat t) {
                        thermostat = t;
                    } else {
                        System.out.println("Device is not a thermostat.");
                        return;
                    }
                } catch (Exception e) {
                    System.out.println("Invalid UUID or device not found.");
                    return;
                }
            }
            case "2" -> {
                System.out.print("Enter thermostat name: ");
                String name = scanner.nextLine().trim().toLowerCase();
                for (SmartDevice device : DeviceRegistry.getInstance().getItems().values()) {
                    if (device.getName().toLowerCase().equals(name) && device instanceof Thermostat t) {
                        thermostat = t;
                        break;
                    }
                }
                if (thermostat == null) {
                    System.out.println("Thermostat not found.");
                    return;
                }
            }
            default -> {
                System.out.println("Invalid choice.");
                return;
            }
        }

        System.out.println("How would you like to find the device to connect (Radiator/AirConditioner)?");
        System.out.println("1. By UUID");
        System.out.println("2. By name");
        System.out.print("Choose (1/2): ");
        String deviceChoice = scanner.nextLine().trim();

        SmartDevice connectableDevice = null;

        switch (deviceChoice) {
            case "1" -> {
                System.out.print("Enter device UUID: ");
                try {
                    UUID id = UUID.fromString(scanner.nextLine().trim());
                    connectableDevice = DeviceRegistry.getInstance().getItem(id);
                } catch (Exception e) {
                    System.out.println("Invalid UUID or device not found.");
                    return;
                }
            }
            case "2" -> {
                System.out.print("Enter device name: ");
                String name = scanner.nextLine().trim().toLowerCase();
                for (SmartDevice device : DeviceRegistry.getInstance().getItems().values()) {
                    if (device.getName().toLowerCase().equals(name)) {
                        connectableDevice = device;
                        break;
                    }
                }
                if (connectableDevice == null) {
                    System.out.println("Device not found.");
                    return;
                }
            }
            default -> {
                System.out.println("Invalid choice.");
                return;
            }
        }

        if (connectableDevice instanceof Radiator radiator) {
            radiator.connectToThermostat(thermostat.getId());
        } else if (connectableDevice instanceof AirConditioner airConditioner) {
            airConditioner.connectToThermostat(thermostat.getId());
        } else {
            System.out.printf("Device of type %s cannot be connected to a thermostat.\n",
                    connectableDevice.getType());
        }
    }

    public void simulateDevice() {
        System.out.println("Only 'Thermostat' and 'Outlet' devices support simulation.");
        System.out.println("How would you like to find the device?");
        System.out.println("1. By UUID");
        System.out.println("2. By name");
        System.out.print("Choose (1/2): ");
        String choice = scanner.nextLine().trim();

        SmartDevice device = findDeviceInternal(choice);

        if (device instanceof Thermostat || device instanceof Outlet) {
            device.simulate();
            System.out.println("Simulation started for device: " + device.getName());
        } else {
            System.out.println("For now simulation is not available for device type: " + device.getType());
        }
    }

    public void stopSimulationForDevice() {
        System.out.println("Only 'Thermostat' and 'Outlet' devices support simulation.");
        System.out.println("How would you like to find the device?");
        System.out.println("1. By UUID");
        System.out.println("2. By name");
        System.out.print("Choose (1/2): ");
        String choice = scanner.nextLine().trim();

        SmartDevice device = findDeviceInternal(choice);
        if (device == null) return;

        if (device instanceof Thermostat || device instanceof Outlet) {
            device.stopSimulation();
            System.out.println("Simulation stopped for device: " + device.getName());
        } else {
            System.out.println("Stopping simulation is not supported for device type: " + device.getType());
        }
    }

    public void deviceStatus() {
        System.out.println("This feature is available only for 'Thermostat' and 'Outlet'.");
        System.out.println("How would you like to find the device?");
        System.out.println("1. By UUID");
        System.out.println("2. By name");
        System.out.print("Choose (1/2): ");
        String choice = scanner.nextLine().trim();

        SmartDevice device = findDeviceInternal(choice);
        if (device == null) return;

        if (device instanceof Thermostat thermostat) {
            System.out.println("Press 'E' and Enter to exit temperature history view.");
            thermostat.showTemperatureHistory();
        } else if (device instanceof Outlet outlet) {
            System.out.println("Press 'E' and Enter to exit power consumption history view.");
            outlet.showPowerConsumptionHistory();
        } else {
            System.out.println("This device does not support status history display. Type: " + device.getType());
        }
    }

    public void on() {
        System.out.println("Turn ON a device (AirConditioner, Radiator, Outlet, LightBulb)");
        System.out.println("How would you like to find the device?");
        System.out.println("1. By UUID");
        System.out.println("2. By name");
        System.out.print("Choose (1/2): ");
        String choice = scanner.nextLine().trim();

        SmartDevice device = findDeviceInternal(choice);
        if (device == null) return;

        try {
            device.getClass().getMethod("turnOn").invoke(device);
            System.out.println("Device turned ON: " + device.getName());
        } catch (NoSuchMethodException e) {
            System.out.println("This device does not support being turned on.");
        } catch (Exception e) {
            System.out.println("Could not turn on device: " + e.getMessage());
        }
    }

    public void off() {
        System.out.println("Turn OFF a device (AirConditioner, Radiator, Outlet, LightBulb)");
        System.out.println("How would you like to find the device?");
        System.out.println("1. By UUID");
        System.out.println("2. By name");
        System.out.print("Choose (1/2): ");
        String choice = scanner.nextLine().trim();

        SmartDevice device = findDeviceInternal(choice);
        if (device == null) return;

        try {
            device.getClass().getMethod("turnOff").invoke(device);
            System.out.println("Device turned OFF: " + device.getName());
        } catch (NoSuchMethodException e) {
            System.out.println("This device does not support being turned off.");
        } catch (Exception e) {
            System.out.println("Could not turn off device: " + e.getMessage());
        }
    }

    private SmartDevice findDeviceInternal(String choice) {
        SmartDevice device = null;
        switch (choice) {
            case "1" -> {
                System.out.print("Enter device UUID: ");
                try {
                    UUID id = UUID.fromString(scanner.nextLine().trim());
                    device = DeviceRegistry.getInstance().getItem(id);
                } catch (Exception e) {
                    System.out.println("Invalid UUID or device not found.");
                }
            }
            case "2" -> {
                System.out.print("Enter device name: ");
                String name = scanner.nextLine().trim().toLowerCase();
                for (SmartDevice d : DeviceRegistry.getInstance().getItems().values()) {
                    if (d.getName().toLowerCase().equals(name)) {
                        device = d;
                        break;
                    }
                }
                if (device == null) {
                    System.out.println("Device not found.");
                }
            }
            default -> {
                System.out.println("Invalid choice.");
            }
        }
        return device;
    }

    private DeviceStatus askForStatus(EnumSet<DeviceStatus> allowedStatuses) {
        System.out.println("Choose status from the following options:");
        for (DeviceStatus status : allowedStatuses) {
            System.out.println(" - " + status);
        }

        while (true) {
            System.out.print("Enter device status: ");
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                DeviceStatus status = DeviceStatus.valueOf(input);
                if (allowedStatuses.contains(status)) {
                    return status;
                } else {
                    System.out.println("This status is not valid for this device. Please try again.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid status. Please enter one of the listed options.");
            }
        }
    }

}
