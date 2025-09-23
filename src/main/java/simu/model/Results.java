package simu.model;

import java.util.HashMap;
import java.util.Map;

public class Results {

    private static long totalCustomers = 0;
    private static double totalWaitingTime = 0;
    private static double totalServiceTime = 0;
    private static double totalResponseTime = 0;

    private static Map<String, Double> busyTimes = new HashMap<>();
    private static double totalSimulationTime = 0;

    public static void recordCustomer(double waitingTime, double serviceTime, double responseTime) {
        totalCustomers++;
        totalWaitingTime += waitingTime;
        totalServiceTime += serviceTime;
        totalResponseTime += responseTime;
    }

    public static void setTotalSimulationTime(double simTime) {
        totalSimulationTime = simTime;
    }

    public static void addBusyTime(String servicePointName, double busyTime) {
        busyTimes.put(servicePointName,
                busyTimes.getOrDefault(servicePointName, 0.0) + busyTime);
    }

    // --- Getters ---
    public static double getAverageWaitingTime() {
        return totalCustomers == 0 ? 0 : totalWaitingTime / totalCustomers;
    }

    public static double getAverageServiceTime() {
        return totalCustomers == 0 ? 0 : totalServiceTime / totalCustomers;
    }

    public static double getAverageResponseTime() {
        return totalCustomers == 0 ? 0 : totalResponseTime / totalCustomers;
    }

    public static double getUtilization(String servicePointName) {
        if (totalSimulationTime == 0) return 0;
        return busyTimes.getOrDefault(servicePointName, 0.0) / totalSimulationTime;
    }

    // UUSI: Getter for total customers
    public static long getTotalCustomers() {
        return totalCustomers;
    }

    public static void printResults() {
        System.out.println("=== Simulation Results ===");
        System.out.println("Total customers: " + totalCustomers);
        System.out.println("Average waiting time: " + getAverageWaitingTime());
        System.out.println("Average service time: " + getAverageServiceTime());
        System.out.println("Average response time: " + getAverageResponseTime());

        for (String sp : busyTimes.keySet()) {
            System.out.println("Utilization of " + sp + ": " + getUtilization(sp));
        }
    }

    public static void reset() {
        totalCustomers = 0;
        totalWaitingTime = 0;
        totalServiceTime = 0;
        totalResponseTime = 0;
        busyTimes.clear();
        totalSimulationTime = 0;
    }
}