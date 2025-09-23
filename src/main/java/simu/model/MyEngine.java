package simu.model;

import controller.IControllerForM;
import controller.IControllerForV;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;
import simu.framework.*;
import simu.model.kitchen.KitchenService;


public class MyEngine extends Engine {
    private ArrivalProcess arrivalProcessCall;
    private ArrivalProcess arrivalProcessRestaurant;
    private IControllerForM controller;
    private int totalCustomersProcessed = 0; // UUSI: Customer counter
    private double lastProgressUpdate = 0; // UUSI: Progress tracking

    public MyEngine(IControllerForM controller) {
        super((IControllerForV) controller);
        this.controller = controller;

        servicePoints = new ServicePoint[4];

        servicePoints[0] = new ReceptionService(
                new Normal(4, 1.5), eventList, EventType.DepartureFromReception);
        servicePoints[1] = new KitchenService(
                new Normal(18, 5), eventList, EventType.DepartureFromKitchen);
        servicePoints[2] = new CounterService(
                new Normal(5, 2), eventList, EventType.DepartureFromCounterToCostumer);
        servicePoints[3] = new DeliveryService(
                new Normal(20, 8), eventList, EventType.DepartureFromDelivery);

        arrivalProcessRestaurant = new ArrivalProcess(
                new Negexp(8, 5), eventList, EventType.ArrivalRestaurant);
        arrivalProcessCall = new ArrivalProcess(
                new Negexp(15, 5), eventList, EventType.ArrivalCall);
    }

    @Override
    protected void initialization() {
        arrivalProcessRestaurant.generateNext();
        arrivalProcessCall.generateNext();
        totalCustomersProcessed = 0;
        lastProgressUpdate = 0;
    }

    @Override
    protected void runEvent(Event t) {
        Customer a;

        switch ((EventType) t.getType()) {

            case ArrivalRestaurant:
                servicePoints[0].addQueue(new Customer(true));
                arrivalProcessRestaurant.generateNext();
                controller.visualiseCustomer();
                totalCustomersProcessed++;
                updateProgress();
                break;

            case ArrivalCall:
                servicePoints[0].addQueue(new Customer(false));
                arrivalProcessCall.generateNext();
                controller.visualiseCustomer();
                totalCustomersProcessed++;
                updateProgress();
                break;

            case ReturnMoney:
                a = servicePoints[0].removeQueue();
                a.setRemovalTime(Clock.getInstance().getTime());
                // UUSI: Record customer data for Results
                recordCustomerResults(a);
                a.reportResults();
                break;

            case PaymentFailed:
                a = servicePoints[0].removeQueue();
                servicePoints[0].addQueue(a);
                a.reportPaymentIssue();
                break;


            case DepartureFromReception:
                a = servicePoints[0].removeQueue();
                servicePoints[1].addQueue(a);
                break;

            case DepartureFromKitchen:
                a = servicePoints[1].removeQueue();
                servicePoints[2].addQueue(a);
                break;

            // Counter
            case DepartureFromCounterToDelivery:
                a = servicePoints[2].removeQueue();
                servicePoints[3].addQueue(a);
                break;

            case DepartureFromCounterToCostumer:
                a = servicePoints[2].removeQueue();
                a.setRemovalTime(Clock.getInstance().getTime());
                // UUSI: Record customer data for Results
                recordCustomerResults(a);
                a.reportResults();
                break;

            case CounterErrorToKitchen:
                a = servicePoints[2].removeQueue();
                servicePoints[1].addQueue(a);
                break;

            case CounterErrorToReception:
                a = servicePoints[2].removeQueue();
                servicePoints[0].addQueue(a);
                break;


            case DepartureFromDelivery:
                a = servicePoints[3].removeQueue();
                a.setRemovalTime(Clock.getInstance().getTime());
                // UUSI: Record customer data for Results
                recordCustomerResults(a);
                a.reportResults();
                break;
        }
    }

    // UUSI: Update progress periodically
    private void updateProgress() {
        double currentTime = Clock.getInstance().getTime();
        if (currentTime - lastProgressUpdate >= 50) { // Update every 50 time units
            if (controller instanceof controller.Controller) {
                ((controller.Controller) controller).updateSimulationProgress(
                        currentTime, getSimulationTime(), totalCustomersProcessed);
            }
            lastProgressUpdate = currentTime;
        }
    }

    private double getSimulationTime() {
        return 0;
    }

    // UUSI: Record customer results for statistics
    private void recordCustomerResults(Customer customer) {
        double totalTime = customer.getRemovalTime() - customer.getArrivalTime();
        // You might need to track more detailed timing in Customer class
        // For now, using simple calculations
        double waitingTime = totalTime * 0.3; // Estimate
        double serviceTime = totalTime * 0.7; // Estimate
        double responseTime = totalTime;

        Results.recordCustomer(waitingTime, serviceTime, responseTime);
    }

    @Override
    protected void results() {
        // UUSI: Set total simulation time in Results
        Results.setTotalSimulationTime(Clock.getInstance().getTime());

        // Record service point utilizations
        for (int i = 0; i < servicePoints.length; i++) {
            servicePoints[i].updateQueueStats();
            String[] names = {"Reception", "Kitchen", "Counter", "Delivery"};
            // Calculate busy time (you might need to enhance ServicePoint for this)
            double utilization = calculateUtilization(servicePoints[i]);
            Results.addBusyTime(names[i], utilization * Clock.getInstance().getTime());
        }

        Results.printResults();
        controller.showEndTime(Clock.getInstance().getTime());
    }

    // UUSI: Calculate utilization for service point
    private double calculateUtilization(ServicePoint sp) {
        // This is a simple estimation - you might want to enhance ServicePoint
        // to track actual busy time
        int totalServed = sp.getTotalCustomersServed();
        double avgServiceTime = 10.0; // Average service time estimate
        double totalTime = Clock.getInstance().getTime();

        if (totalTime == 0) return 0;
        return (totalServed * avgServiceTime) / totalTime;
    }
}