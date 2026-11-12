package Task09;

/**
 * Demonstrates:
 * 1. Abstract class (Vehicle) vs Interfaces (Chargeable, Payable)
 * 2. Multiple behaviors through multiple interfaces
 * 3. Default and static methods in interfaces
 */
public class AbstractionDemo {

    public interface Chargeable {
        // Abstract method
        double chargingFee(int kWh);

        // Static utility method in interface
        static double getCleanEnergyCessRate() {
            return 0.05; // 5% cess
        }

        // Default method: provides reusable behavior without breaking existing implementors
        default double calculateTotalChargingCost(int kWh) {
            double baseCharging = chargingFee(kWh);
            return baseCharging + (baseCharging * getCleanEnergyCessRate());
        }
    }

    public interface Payable {
        double processPayment(double amount);

        default void printReceipt(String txId, double amount) {
            System.out.printf("[RECEIPT] TxID: %s | Paid: Rs %.2f%n", txId, amount);
        }
    }

    public static abstract class Vehicle {
        private final String regNo;

        protected Vehicle(String regNo) {
            this.regNo = regNo;
        }

        public final String getRegNo() { return regNo; }
        protected abstract double ratePerHour();

        public double calculateFee(int minutes) {
            int hours = (minutes + 59) / 60;
            return hours * ratePerHour();
        }
    }

    // ElectricCar gains multiple behaviors: is-a Vehicle, is Chargeable, is Payable
    public static class ElectricCar extends Vehicle implements Chargeable, Payable {
        public ElectricCar(String regNo) {
            super(regNo);
        }

        @Override
        protected double ratePerHour() {
            return 30.0;
        }

        @Override
        public double chargingFee(int kWh) {
            return kWh * 12.0;
        }

        @Override
        public double processPayment(double amount) {
            System.out.printf("Processing secure campus cashless payment for %s: Rs %.2f%n", getRegNo(), amount);
            return amount;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== 1. Abstract Class & Interface Integration ===");
        ElectricCar ev = new ElectricCar("TN05EV9000");

        double parkingFee = ev.calculateFee(125); // 3 hours * 30 = 90.0
        double totalCharging = ev.calculateTotalChargingCost(20); // 20 * 12 = 240 + 5% = 252.0
        double totalDue = parkingFee + totalCharging;

        System.out.printf("Vehicle: %s%n", ev.getRegNo());
        System.out.printf("Base Parking Fee (125 min): Rs %.2f%n", parkingFee);
        System.out.printf("EV Charging (20 kWh + 5%% Clean Energy Cess): Rs %.2f%n", totalCharging);
        System.out.printf("Total Payable Amount: Rs %.2f%n", totalDue);

        System.out.println("\n=== 2. Multiple Interface Behaviors ===");
        ev.processPayment(totalDue);
        ev.printReceipt("TXN-77881", totalDue);
    }
}
