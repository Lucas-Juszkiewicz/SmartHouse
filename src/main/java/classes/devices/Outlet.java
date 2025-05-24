package classes.devices;

import classes.SmartDevice;
import enums.DeviceStatus;
import enums.DeviceType;
import enums.RoomType;
import interfaces.DeviceObserver;
import interfaces.SensorDevice;
import interfaces.Switchable;
import util.GroundFloorTemperature;
import util.PowerConsumption;
import util.TerminalColors;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Outlet extends SmartDevice implements Switchable, DeviceObserver, SensorDevice<String> {
    private boolean isInUse = false;
    private Deque<String[]> powerConsumptionHistory = new ArrayDeque<>();
    private int powerConsumption = 0;
    private final Object powerConsumptionLock = new Object();
    private volatile boolean stopShowPowerConsumptionHistory = false;
    private final Scanner scanner;

    public Outlet(String name, DeviceType type, RoomType roomType, UUID houseId, Scanner scanner) throws Exception {
        super(name, type, roomType, houseId);
        setPossibleStatuses(
                EnumSet.of(
                        DeviceStatus.NEEDS_REPAIR,
                        DeviceStatus.ON,
                        DeviceStatus.OFF
                )
        );
        setStatus(DeviceStatus.OFF);
        this.scanner = scanner;
    }

    public boolean isInUse() {
        return isInUse;
    }

    public void setInUse(boolean inUse) {
        isInUse = inUse;
    }

    public int getPowerConsumption() {
        synchronized (powerConsumptionLock) {
            return powerConsumption;
        }
    }

    public void setPowerConsumption(double powerConsumption) {
        synchronized (powerConsumptionLock) {
            this.powerConsumption = (int) powerConsumption;
        }
    }

    public void showPowerConsumptionHistoryLoop() {
        while (!stopShowPowerConsumptionHistory) {
            printPowerConsumptionHistoryAsterisk();
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void showPowerConsumptionHistory() {
        this.stopShowPowerConsumptionHistory = false;
        Thread inputThread = new Thread(() -> {
            String input = "";
            input = scanner.nextLine();
            if (input.matches("[Ee]")) {
                stopShowPowerConsumptionHistory = true;
            }
        });
        Thread showPowerConsumptionThread = new Thread(() -> {
            showPowerConsumptionHistoryLoop();
            inputThread.interrupt();
            Thread.currentThread().interrupt();
        });
        showPowerConsumptionThread.start();
        inputThread.start();
        try {
            showPowerConsumptionThread.join();
            inputThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void printPowerConsumptionHistoryAsterisk () {
        StringBuilder master = new StringBuilder();
            master.append(" \t      | PowerConsumption (Watt)\n");
        StringBuilder columnOneRowA = new StringBuilder(" \t 4000 | ");
        StringBuilder columnOneRowB = new StringBuilder(" \t 3600 | ");
        StringBuilder columnOneRowC = new StringBuilder(" \t 3200 | ");
        StringBuilder columnOneRowD = new StringBuilder(" \t 2800 | ");
        StringBuilder columnOneRowE = new StringBuilder(" \t 2400 | ");
        StringBuilder columnOneRowF = new StringBuilder(" \t 2000 | ");
        StringBuilder columnOneRowG = new StringBuilder(" \t 1600 | ");
        StringBuilder columnOneRowH = new StringBuilder(" \t 1200 | ");
        StringBuilder columnOneRowI = new StringBuilder(" \t  800 | ");
        StringBuilder columnOneRowJ = new StringBuilder(" \t  400 | ");
        String markedPowerConsumptionColor;
        if (powerConsumption > 3600) {
            markedPowerConsumptionColor = TerminalColors.ANSI_BRIGHT_RED;
        } else if (powerConsumption <= 3400) {
            markedPowerConsumptionColor = TerminalColors.ANSI_BRIGHT_BLUE;
        } else {
            markedPowerConsumptionColor = TerminalColors.ANSI_BRIGHT_YELLOW;
        }
            int[] powerPrevious = new int[powerConsumptionHistory.size()];
            int i = 0;
        for (String[] record : powerConsumptionHistory) {
            if (record != null && record.length > 0) {
                int power = Integer.parseInt(record[0]);
                String time = record[1];
                int row = 9 - power/400;
                StringBuilder column = new StringBuilder();
                for (int j = 0; j < 10; j++) {
                    if(j==row){
                        if(i!=0 && powerPrevious[i-1]>power){
                            column.append("\\   \n");
                            column.append(" \\  \n");
                            column.append("  \\ \n");
                            column.append(markedPowerConsumptionColor).append("   *").append(TerminalColors.ANSI_RESET).append("\n");
                        }else if(i!=0 && powerPrevious[i-1]==power){
                            column.append("---").append(markedPowerConsumptionColor).append("*").append(TerminalColors.ANSI_RESET).append("\n");
                        }else if(i!=0 && powerPrevious[i-1]<power){
                            column.append(markedPowerConsumptionColor).append("   *").append(TerminalColors.ANSI_RESET).append("\n");
                            column.append("  / \n");
                            column.append(" /  \n");
                            column.append("/   \n");
                        }else {
                            column.append("    ").append(markedPowerConsumptionColor).append("*").append(TerminalColors.ANSI_RESET).append("\n");
                        }
                    }else {
                        column.append("    \n");
                    }
                }
                column.append(time).append("\n");
                powerPrevious[i++] = power;
                master.append(column);
            }
        }
        System.out.println(master);
    }

    @Override
    public void updateObservedValue() {
        setPowerConsumption(PowerConsumption.getInstance().getValue());
        if (powerConsumptionHistory.size() == 10) {
            powerConsumptionHistory.removeFirst();
        }
        int powerConsumption = getPowerConsumption();
        String[] record = {getPowerConsumption() + "", getTime()};
        powerConsumptionHistory.addLast(record);

        if (powerConsumption > 3600) {
            try {
                setStatus(DeviceStatus.NEEDS_REPAIR);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void simulate() {
        PowerConsumption.getInstance().startGenerating(0.00, 4000.00, 10.00, 500.00);
        PowerConsumption.getInstance().addObserver(this);
    }

    @Override
    public void stopSimulation() {
        PowerConsumption.getInstance().removeObserver(this);
        PowerConsumption.getInstance().stopGenerating();
    }

    @Override
    public void turnOn() throws Exception {
        if (isInUse) {
            setStatus(DeviceStatus.ON);
        }
    }

    @Override
    public void turnOff() throws Exception {
        setStatus(DeviceStatus.OFF);
    }

    @Override
    public boolean isOn() {
        return getStatus() == DeviceStatus.ON;
    }


    @Override
    public String readValue() {
        return TerminalColors.ANSI_BRIGHT_BLUE + getPowerConsumption() + TerminalColors.ANSI_RESET + getUnit();
    }

    @Override
    public String getUnit() {
        return "W";
    }

    private String getTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return now.format(formatter).replace(":", ":\n");
    }
}
