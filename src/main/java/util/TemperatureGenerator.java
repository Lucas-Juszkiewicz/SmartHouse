package util;

public class TemperatureGenerator {
    private double temperature;
    private boolean isGenerating = false;

    public TemperatureGenerator() {
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getTemperature() {
        return temperature;
    }

    public void startGenerating(){
        isGenerating = true;
    }

    public void stopGenerating(){
        isGenerating = false;
    }

    public double generate(double minTemperature, double maxTemperature){
        do{

        }while(isGenerating);
    }
}
