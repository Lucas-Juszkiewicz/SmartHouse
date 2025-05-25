package util;

import classes.House;
import classes.Room;
import classes.devices.AirConditioner;
import classes.devices.Outlet;
import classes.devices.Radiator;
import classes.devices.TemperatureSensor;
import classes.devices.Thermostat;
import enums.DeviceStatus;
import enums.DeviceType;
import enums.RoomType;

import java.util.Scanner;

public class DemoDataGenerator {
    private Scanner scanner;

    public DemoDataGenerator(Scanner scanner) {
        this.scanner = scanner;
    }

    public void demoSetUp() throws Exception {

        Coordinates coordinates = new Coordinates(52.2297, 21.0122);
        House house = new House("DemoHouse", coordinates);

        Room livingRoom = new Room("Living Room", RoomType.LIVING_ROOM, 25.0, house.getId());
        Room kitchen = new Room("Kitchen", RoomType.KITCHEN, 15.0, house.getId());
        Room bedroom = new Room("Bedroom", RoomType.BEDROOM, 20.0, house.getId());
        Room garden = new Room("Garden", RoomType.GARDEN, 100.0, house.getId());

        Thermostat thermostat = new Thermostat("Main Thermostat", DeviceStatus.OK, RoomType.LIVING_ROOM, house.getId(), 23.00, scanner);

        AirConditioner ac = new AirConditioner("AC Unit", DeviceStatus.OK, RoomType.LIVING_ROOM, house.getId());

        Outlet outlet = new Outlet("Kitchen Outlet", DeviceType.OUTLET, RoomType.KITCHEN, house.getId(), scanner);
        outlet.setStatus(DeviceStatus.ON);

        TemperatureSensor gardenTempSensor = new TemperatureSensor("Garden Temp Sensor", DeviceType.TEMPERATURE_SENSOR, RoomType.GARDEN, house.getId());
        gardenTempSensor.setStatus(DeviceStatus.OK);

        Radiator radiator = new Radiator("Radiator", RoomType.LIVING_ROOM, house.getId());
        radiator.setStatus(DeviceStatus.ON);

        System.out.println("Demo house with rooms and devices has been successfully created.");
    }
}
