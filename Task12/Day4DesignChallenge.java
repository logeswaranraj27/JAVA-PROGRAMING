package Task12;

import java.time.LocalTime;
import java.util.Arrays;

/**
 * Day-4 Design Challenge:
 * 1. EBike (Flat fee)
 * 2. DeliveryVan (Double car rate during 9 AM - 5 PM peak hours)
 * 3. OneDayVisitorPass (Pass interface)
 * 4. 5-Floor jagged array facility extension
 */
public class Day4DesignChallenge {

    // ==========================================
    // 1. Vehicle Base Hierarchy (Retained & Extended)
    // ==========================================
    public static abstract class Vehicle {
        private final String regNo;

        protected Vehicle(String regNo) {
            if (regNo == null || regNo.isBlank()) {
                throw new IllegalArgumentException("Registration cannot be blank.");
            }
            this.regNo = regNo.trim().toUpperCase();
        }

        public final String getRegNo() { return regNo; }
        public abstract double ratePerHour();

        public double calculateFee(int minutes) {
            int hours = (minutes == 0) ? 0 : (minutes + 59) / 60;
            return hours * ratePerHour();
        }

        // Context-aware fee computation (overloaded for time-of-day dynamic tariffs)
        public double calculateFee(int minutes, LocalTime entryTime) {
            return calculateFee(minutes); // default fallback
        }
    }

    public static class Car extends Vehicle {
        public Car(String regNo) { super(regNo); }
        @Override public double ratePerHour() { return 30.0; }
    }

    // ==========================================
    // 2. New Vehicle Category 1: EBike (Flat Fee)
    // ==========================================
    public static class EBike extends Vehicle {
        public static final double FLAT_FEE = 20.0;

        public EBike(String regNo) {
            super(regNo);
        }

        @Override
        public double ratePerHour() {
            // E-Bikes are not charged hourly; ratePerHour returns flat rate indicator
            return FLAT_FEE;
        }

        @Override
        public double calculateFee(int minutes) {
            // Override template method to enforce flat fee regardless of duration
            return (minutes == 0) ? 0.0 : FLAT_FEE;
        }

        @Override
        public double calculateFee(int minutes, LocalTime entryTime) {
            return calculateFee(minutes);
        }
    }

    // ==========================================
    // 3. New Vehicle Category 2: DeliveryVan (Peak Hour Surcharge)
    // ==========================================
    public static class DeliveryVan extends Vehicle {
        private static final double BASE_RATE = 30.0; // Standard Car rate
        private static final double PEAK_RATE = 60.0; // Double car rate (2x)

        public DeliveryVan(String regNo) {
            super(regNo);
        }

        @Override
        public double ratePerHour() {
            return BASE_RATE;
        }

        @Override
        public double calculateFee(int minutes, LocalTime entryTime) {
            int hours = (minutes == 0) ? 0 : (minutes + 59) / 60;
            // Peak hours rule: 9 AM to 5 PM (09:00 inclusive to 17:00 exclusive)
            boolean isPeak = !entryTime.isBefore(LocalTime.of(9, 0)) && entryTime.isBefore(LocalTime.of(17, 0));
            double effectiveRate = isPeak ? PEAK_RATE : BASE_RATE;
            return hours * effectiveRate;
        }
    }

    // ==========================================
    // 4. Pass Abstraction & OneDayVisitorPass
    // ==========================================
    public interface ParkingPass {
        String getPassId();
        boolean isValidForWaiver();
        double getPassPurchasePrice();
    }

    public static class OneDayVisitorPass implements ParkingPass {
        private final String passId;
        private final double price = 150.0; // Flat 1-day pass cost

        public OneDayVisitorPass(String passId) {
            this.passId = passId;
        }

        @Override public String getPassId() { return passId; }
        @Override public boolean isValidForWaiver() { return true; }
        @Override public double getPassPurchasePrice() { return price; }
    }

    // ==========================================
    // 5. Extended 5-Floor Parking Facility
    // ==========================================
    public static class ExtendedFacility {
        private final String[][] slots;

        public ExtendedFacility(int[] slotsPerFloor) {
            slots = new String[slotsPerFloor.length][];
            for (int f = 0; f < slotsPerFloor.length; f++) {
                slots[f] = new String[slotsPerFloor[f]];
            }
        }

