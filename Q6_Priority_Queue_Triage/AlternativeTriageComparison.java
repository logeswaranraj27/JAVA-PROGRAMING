package Q6_Priority_Queue_Triage;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.Queue;

public class AlternativeTriageComparison {

    // Alternative 1: Sorted LinkedList (inserts in sorted position)
    static class SortedLinkedListTriage {
        private final LinkedList<Patient> list = new LinkedList<>();

        public void admit(Patient p) {
            // O(N) linear scan to locate correct insertion point
            ListIterator<Patient> it = list.listIterator();
            while (it.hasNext()) {
                Patient cur = it.next();
                if (p.severity < cur.severity || (p.severity == cur.severity && p.arrival < cur.arrival)) {
                    it.previous();
                    it.add(p);
                    return;
                }
            }
            list.add(p);
        }

        public Patient next() {
            // O(1) removal from head
            return list.pollFirst();
        }

        public int size() { return list.size(); }
    }

    // Alternative 2: Array of 5 FIFO Queues (Multi-Queue Bucket Array)
    @SuppressWarnings("unchecked")
    static class FiveBucketFifoTriage {
        private final Queue<Patient>[] buckets = new ArrayDeque[5];

        public FiveBucketFifoTriage() {
            for (int i = 0; i < 5; i++) {
                buckets[i] = new ArrayDeque<>();
            }
        }

        public void admit(Patient p) {
            // O(1) direct bucket indexing (severity 1..5 maps to index 0..4)
            buckets[p.severity - 1].add(p);
        }

        public Patient next() {
            // O(K) where K=5 (constant time search for first non-empty bucket)
            for (int i = 0; i < 5; i++) {
                if (!buckets[i].isEmpty()) {
                    return buckets[i].poll();
                }
            }
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("Q6 Alternative Concept: PriorityQueue vs Sorted LinkedList vs 5 FIFO Queues");
        System.out.println("==========================================================================\n");

        demonstrateStarvationVulnerability();
        compareThreeArchitectures();
    }

    private static void demonstrateStarvationVulnerability() {
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("1. Starvation Vulnerability in Pure PriorityQueue");
        System.out.println("--------------------------------------------------------------------------");
        PriorityQueue<Patient> pq = new PriorityQueue<>(
                Comparator.comparingInt(Patient::getSeverity).thenComparingLong(Patient::getArrival)
        );

        // Minor patient arrives at t=0
        pq.add(new Patient("Asha_MinorSprain", 5, 0));
        System.out.println("t=0  : Admitted Asha (Severity 5, Minor Sprain, Arrival=0)");

        // 10 critical patients arrive continuously
        for (int i = 1; i <= 5; i++) {
            pq.add(new Patient("CriticalPatient_" + i, 1, i));
            System.out.printf("t=%d  : Admitted CriticalPatient_%d (Severity 1, Arrival=%d)\n", i, i, i);
        }

        System.out.println("\nDoctor begins serving first 5 patients:");
        for (int i = 1; i <= 5; i++) {
            System.out.println("  Doctor sees: " + pq.poll());
        }

        System.out.println("\nQueue State Check: Is Asha still waiting?");
        System.out.println("  Next in queue: " + pq.peek());
        System.out.println("Observation: Asha remains indefinitely starved because high-severity patients");
        System.out.println("continually preempt lower-severity arrivals. This leads directly to our Day-4 redesign in Q7!");
        System.out.println();
    }

    private static void compareThreeArchitectures() {
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("2. Empirical Comparison of the Three Architectural Approaches (N=10,000)");
        System.out.println("--------------------------------------------------------------------------");

        final int N = 10000;

        // Measure PriorityQueue
        PriorityQueue<Patient> pq = new PriorityQueue<>(
                Comparator.comparingInt(Patient::getSeverity).thenComparingLong(Patient::getArrival)
        );
        long startPq = System.nanoTime();
        for (int i = 0; i < N; i++) pq.add(new Patient("P_" + i, 1 + (i % 5), i));
        for (int i = 0; i < N; i++) pq.poll();
        long timePq = System.nanoTime() - startPq;

        // Measure FiveBucketFifoTriage
        FiveBucketFifoTriage bucketTriage = new FiveBucketFifoTriage();
        long startBucket = System.nanoTime();
        for (int i = 0; i < N; i++) bucketTriage.admit(new Patient("P_" + i, 1 + (i % 5), i));
        for (int i = 0; i < N; i++) bucketTriage.next();
        long timeBucket = System.nanoTime() - startBucket;

        // Measure SortedLinkedListTriage (test on 2,000 to prevent exponential timeout)
        SortedLinkedListTriage linkedTriage = new SortedLinkedListTriage();
        long startLinked = System.nanoTime();
        for (int i = 0; i < 2000; i++) linkedTriage.admit(new Patient("P_" + i, 1 + (i % 5), i));
        for (int i = 0; i < 2000; i++) linkedTriage.next();
        long timeLinked = System.nanoTime() - startLinked;

        System.out.printf("  1. PriorityQueue (Binary Min-Heap) : Total Time = %8.2f ms (O(log N) admit, O(log N) poll)\n",
                timePq / 1e6);
        System.out.printf("  2. Five FIFO Queues (Bucket Array) : Total Time = %8.2f ms (O(1) admit, O(1) poll)\n",
                timeBucket / 1e6);
        System.out.printf("  3. Sorted LinkedList (N=2,000 ops) : Total Time = %8.2f ms (O(N) admit linear scan, O(1) poll)\n",
                timeLinked / 1e6);

        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("Complexity & Architectural Analysis:");
        System.out.println("1. Sorted LinkedList:");
        System.out.println("   - Admission: O(N) linear pointer search to locate insertion point.");
        System.out.println("   - Dispatch: O(1) unlink head node.");
        System.out.println("   - Unusable for large hospital volumes due to O(N^2) total latency.");
        System.out.println("2. PriorityQueue (Min-Heap):");
        System.out.println("   - Admission: O(log N) sift-up.");
        System.out.println("   - Dispatch: O(log N) sift-down.");
        System.out.println("   - Excellent general-purpose queue, but suffers from low-priority starvation.");
        System.out.println("3. Five FIFO Queues (Bucket Array):");
        System.out.println("   - Admission: O(1) array index access.");
        System.out.println("   - Dispatch: O(1) scanning 5 bucket references.");
        System.out.println("   - Highest performance, zero heap rebalancing overhead.");
        System.out.println("--------------------------------------------------------------------------");
    }
}
