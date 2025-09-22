package simu.model;

import eduni.distributions.ContinuousGenerator;
import simu.framework.Clock;
import simu.framework.Event;
import simu.framework.EventList;

public class ReceptionService extends ServicePoint {

    public ReceptionService(ContinuousGenerator generator, EventList eventList, EventType eventType) {
        super(generator, eventList, eventType);
    }

    public void beginService() {
        reserved = true;
        double serviceTime = generator.sample();
        Customer customer=jono.peek();
        double chance = Math.random();
        updateQueueStats(); //
        if (customer.getIsFaulty()){
            this.eventTypeScheduled = EventType.ReturnMoney;
        }
        else if  (chance>0.9){
            this.eventTypeScheduled = EventType.PaymentFailed;
        }
        else {
            this.eventTypeScheduled = EventType.DepartureFromReception;
        }
        eventList.add(new Event(this.eventTypeScheduled, Clock.getInstance().getTime()+serviceTime));
    }
}


