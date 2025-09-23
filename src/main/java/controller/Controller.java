package controller;

import javafx.application.Platform;
import simu.framework.IEngine;
import simu.model.MyEngine;
import simu.model.Results;
import view.ISimulatorUI;
import view.SimulatorGUI;

public class Controller implements IControllerForM, IControllerForV {

    private IEngine engine;
    private ISimulatorUI ui;
    private SimulatorGUI gui; // UUSI: Direct reference for enhanced features

    public Controller(ISimulatorUI ui) {
        this.ui = ui;
        if (ui instanceof SimulatorGUI) {
            this.gui = (SimulatorGUI) ui;
        }
    }

    @Override
    public void startSimulation() {
        // UUSI: Reset Results before new simulation
        Results.reset();

        engine = new MyEngine(this);
        engine.setSimulationTime(ui.getTime());
        engine.setDelay(ui.getDelay());
        ui.getVisualization().clearDisplay();

        Thread engineThread = new Thread((Runnable) engine);
        engineThread.setDaemon(true);
        engineThread.start();
    }

    @Override
    public void slowDown(double time) {
        if (engine != null) {
            engine.setDelay((long) time);
            Platform.runLater(() -> ui.updateDelayInfo(engine.getDelay()));
        }
    }

    @Override
    public void speedUp() {
        if (engine != null) {
            long currentDelay = engine.getDelay();
            if (currentDelay > 1) {
                engine.setDelay((long) (currentDelay * 0.9));
            }
            Platform.runLater(() -> ui.updateDelayInfo(engine.getDelay()));
        }
    }

    @Override
    public void showEndTime(double time) {
        Platform.runLater(() -> {
            ui.setEndTime(time);
            // UUSI: Update final statistics from Results class
            updateFinalStatistics();
        });
    }

    @Override
    public void visualiseCustomer() {
        Platform.runLater(() -> ui.getVisualization().newCustomer());
    }

    // UUSI: Update statistics during simulation
    public void updateSimulationProgress(double currentTime, double totalTime, int totalCustomers) {
        if (gui != null) {
            Platform.runLater(() -> {
                gui.updateProgress(currentTime, totalTime);
                gui.updateTotalCustomers(totalCustomers);
            });
        }
    }

    // UUSI: Update final statistics
    private void updateFinalStatistics() {
        if (gui != null) {
            double avgWait = Results.getAverageWaitingTime();
            double avgService = Results.getAverageServiceTime();
            double avgResponse = Results.getAverageResponseTime();
            double utilRec = Results.getUtilization("Reception");
            double utilKit = Results.getUtilization("Kitchen");
            double utilCount = Results.getUtilization("Counter");
            double utilDel = Results.getUtilization("Delivery");

            // Get total customers from somewhere - you might need to modify Results class
            int totalCustomers = (int) Results.getTotalCustomers(); // You'll need to add this method

            gui.updateStats(avgWait, avgService, avgResponse,
                    utilRec, utilKit, utilCount, utilDel, totalCustomers);
        }
    }
}
