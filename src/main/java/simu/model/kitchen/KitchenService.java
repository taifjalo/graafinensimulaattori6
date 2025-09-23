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

    public KitchenService(ContinuousGenerator generator, EventList eventList, EventType eventType) {
        super(generator, eventList, eventType);

        // create 3 cooks with different competencies
        cooks.add(new Cook(CookCompetency.EXPERT, generator));
        cooks.add(new Cook(CookCompetency.INEXPERIENCED, generator));
        cooks.add(new Cook(CookCompetency.INEXPERIENCED, generator));
    }

    @Override
    public void beginService() {
        for (Cook cook: cooks) {
            if (!cook.isBusy()) {
                Order succesfulOrder = cook.prepareMeal();
                Customer customer = jono.peek();

                if (!succesfulOrder.isFailed()) {
                    // handle case where meal preparation failed
                    customer.setIsFaulty(true);
                }
                eventList.add(new Event(this.eventTypeScheduled, Clock.getInstance().getTime() + succesfulOrder.getPreparationTime()));
                break;
            }
        }
    }

}