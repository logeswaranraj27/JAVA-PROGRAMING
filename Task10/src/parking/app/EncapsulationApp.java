package parking.app;

import parking.facility.ParkingFacility;
import parking.model.Car;
import parking.model.ParkingTicket;

import java.time.LocalDateTime;

public class EncapsulationApp {
    public static void main(String[] args) {
        System.out.println("=== 1. Testing Encapsulated Package Structure ===");
        ParkingFacility facility = new ParkingFacility(new int[]{3, 2});
        Car car = new Car("TN10XY1234");
        int[] spot = facility.allocate(car.getRegNo());
        System.out.println("Allocated car " + car.getRegNo() + " at: [" + spot[0] + ", " + spot[1] + "]");

        ParkingTicket ticket = new ParkingTicket("TICK-501", car.getRegNo(), spot[0], spot[1], LocalDateTime.now().minusHours(2));
        System.out.println("Ticket created: ID=" + ticket.getTicketId() + " for " + ticket.getRegNo());

        System.out.println("\n=== 2. Validated Mutator Defense ===");
        try {
            // Attempting to record an exit time before entry time
            ticket.recordExit(LocalDateTime.now().minusHours(3), 60.0);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught Expected Invariant Violation: " + e.getMessage());
        }

        // Proper exit
        ticket.recordExit(LocalDateTime.now(), car.calculateFee(120));
        System.out.printf("Valid exit recorded! Fee: Rs %.2f%n", ticket.getFee());

        System.out.println("\n=== 3. Wrapper Classes and Autoboxing Pitfalls ===");
        // Autoboxing integer comparison pitfall
        Integer cached1 = 100;
        Integer cached2 = 100;
        System.out.println("Comparing cached Integers (100 == 100): " + (cached1 == cached2)); // true (-128 to 127)

        Integer nonCached1 = 200;
        Integer nonCached2 = 200;
        System.out.println("Comparing non-cached Integers (200 == 200 via ==): " + (nonCached1 == nonCached2)); // FALSE!
        System.out.println("Comparing non-cached Integers (200.equals(200)): " + nonCached1.equals(nonCached2)); // TRUE!

        // Autounboxing NullPointerException pitfall
        Integer nullSlotWrapper = null;
        try {
            int primitiveSlot = nullSlotWrapper; // Triggers NullPointerException!
        } catch (NullPointerException npe) {
            System.out.println("Caught Expected Autounboxing NullPointerException when unboxing null Integer!");
        }
    }
}
