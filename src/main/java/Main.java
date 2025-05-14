import classes.House;
import classes.Room;
import classes.devices.Thermostat;
import enums.DeviceStatus;
import enums.RoomType;
import util.Coordinates;

import java.sql.SQLOutput;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        House house = new House("Castle in Scotland", new Coordinates(52.01, 24.32));
        Room room = new Room("Living room on the ground floor", RoomType.LIVING_ROOM, 50.00, house.getId());
        Room basement = new Room("Basement", RoomType.BASEMENT, 30.00, house.getId());
        Thermostat thermostat;
        try {
            thermostat = new Thermostat(
                    "Thermo on the ground floor",
                    DeviceStatus.OK,
                    RoomType.BASEMENT,
                    house.getId(),
                    20.00);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(house);
//        System.out.println(room);
        System.out.println(thermostat);

        // Need to check why thermostats type is null
        // toString in progress
        // Need to get rid of 'SmartDevice' title in Smart device toString

    }
}
