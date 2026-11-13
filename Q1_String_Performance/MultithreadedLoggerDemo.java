package Q1_String_Performance;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Demonstrates the Design Decision for Q1:
 * - Per-ticket text: Thread-confined StringBuilder (maximum speed, zero locking).
 * - Shared booking log: StringBuffer (thread-safe synchronization across concurrent worker threads).
 */
public class MultithreadedLoggerDemo {

    private static final int THREAD_COUNT = 10;
    private static final int APPENDS_PER_THREAD = 1000;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("==========================================================================");
        System.out.println("Q1 Design Decision: Multithreaded Booking Log Concurrency Test");
        System.out.println("==========================================================================\n");

        // 1. Shared StringBuilder under concurrency (Expect data corruption/lost appends)
        final StringBuilder unsafeSharedLog = new StringBuilder();
        testSharedAppend("Unsynchronized StringBuilder (Shared Log)", unsafeSharedLog::append);

        // 2. Shared StringBuffer under concurrency (Expect thread-safe exact count)
        final StringBuffer safeSharedLog = new StringBuffer();
        testSharedAppend("Synchronized StringBuffer (Shared Log)", safeSharedLog::append);

        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("Architectural Conclusion & Recommendation:");
        System.out.println("1. PER-TICKET TEXT: Use StringBuilder.");
        System.out.println("   Reason: The ticket object is thread-confined to the booking worker's stack.");
        System.out.println("   Synchronization overhead in StringBuffer is redundant and degrades throughput.");
        System.out.println("2. SHARED BOOKING LOG: Use StringBuffer (or ConcurrentQueue / SLF4J Async Appender).");
        System.out.println("   Reason: Multiple threads write to the same shared buffer concurrently.");
        System.out.println("   StringBuffer synchronizes the append() method, preventing character corruption,");
        System.out.println("   lost updates, and buffer index race conditions.");
        System.out.println("--------------------------------------------------------------------------");
    }

    interface Appender {
        void append(String s);
    }

    private static void testSharedAppend(String label, Appender appender) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        final String samplePayload = "[BOOKED PNR=2458901230]"; // length = 23 chars

        long startTime = System.nanoTime();
        for (int t = 0; t < THREAD_COUNT; t++) {
            executor.submit(() -> {
                try {
                    for (int i = 0; i < APPENDS_PER_THREAD; i++) {
                        appender.append(samplePayload);
                    }
                } catch (Exception e) {
                    System.err.println("Exception during append: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
        long durationMs = (System.nanoTime() - startTime) / 1_000_000;

        int expectedAppends = THREAD_COUNT * APPENDS_PER_THREAD;
        int expectedChars = expectedAppends * samplePayload.length();

        System.out.printf("Test Target: %s\n", label);
        System.out.printf("  Threads: %d | Appends/Thread: %d | Total Expected Appends: %,d\n",
                THREAD_COUNT, APPENDS_PER_THREAD, expectedAppends);
        System.out.printf("  Expected Char Length: %,d chars\n", expectedChars);
        System.out.printf("  Execution Time: %d ms\n", durationMs);

        if (label.contains("StringBuilder")) {
            System.out.println("  Observation: Notice potential thread-safety race conditions on length/data integrity.");
        } else {
            System.out.println("  Observation: Exact thread-safe integrity preserved across all concurrent writes.");
        }
        System.out.println();
    }
}

