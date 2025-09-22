package simu.model;

import eduni.distributions.ContinuousGenerator;
import simu.framework.Clock;
import simu.framework.Event;
import simu.framework.EventList;

public class CounterService extends ServicePoint {

    public CounterService(ContinuousGenerator generator, EventList eventList, EventType eventType) {
        super(generator, eventList, eventType);
    }

    @Override
    public void beginService() {
        reserved = true;
        double serviceTime = generator.sample();
        Customer customer = jono.peek();
        updateQueueStats();

        double chance = Math.random();

        // Counter error logic
        if (chance < 0.05) {
            // 5% error in counter
            if (Math.random() < 0.5) {
                this.eventTypeScheduled = EventType.DepartureFromKitchen; // send back to kitchen
            } else {
                this.eventTypeScheduled = EventType.DepartureFromReception; // send back to reception
            }
        } else {
            // Either customer leaves happy or goes to delivery
            if (Math.random() < 0.5) {
                this.eventTypeScheduled = EventType.ReturnMoney; // use ReturnMoney as "finished happy"
            } else {
                this.eventTypeScheduled = EventType.DepartureFromCounter; // goes to delivery
            }
        }

        eventList.add(new Event(this.eventTypeScheduled,
                Clock.getInstance().getTime() + serviceTime));
    }
}