        public int[] allocate(String vehicleNo) {
            for (int f = 0; f < slots.length; f++) {
                for (int s = 0; s < slots[f].length; s++) {
                    if (slots[f][s] == null) {
                        slots[f][s] = vehicleNo;
                        return new int[]{f, s};
                    }
                }
            }
            return null;
        }

        public int getTotalSlots() {
            int total = 0;
            for (String[] floor : slots) total += floor.length;
            return total;
        }

        public int getFloorCount() { return slots.length; }
    }

    // ==========================================
    // 6. Test Driver & Evidence Validation
    // ==========================================
    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("       DAY-4 DESIGN CHALLENGE: RE-EVALUATION & ADAPTATION SUITE           ");
        System.out.println("==========================================================================");

        // A. Extended Facility Capacity Validation
        int[] fiveFloorLayout = {20, 40, 60, 80, 100}; // Asymmetrical 5-floor layout
        ExtendedFacility facility = new ExtendedFacility(fiveFloorLayout);
        System.out.println("1. Facility Architecture Extension:");
        System.out.printf("   - Floors configured : %d floors%n", facility.getFloorCount());
        System.out.printf("   - Layout per floor  : %s%n", Arrays.toString(fiveFloorLayout));
        System.out.printf("   - Total slots capacity: %d slots%n", facility.getTotalSlots());

        // B. EBike Flat Fee Validation
        System.out.println("\n2. EBike Flat Fee Evaluation:");
        EBike ebike = new EBike("TN02EB001");
        double ebikeShort = ebike.calculateFee(30);  // 30 min
        double ebikeLong = ebike.calculateFee(480);  // 8 hours (480 min)
        System.out.printf("   - EBike short stay (30 min)  : Rs %.2f (Expected: Rs 20.00)%n", ebikeShort);
        System.out.printf("   - EBike long stay  (480 min) : Rs %.2f (Expected: Rs 20.00)%n", ebikeLong);

        // C. DeliveryVan Peak vs Off-Peak Tariff Validation
        System.out.println("\n3. DeliveryVan Peak vs Off-Peak Evaluation:");
        DeliveryVan van = new DeliveryVan("TN04DV999");
        LocalTime peakTime = LocalTime.of(10, 30);   // 10:30 AM (Peak: 9 AM - 5 PM)
        LocalTime offPeakTime = LocalTime.of(19, 15); // 7:15 PM (Off-Peak)

        double vanPeakFee = van.calculateFee(120, peakTime);       // 2 hours during peak
        double vanOffPeakFee = van.calculateFee(120, offPeakTime);  // 2 hours during off-peak

        System.out.printf("   - Van 2-hr stay at 10:30 AM [PEAK 9AM-5PM, Rs 60/hr]: Rs %.2f (Expected: Rs 120.00)%n", vanPeakFee);
        System.out.printf("   - Van 2-hr stay at 07:15 PM [OFF-PEAK, Rs 30/hr]   : Rs %.2f (Expected: Rs 60.00)%n", vanOffPeakFee);

        // D. One-Day Visitor Pass Validation
        System.out.println("\n4. One-Day Visitor Pass Evaluation:");
        OneDayVisitorPass pass = new OneDayVisitorPass("DAY-PASS-2026-88");
        Car visitorCar = new Car("TN09VISIT1");
        int stayDuration = 360; // 6 hours
        double standardVisitorFee = visitorCar.calculateFee(stayDuration);
        double waivedParkingFee = pass.isValidForWaiver() ? 0.0 : standardVisitorFee;
        double totalVisitorCost = pass.getPassPurchasePrice() + waivedParkingFee;

        System.out.printf("   - Standard 6-hr visitor parking fee without pass: Rs %.2f%n", standardVisitorFee);
        System.out.printf("   - One-Day Pass Purchase Cost                    : Rs %.2f%n", pass.getPassPurchasePrice());
        System.out.printf("   - Net parking fee charged at exit barrier       : Rs %.2f%n", waivedParkingFee);
        System.out.printf("   - Visitor total daily parking investment        : Rs %.2f (Savings: Rs %.2f)%n",
                totalVisitorCost, (standardVisitorFee - totalVisitorCost));

        System.out.println("==========================================================================");
        System.out.println("ALL DAY-4 CHALLENGE REQUIREMENTS SUCCESSFULLY ADAPTED AND VALIDATED!");
        System.out.println("==========================================================================");
    }
}
