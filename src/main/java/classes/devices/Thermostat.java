package classes.devices;

import classes.SmartDevice;
import enums.DeviceStatus;
import enums.DeviceType;
import enums.RoomType;
import interfaces.DeviceObserver;
import interfaces.SensorDevice;
import util.GroundFloorTemperature;
import util.TerminalColors;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Thermostat extends SmartDevice implements DeviceObserver, SensorDevice<Double> {
    private double airTemperature;
    private double desiredTemperature;
    private Deque<Double> temperatureHistory = new ArrayDeque<>();
    private boolean stopShowTemperatureHistory = false;
    private final ArrayList<AirConditioner> airConditioners = new ArrayList<>();
    private final ArrayList<Radiator> radiators = new ArrayList<>();
    private final Object desiredTemperatureLock = new Object();
    private final Object airTemperatureLock = new Object();

    public Thermostat(String name, DeviceStatus status, RoomType roomType, UUID houseId, Double desiredTemperature) throws Exception {
        super(name, DeviceType.THERMOSTAT, roomType, houseId);
        this.desiredTemperature = desiredTemperature;
        setPossibleStatuses(
                EnumSet.of(
                        DeviceStatus.NEEDS_REPAIR,
                        DeviceStatus.NEEDS_UPDATE,
                        DeviceStatus.OK
                )
        );
        setStatus(status);
    }

    public double getAirTemperature() {
        synchronized (airTemperatureLock){
        return airTemperature;
        }
    }

    public void setAirTemperature(double airTemperature) {
        synchronized (airTemperatureLock) {
            this.airTemperature = airTemperature;
        }
    }

    public double getDesiredTemperature() {
        synchronized (desiredTemperatureLock) {
            return desiredTemperature;
        }
    }

    public void setDesiredTemperature(double desiredTemperature) {
        synchronized (desiredTemperatureLock) {
            this.desiredTemperature = desiredTemperature;
        }
    }

    public void inputLoop(){
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
//            if (!scanner.hasNextLine()) {
//                System.out.println("No more input. Stopping input thread.");
//                scanner.close();
//            }
            if(input.matches("[Dd]")){
                System.out.println("Type a value from 00,00 to 40,00 and press ENTER:\n");
                String valueInput = scanner.nextLine().replace(',', '.');
                double value = Double.parseDouble(valueInput);
                setDesiredTemperature(value);
            } else if(input.matches("[Ee]")){
                this.stopShowTemperatureHistory = true;
            }
        scanner.close();
    }

    public void showTemperatureHistory() {
        this.stopShowTemperatureHistory = false;
        Thread showTemperatureThread = new Thread(() -> {
            while (!stopShowTemperatureHistory) {
                markTemperature(getAirTemperature());
                inputLoop();
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

        });
        showTemperatureThread.start();
    }

    public void startTemperatureControl() {

    }

    public void stopTemperatureControl() {

    }

    public void connect(SmartDevice device) {
        String type = null;

        if (device instanceof AirConditioner airConditioner) {
            airConditioners.add(airConditioner);
            type = "AirConditioner";
        } else if (device instanceof Radiator radiator) {
            radiators.add(radiator);
            type = "Radiator";
        }

        if (type != null) {
            System.out.printf("%s in %s(%s) has been connected to the thermostat.\n",
                    type, device.getLocation(), device.getId());
        } else {
            System.out.printf("You have passed %s instead of AirConditioner or Radiator\n",
                    device.getType());
        }
    }

    public void disconnect(SmartDevice device) {
        String type = null;

        if (device instanceof AirConditioner airConditioner) {
            airConditioners.remove(airConditioner);
            type = "AirConditioner";
        } else if (device instanceof Radiator radiator) {
            radiators.remove(radiator);
            type = "Radiator";
        }

        if (type != null) {
            System.out.printf("%s in %s(%s) has been disconnected.\n",
                    type, device.getLocation(), device.getId());
        } else {
            System.out.printf("You have passed %s instead of AirConditioner or Radiator\n",
                    device.getType());
        }
    }

    public void disconnectByLocation(RoomType roomType, DeviceType type) {
        List<? extends SmartDevice> toRemove;
        String typeName = type.toString();
        String location;

        switch (type) {
            case AIR_CONDITIONER -> {
                toRemove = airConditioners.stream()
                        .filter(ac -> ac.getRoomType().equals(roomType))
                        .toList();
                location = toRemove.getFirst().getLocation();
                airConditioners.removeAll(toRemove);
            }
            case RADIATOR -> {
                toRemove = radiators.stream()
                        .filter(rad -> rad.getRoomType().equals(roomType))
                        .toList();
                location = toRemove.getFirst().getLocation();
                radiators.removeAll(toRemove);
            }
            default -> {
                System.out.printf("Unsupported device type: %s\n", type);
                return;
            }
        }

        int count = toRemove.size();

        if (count > 0) {
            System.out.printf("%ss in %s (count %d) %s been disconnected.\n",
                    typeName, location, count, (count > 1 ? "had" : "has"));
        } else {
            System.out.printf("There is no %s in %s.\n", typeName, location);
        }
    }


    @Override
    public void simulate() {
        GroundFloorTemperature.getInstance().startGenerating(0.00, 40.00);
        GroundFloorTemperature.getInstance().addObserver(this);
    }

    @Override
    public void stopSimulation() {
        GroundFloorTemperature.getInstance().removeObserver(this);
        GroundFloorTemperature.getInstance().stopGenerating();
    }

    @Override
    public void updateObservedValue() {
        setAirTemperature(GroundFloorTemperature.getInstance().getTemperature());
        if (temperatureHistory.size() == 10) {
            temperatureHistory.removeFirst();
        }
        temperatureHistory.addLast(getAirTemperature());

        if (getAirTemperature() + 1.00 > desiredTemperature) {
            airConditioners.forEach(AirConditioner::startCooling);
        } else if (getAirTemperature() - 1.00 < desiredTemperature) {
            radiators.forEach(Radiator::startHeating);
        } else {
            airConditioners.forEach(AirConditioner::stopCooling);
            radiators.forEach(Radiator::stopHeating);
        }
    }

    @Override
    public Double readValue() {
        return Math.round(getAirTemperature() * 100.0) / 100.0;
    }

    public Double readDesiredTemperature() {
        return Math.round(this.desiredTemperature * 100.0) / 100.0;
    }

    @Override
    public String getUnit() {
        return "'C";
    }

    private void markTemperature(double temperature) {
        String markedTemperatureColor;
        if (temperature > desiredTemperature + 0.5) {
            markedTemperatureColor = TerminalColors.ANSI_BRIGHT_RED;
        } else if (temperature < desiredTemperature - 0.5) {
            markedTemperatureColor = TerminalColors.ANSI_BRIGHT_BLUE;
        } else {
            markedTemperatureColor = TerminalColors.ANSI_BRIGHT_GREEN;
        }
        String desiredTemperatureF = String.format(Locale.FRANCE, "%05.2f", desiredTemperature);
        String markedValueF = markedTemperatureColor + String.format(Locale.FRANCE, "%05.2f", temperature) + TerminalColors.ANSI_RESET;
        StringBuilder masterZero = new StringBuilder().append(" \t|  ");
        StringBuilder masterOne = new StringBuilder().append(" \t|  ");
        StringBuilder masterTwo = new StringBuilder().append(" \t|  ");
        StringBuilder masterThree = new StringBuilder().append(" \t|  ");
        StringBuilder masterFour = new StringBuilder().append(" \t|  ");
        for (int i = 0; i <= 40; i = i + 5) {
            String iF = String.format(Locale.FRANCE, "%05.2f", (double) i);
            int markedValueInt = (int) temperature;
            String partZero = "";
            String partOne = "";
            String partTwo = "";
            String partThree = "";
            String partFive = "";
            if (i <= markedValueInt && markedValueInt < i + 5) {

                if (markedValueInt == i) {
                    partZero = markedValueF + getUnit() + "   ";
                    partOne = " |        ";
                    partTwo = " | _ _ _ _";
                    partThree = " |        ";
                    partFive = iF + getUnit() + "   ";
                } else if (markedValueInt == i + 1) {
                    partZero = "  " + markedValueF + getUnit() + " ";
                    partOne = "   |      ";
                    partTwo = " _ | _ _ _";
                    partThree = "   |      ";
                    partFive = iF + getUnit() + "   ";
                } else if (markedValueInt == i + 2) {
                    partZero = "   " + markedValueF + getUnit();
                    partOne = "     |    ";
                    partTwo = " _ _ | _ _";
                    partThree = " |        ";
                    partFive = iF + getUnit() + "   ";
                } else if (markedValueInt == i + 3) {
                    partZero = "   " + markedValueF + getUnit();
                    partOne = "       |  ";
                    partTwo = " _ _ _ | _";
                    partThree = " |        ";
                    partFive = iF + getUnit() + "   ";
                } else if (markedValueInt == i + 4) {
                    partZero = "   " + markedValueF + getUnit();
                    partOne = "         |";
                    partTwo = " _ _ _ _ |";
                    partThree = " |        ";
                    partFive = iF + getUnit() + "   ";
                }
            } else {
                partZero = "          ";
                partOne = "          ";
                partTwo = " _ _ _ _ _";
                partThree = " |        ";
                partFive = iF + getUnit() + "   ";
            }
            masterZero.append(partZero);
            masterOne.append(partOne);
            masterTwo.append(partTwo);
            masterThree.append(partThree);
            masterFour.append(partFive);
        }
        System.out.printf("\t|    %s                                                                                  |\n", getTime());
        String stringMasterZero = masterZero.append("  |").toString();
        String stringMasterOne = masterOne.append("  |").toString();
        String stringMasterTwo = masterTwo.append("  |").toString();
        String stringMasterThree = masterThree.append("  |").toString();
        String stringMasterFour = masterFour.append("  |").toString();
        System.out.println(stringMasterZero);
        System.out.println(stringMasterOne);
        System.out.println(stringMasterTwo);
        System.out.println(stringMasterThree);
        System.out.println(stringMasterFour);
        System.out.println(
                TerminalColors.ANSI_GRAY +
                        " \t|                                  " +
                        "                                                            |" +
                        TerminalColors.ANSI_RESET);
        System.out.println(
                TerminalColors.ANSI_GRAY +
                        "\t|\t" + TerminalColors.ANSI_BRIGHT_YELLOW + "Desired temperature = " + desiredTemperatureF + TerminalColors.ANSI_RESET +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t   " + TerminalColors.ANSI_GRAY + "|" +
                        TerminalColors.ANSI_RESET);
        System.out.println(
                TerminalColors.ANSI_GRAY +
                        "\t|\t" + TerminalColors.ANSI_RESET + "To change " + TerminalColors.ANSI_BRIGHT_YELLOW + "Desired temperature " + TerminalColors.ANSI_RESET + "enter a value from 00,00 to 40,00" +
                        "\t\t\t\t\t\t\t   " + TerminalColors.ANSI_GRAY + "|" +
                        TerminalColors.ANSI_RESET);
        System.out.println(
                TerminalColors.ANSI_GRAY +
                        "\t|\t" + TerminalColors.ANSI_RESET + "and press ENTER" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t   " + TerminalColors.ANSI_GRAY + "|" +
                        TerminalColors.ANSI_RESET);
        System.out.println(
                TerminalColors.ANSI_GRAY +
                        "\t|\t\t\t\t\t\t" + TerminalColors.ANSI_YELLOW + "If you want to stop press E and than ENTER." + TerminalColors.ANSI_RESET +
                        "\t\t\t\t\t\t\t   " + TerminalColors.ANSI_GRAY + "|" +
                        TerminalColors.ANSI_RESET);
        System.out.println(
                TerminalColors.ANSI_GRAY +
                        " \t|                                  " +
                        "                                                            |" +
                        TerminalColors.ANSI_RESET);
        System.out.println(
                TerminalColors.ANSI_GRAY +
                        " \t|__________________________________" +
                        "____________________________________________________________|" +
                        TerminalColors.ANSI_RESET);
//        for (int i = 0; i < 2; i++) {
//            System.out.println(
//                    TerminalColors.ANSI_GRAY +
//                            " \t|                                  " +
//                            "                                                            |" +
//                            TerminalColors.ANSI_RESET);
//        }
    }

    private String getTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return now.format(formatter);
    }

    @Override
    public String toString() {
        return TerminalColors.ANSI_YELLOW + "Thermostat \n" + TerminalColors.ANSI_RESET +
                "airTemperature: " + TerminalColors.ANSI_YELLOW + readValue() + getUnit() + TerminalColors.ANSI_RESET + "\n" +
                "desiredTemperature: " + TerminalColors.ANSI_YELLOW + readDesiredTemperature() + getUnit() + TerminalColors.ANSI_RESET + "\n" +
                super.toString();
    }

    public String toStringNested() {
        return TerminalColors.ANSI_YELLOW + "\tThermostat \n" + TerminalColors.ANSI_RESET +
                "\tairTemperature: " + TerminalColors.ANSI_YELLOW + readValue() + getUnit() + TerminalColors.ANSI_RESET + "\n" +
                "\tdesiredTemperature: " + TerminalColors.ANSI_YELLOW + readDesiredTemperature() + getUnit() + TerminalColors.ANSI_RESET + "\n" +
                super.toStringNested();
    }

}
