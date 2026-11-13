package Q2_Playlist_Collections;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Evaluates the Changed Condition in Q2:
 * "Up Next" Queue where songs are frequently added at both ends and removed from the front.
 * Compares ArrayList, LinkedList, and ArrayDeque.
 */
public class UpNextQueueEvaluation {

    private static final int QUEUE_OPERATIONS = 50000;

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("Q2 Changed Condition: 'Up Next' Queue Evaluation");
        System.out.println("Workload: " + QUEUE_OPERATIONS + " operations (addFront, addBack, pollFront)");
        System.out.println("Comparing: ArrayList vs LinkedList vs ArrayDeque");
        System.out.println("==========================================================================\n");

        // 1. ArrayList as a Deque
        long tArrayList = benchmarkArrayList(QUEUE_OPERATIONS);
        System.out.printf("  %-12s : Total Time = %10.2f ms | Avg per Op = %8.1f ns\n",
                "ArrayList", tArrayList / 1e6, (double) tArrayList / (3.0 * QUEUE_OPERATIONS));

        // 2. LinkedList as a Deque
        long tLinkedList = benchmarkLinkedList(QUEUE_OPERATIONS);
        System.out.printf("  %-12s : Total Time = %10.2f ms | Avg per Op = %8.1f ns\n",
                "LinkedList", tLinkedList / 1e6, (double) tLinkedList / (3.0 * QUEUE_OPERATIONS));

        // 3. ArrayDeque as a Deque
        long tArrayDeque = benchmarkArrayDeque(QUEUE_OPERATIONS);
        System.out.printf("  %-12s : Total Time = %10.2f ms | Avg per Op = %8.1f ns\n",
                "ArrayDeque", tArrayDeque / 1e6, (double) tArrayDeque / (3.0 * QUEUE_OPERATIONS));

        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("Engineering Decision & Evaluation:");
        System.out.println("1. ArrayList is INAPPROPRIATE for an 'Up Next' queue because adding or removing");
        System.out.println("   from the front requires O(N) array shifts via System.arraycopy(), leading to");
        System.out.println("   massive latency degradation as the queue grows.");
        System.out.println("2. LinkedList provides O(1) pointer updates at head/tail, but creates a separate");
        System.out.println("   Node object for every song (24-32 bytes overhead per element) causing GC pressure");
        System.out.println("   and cache misses during traversal.");
        System.out.println("3. ArrayDeque is the OPTIMAL choice: backed by a circular resizable array,");
        System.out.println("   head and tail pointers advance in O(1) time without shifting elements,");
        System.out.println("   with ZERO node allocation overhead and contiguous cache locality.");
        System.out.println("--------------------------------------------------------------------------");
    }

    private static long benchmarkArrayList(int ops) {
        List<Song> list = new ArrayList<>();
        long start = System.nanoTime();
        for (int i = 0; i < ops; i++) {
            Song s = new Song("S_" + i, "Queue Song", "Artist", 190);
            list.add(0, s);        // Add front (O(N) shift)
            list.add(s);           // Add end (O(1))
            if (!list.isEmpty()) {
                list.remove(0);    // Remove front (O(N) shift)
            }
        }
        return System.nanoTime() - start;
    }

    private static long benchmarkLinkedList(int ops) {
        Deque<Song> deque = new LinkedList<>();
        long start = System.nanoTime();
        for (int i = 0; i < ops; i++) {
            Song s = new Song("S_" + i, "Queue Song", "Artist", 190);
            deque.addFirst(s);     // O(1) node prepend
            deque.addLast(s);      // O(1) node append
            deque.pollFirst();     // O(1) node unlink
        }
        return System.nanoTime() - start;
    }

    private static long benchmarkArrayDeque(int ops) {
        Deque<Song> deque = new ArrayDeque<>();
        long start = System.nanoTime();
        for (int i = 0; i < ops; i++) {
            Song s = new Song("S_" + i, "Queue Song", "Artist", 190);
            deque.addFirst(s);     // O(1) circular head pointer decrement
            deque.addLast(s);      // O(1) circular tail pointer increment
            deque.pollFirst();     // O(1) circular head pointer advance
        }
        return System.nanoTime() - start;
    }
}
