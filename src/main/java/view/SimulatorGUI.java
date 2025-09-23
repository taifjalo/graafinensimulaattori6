package view;

import controller.Controller;
import controller.IControllerForV;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import simu.framework.Trace;
import simu.framework.Trace.Level;

import java.text.DecimalFormat;

public class SimulatorGUI extends Application implements ISimulatorUI, IControllerForV {

    private IControllerForV controller;

    // UI Components
    private TextField timeField;
    private TextField delayField;
    private Label resultLabel;
    private Label delayInfoLabel;
    private Label simulationStatusLabel;
    private ProgressIndicator modernProgressIndicator;
    private StackPane loadingContainer;

    // Statistics Labels
    private Label totalCustomersLabel;
    private Label valWait, valService, valResponse;
    private Label valRec, valKit, valCount, valDel;
    private Label progressLabel;

    // Visualization
    private IVisualization display;

    // Animation components
    private Circle[] loadingCircles;
    private Timeline loadingAnimation;

    // Control buttons
    private Button startBtn, resetBtn, speedUpBtn, slowDownBtn;

    @Override
    public void init() {
        Trace.setTraceLevel(Level.INFO);
        controller = new Controller(this);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create main layout with modern design
        BorderPane root = createModernLayout();

        Scene scene = new Scene(root, 1200, 800);

        primaryStage.setScene(scene);
        primaryStage.setTitle("🚀 Ultra Modern Restaurant Simulator");
        primaryStage.setResizable(true);
        primaryStage.setOnCloseRequest(e -> {
            stopAllAnimations();
            Platform.exit();
            System.exit(0);
        });

        // Add entrance animation
        createEntranceAnimation(root);
        primaryStage.show();
    }

    private BorderPane createModernLayout() {
        BorderPane root = new BorderPane();
        root.setStyle("""
            -fx-background: linear-gradient(to bottom right, #667eea 0%, #764ba2 100%);
            """);

        // Top control panel
        root.setTop(createModernControlPanel());

        // Center visualization area
        root.setCenter(createVisualizationArea());

        // Bottom statistics
        root.setBottom(createModernStatistics());

        // Left side panel
        root.setLeft(createSidePanel());

        return root;
    }

    private VBox createModernControlPanel() {
        VBox controlPanel = new VBox(15);
        controlPanel.setPadding(new Insets(20));
        controlPanel.setStyle("""
            -fx-background-color: rgba(255, 255, 255, 0.1);
            -fx-background-radius: 15;
            -fx-border-color: rgba(255, 255, 255, 0.2);
            -fx-border-width: 1;
            -fx-border-radius: 15;
            """);

        // Title with glow effect
        Label titleLabel = new Label("🎯 RESTAURANT SIMULATOR");
        titleLabel.setStyle("""
            -fx-font-size: 28px;
            -fx-font-weight: bold;
            -fx-text-fill: white;
            -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.8), 10, 0, 0, 0);
            """);

        // Input section
        HBox inputSection = createInputSection();

        // Button section with amazing animations
        HBox buttonSection = createAnimatedButtonSection();

        // Status section
        HBox statusSection = createStatusSection();

        controlPanel.getChildren().addAll(titleLabel, inputSection, buttonSection, statusSection);

        // Add breathing animation to control panel
        createBreathingAnimation(controlPanel);

        return controlPanel;
    }

    private HBox createInputSection() {
        HBox inputBox = new HBox(20);
        inputBox.setAlignment(Pos.CENTER);

        // Simulation time input
        VBox timeBox = new VBox(5);
        Label timeLabel = new Label("⏱ Simulation Time");
        timeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        timeField = new TextField("1000");
        styleModernTextField(timeField);

        timeBox.getChildren().addAll(timeLabel, timeField);

        // Delay input
        VBox delayBox = new VBox(5);
        Label delayLabel = new Label("⚡ Initial Delay");
        delayLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        delayField = new TextField("100");
        styleModernTextField(delayField);

        delayBox.getChildren().addAll(delayLabel, delayField);

        inputBox.getChildren().addAll(timeBox, delayBox);
        return inputBox;
    }

