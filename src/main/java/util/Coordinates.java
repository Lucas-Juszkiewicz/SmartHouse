package util;

public record Coordinates(double latitude, double longitude) {
    @Override
    public String toString() {
        String latitudeDirection = latitude >= 0 ? "N" : "S";
        String longitudeDirection = longitude >= 0 ? "E" : "W";
        return String.format("Location: Lat: %.6f %s, Lon: %.6f %s", latitude, latitudeDirection, longitude, longitudeDirection);
    }
}