package Task06;

/**
 * Traces constructor execution order in multilevel inheritance:
 * Object -> Vehicle -> Car -> ElectricCar
 */
public class InheritanceTraceDemo {

    static abstract class Vehicle {
        private final String regNo;

        protected Vehicle(String regNo) {
            System.out.println("[TRACE 2] Vehicle constructor executed for: " + regNo);
            this.regNo = regNo;
        }

        public String getRegNo() { return regNo; }
        protected abstract double ratePerHour();
    }

    static class Car extends Vehicle {
        public Car(String regNo) {
            super(regNo); // Explicit call to Vehicle constructor
            System.out.println("[TRACE 3] Car constructor executed for: " + regNo);
        }

        @Override
        protected double ratePerHour() {
            return 30.0;
        }
    }

    interface Chargeable {
        double chargingFee(int kWh);
    }

    static class ElectricCar extends Car implements Chargeable {
        private final double batteryCapacityKWh;

        public ElectricCar(String regNo, double batteryCapacityKWh) {
            super(regNo); // Explicit call to Car constructor
            System.out.println("[TRACE 4] ElectricCar constructor executed. Battery: " + batteryCapacityKWh + " kWh");
            this.batteryCapacityKWh = batteryCapacityKWh;
        }

        @Override
        public double chargingFee(int kWh) {
            return kWh * 12.0;
        }
    }

    public static void main(String[] args) {
        System.out.println("Initiating creation of ElectricCar(\"TN03C3\", 45.0):");
        System.out.println("[TRACE 1] java.lang.Object constructor (implicit root)");
        ElectricCar ev = new ElectricCar("TN03C3", 45.0);
        System.out.println("Object successfully instantiated: " + ev.getRegNo() + " with hourly rate Rs " + ev.ratePerHour());
    }
}
