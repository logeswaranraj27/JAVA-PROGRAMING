package Task11;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Complete, integrated Smart-Parking System implementation and test driver.
 */
public class CampusSmartParkingSystem {

    // ==========================================
    // 1. Interfaces & Domain Models
    // ==========================================
    public interface Chargeable {
        double chargingFee(int kWh);
    }

    public static abstract class Vehicle {
        private final String regNo;

        protected Vehicle(String regNo) {
            if (regNo == null || regNo.isBlank()) {
                throw new IllegalArgumentException("Registration number cannot be blank.");
            }
            this.regNo = regNo.trim().toUpperCase();
        }

        public final String getRegNo() { return regNo; }
        public abstract double ratePerHour();

        public double calculateFee(int minutes) {
            if (minutes < 0) {
                throw new IllegalArgumentException("Duration cannot be negative.");
            }
            int hours = (minutes == 0) ? 0 : (minutes + 59) / 60;
            return hours * ratePerHour();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Vehicle other)) return false;
            return this.regNo.equalsIgnoreCase(other.regNo);
        }

        @Override
        public int hashCode() {
            return regNo.hashCode();
        }

        @Override
        public String toString() {
            return String.format("%s[%s, Rate=Rs %.1f/hr]", getClass().getSimpleName(), regNo, ratePerHour());
        }
    }

    public static class TwoWheeler extends Vehicle {
        public TwoWheeler(String regNo) { super(regNo); }
        @Override public double ratePerHour() { return 10.0; }
    }

    public static class Car extends Vehicle {
        public Car(String regNo) { super(regNo); }
        @Override public double ratePerHour() { return 30.0; }
    }

    public static class ElectricCar extends Car implements Chargeable {
        public ElectricCar(String regNo) { super(regNo); }
        @Override
        public double chargingFee(int kWh) {
            if (kWh < 0) throw new IllegalArgumentException("kWh cannot be negative.");
            return kWh * 12.0;
        }
    }

    // ==========================================
    // 2. Parking Ticket & Passes
    // ==========================================
    public static class ParkingTicket {
        private static int counter = 5000;
        private final String ticketId;
        private final Vehicle vehicle;
        private final int floor;
        private final int slot;
        private final LocalDateTime entryTime;
        private final boolean hasMonthlyPass;
        private LocalDateTime exitTime;
        private double totalFee;

        public ParkingTicket(Vehicle vehicle, int floor, int slot, boolean hasMonthlyPass) {
            this.ticketId = "TICK-" + (++counter);
            this.vehicle = vehicle;
            this.floor = floor;
            this.slot = slot;
            this.hasMonthlyPass = hasMonthlyPass;
            this.entryTime = LocalDateTime.now();
        }

        public String getTicketId() { return ticketId; }
        public Vehicle getVehicle() { return vehicle; }
        public int getFloor() { return floor; }
        public int getSlot() { return slot; }
        public boolean isMonthlyPass() { return hasMonthlyPass; }
        public double getTotalFee() { return totalFee; }

        public void completeCheckout(int durationMinutes, int chargingKWh) {
            this.exitTime = entryTime.plusMinutes(durationMinutes);
            if (hasMonthlyPass) {
                // Rule: Monthly pass holders park free of charge!
                this.totalFee = 0.0;
            } else {
                this.totalFee = vehicle.calculateFee(durationMinutes);
            }

            if (vehicle instanceof Chargeable c && chargingKWh > 0) {
                this.totalFee += c.chargingFee(chargingKWh);
            }
        }
    }

    // ==========================================
    // 3. Parking Facility Management
    // ==========================================
    public static class ParkingFacility {
        private final String[][] slots;
        private final Map<String, ParkingTicket> activeTickets = new HashMap<>();
        private double totalDailyRevenue = 0.0;

        public ParkingFacility(int[] slotsPerFloor) {
            slots = new String[slotsPerFloor.length][];
            for (int f = 0; f < slotsPerFloor.length; f++) {
                slots[f] = new String[slotsPerFloor[f]];
            }
        }

        public ParkingTicket parkVehicle(Vehicle vehicle, boolean hasMonthlyPass) {
            String regNo = vehicle.getRegNo();
            if (activeTickets.containsKey(regNo)) {
                throw new IllegalStateException("Duplicate Check-in Denied: Vehicle " + regNo + " is already inside!");
            }

            // Linear search for first free slot
            for (int f = 0; f < slots.length; f++) {
                for (int s = 0; s < slots[f].length; s++) {
                    if (slots[f][s] == null) {
                        slots[f][s] = regNo;
                        ParkingTicket ticket = new ParkingTicket(vehicle, f, s, hasMonthlyPass);
                        activeTickets.put(regNo, ticket);
                        return ticket;
                    }
                }
            }
            return null; // Facility full
        }

        public double exitVehicle(String regNo, int durationMinutes, int chargingKWh) {
            ParkingTicket ticket = activeTickets.get(regNo);
            if (ticket == null) {
                throw new IllegalArgumentException("Unknown Vehicle Exit Attempt: " + regNo);
            }

            // Release slot
            slots[ticket.getFloor()][ticket.getSlot()] = null;
            activeTickets.remove(regNo);

            ticket.completeCheckout(durationMinutes, chargingKWh);
            totalDailyRevenue += ticket.getTotalFee();
            return ticket.getTotalFee();
        }

        public double getTotalDailyRevenue() { return totalDailyRevenue; }
        public int getActiveCount() { return activeTickets.size(); }
    }

    // ==========================================
    // 4. Test Suite Execution Driver
    // ==========================================
    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("        CAMPUS SMART-PARKING SYSTEM - COMPREHENSIVE INTEGRATION SUITE      ");
        System.out.println("==========================================================================");

        // Facility layout: Floor 0 has 2 slots, Floor 1 has 1 slot (Total capacity = 3)
        ParkingFacility facility = new ParkingFacility(new int[]{2, 1});

        // 1. Normal Operations
        System.out.println("\n--- TEST SUITE 1: NORMAL OPERATIONS ---");
        Vehicle bike = new TwoWheeler("TN01AA1001");
        Vehicle car = new Car("TN02BB2002");
        Vehicle ev = new ElectricCar("TN03CC3003");

        ParkingTicket t1 = facility.parkVehicle(bike, false);
        System.out.printf("Checked In: %s -> Ticket %s at [F%d:S%d]%n", bike.getRegNo(), t1.getTicketId(), t1.getFloor(), t1.getSlot());

        ParkingTicket t2 = facility.parkVehicle(car, false);
        System.out.printf("Checked In: %s -> Ticket %s at [F%d:S%d]%n", car.getRegNo(), t2.getTicketId(), t2.getFloor(), t2.getSlot());

        // 2. Boundary Cases
        System.out.println("\n--- TEST SUITE 2: BOUNDARY CASES ---");
        // Last free slot allocation
        ParkingTicket t3 = facility.parkVehicle(ev, false);
        System.out.printf("[Boundary: Last Free Slot] %s -> Ticket %s at [F%d:S%d]%n", ev.getRegNo(), t3.getTicketId(), t3.getFloor(), t3.getSlot());

        // Facility Full Case
        Vehicle visitorCar = new Car("TN04DD4004");
        ParkingTicket tFull = facility.parkVehicle(visitorCar, false);
        System.out.println("[Boundary: Facility Full Check] Expecting null: " + tFull);

        // Zero-Minute Parking Stay (Immediate turnaround)
        double zeroMinFee = bike.calculateFee(0);
        System.out.printf("[Boundary: Zero-Minute Parking] Duration: 0 min -> Fee: Rs %.1f%n", zeroMinFee);

        // Exact Fee Slab Boundary Check
        System.out.printf("[Boundary: Exact 60 Min] Duration: 60 min -> Fee: Rs %.1f (1 hr billable)%n", car.calculateFee(60));
        System.out.printf("[Boundary: 61 Min Slab Crossing] Duration: 61 min -> Fee: Rs %.1f (2 hr billable)%n", car.calculateFee(61));
        System.out.printf("[Boundary: Exact 120 Min] Duration: 120 min -> Fee: Rs %.1f (2 hr billable)%n", car.calculateFee(120));

        // 3. Invalid Cases & Defensive Rejection
        System.out.println("\n--- TEST SUITE 3: INVALID CASES & DEFENSIVE CHECKS ---");
        try {
            // Attempting to check-in already parked vehicle
            facility.parkVehicle(new Car("TN01AA1001"), false);
        } catch (IllegalStateException e) {
            System.out.println("[Handled] " + e.getMessage());
        }

        try {
            // Attempting checkout for unknown vehicle
            facility.exitVehicle("GHOST999", 30, 0);
        } catch (IllegalArgumentException e) {
            System.out.println("[Handled] " + e.getMessage());
        }

        // 4. Hand-Calculated Revenue Verification Scenario
        System.out.println("\n--- TEST SUITE 4: HAND-CALCULATED REVENUE VERIFICATION ---");
        // Vehicle 1: TwoWheeler (TN01AA1001), 45 min -> 1 hr * 10 = Rs 10.0
        double fee1 = facility.exitVehicle("TN01AA1001", 45, 0);
        System.out.printf("Departed TN01AA1001 (45 min): Billed Rs %.2f | Hand-Calculated: Rs 10.00%n", fee1);

        // Vehicle 2: Car (TN02BB2002), 120 min -> 2 hrs * 30 = Rs 60.0
        double fee2 = facility.exitVehicle("TN02BB2002", 120, 0);
        System.out.printf("Departed TN02BB2002 (120 min): Billed Rs %.2f | Hand-Calculated: Rs 60.00%n", fee2);

        // Vehicle 3: ElectricCar (TN03CC3003), 130 min + 15 kWh charging -> 3 hrs * 30 + 15 * 12 = 90 + 180 = Rs 270.0
        double fee3 = facility.exitVehicle("TN03CC3003", 130, 15);
        System.out.printf("Departed TN03CC3003 (130 min, 15 kWh): Billed Rs %.2f | Hand-Calculated: Rs 270.00%n", fee3);

        // Now slot is freed, let Student Car with Monthly Pass park!
        Vehicle studentCar = new Car("TN04DD4004");
        facility.parkVehicle(studentCar, true); // hasMonthlyPass = true
        // Vehicle 4: Student Car, 180 min with Monthly Pass -> Rs 0.0
        double fee4 = facility.exitVehicle("TN04DD4004", 180, 0);
        System.out.printf("Departed TN04DD4004 (Monthly Pass Holder, 180 min): Billed Rs %.2f | Hand-Calculated: Rs 0.00%n", fee4);

        double actualTotal = facility.getTotalDailyRevenue();
        double expectedTotal = 10.0 + 60.0 + 270.0 + 0.0; // Rs 340.0
        System.out.println("--------------------------------------------------------------------------");
        System.out.printf("TOTAL SYSTEM REVENUE: Rs %.2f | HAND-CALCULATED REVENUE: Rs %.2f%n", actualTotal, expectedTotal);
        System.out.println("VERIFICATION STATUS : " + (actualTotal == expectedTotal ? "PASSED (EXACT MATCH!)" : "FAILED"));
        System.out.println("==========================================================================");

        // 5. Benchmark Performance Comparison (Exit lookup: Array vs ArrayList vs HashMap)
        System.out.println("\n--- TEST SUITE 5: ALTERNATIVE CONCEPT EXIT-LOOKUP BENCHMARK ---");
        int testN = 10000;
        List<ParkingTicket> arrayListTickets = new ArrayList<>(testN);
        Map<String, ParkingTicket> mapTickets = new HashMap<>(testN);

        for (int i = 0; i < testN; i++) {
            String reg = "TEST" + i;
            ParkingTicket pt = new ParkingTicket(new Car(reg), 0, 0, false);
            arrayListTickets.add(pt);
            mapTickets.put(reg, pt);
        }

        String targetReg = "TEST" + (testN - 1); // Worst case (last element)

        // Measure ArrayList linear scan
        long startList = System.nanoTime();
        ParkingTicket foundInList = null;
        for (ParkingTicket pt : arrayListTickets) {
            if (pt.getVehicle().getRegNo().equals(targetReg)) {
                foundInList = pt;
                break;
            }
        }
        long listTimeNs = System.nanoTime() - startList;

        // Measure HashMap constant time lookup
        long startMap = System.nanoTime();
        ParkingTicket foundInMap = mapTickets.get(targetReg);
        long mapTimeNs = System.nanoTime() - startMap;

        System.out.printf("Search for ticket among %d parked vehicles:%n", testN);
        System.out.printf(" - ArrayList (Linear Scan O(N)) : %,d ns%n", listTimeNs);
        System.out.printf(" - HashMap   (Hash Lookup O(1)) : %,d ns (Speedup: %.1fx)%n", mapTimeNs, (double) listTimeNs / Math.max(1, mapTimeNs));
        System.out.println("==========================================================================");
    }
}
