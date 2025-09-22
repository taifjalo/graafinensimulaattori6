package controller;

import javafx.application.Platform;
import simu.framework.IEngine;
import simu.model.MyEngine;
import view.ISimulatorUI;

public class Controller implements IControllerForM, IControllerForV {   // NEW

    private IEngine engine;
    private ISimulatorUI ui;

    public Controller(ISimulatorUI ui) {
        this.ui = ui;
    }

    // Controlling the engine:

    @Override
    public void startSimulation() {
        // كل تشغيل جديد نسوي Engine جديد
        engine = new MyEngine(this);

        // نضبط إعدادات من GUI
        engine.setSimulationTime(ui.getTime());
        engine.setDelay(ui.getDelay());

        // نفرغ الشاشة القديمة
        ui.getVisualization().clearDisplay();

        // نشغّل المحرك على Thread منفصل
        Thread engineThread = new Thread((Runnable) engine);
        engineThread.setDaemon(true); // حتى يوقف ويا البرنامج
        engineThread.start();

        // ⚠️ ملاحظة: ممنوع نستعمل run() مباشرة، لأن راح ينفذ بنفس Thread مال JavaFX ويوقف الواجهة
    }

    @Override
    public void slowDown(double time) {
        if (engine != null) {
            engine.setDelay((long) time);
            Platform.runLater(() -> ui.updateDelayInfo(engine.getDelay())); // NEW
        }
    }

    @Override
    public void speedUp() {
        if (engine != null) {
            long currentDelay = engine.getDelay();
            if (currentDelay > 1) {
                engine.setDelay((long) (currentDelay * 0.9));
            }
            Platform.runLater(() -> ui.updateDelayInfo(engine.getDelay())); // NEW
        }
    }


    // Passing simulation results to the UI.
    // FX UI لازم يتحدث من JavaFX thread → نستخدم Platform.runLater

    @Override
    public void showEndTime(double time) {
        Platform.runLater(() -> ui.setEndTime(time));
    }

    @Override
    public void visualiseCustomer() {
        Platform.runLater(() -> ui.getVisualization().newCustomer());
    }
}
