package simu.model;

import eduni.distributions.ContinuousGenerator;
import simu.framework.Clock;
import simu.framework.Event;
import simu.framework.EventList;

public class DeliveryService extends ServicePoint {

    public DeliveryService(ContinuousGenerator generator, EventList eventList, EventType eventType) {
        super(generator, eventList, eventType);
    }

    @Override
    public void beginService() {
        reserved = true;
        double serviceTime = generator.sample();
        Customer customer = jono.peek();
        updateQueueStats();








        double chance = Math.random();

        // Delivery error logic
        if (chance < 0.05) {
            // 5% delivery failure -> return to kitchen
            this.eventTypeScheduled = EventType.DepartureFromKitchen;
        } else {
            // Successful delivery
            this.eventTypeScheduled = EventType.ReturnMoney; // reuse ReturnMoney as "success end"
        }

        eventList.add(new Event(this.eventTypeScheduled,
                Clock.getInstance().getTime() + serviceTime));
    }
}
