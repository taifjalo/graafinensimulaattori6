package view;

import controller.Controller;
import controller.IControllerForV;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import simu.framework.Trace;
import simu.framework.Trace.Level;

import java.text.DecimalFormat;

public class SimulatorGUI extends Application implements ISimulatorUI, IControllerForV {

    private IControllerForV controller;

    // UI components
    private TextField timeField;
    private TextField delayField;
    private Label result;
    private Label delayInfo;

    // Statistics labels
    private Label valWait;
    private Label valService;
    private Label valResponse;
    private Label valRec;
    private Label valKit;
    private Label valCount;
    private Label valDel;

    // Visualization
    private IVisualization display;

    @Override
    public void init() {
        Trace.setTraceLevel(Level.INFO);
        controller = new Controller(this);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Controls
        root.setTop(buildControls());

        // Visualization
        display = new Visualization(600, 400);
        root.setCenter((Canvas) display);

        // Stats
        root.setBottom(buildStats());

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Restaurant Simulator");
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();
    }

    // Build top controls
    private HBox buildControls() {
        HBox box = new HBox(10);
        box.setPadding(new Insets(10));

        Label timeLabel = new Label("Sim Time:");
        timeLabel.setFont(Font.font(16));
        timeField = new TextField("100");
        timeField.setPrefWidth(80);

        Label delayLabel = new Label("Initial Delay:");
        delayLabel.setFont(Font.font(16));
        delayField = new TextField("100");
        delayField.setPrefWidth(80);

        Button startBtn = new Button("Start");
        Button resetBtn = new Button("Reset");
        Button speedUpBtn = new Button("Speed Up");
        Button slowDownBtn = new Button("Slow Down");

        Label delayInfoLabel = new Label("Current Delay:");
        delayInfoLabel.setFont(Font.font(16));
        delayInfo = new Label("0 ms");
        delayInfo.setFont(Font.font(16));

        result = new Label("Total Time: 0");
        result.setFont(Font.font(16));

        startBtn.setOnAction(e -> {
            controller.startSimulation();
            startBtn.setDisable(true);
        });
        resetBtn.setOnAction(e -> {
            display.clearDisplay();
            result.setText("Total Time: 0");
            updateDelayInfo(0);
            startBtn.setDisable(false);
        });
        speedUpBtn.setOnAction(e -> controller.speedUp());
        slowDownBtn.setOnAction(e -> controller.slowDown(getTime()));

        box.getChildren().addAll(timeLabel, timeField, delayLabel, delayField,
                startBtn, resetBtn, speedUpBtn, slowDownBtn,
                delayInfoLabel, delayInfo, result);

        return box;
    }

    // Build bottom statistics
    private GridPane buildStats() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(20);
        grid.setVgap(5);

        Label avgWait = new Label("Avg Waiting Time:");
        Label avgService = new Label("Avg Service Time:");
        Label avgResponse = new Label("Avg Response Time:");
        Label utilizationReception = new Label("Reception Utilization:");
        Label utilizationKitchen = new Label("Kitchen Utilization:");
        Label utilizationCounter = new Label("Counter Utilization:");
        Label utilizationDelivery = new Label("Delivery Utilization:");

        valWait = new Label("0");
        valService = new Label("0");
        valResponse = new Label("0");
        valRec = new Label("0");
        valKit = new Label("0");
        valCount = new Label("0");
        valDel = new Label("0");

        grid.addRow(0, avgWait, valWait);
        grid.addRow(1, avgService, valService);
        grid.addRow(2, avgResponse, valResponse);
        grid.addRow(3, utilizationReception, valRec);
        grid.addRow(4, utilizationKitchen, valKit);
        grid.addRow(5, utilizationCounter, valCount);
        grid.addRow(6, utilizationDelivery, valDel);

        return grid;
    }

    // --- ISimulatorUI methods ---
    @Override
    public double getTime() {
        return Double.parseDouble(timeField.getText());
    }

    @Override
    public long getDelay() {
        return Long.parseLong(delayField.getText());
    }

    @Override
    public void setEndTime(double time) {
        DecimalFormat formatter = new DecimalFormat("#0.00");
        this.result.setText("Total Time: " + formatter.format(time));
    }

    @Override
    public IVisualization getVisualization() {
        return display;
    }

    @Override
    public void updateDelayInfo(long delay) {
        delayInfo.setText(delay + " ms");
    }

    // --- IControllerForV methods ---
    @Override
    public void startSimulation() {
        // not used
    }

    @Override
    public void speedUp() {
        // not used
    }

    @Override
    public void slowDown(double time) {
        // not used
    }

    @Override
    public void visualiseCustomer() {
        // لاحقاً: نرسم زبون جديد على الـ Canvas
        display.newCustomer();
    }

    // NEW helper: update statistics after simulation ends
    public void updateStats(double avgWait, double avgService, double avgResponse,
                            double utilRec, double utilKit, double utilCount, double utilDel) {
        valWait.setText(String.format("%.2f", avgWait));
        valService.setText(String.format("%.2f", avgService));
        valResponse.setText(String.format("%.2f", avgResponse));
        valRec.setText(String.format("%.2f", utilRec));
        valKit.setText(String.format("%.2f", utilKit));
        valCount.setText(String.format("%.2f", utilCount));
        valDel.setText(String.format("%.2f", utilDel));
    }
}
