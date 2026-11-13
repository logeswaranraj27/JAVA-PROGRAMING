package Q4_Pharmacy_Inventory_Maps;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Scalability Inquiry for Q4:
 * Demonstrating why java.util.Hashtable is obsolete and not preferred in modern Java code.
 */
public class HashtableVsConcurrentComparison {

    private static final int THREADS = 8;
    private static final int OPERATIONS_PER_THREAD = 50000;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("==========================================================================");
        System.out.println("Q4 Scalability Inquiry: Why Hashtable Is Obsolete in Modern Java");
        System.out.println("Concurrent throughput test: " + THREADS + " threads x " + OPERATIONS_PER_THREAD + " lookups");
        System.out.println("==========================================================================\n");

        // 1. Legacy Hashtable
        Hashtable<String, String> legacyTable = new Hashtable<>();
        for (int i = 0; i < 5000; i++) legacyTable.put("MED_" + i, "Val_" + i);
        long timeHashtable = runConcurrentLookups("Hashtable (Legacy coarse lock)", legacyTable);

        // 2. Modern ConcurrentHashMap
        ConcurrentHashMap<String, String> concurrentMap = new ConcurrentHashMap<>();
        for (int i = 0; i < 5000; i++) concurrentMap.put("MED_" + i, "Val_" + i);
        long timeConcurrent = runConcurrentLookups("ConcurrentHashMap (Modern lock-striping / CAS)", concurrentMap);

        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("Engineering Analysis: Why Hashtable is Obsolete:");
        System.out.println("1. Monolithic Synchronization: Every method (get, put, remove) is synchronized");
        System.out.println("   on the single instance monitor ('this'). All threads queue up serially.");
        System.out.println("2. Non-Null Strictness: Throws NullPointerException on null keys or null values,");
        System.out.println("   unlike HashMap which handles a null key cleanly in bucket 0.");
        System.out.println("3. Legacy API: Relies on java.util.Enumeration rather than modern Iterable/Stream API.");
        System.out.println("4. In modern Java, standard practice is:");
        System.out.println("   - Single-threaded or local: HashMap / LinkedHashMap (zero lock overhead)");
        System.out.println("   - High concurrency: ConcurrentHashMap (lock-free reads, bucket-level sync)");
        System.out.println("--------------------------------------------------------------------------");
    }

    private static long runConcurrentLookups(String label, Map<String, String> map) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CountDownLatch latch = new CountDownLatch(THREADS);

        long start = System.nanoTime();
        for (int t = 0; t < THREADS; t++) {
            executor.submit(() -> {
                try {
                    for (int i = 0; i < OPERATIONS_PER_THREAD; i++) {
                        String v = map.get("MED_" + (i % 5000));
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
        long durationMs = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("  %-45s : Total Time = %6d ms\n", label, durationMs);
        return durationMs;
    }
}

