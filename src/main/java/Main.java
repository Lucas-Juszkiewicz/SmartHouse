import classes.House;
import classes.Room;
import classes.devices.AirConditioner;
import classes.devices.Outlet;
import classes.devices.Radiator;
import classes.devices.Thermostat;
import enums.DeviceStatus;
import enums.DeviceType;
import enums.RoomType;
import util.CliController;
import util.Coordinates;

import java.sql.SQLOutput;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CliController instance = new CliController(scanner);
        instance.cliThread.start();
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        House house = new House("Castle in Scotland", new Coordinates(52.01, 24.32));
//        Room room = new Room("Living room on the ground floor", RoomType.LIVING_ROOM, 50.00, house.getId());
//        Room basement = new Room("Basement", RoomType.BASEMENT, 30.00, house.getId());
//        Thermostat thermostat;
//        try {
//            thermostat = new Thermostat(
//                    "Thermo on the ground floor",
//                    DeviceStatus.OK,
//                    RoomType.BASEMENT,
//                    house.getId(),
//                    23.00,
//                    scanner);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        Radiator radiator;
//        try {
//            radiator = new Radiator("Radiator1", RoomType.LIVING_ROOM, house.getId());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        radiator.connectToThermostat(thermostat.getId());
//        AirConditioner airConditioner;
//        Outlet outlet;
//        try {
//            airConditioner = new AirConditioner("AC1", DeviceStatus.ON, RoomType.LIVING_ROOM, house.getId());
//            outlet = new Outlet("O1", DeviceType.OUTLET, RoomType.LIVING_ROOM, house.getId(), scanner);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        airConditioner.connectToThermostat(thermostat.getId());
//        thermostat.simulate();
//        System.out.println(house);
////        thermostat.inputLoop();
////        thermostat.showTemperatureHistory();
//
//        outlet.simulate();
//        outlet.showPowerConsumptionHistory();
                        scanner.close();
    }
}
