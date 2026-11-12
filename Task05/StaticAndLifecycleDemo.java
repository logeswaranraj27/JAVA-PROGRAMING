package Task05;

/**
 * Demonstrates:
 * 1. Static vs Instance members (Ticket Counter, Occupancy)
 * 2. Multi-facility scenario: North Gate vs South Gate
 * 3. Static initialization block
 * 4. Pass-by-value of object references and garbage collection eligibility
 */
public class StaticAndLifecycleDemo {

    public static class GateFacility {
        private final String gateName;
        // Instance counter: counts tickets issued ONLY at this gate
        private int gateLocalCounter = 0;
        // Static counter: shared across ALL gates for globally unique serial numbers
        private static int globalTicketSequence = 0;
        private static int totalSystemOccupancy = 0;

        // Static block: runs once when class is loaded into JVM
        static {
            System.out.println("[STATIC BLOCK] Initializing Smart-Parking Global Subsystem...");
            totalSystemOccupancy = 0;
        }

        public GateFacility(String gateName) {
            this.gateName = gateName;
        }

        public String issueTicketWithInstanceCounter() {
            gateLocalCounter++;
            return gateName + "-TICKET-" + gateLocalCounter;
        }

        public String issueTicketWithGlobalCounter() {
            globalTicketSequence++;
            totalSystemOccupancy++;
            return String.format("UNIV-TICK-%04d (%s)", globalTicketSequence, gateName);
        }

        public void vehicleDeparted() {
            if (totalSystemOccupancy > 0) {
                totalSystemOccupancy--;
            }
        }

        public static int getTotalSystemOccupancy() {
            return totalSystemOccupancy;
        }
    }

    // Method to demonstrate Java's pass-by-value of references
    public static void modifyReference(GateFacility facility) {
        // Mutating the object changes the state on the heap
        facility.issueTicketWithGlobalCounter();
        // Reassigning the parameter reference does NOT affect the caller's variable
        facility = new GateFacility("TempGate");
    }

    public static void main(String[] args) {
        System.out.println("=== 1. Instantiating North Gate and South Gate Facilities ===");
        GateFacility northGate = new GateFacility("NorthGate");
        GateFacility southGate = new GateFacility("SouthGate");

        System.out.println("\n=== 2. Flaw of Instance Counter in Multi-Facility Campus ===");
        System.out.println("North: " + northGate.issueTicketWithInstanceCounter());
        System.out.println("South: " + southGate.issueTicketWithInstanceCounter());
        System.out.println("North: " + northGate.issueTicketWithInstanceCounter());
        System.out.println("South: " + southGate.issueTicketWithInstanceCounter());
        System.out.println(">> Notice ticket collision if prefix is stripped: both issued local ticket #1 and #2!");

        System.out.println("\n=== 3. Proper Static Global Counter Solution ===");
        System.out.println(northGate.issueTicketWithGlobalCounter());
        System.out.println(southGate.issueTicketWithGlobalCounter());
        System.out.println(northGate.issueTicketWithGlobalCounter());
        System.out.println("Current Total Campus Occupancy: " + GateFacility.getTotalSystemOccupancy());

        System.out.println("\n=== 4. Pass-By-Value Demonstration ===");
        modifyReference(northGate);
        System.out.println("After method call, Occupancy: " + GateFacility.getTotalSystemOccupancy());

        System.out.println("\n=== 5. Object Lifecycle & GC Eligibility ===");
        GateFacility temp = new GateFacility("EastOverflowGate");
        System.out.println("Created temporary facility: " + temp.issueTicketWithGlobalCounter());
        temp = null; // Object is now unreferenced and eligible for Garbage Collection
        System.gc();  // Suggest GC run
        System.out.println("Reference set to null. Object is unreachable from GC roots.");
    }
}
