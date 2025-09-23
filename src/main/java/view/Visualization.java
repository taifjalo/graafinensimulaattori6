package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;

public class Visualization extends Canvas implements IVisualization {

    private final GraphicsContext gc;
    private double customerX = 50;
    private double customerY = 50;
    private int customerCount = 0;
    private Color[] customerColors = {
            Color.web("#E74C3C"), Color.web("#3498DB"), Color.web("#2ECC71"),
            Color.web("#F39C12"), Color.web("#9B59B6"), Color.web("#1ABC9C")
    };

    public Visualization(int w, int h) {
        super(w, h);
        gc = this.getGraphicsContext2D();
        clearDisplay();
    }

    @Override
    public void clearDisplay() {
        // Modern gradient background
        gc.setFill(new RadialGradient(0, 0, 0.5, 0.5, 0.8, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#34495E")),
                new Stop(1, Color.web("#2C3E50"))));
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());

        // Reset customer position
        customerX = 50;
        customerY = 50;
        customerCount = 0;

        // Draw restaurant layout
        drawRestaurantLayout();
    }

    private void drawRestaurantLayout() {
        // Reception area
        gc.setFill(Color.web("#3498DB", 0.7));
        gc.fillRoundRect(50, 50, 150, 80, 15, 15);
        gc.setFill(Color.WHITE);
        gc.fillText("🏪 RECEPTION", 85, 95);

        // Kitchen area
        gc.setFill(Color.web("#E74C3C", 0.7));
        gc.fillRoundRect(250, 50, 150, 80, 15, 15);
        gc.setFill(Color.WHITE);
        gc.fillText("👨‍🍳 KITCHEN", 295, 95);

        // Counter area
        gc.setFill(Color.web("#2ECC71", 0.7));
        gc.fillRoundRect(450, 50, 150, 80, 15, 15);
        gc.setFill(Color.WHITE);
        gc.fillText("🛎️ COUNTER", 485, 95);

        // Delivery area
        gc.setFill(Color.web("#F39C12", 0.7));
        gc.fillRoundRect(650, 50, 120, 80, 15, 15);
        gc.setFill(Color.WHITE);
        gc.fillText("🚚 DELIVERY", 675, 95);

        // Draw connecting arrows
        gc.setStroke(Color.web("#BDC3C7"));
        gc.setLineWidth(3);

        // Reception → Kitchen
        gc.strokeLine(200, 90, 250, 90);
        drawArrow(240, 85, 240, 95, 250, 90);

        // Kitchen → Counter
        gc.strokeLine(400, 90, 450, 90);
        drawArrow(440, 85, 440, 95, 450, 90);

        // Counter → Delivery
        gc.strokeLine(600, 90, 650, 90);
        drawArrow(640, 85, 640, 95, 650, 90);
    }

    private void drawArrow(double x1, double y1, double x2, double y2, double tipX, double tipY) {
        gc.strokeLine(x1, y1, tipX, tipY);
        gc.strokeLine(x2, y2, tipX, tipY);
    }

    @Override
    public void newCustomer() {
        customerCount++;

        // Use different colors for customers
        Color customerColor = customerColors[customerCount % customerColors.length];

        // Create glowing effect
        gc.setFill(new RadialGradient(0, 0, 0.5, 0.5, 1.0, true, CycleMethod.NO_CYCLE,
                new Stop(0, customerColor),
                new Stop(0.7, customerColor.deriveColor(0, 1, 1, 0.8)),
                new Stop(1, customerColor.deriveColor(0, 1, 1, 0.3))));

        // Draw customer with glow
        gc.fillOval(customerX - 8, customerY - 8, 25, 25);

        // Draw customer emoji
        gc.setFill(Color.WHITE);
        gc.fillText("👤", customerX - 2, customerY + 8);

        // Draw customer number
        gc.setFill(Color.WHITE);
        gc.fillText(String.valueOf(customerCount), customerX + 15, customerY + 5);

        // Update position for next customer
        customerX += 35;
        if (customerX > this.getWidth() - 50) {
            customerX = 50;
            customerY += 35;
            if (customerY > this.getHeight() - 50) {
                customerY = 50;
            }
        }

        // Add sparkle effect
        drawSparkles(customerX - 17, customerY - 5);
    }

    private void drawSparkles(double x, double y) {
        gc.setFill(Color.YELLOW);

        // Draw small sparkles around customer
        for (int i = 0; i < 5; i++) {
            double sparkleX = x + (Math.random() - 0.5) * 60;
            double sparkleY = y + (Math.random() - 0.5) * 60;

            gc.fillOval(sparkleX, sparkleY, 3, 3);

            // Cross sparkle
            gc.fillRect(sparkleX - 1, sparkleY - 5, 2, 10);
            gc.fillRect(sparkleX - 5, sparkleY - 1, 10, 2);
        }
    }
}