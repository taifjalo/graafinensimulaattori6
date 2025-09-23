package simu.model.kitchen;
import eduni.distributions.Bernoulli;
import eduni.distributions.ContinuousGenerator;

public class Cook  {
    private CookCompetency competency;
    private Boolean isBusy = false;
    private Boolean orderFailed = false;
    private ContinuousGenerator generator;

    Bernoulli bernoulli = new Bernoulli(0.85); // 85% success rate = 15% fail rate

    public Cook(CookCompetency competency, ContinuousGenerator generator) {
        this.competency = competency;
        this.generator = generator;
    }

    // method to prepare meal, returns true if successful, false if failed
    public Order prepareMeal() {
        isBusy = true;
        orderFailed = false;

        if (competency == CookCompetency.EXPERT) {

            double aika = 0;
            while (aika > 18) {
                aika = generator.sample();
            }

            Order expertOrder = new Order(aika);

            isBusy = false;
            return expertOrder;
        } else {

            double aika = 0;
            while (aika < 18) {
                aika = generator.sample();
            }

            Order inexpOrder = new Order(aika);

            orderFailed = bernoulli.sample() == 0;
            if (!orderFailed) {
                orderFailed = true;
                inexpOrder.setFailed(orderFailed);
            }

            isBusy = false;
            return inexpOrder;
        }
    }

    // getter for checking if cook is busy
    public Boolean isBusy() {
        return isBusy;
    }

}