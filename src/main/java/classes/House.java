package classes;

import util.Coordinates;
import util.DeviceRegistry;
import util.HouseRegistry;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class House {
    private final UUID id;
    private final String name;
    private final Coordinates coordinates;
    private final Double area;
    private final HashSet<Room> rooms = new HashSet<>();

    public House(String name, Coordinates coordinates, Double area) {
        this.id = HouseRegistry.getInstance().generateId();
        this.name = name;
        this.coordinates = coordinates;
        this.area = area;
        HouseRegistry.getInstance().addItem(this.id, this);
    }

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

    public List<SmartDevice> getSmartDevices() {
        return DeviceRegistry.getInstance().getDevicesByHouse(this.id);
    }

    public void addRoom(Room room) {
        this.rooms.add(room);
    }

    @Override
    public String toString() {
        return "House\n" +
                "id: " + id + "\n" +
                "name: " + name + "\n" +
                "coordinates: " + coordinates + "\n" +
                "area: " + area + "\n" +
                "rooms: " + rooms + "\n";
    }
}
