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
        if (!isOnQueue() || isReserved()) return;

        reserved = true;
        double serviceTime = generator.sample();
        Customer customer = jono.peek();
        updateQueueStats();

        if (customer.getWalkIn()) {

            this.eventTypeScheduled = EventType.DepartureFromCounterToDelivery; // order for delivery
            // this.eventTypeScheduled = EventType.CounterErrorToReception;   // Customer complains about wrong order → back to reception
        }
        else if (customer.getIsFaulty() == true) {
            // 5% chance → counter makes an error
            if (Math.random() < 0.5) {
                this.eventTypeScheduled = EventType.CounterErrorToKitchen;   // resend to kitchen
            } else {
                this.eventTypeScheduled = EventType.CounterErrorToReception; // refund at reception
            }
        }
        else {
            this.eventTypeScheduled = EventType.DepartureFromCounterToCostumer; // customer finished

        }

        // Schedule event
        eventList.add(new Event(this.eventTypeScheduled,
                Clock.getInstance().getTime() + serviceTime));
    }
}
