package simu.model;

import simu.framework.IEventType;

// Event types are defined by the requirements of the simulation model
public enum EventType implements IEventType {

    // Arrivals
    ArrivalRestaurant,   // Customer enters from the restaurant
    ArrivalCall,         // Customer calls on the phone

    // Reception
    DepartureFromReception, // After finishing payment or registration
    PaymentFailed,          // Payment failed
    ReturnMoney,            // Refund

    // Kitchen
    DepartureFromKitchen,   // Meal finished from the kitchen
    KitchenError,           // Wrong order from the kitchen

    // Counter
    DepartureFromCounter,   // Customer finished at the counter
    CounterError,           // Receipt error or complaint

    // Delivery
    DepartureFromDelivery,  // Successful delivery
    DeliveryError,          // Delivery problem

    // End
    CustomerLeavesHappy     // Customer leaves happy
}
