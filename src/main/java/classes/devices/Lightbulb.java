package classes.devices;

import classes.SmartDevice;
import enums.DeviceStatus;
import enums.DeviceType;
import enums.RoomType;
import interfaces.Switchable;
import util.TerminalColors;

import java.util.EnumSet;
import java.util.UUID;

public class Lightbulb extends SmartDevice implements Switchable {
    private int hue;
    private Double saturation;
    private Double value;
    public Lightbulb(String name, DeviceType type, RoomType roomType, UUID houseId) throws Exception {
        super(name, type, roomType, houseId);
        this.hue = 30;
        this.saturation = 0.01;
        this.value = 0.98;
        setPossibleStatuses(
                EnumSet.of(
                        DeviceStatus.ON,
                        DeviceStatus.OFF
                )
        );
    }

    public int getHue() {
        return hue;
    }

    public void setHue(int hue) {
        if (hue>=0 && hue<360) {
            this.hue = hue;
        }else if(hue<0){
            this.hue = 0;
        }else {
            this.hue = 359;
        }
    }

    public Double getSaturation() {
        return saturation;
    }

    public void setSaturation(Double saturation) {
        if (saturation>=0 && saturation<=1) {
            this.saturation = saturation;
        }else if(saturation<0){
            this.saturation=0.0;
        }else {
            this.saturation = 1.0;
        }
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        if (value>=0 && value<=1) {
            this.value = value;
        }else if(value<0){
            this.value=0.0;
        }else {
            this.value = 1.0;
        }
    }

    @Override
    public void simulate() {

    }

    @Override
    public void stopSimulation() {

    }

    @Override
    public void turnOn() throws Exception {
            setStatus(DeviceStatus.ON);
            setHue(30);
            setSaturation(0.01);
            setValue(0.98);
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
    public String toString() {
        return "\n" + TerminalColors.ANSI_YELLOW + "Light bulb \n" + TerminalColors.ANSI_RESET +
                "Hue: " + TerminalColors.ANSI_YELLOW + hue + TerminalColors.ANSI_RESET + "\n" +
                "Saturation: " + TerminalColors.ANSI_YELLOW + saturation + TerminalColors.ANSI_RESET + "\n" +
                "Value: " + TerminalColors.ANSI_YELLOW + value + TerminalColors.ANSI_RESET + "\n" +
                super.toString();
    }

    public String toStringNested() {
        return TerminalColors.ANSI_YELLOW + "\tLight bulb \n" + TerminalColors.ANSI_RESET +
                "\tHue: " + TerminalColors.ANSI_YELLOW + hue + TerminalColors.ANSI_RESET + "\n" +
                "\tSaturation: " + TerminalColors.ANSI_YELLOW + saturation + TerminalColors.ANSI_RESET + "\n" +
                "\tValue: " + TerminalColors.ANSI_YELLOW + value + TerminalColors.ANSI_RESET + "\n" +
                super.toStringNested();
    }
}