    private void styleModernTextField(TextField field) {
        field.setStyle("""
            -fx-background-color: rgba(255, 255, 255, 0.2);
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-background-radius: 25;
            -fx-border-radius: 25;
            -fx-border-color: rgba(255, 255, 255, 0.3);
            -fx-border-width: 2;
            -fx-padding: 10;
            -fx-pref-width: 120;
            """);

        // Add focus animation
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), field);
                scale.setToX(1.05);
                scale.setToY(1.05);
                scale.play();
            } else {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), field);
                scale.setToX(1.0);
                scale.setToY(1.0);
                scale.play();
            }
        });
    }

    private HBox createAnimatedButtonSection() {
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        // Start button with epic animation
        startBtn = createModernButton("🚀 START", "#4CAF50", "#45a049");
        resetBtn = createModernButton("🔄 RESET", "#f44336", "#da190b");
        speedUpBtn = createModernButton("⚡ SPEED UP", "#2196F3", "#1976D2");
        slowDownBtn = createModernButton("🐌 SLOW DOWN", "#FF9800", "#F57C00");

        // Button event handlers
        startBtn.setOnAction(e -> {
            startSimulation();
            createStartAnimation();
        });

        resetBtn.setOnAction(e -> {
            resetSimulation();
            createResetAnimation();
        });

        speedUpBtn.setOnAction(e -> {
            controller.speedUp();
            createSpeedAnimation(speedUpBtn);
        });

        slowDownBtn.setOnAction(e -> {
            controller.slowDown(getTime());
            createSpeedAnimation(slowDownBtn);
        });

        buttonBox.getChildren().addAll(startBtn, resetBtn, speedUpBtn, slowDownBtn);
        return buttonBox;
    }

    private Button createModernButton(String text, String color, String hoverColor) {
        Button button = new Button(text);
        button.setStyle(String.format("""
            -fx-background-color: %s;
            -fx-text-fill: white;
            -fx-font-size: 12px;
            -fx-font-weight: bold;
            -fx-background-radius: 25;
            -fx-border-radius: 25;
            -fx-padding: 10 20;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);
            """, color));

        // Hover animation
        button.setOnMouseEntered(e -> {
            button.setStyle(String.format("""
                -fx-background-color: %s;
                -fx-text-fill: white;
                -fx-font-size: 12px;
                -fx-font-weight: bold;
                -fx-background-radius: 25;
                -fx-border-radius: 25;
                -fx-padding: 10 20;
                -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);
                """, hoverColor));

            ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
            scale.setToX(1.1);
            scale.setToY(1.1);
            scale.play();
        });

        button.setOnMouseExited(e -> {
            button.setStyle(String.format("""
                -fx-background-color: %s;
                -fx-text-fill: white;
                -fx-font-size: 12px;
                -fx-font-weight: bold;
                -fx-background-radius: 25;
                -fx-border-radius: 25;
                -fx-padding: 10 20;
                -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);
                """, color));

            ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });

        return button;
    }

    private HBox createStatusSection() {
        HBox statusBox = new HBox(30);
        statusBox.setAlignment(Pos.CENTER);

        // Status label with animation
        simulationStatusLabel = new Label("🟢 Ready to Simulate");
        simulationStatusLabel.setStyle("""
            -fx-text-fill: #4CAF50;
            -fx-font-size: 16px;
            -fx-font-weight: bold;
            -fx-effect: dropshadow(gaussian, rgba(76,175,80,0.5), 5, 0, 0, 0);
            """);

        // Progress label
        progressLabel = new Label("Progress: 0%");
        progressLabel.setStyle("""
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            """);

        // Delay info
        delayInfoLabel = new Label("Delay: 0ms");
        delayInfoLabel.setStyle("""
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            """);

        // Result label
        resultLabel = new Label("Total Time: 0");
        resultLabel.setStyle("""
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            """);

        statusBox.getChildren().addAll(simulationStatusLabel, progressLabel, delayInfoLabel, resultLabel);
        return statusBox;
    }

    private StackPane createVisualizationArea() {
        StackPane centerArea = new StackPane();
        centerArea.setPadding(new Insets(20));

        // Background with gradient
        Rectangle background = new Rectangle();
        background.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#2C3E50")),
                new Stop(1, Color.web("#34495E"))));
        background.setArcWidth(20);
        background.setArcHeight(20);
        background.widthProperty().bind(centerArea.widthProperty().subtract(40));
        background.heightProperty().bind(centerArea.heightProperty().subtract(40));

        // Visualization canvas
        display = new Visualization(800, 500);
        Canvas canvas = (Canvas) display;

        // Loading animation container
        loadingContainer = createEpicLoadingAnimation();
        loadingContainer.setVisible(false);

        centerArea.getChildren().addAll(background, canvas, loadingContainer);
        return centerArea;
    }

    private StackPane createEpicLoadingAnimation() {
        StackPane container = new StackPane();
        container.setAlignment(Pos.CENTER);

        // Background blur
        Rectangle blur = new Rectangle(800, 500);
        blur.setFill(Color.web("#2C3E50", 0.8));
        blur.setArcWidth(20);
        blur.setArcHeight(20);

        // Loading circles
        HBox circleBox = new HBox(10);
        circleBox.setAlignment(Pos.CENTER);

        loadingCircles = new Circle[5];
        Color[] colors = {
                Color.web("#E74C3C"), Color.web("#F39C12"), Color.web("#F1C40F"),
                Color.web("#2ECC71"), Color.web("#3498DB")
        };

        for (int i = 0; i < 5; i++) {
            loadingCircles[i] = new Circle(15);
            loadingCircles[i].setFill(colors[i]);
            loadingCircles[i].setEffect(new DropShadow(10, colors[i]));
            circleBox.getChildren().add(loadingCircles[i]);
        }

        // Loading text
        Label loadingText = new Label("🔄 SIMULATION RUNNING...");
        loadingText.setStyle("""
            -fx-text-fill: white;
            -fx-font-size: 24px;
            -fx-font-weight: bold;
            -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.5), 10, 0, 0, 0);
            """);

        // Progress indicator
        modernProgressIndicator = new ProgressIndicator();
        modernProgressIndicator.setPrefSize(80, 80);
        modernProgressIndicator.setStyle("""
            -fx-progress-color: #3498DB;
            -fx-accent: #3498DB;
            """);

        VBox loadingContent = new VBox(20);
        loadingContent.setAlignment(Pos.CENTER);
        loadingContent.getChildren().addAll(circleBox, loadingText, modernProgressIndicator);

        container.getChildren().addAll(blur, loadingContent);

        // Create epic loading animation
        createLoadingCircleAnimation();

        return container;
    }

    private void createLoadingCircleAnimation() {
        loadingAnimation = new Timeline();

        for (int i = 0; i < loadingCircles.length; i++) {
            Circle circle = loadingCircles[i];

            // Bounce animation
            TranslateTransition bounce = new TranslateTransition(Duration.millis(800), circle);
            bounce.setFromY(0);
            bounce.setToY(-30);
            bounce.setCycleCount(Animation.INDEFINITE);
            bounce.setAutoReverse(true);
            bounce.setDelay(Duration.millis(i * 100));

            // Scale animation
            ScaleTransition scale = new ScaleTransition(Duration.millis(400), circle);
            scale.setFromX(1.0);
            scale.setFromY(1.0);
            scale.setToX(1.3);
            scale.setToY(1.3);
            scale.setCycleCount(Animation.INDEFINITE);
            scale.setAutoReverse(true);
            scale.setDelay(Duration.millis(i * 150));

            ParallelTransition parallel = new ParallelTransition(bounce, scale);

            loadingAnimation.getKeyFrames().add(
                    new KeyFrame(Duration.millis(0), e -> parallel.play())
            );
        }

        loadingAnimation.setCycleCount(Animation.INDEFINITE);
    }

    private VBox createSidePanel() {
        VBox sidePanel = new VBox(15);
        sidePanel.setPrefWidth(250);
        sidePanel.setPadding(new Insets(20));
        sidePanel.setStyle("""
            -fx-background-color: rgba(44, 62, 80, 0.9);
            -fx-background-radius: 15;
            -fx-border-color: rgba(255, 255, 255, 0.1);
            -fx-border-width: 1;
            -fx-border-radius: 15;
            """);

        Label title = new Label("📊 LIVE STATISTICS");
        title.setStyle("""
            -fx-text-fill: white;
            -fx-font-size: 18px;
            -fx-font-weight: bold;
            -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.3), 5, 0, 0, 0);
            """);

        // Statistics cards
        VBox statsCards = new VBox(10);

        // Customer metrics card
        VBox customerCard = createStatCard("👥 CUSTOMER METRICS");

        // Initialize stat labels
        totalCustomersLabel = new Label("0");
        valWait = new Label("0.00");
        valService = new Label("0.00");
        valResponse = new Label("0.00");

        // Add customer metrics to card
        customerCard.getChildren().addAll(
                createStatRow("Total Customers:", totalCustomersLabel),
                createStatRow("Avg Wait Time:", valWait),
                createStatRow("Avg Service Time:", valService),
                createStatRow("Avg Response Time:", valResponse)
        );

        // Utilization card
        VBox utilizationCard = createStatCard("⚙ UTILIZATION");

        // Initialize utilization labels
        valRec = new Label("0%");
        valKit = new Label("0%");
        valCount = new Label("0%");
        valDel = new Label("0%");

        // Add utilization metrics to card
        utilizationCard.getChildren().addAll(
                createStatRow("Reception:", valRec),
                createStatRow("Kitchen:", valKit),
                createStatRow("Counter:", valCount),
                createStatRow("Delivery:", valDel)
        );

        statsCards.getChildren().addAll(customerCard, utilizationCard);

        sidePanel.getChildren().addAll(title, statsCards);
        return sidePanel;
    }

    private VBox createStatCard(String title) {
        VBox card = new VBox(8);
        card.setStyle("""
            -fx-background-color: rgba(255, 255, 255, 0.1);
            -fx-background-radius: 10;
            -fx-padding: 15;
            -fx-border-color: rgba(255, 255, 255, 0.2);
            -fx-border-width: 1;
            -fx-border-radius: 10;
            """);

        Label cardTitle = new Label(title);
        cardTitle.setStyle("""
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            """);

        card.getChildren().add(cardTitle);
        return card;
    }

    private HBox createStatRow(String labelText, Label valueLabel) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #BDC3C7; -fx-font-size: 12px;");

        valueLabel.setStyle("""
            -fx-text-fill: #3498DB;
            -fx-font-size: 12px;
            -fx-font-weight: bold;
            """);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(label, spacer, valueLabel);
        return row;
    }

    private VBox createModernStatistics() {
        VBox statsArea = new VBox(10);
        statsArea.setPadding(new Insets(20));
        statsArea.setStyle("""
            -fx-background-color: rgba(255, 255, 255, 0.05);
            -fx-border-color: rgba(255, 255, 255, 0.1);
            -fx-border-width: 1 0 0 0;
            """);

        Label title = new Label("🏆 SIMULATION RESULTS");
        title.setStyle("""
            -fx-text-fill: white;
            -fx-font-size: 20px;
            -fx-font-weight: bold;
            -fx-effect: dropshadow(gaussian, rgba(255,255,255,0.3), 5, 0, 0, 0);
            """);

        // Results will be shown here after simulation
        Label resultsPlaceholder = new Label("🔍 Results will appear here after simulation completes");
        resultsPlaceholder.setStyle("""
            -fx-text-fill: #BDC3C7;
            -fx-font-size: 14px;
            -fx-font-style: italic;
            """);

        statsArea.getChildren().addAll(title, resultsPlaceholder);
        return statsArea;
    }

    // ===== ANIMATION METHODS =====

    private void createEntranceAnimation(BorderPane root) {
        root.setOpacity(0);
        root.setScaleX(0.8);
        root.setScaleY(0.8);

        FadeTransition fade = new FadeTransition(Duration.millis(800), root);
        fade.setFromValue(0);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.millis(800), root);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1.0);
        scale.setToY(1.0);

        ParallelTransition entrance = new ParallelTransition(fade, scale);
        entrance.play();
    }

    private void createBreathingAnimation(VBox panel) {
        ScaleTransition breathe = new ScaleTransition(Duration.millis(3000), panel);
        breathe.setFromX(1.0);
        breathe.setFromY(1.0);
        breathe.setToX(1.02);
        breathe.setToY(1.02);
        breathe.setCycleCount(Animation.INDEFINITE);
        breathe.setAutoReverse(true);
        breathe.play();
    }

    private void createStartAnimation() {
        simulationStatusLabel.setText("🟡 Simulation Starting...");
        simulationStatusLabel.setStyle("""
            -fx-text-fill: #F39C12;
            -fx-font-size: 16px;
            -fx-font-weight: bold;
            -fx-effect: dropshadow(gaussian, rgba(243,156,18,0.5), 5, 0, 0, 0);
            """);

        loadingContainer.setVisible(true);
        loadingAnimation.play();

        // Disable start button with animation
        startBtn.setDisable(true);
        FadeTransition fade = new FadeTransition(Duration.millis(300), startBtn);
        fade.setToValue(0.5);
        fade.play();

        // Update status after delay
        Timeline statusUpdate = new Timeline(
                new KeyFrame(Duration.millis(1000), e -> {
                    simulationStatusLabel.setText("🔥 Simulation Running!");
                    simulationStatusLabel.setStyle("""
                    -fx-text-fill: #E74C3C;
                    -fx-font-size: 16px;
                    -fx-font-weight: bold;
                    -fx-effect: dropshadow(gaussian, rgba(231,76,60,0.5), 5, 0, 0, 0);
                    """);
                })
        );
        statusUpdate.play();
    }

    private void createResetAnimation() {
        loadingContainer.setVisible(false);
        loadingAnimation.stop();

        simulationStatusLabel.setText("🟢 Ready to Simulate");
        simulationStatusLabel.setStyle("""
            -fx-text-fill: #4CAF50;
            -fx-font-size: 16px;
            -fx-font-weight: bold;
            -fx-effect: dropshadow(gaussian, rgba(76,175,80,0.5), 5, 0, 0, 0);
            """);

        // Re-enable start button
        startBtn.setDisable(false);
        FadeTransition fade = new FadeTransition(Duration.millis(300), startBtn);
        fade.setToValue(1.0);
        fade.play();

        // Reset progress
        progressLabel.setText("Progress: 0%");
        resultLabel.setText("Total Time: 0");

        // Reset all statistics
        resetStatistics();
    }

    private void createSpeedAnimation(Button button) {
        RotateTransition rotate = new RotateTransition(Duration.millis(200), button);
        rotate.setFromAngle(0);
        rotate.setToAngle(360);
        rotate.play();
    }

    private void stopAllAnimations() {
        if (loadingAnimation != null) loadingAnimation.stop();
    }

    // ===== HELPER METHODS =====

    private void resetStatistics() {
        totalCustomersLabel.setText("0");
        valWait.setText("0.00");
        valService.setText("0.00");
        valResponse.setText("0.00");
        valRec.setText("0%");
        valKit.setText("0%");
        valCount.setText("0%");
        valDel.setText("0%");
    }



    private void resetSimulation() {
        display.clearDisplay();
        createResetAnimation();
    }

    // Update progress during simulation
    public void updateProgress(double currentTime, double totalTime) {
        Platform.runLater(() -> {
            double progress = (currentTime / totalTime) * 100;
            progressLabel.setText(String.format("Progress: %.1f%%", progress));
            modernProgressIndicator.setProgress(progress / 100.0);
        });
    }

    // Update total customers count during simulation
    public void updateTotalCustomers(int count) {
        Platform.runLater(() -> {
            totalCustomersLabel.setText(String.valueOf(count));
        });
    }

    // Update statistics with detailed info
    public void updateStats(double avgWait, double avgService, double avgResponse,
                            double utilRec, double utilKit, double utilCount, double utilDel,
                            int totalCustomers) {
        Platform.runLater(() -> {
            valWait.setText(String.format("%.2f", avgWait));
            valService.setText(String.format("%.2f", avgService));
            valResponse.setText(String.format("%.2f", avgResponse));
            valRec.setText(String.format("%.2f%%", utilRec * 100));
            valKit.setText(String.format("%.2f%%", utilKit * 100));
            valCount.setText(String.format("%.2f%%", utilCount * 100));
            valDel.setText(String.format("%.2f%%", utilDel * 100));
            totalCustomersLabel.setText(String.valueOf(totalCustomers));
        });
    }

    // ===== INTERFACE IMPLEMENTATIONS =====

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
        Platform.runLater(() -> {
            resultLabel.setText("Total Time: " + formatter.format(time));
            simulationStatusLabel.setText("✅ Simulation Completed!");
            simulationStatusLabel.setStyle("""
                -fx-text-fill: #4CAF50;
                -fx-font-size: 16px;
                -fx-font-weight: bold;
                -fx-effect: dropshadow(gaussian, rgba(76,175,80,0.8), 10, 0, 0, 0);
                """);
            progressLabel.setText("Progress: 100%");
            loadingContainer.setVisible(false);
            loadingAnimation.stop();

            // Re-enable start button
            startBtn.setDisable(false);
            FadeTransition fade = new FadeTransition(Duration.millis(300), startBtn);
            fade.setToValue(1.0);
            fade.play();
        });
    }

    @Override
    public IVisualization getVisualization() {
        return display;
    }

    @Override
    public void updateDelayInfo(long delay) {
        Platform.runLater(() -> {
            delayInfoLabel.setText("Delay: " + delay + "ms");
        });
    }

    @Override
    public void startSimulation() {
        controller.startSimulation();
    }

    @Override
    public void speedUp() {
        controller.speedUp();
    }

    @Override
    public void slowDown(double time) {
        controller.slowDown(time);
    }

    @Override
    public void visualiseCustomer() {
        display.newCustomer();
    }
}