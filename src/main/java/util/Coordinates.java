package util;

public record Coordinates(double latitude, double longitude) {
    @Override
    public String toString() {
        String latitudeDirection = latitude >= 0 ? "N" : "S";
        String longitudeDirection = longitude >= 0 ? "E" : "W";
        return String.format(
                        " %.2f"
                        + TerminalColors.ANSI_RED + "%s"
                        + TerminalColors.ANSI_RESET +
                        ", %.2f"
                        + TerminalColors.ANSI_GREEN +
                        "%s" + TerminalColors.ANSI_RESET,
                latitude, latitudeDirection, longitude, longitudeDirection);

    }
}