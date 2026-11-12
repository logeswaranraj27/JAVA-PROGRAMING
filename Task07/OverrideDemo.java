package Task07;

import java.util.HashSet;
import java.util.Set;

/**
 * Demonstrates:
 * 1. Method overriding rules and @Override annotation
 * 2. Overriding equals(), hashCode(), and toString() for Vehicle equivalence
 * 3. Use of final for immutable identity (regNo)
 * 4. Deduplication behavior in a HashSet
 */
public class OverrideDemo {

    static abstract class Vehicle {
        private final String regNo; // final: immutable identity

        protected Vehicle(String regNo) {
            if (regNo == null || regNo.trim().isEmpty()) {
                throw new IllegalArgumentException("Registration number cannot be null or empty.");
            }
            this.regNo = regNo;
        }

        public final String getRegNo() { // final method: prevents subversion of identity
            return regNo;
        }

        protected abstract double ratePerHour();

        public double calculateFee(int minutes) {
            int hours = (minutes + 59) / 60;
            return hours * ratePerHour();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true; // Reflexivity
            if (!(o instanceof Vehicle other)) return false; // Null-safe type check
            return this.regNo.equalsIgnoreCase(other.regNo); // Equality based on regNo
        }

        @Override
        public int hashCode() {
            return regNo.toUpperCase().hashCode();
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "[RegNo=" + regNo + ", Rate=Rs " + ratePerHour() + "/hr]";
        }
    }

    static class TwoWheeler extends Vehicle {
        public TwoWheeler(String regNo) { super(regNo); }
        @Override protected double ratePerHour() { return 10.0; }
    }

    static class Car extends Vehicle {
        public Car(String regNo) { super(regNo); }
        @Override protected double ratePerHour() { return 30.0; }
    }

    public static void main(String[] args) {
        Vehicle v1 = new Car("TN09AZ9999");
        Vehicle v2 = new Car("tn09az9999"); // same regNo, different casing
        Vehicle v3 = new TwoWheeler("TN09AZ9999"); // same regNo, different subtype!
        Vehicle v4 = new Car("TN01AA0001");

        System.out.println("=== 1. Testing equals() and hashCode() Contract ===");
        System.out.println("v1.equals(v2) [Case-Insensitive Match]: " + v1.equals(v2));
        System.out.println("v1.equals(v3) [Cross-Subtype Match on RegNo]: " + v1.equals(v3));
        System.out.println("v1.equals(v4) [Different RegNo]: " + v1.equals(v4));
        System.out.println("v1.hashCode() == v2.hashCode(): " + (v1.hashCode() == v2.hashCode()));

        System.out.println("\n=== 2. Set Deduplication Based on Overridden Methods ===");
        Set<Vehicle> set = new HashSet<>();
        set.add(v1);
        set.add(v2); // Should be recognized as duplicate and rejected
        set.add(v4);
        System.out.println("Set size (Expected 2): " + set.size());
        for (Vehicle v : set) {
            System.out.println(" -> " + v);
        }
    }
}
