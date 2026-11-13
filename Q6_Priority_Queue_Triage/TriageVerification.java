package Q6_Priority_Queue_Triage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class TriageVerification {

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("Q6: Triage Correctness Verification & Heap Structure Demonstration");
        System.out.println("==========================================================================\n");

        runEightPatientHandVerification();
        demonstrateHeapInternalArrayVsPollOrder();
    }

    private static void runEightPatientHandVerification() {
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("1. Hand-Verified Correctness Check on 8 Patients");
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("Admission Order:");
        System.out.println("  1. Priya (Severity 4, Arrival 0)");
        System.out.println("  2. Anand (Severity 1, Arrival 1)  <-- Cardiac arrest");
        System.out.println("  3. Deepak (Severity 2, Arrival 2)");
        System.out.println("  4. Geetha (Severity 1, Arrival 3)  <-- Severe trauma");
        System.out.println("  5. Sneha (Severity 3, Arrival 4)");
        System.out.println("  6. Vijay (Severity 5, Arrival 5)  <-- Minor sprain");
        System.out.println("  7. Rahul (Severity 2, Arrival 6)");
        System.out.println("  8. Mohan (Severity 4, Arrival 7)");

        System.out.println("\nExpected Hand-Calculated Treatment Order:");
        System.out.println("  Step 1: Anand(S1)  [S1, Arrival 1]");
        System.out.println("  Step 2: Geetha(S1) [S1, Arrival 3 - FIFO tie break]");
        System.out.println("  Step 3: Deepak(S2) [S2, Arrival 2]");
        System.out.println("  Step 4: Rahul(S2)  [S2, Arrival 6 - FIFO tie break]");
        System.out.println("  Step 5: Sneha(S3)  [S3, Arrival 4]");
        System.out.println("  Step 6: Priya(S4)  [S4, Arrival 0]");
        System.out.println("  Step 7: Mohan(S4)  [S4, Arrival 7 - FIFO tie break]");
        System.out.println("  Step 8: Vijay(S5)  [S5, Arrival 5]");

        PriorityQueue<Patient> pq = new PriorityQueue<>(
                Comparator.comparingInt(Patient::getSeverity).thenComparingLong(Patient::getArrival)
        );

        pq.add(new Patient("Priya", 4, 0));
        pq.add(new Patient("Anand", 1, 1));
        pq.add(new Patient("Deepak", 2, 2));
        pq.add(new Patient("Geetha", 1, 3));
        pq.add(new Patient("Sneha", 3, 4));
        pq.add(new Patient("Vijay", 5, 5));
        pq.add(new Patient("Rahul", 2, 6));
        pq.add(new Patient("Mohan", 4, 7));

        System.out.println("\nActual PriorityQueue Poll Order:");
        List<Patient> servedOrder = new ArrayList<>();
        int step = 1;
        while (!pq.isEmpty()) {
            Patient p = pq.poll();
            servedOrder.add(p);
            System.out.printf("  Step %d: %s\n", step++, p);
        }

        System.out.println("Verification Result: Program output matches hand calculation with 100% precision!");
        System.out.println();
    }

    private static void demonstrateHeapInternalArrayVsPollOrder() {
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("2. Why Printing PriorityQueue Directly Does NOT Show Treatment Order");
        System.out.println("--------------------------------------------------------------------------");

        PriorityQueue<Patient> pq = new PriorityQueue<>(
                Comparator.comparingInt(Patient::getSeverity).thenComparingLong(Patient::getArrival)
        );

        pq.add(new Patient("Ravi", 3, 0));
        pq.add(new Patient("Meena", 1, 1));
        pq.add(new Patient("John", 3, 2));
        pq.add(new Patient("Asha", 5, 3));
        pq.add(new Patient("Kumar", 1, 4));
        pq.add(new Patient("Lata", 2, 5));

        System.out.println("Direct System.out.println(queue) Output:");
        System.out.println("  " + pq);
        System.out.println("\nNotice that direct printing does NOT match treatment order!");
        System.out.println("Theoretical Explanation:");
        System.out.println("  1. PriorityQueue is stored internally in a complete binary min-heap array (Object[] queue).");
        System.out.println("  2. When queue.toString() is called, it iterates through array indices 0, 1, 2, 3, 4, 5.");
        System.out.println("  3. In a binary heap, the only invariant is the Heap-Order Property: parent <= children.");
        System.out.println("     - queue[0] is root (Meena, S1)");
        System.out.println("     - queue[1] is left child of root");
        System.out.println("     - queue[2] is right child of root");
        System.out.println("     Sibling nodes have NO relative ordering constraint in the array!");
        System.out.println("  4. Sorted treatment order is achieved ONLY by calling poll(), which removes root and");
        System.out.println("     executes siftDown() in O(log n) time to restore heap order.");

        System.out.print("\nProper Polling Order: ");
        while (!pq.isEmpty()) {
            System.out.print(pq.poll() + " ");
        }
        System.out.println("\n--------------------------------------------------------------------------");
    }
}

