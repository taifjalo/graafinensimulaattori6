package simu.model;

import controller.IControllerForV;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;
import simu.framework.*;

public class MyEngine extends Engine {
    private ArrivalProcess arrivalProcessCall;
    private ArrivalProcess arrivalProcessRestaurant;
    private IControllerForV controller;

    public MyEngine(IControllerForV controller) {
        super(controller);
        this.controller = controller;

        // 4 service points
        servicePoints = new ServicePoint[4];

        servicePoints[0] = new ReceptionService(
                new Normal(4, 1.5), eventList, EventType.DepartureFromReception);
        servicePoints[1] = new KitchenService(
                new Normal(15, 5), eventList, EventType.DepartureFromKitchen);
        servicePoints[2] = new CounterService(
                new Normal(5, 2), eventList, EventType.DepartureFromCounter);
        servicePoints[3] = new DeliveryService(
                new Normal(20, 8), eventList, EventType.DepartureFromDelivery);

        // Customer arrivals (restaurant + call)
        arrivalProcessRestaurant = new ArrivalProcess(
                new Negexp(8, 5), eventList, EventType.ArrivalRestaurant);
        arrivalProcessCall = new ArrivalProcess(
                new Negexp(15, 5), eventList, EventType.ArrivalCall);
    }

    @Override
    protected void initialization() {
        arrivalProcessRestaurant.generateNext();
        arrivalProcessCall.generateNext();
    }

    @Override
    protected void runEvent(Event t) {
        Customer a;

        switch ((EventType) t.getType()) {

            case ArrivalRestaurant:
                servicePoints[0].addQueue(new Customer(true));
                arrivalProcessRestaurant.generateNext();
                controller.visualiseCustomer();
                break;

            case ArrivalCall:
                servicePoints[0].addQueue(new Customer(false));
                arrivalProcessCall.generateNext();
                controller.visualiseCustomer();
                break;

            case ReturnMoney:
                // Final exit (refund or successful end)
                a = servicePoints[0].removeQueue();
                a.setRemovalTime(Clock.getInstance().getTime());
                a.reportResults();
                break;

            case PaymentFailed:
                // Retry reception
                a = servicePoints[0].removeQueue();
                servicePoints[0].addQueue(a);
                a.reportPaymentIssue();
                break;

            case DepartureFromReception:
                // Reception decided → goes to kitchen
                a = servicePoints[0].removeQueue();
                servicePoints[1].addQueue(a);
                break;

            case DepartureFromKitchen:
                // Kitchen decided → goes to counter or refund
                a = servicePoints[1].removeQueue();
                servicePoints[2].addQueue(a);
                break;

            case DepartureFromCounter:
                // Counter decided → goes to delivery or exit
                a = servicePoints[2].removeQueue();
                servicePoints[3].addQueue(a);
                break;

            case DepartureFromDelivery:
                // Delivery decided → exit or back to kitchen
                a = servicePoints[3].removeQueue();
                a.setRemovalTime(Clock.getInstance().getTime());
                a.reportResults();
                break;
        }
    }

    @Override
    protected void results() {
        for (ServicePoint sp : servicePoints) {
            sp.updateQueueStats();
        }
        Trace.out(Trace.Level.INFO, "Total reception served: " + servicePoints[0].getTotalCustomersServed());
        Trace.out(Trace.Level.INFO, "Average reception queue length: " + servicePoints[0].getAverageQueueLength());
        controller.slowDown(Clock.getInstance().getTime());
    }
}
