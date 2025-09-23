package simu.model.kitchen;

import simu.model.Customer;
import simu.model.EventType;
import simu.model.ServicePoint;
import eduni.distributions.ContinuousGenerator;
import simu.framework.Clock;
import simu.framework.Event;
import simu.framework.EventList;
import java.util.ArrayList;

public class KitchenService extends ServicePoint {
    ArrayList<Cook> cooks = new ArrayList<>();

    public KitchenService(ContinuousGenerator generator, EventList eventList, EventType eventType, int cooksCount) {
        super(generator, eventList, eventType);
        for (int i = 0; i < cooksCount; i++) {
            cooks.add(new Cook(10, CookCompetency.EXPERT));
        }
    }

    @Override
    public void beginService() {

        for (Cook cook: cooks) {
            if (!cook.isBusy()) {
                // get the specific customer to serve

                Order succesfulOrder = cook.prepareMeal();
                Customer customer = jono.peek();

                if (!succesfulOrder.isFailed()) {
                    // handle case where meal preparation failed
                    customer.setIsFaulty(true);
                }

                eventList.add(new Event(this.eventTypeScheduled, Clock.getInstance().getTime() + succesfulOrder.getPreparationTime()));
            }

        }
    }

}