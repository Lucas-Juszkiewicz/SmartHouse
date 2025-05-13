import classes.House;
import classes.Room;
import enums.RoomType;
import util.Coordinates;

import java.sql.SQLOutput;

public class Main {
    public static void main(String[] args) {
        House house = new House("Castle in Scotland", new Coordinates(52.01, 24.32), 312.00);
        new Room("Living room on the ground floor", RoomType.LIVING_ROOM, 50.00, house.getId());

        System.out.println(house);

    }
}
