package simu.model.kitchen;

public class Order {
    private boolean failed = false;
    private double preparationTime;

    public Order(double preparationTime) {
        this.failed = false;
        this.preparationTime = preparationTime;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public double getPreparationTime() {
        return preparationTime;
    }

    public boolean isFailed() {
        return failed;
    }
}