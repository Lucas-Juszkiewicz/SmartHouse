package classes;

import java.time.LocalDate;

public class Warning {
    String message;
    LocalDate date = LocalDate.now();
    Boolean blockDevice;

    public Warning(String message, Boolean blockDevice) {
        this.message = message;
        this.blockDevice = blockDevice;
    }

    @Override
    public String toString() {
        return "Warning " + date + "\n"
                + message + "\n"
                + "blockDevice = " + blockDevice + "\n";
    }
}
