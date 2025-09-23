package simu.model;

import simu.framework.Clock;
import simu.framework.Trace;



// TODO:
// Customer to be implemented according to the requirements of the simulation model (data!)

public class Customer {
    private double arrivalTime;
    private double removalTime;
    private int id;
    private static int i = 1;
    private static long sum = 0;
    private boolean walkIn;
    private boolean isFaulty;



    public Customer(boolean walkIn) {
        id = i++;
        this.walkIn = walkIn;
        isFaulty = false;
        arrivalTime = Clock.getInstance().getTime();
        Trace.out(Trace.Level.INFO, "New customer: " + id + "\narrived at: " + arrivalTime);
    }

    public double getRemovalTime() {
        return removalTime;
    }

    public void setRemovalTime(double removalTime) {
        this.removalTime = removalTime;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public boolean getIsFaulty(){
        return isFaulty;
    }

    public void setIsFaulty(boolean isFaulty){
        this.isFaulty = isFaulty;
    }

    public void reportPaymentIssue(){
        Trace.out(Trace.Level.ERR, "Customer " + id + " return to the Reception queue. Payment problem at" + Clock.getInstance().getTime());
    }

    public void reportResults() {
        Trace.out(Trace.Level.INFO, "\nCustomer " + id + " ready! ");
        Trace.out(Trace.Level.INFO, "Customer "   + id + " arrived: " + arrivalTime);
        Trace.out(Trace.Level.INFO,"Customer "    + id + " removed: " + removalTime);
        Trace.out(Trace.Level.INFO,"Customer "    + id + " stayed: "  + (removalTime - arrivalTime));


        sum += (removalTime - arrivalTime);
        double mean = sum/id;
        System.out.println("Current mean of the customer service times " + mean + "\n");
    }

    public int getId(){
        return id;
    }

    public boolean getWalkIn() {
        return walkIn;
    }
}