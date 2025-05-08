package classes;

import util.Coordinates;

import java.util.HashSet;
import java.util.UUID;

public class House {
    private UUID id;
    private String name;
    private Coordinates coordinates;
    private Double area;
    private HashSet<Room> rooms;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Double getArea() {
        return area;
    }

    public HashSet<Room> getRooms() {
        return rooms;
    }
}
