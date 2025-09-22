package view;

public interface ISimulatorUI {

    // Controller needs inputs that it passes to the Engine
    public double getTime();
    public long getDelay();

    // Controller gives the UI results produced by the Engine
    public void setEndTime(double time);

    // Controller needs visualization
    public IVisualization getVisualization();

    public void updateDelayInfo(long delay); // NEW

}
