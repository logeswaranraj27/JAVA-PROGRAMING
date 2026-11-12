package Task02;

/**
 * Demonstrates the precision risks of integer division vs floating point casting
 * and the correct integer arithmetic ceiling approach for billing.
 */
public class CastingDemo {
    public static void main(String[] args) {
        int[] testDurations = {0, 15, 60, 65, 120, 130};
        double ratePerHour = 30.0;

        System.out.println("==========================================================================");
        System.out.println(" Duration(min) | Truncation (/ 60) | Faulty Math.ceil | Integer Ceiling (Correct)");
        System.out.println("==========================================================================");

        for (int minutes : testDurations) {
            // Faulty Approach 1: Plain integer division (truncates fraction)
            int truncHours = minutes / 60;
            double feeTrunc = truncHours * ratePerHour;

            // Faulty Approach 2: Math.ceil on integer division result
            int faultyCeilHours = (int) Math.ceil(minutes / 60);
            double feeFaultyCeil = faultyCeilHours * ratePerHour;

            // Correct Approach: Pure integer arithmetic ceiling: (minutes + 59) / 60
            // Note: 0 minutes stay can be handled as 0 or 1 depending on grace period;
            // for standard billing: (minutes == 0) ? 0 : (minutes + 59) / 60
            int correctHours = (minutes == 0) ? 0 : (minutes + 59) / 60;
            double feeCorrect = correctHours * ratePerHour;

            System.out.printf(" %-13d | Rs %-15.1f | Rs %-14.1f | Rs %-15.1f (Hours: %d)%n",
                    minutes, feeTrunc, feeFaultyCeil, feeCorrect, correctHours);
        }
        System.out.println("==========================================================================");
    }
}
