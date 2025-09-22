package simu.model;

import eduni.distributions.ContinuousGenerator;
import simu.framework.Clock;
import simu.framework.Event;
import simu.framework.EventList;

public class KitchenService extends ServicePoint {

    public KitchenService(ContinuousGenerator generator, EventList eventList, EventType eventType) {
        super(generator, eventList, eventType);
    }

    @Override
    public void beginService() {
        reserved = true;
        double serviceTime = generator.sample();
        Customer customer = jono.peek();
        updateQueueStats();

        double chance = Math.random();

        // Kitchen error logic
        if (chance < 0.1) {
            // 10% error in kitchen
            if (Math.random() < 0.5) {
                this.eventTypeScheduled = EventType.DepartureFromKitchen; // retry kitchen
            } else {
                this.eventTypeScheduled = EventType.ReturnMoney; // send back to reception for refund
            }
        } else {
            this.eventTypeScheduled = EventType.DepartureFromKitchen; // goes to counter
        }

        eventList.add(new Event(this.eventTypeScheduled,
                Clock.getInstance().getTime() + serviceTime));
    }
}
