package Task04;

import java.time.LocalDateTime;

/**
 * Demonstrates:
 * 1. Overloaded constructors and constructor chaining using this(...)
 * 2. Copy constructor for re-issuing duplicate tickets
 * 3. Comparison with default constructor + setters (partial state risk)
 * 4. Comparison with static factory methods
 */
public class ConstructorDemo {

    public static class ParkingTicket {
        private final String ticketId;
        private final String regNo;
        private final int floor;
        private final int slot;
        private final LocalDateTime entryTime;

        private static int counter = 1000;

        // Fully parameterized constructor (Master Constructor)
        public ParkingTicket(String ticketId, String regNo, int floor, int slot, LocalDateTime entryTime) {
            if (regNo == null || regNo.trim().isEmpty()) {
                throw new IllegalArgumentException("Registration number cannot be null or empty.");
            }
            if (floor < 0 || slot < 0) {
                throw new IllegalArgumentException("Floor and Slot must be non-negative.");
            }
            this.ticketId = (ticketId != null) ? ticketId : "TCK-" + (++counter);
            this.regNo = regNo;
            this.floor = floor;
            this.slot = slot;
            this.entryTime = (entryTime != null) ? entryTime : LocalDateTime.now();
        }

        // Overloaded constructor 1: Chains using this(...) with current timestamp
        public ParkingTicket(String regNo, int floor, int slot) {
            this(null, regNo, floor, slot, LocalDateTime.now());
        }

        // Overloaded constructor 2: Copy Constructor for re-issuing duplicate tickets
        public ParkingTicket(ParkingTicket original) {
            if (original == null) {
                throw new IllegalArgumentException("Original ticket cannot be null.");
            }
            // Preserve ticket properties but mark as duplicate copy
            this.ticketId = original.ticketId + "-DUP";
            this.regNo = original.regNo;
            this.floor = original.floor;
            this.slot = original.slot;
            this.entryTime = original.entryTime;
        }

        // Static Factory Method
        public static ParkingTicket createTicket(String regNo, int floor, int slot) {
            return new ParkingTicket(regNo, floor, slot);
        }

        public String getTicketId() { return ticketId; }
        public String getRegNo() { return regNo; }
        public int getFloor() { return floor; }
        public int getSlot() { return slot; }
        public LocalDateTime getEntryTime() { return entryTime; }

        @Override
        public String toString() {
            return String.format("Ticket[ID=%s, RegNo=%s, Location=F%d:S%d, Entry=%s]",
                    ticketId, regNo, floor, slot, entryTime.toLocalTime());
        }
    }

    public static void main(String[] args) {
        System.out.println("=== 1. Constructor Chaining (Overloaded Constructor) ===");
        ParkingTicket t1 = new ParkingTicket("TN01AA1111", 0, 1);
        System.out.println("Issued: " + t1);

        System.out.println("\n=== 2. Static Factory Method Creation ===");
        ParkingTicket t2 = ParkingTicket.createTicket("TN02BB2222", 1, 5);
        System.out.println("Issued via Factory: " + t2);

        System.out.println("\n=== 3. Copy Constructor (Duplicate Ticket Issuance) ===");
        ParkingTicket duplicateT1 = new ParkingTicket(t1);
        System.out.println("Original Ticket:  " + t1);
        System.out.println("Duplicate Ticket: " + duplicateT1);

        System.out.println("\n=== 4. Validation Enforcement ===");
        try {
            new ParkingTicket("", -1, 0);
        } catch (IllegalArgumentException ex) {
            System.out.println("Caught Expected Validation Error: " + ex.getMessage());
        }
    }
}
