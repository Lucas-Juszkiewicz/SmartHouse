package classes.devices;

import classes.SmartDevice;
import enums.DeviceStatus;
import enums.DeviceType;
import enums.RoomType;
import interfaces.DeviceObserver;
import interfaces.SensorDevice;
import interfaces.Switchable;
import util.PowerConsumption;
import util.TerminalColors;
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

    public Deque<String[]> getPowerConsumptionHistory() {
        synchronized (powerConsumptionLock) {
            return powerConsumptionHistory;
        }
    }

    public void setPowerConsumptionHistory(Deque<String[]> updated) {
        synchronized (powerConsumptionLock) {
            this.powerConsumptionHistory = updated;
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
        inputThread.start();
        showPowerConsumptionThread.start();
        try {
            showPowerConsumptionThread.join();
            inputThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static final int COL_WIDTH = 5;

    private void printPowerConsumptionHistoryAsterisk() {
        StringBuilder top = new StringBuilder("\n\n \t      | PowerConsumption (Watt)");
        StringBuilder rowA = new StringBuilder(" \t 4000 | ");
        StringBuilder rowB = new StringBuilder(" \t 3600 | ");
        StringBuilder rowC = new StringBuilder(" \t 3200 | ");
        StringBuilder rowD = new StringBuilder(" \t 2800 | ");
        StringBuilder rowE = new StringBuilder(" \t 2400 | ");
        StringBuilder rowF = new StringBuilder(" \t 2000 | ");
        StringBuilder rowG = new StringBuilder(" \t 1600 | ");
        StringBuilder rowH = new StringBuilder(" \t 1200 | ");
        StringBuilder rowI = new StringBuilder(" \t  800 | ");
        StringBuilder rowJ = new StringBuilder(" \t  400 | ");
        StringBuilder rowK = new StringBuilder(" \t   T  | ");
        StringBuilder rowL = new StringBuilder(" \t   I  | ");
        StringBuilder rowM = new StringBuilder(" \t   M  | ");
        StringBuilder rowN = new StringBuilder(" \t   E  | ");

        int recordCounter = 0;
        for (String[] record : getPowerConsumptionHistory()) {
            ++recordCounter;
            if (record != null && record.length > 1) {
                int power = Integer.parseInt(record[0]);
                String time = record[1];
                String[] splitTime = time.split(":");

                int rowIndex = 10 - power / 400;
                if (rowIndex < 0) rowIndex = 0;
                if (rowIndex > 9) rowIndex = 9;

                String cell = "===";
                String cellFormatted = String.format("%" + COL_WIDTH + "s", cell);
                if (record[0].equals(getPowerConsumptionHistory().getLast()[0]) || recordCounter == 10) {
                    cellFormatted = getColor(power) + cellFormatted + TerminalColors.ANSI_RESET;
                }

                String empty = String.format("%" + COL_WIDTH + "s", "");

                rowA.append(rowIndex == 0 ? cellFormatted : empty);
                rowB.append(rowIndex == 1 ? cellFormatted : empty);
                rowC.append(rowIndex == 2 ? cellFormatted : empty);
                rowD.append(rowIndex == 3 ? cellFormatted : empty);
                rowE.append(rowIndex == 4 ? cellFormatted : empty);
                rowF.append(rowIndex == 5 ? cellFormatted : empty);
                rowG.append(rowIndex == 6 ? cellFormatted : empty);
                rowH.append(rowIndex == 7 ? cellFormatted : empty);
                rowI.append(rowIndex == 8 ? cellFormatted : empty);
                rowJ.append(rowIndex == 9 ? cellFormatted : empty);

                rowK.append(String.format("%" + COL_WIDTH + "s", "_____"));
                rowL.append(String.format("%" + COL_WIDTH + "s", splitTime[0] + "h"));
                rowM.append(String.format("%" + COL_WIDTH + "s", splitTime[1] + "m"));
                rowN.append(String.format("%" + COL_WIDTH + "s", splitTime[2] + "s"));
            }
        }

        print(top, rowA, rowB, rowC, rowD, rowE, rowF);
        print(rowG, rowH, rowI, rowJ, rowK, rowL, rowM);
        System.out.print(rowN);
    }

    private String getColor(int power) {
        String markedPowerConsumptionColor;
        if (power > 3600) {
            markedPowerConsumptionColor = TerminalColors.ANSI_BRIGHT_RED;
        } else if (power <= 3200) {
            markedPowerConsumptionColor = TerminalColors.ANSI_BRIGHT_BLUE;
        } else {
            markedPowerConsumptionColor = TerminalColors.ANSI_BRIGHT_YELLOW;
        }
        return markedPowerConsumptionColor;
    }

    public void print(StringBuilder top, StringBuilder rowA, StringBuilder rowB, StringBuilder rowC, StringBuilder rowD, StringBuilder rowE, StringBuilder rowF) {
        System.out.println(top);
        System.out.println(rowA);
        System.out.println(rowB);
        System.out.println(rowC);
        System.out.println(rowD);
        System.out.println(rowE);
        System.out.println(rowF);
    }

    @Override
    public void updateObservedValue() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = now.format(formatter);
        setPowerConsumption(PowerConsumption.getInstance().getValue());
        if (getPowerConsumptionHistory().size() == 10) {
            getPowerConsumptionHistory().removeFirst();
        }
        int powerConsumption = getPowerConsumption();
        String[] record = {getPowerConsumption() + "", time};
        getPowerConsumptionHistory().addLast(record);

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
        PowerConsumption.getInstance().startGenerating(400.00, 4000.00, 10.00, 500.00);
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


}
