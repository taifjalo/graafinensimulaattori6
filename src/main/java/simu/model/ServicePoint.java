package simu.model;

import eduni.distributions.ContinuousGenerator;
import simu.framework.Clock;
import simu.framework.Event;
import simu.framework.EventList;

import java.util.LinkedList;

// TODO:
// Service Point functionalities & calculations (+ variables needed) and reporting to be implemented
public class ServicePoint {
    protected LinkedList<Customer> jono = new LinkedList<Customer>(); // Data Structure used
    protected ContinuousGenerator generator;
    protected EventList eventList;
    protected EventType eventTypeScheduled;
    //Queuestrategy strategy; // option: ordering of the customer
    protected boolean reserved = false;
    private int totalCustomersServed = 0;
    private double areaUnderQueue = 0.0;
    private double lastUpdateTime = 0.0;

    public ServicePoint(ContinuousGenerator generator, EventList tapahtumalista, EventType tyyppi){
        this.eventList = tapahtumalista;
        this.generator = generator;
        this.eventTypeScheduled = tyyppi;

    }

    public void addQueue(Customer a){   // First customer at the queue is always on the service
        updateQueueStats();
        jono.add(a);
    }

    public Customer removeQueue(){		// Remove serviced customer
        updateQueueStats();
        reserved = false;
        totalCustomersServed++;
        return jono.poll();
    }

    public void beginService() {  		// Begins a new service, customer is on the queue during the service
        reserved = true;
        double serviceTime = generator.sample();
        eventList.add(new Event(eventTypeScheduled, Clock.getInstance().getTime()+serviceTime));
    }

    public boolean isReserved(){
        return reserved;
    }

    public boolean isOnQueue(){
        return jono.size() != 0;
    }

    public int getTotalCustomersServed() {
        return totalCustomersServed;
    }

    public void updateQueueStats() {
        double now = Clock.getInstance().getTime();
        int effectiveQueueLength = jono.size();

        // jos palvelupiste varattu, vähennetään yksi (asiakas palvelussa ei ole jonossa)
        if (reserved && effectiveQueueLength > 0) {
            effectiveQueueLength--;
        }

        areaUnderQueue += effectiveQueueLength * (now - lastUpdateTime);
        lastUpdateTime = now;
    }

    public double getAverageQueueLength() {
        double simTime = Clock.getInstance().getTime();
        if (simTime == 0) return 0;
        return areaUnderQueue / simTime;
    }
}