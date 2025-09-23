package simu.model.kitchen;
import eduni.distributions.Bernoulli;

public class Cook  {
    private double speed;
    private CookCompetency competency;
    private Boolean isBusy = false;
    private Boolean orderFailed = false;
    private double preparationTimeExpert = 5;
    private double preparationTimeInexperienced = 15;

    Bernoulli bernoulli = new Bernoulli(0.85); // 85% success rate = 15% fail rate

    public Cook(double speed, CookCompetency competency) {
        this.speed = speed;
        this.competency = competency;
    }

    // method to prepare meal, returns true if successful, false if failed
    public Order prepareMeal() {
        try {
            isBusy = true;
            orderFailed = false;
            if (competency == CookCompetency.EXPERT) {
                Order expertOrder = new Order(preparationTimeExpert);

                Thread.sleep((long) (preparationTimeExpert / speed)); // Faster preparation time for expert cooks

                return expertOrder;
            } else {
                Order inexpOrder = new Order(preparationTimeInexperienced);

                orderFailed = bernoulli.sample() == 0;
                if (!orderFailed) {
                    orderFailed = true;
                    inexpOrder.setFailed(orderFailed);
                }
                Thread.sleep((long) (preparationTimeInexperienced / speed)); // Slower preparation time for inexperienced cooks
                return inexpOrder;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            return new Order(0); // Meal preparation failed
        } finally {
            isBusy = false;
        }
    }

    // getter for checking if cook is busy
    public Boolean isBusy() {
        return isBusy;
    }

}