package classes;

import util.Coordinates;
import util.DeviceRegistry;
import util.HouseRegistry;
import util.TerminalColors;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class House {
    private final UUID id;
    private String name;
    private final Coordinates coordinates;
    private Double area;
    private final HashSet<Room> rooms = new HashSet<>();

    public House(String name, Coordinates coordinates) {
        this.id = HouseRegistry.getInstance().generateId();
        this.name = name;
        this.coordinates = coordinates;
        this.area = 0.0;
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
        this.area += room.getArea();
    }

    @Override
    public String toString() {
        StringBuilder nestedRooms = new StringBuilder();
        rooms.forEach(room -> {
            nestedRooms.append(room.toStringNested());
        });
        return TerminalColors.ANSI_BRIGHT_YELLOW + "House\n" + TerminalColors.ANSI_RESET +
                "id: " + id + "\n" +
                "name: " + name + "\n" +
                "coordinates: " + coordinates + "\n" +
                "area: " + area + "m^2" + "\n" +
                "rooms:\n" + nestedRooms + "\n";
    }
}
