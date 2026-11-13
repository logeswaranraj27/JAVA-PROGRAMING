package Q4_Pharmacy_Inventory_Maps;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

public class MapBenchmark {

    private static final int[] INVENTORY_SIZES = {1000, 10000, 100000};
    private static final int LOOKUP_COUNT = 100000;
    private static final int WARMUP_RUNS = 3;

    public static void main(String[] args) throws Exception {
        System.out.println("==========================================================================");
        System.out.println("Q4: Hospital Pharmacy Inventory Map Selection Benchmark");
        System.out.println("Comparing: HashMap vs TreeMap vs LinkedHashMap");
        System.out.println("Sizes: 1,000 | 10,000 | 100,000 medicines | Lookups: 100,000");
        System.out.println("==========================================================================\n");

        List<BenchmarkRecord> records = new ArrayList<>();

        for (int size : INVENTORY_SIZES) {
            System.out.printf(">>> Testing Inventory Size N = %,d medicines...\n", size);
            runBenchmarkForSize(size, records);
            System.out.println();
        }

        // Export to CSV
        String csvPath = "Experimental_Data/Q4_map_benchmarks.csv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(csvPath))) {
            pw.println("MapType,InventorySize,100kLookupsTimeMs,AvgLookupNs,CodeReportTimeMs,CodeOrderValid,InsertionReportTimeMs,InsertionOrderValid");
            for (BenchmarkRecord r : records) {
                pw.printf("%s,%d,%.4f,%.2f,%.4f,%b,%.4f,%b\n",
                        r.mapType, r.size, r.lookupTimeMs, r.avgLookupNs,
                        r.codeReportMs, r.codeOrderValid, r.insertionReportMs, r.insertionOrderValid);
            }
        }
        System.out.println("[SUCCESS] Raw experimental data saved to " + csvPath);

        printSummaryTable(records);
    }

    private static void runBenchmarkForSize(int size, List<BenchmarkRecord> records) {
        // Prepare randomized dataset with unique codes and known addition sequence
        List<Medicine> dataset = new ArrayList<>(size);
        Random rng = new Random(12345);
        // Generate unique keys with pseudo-random code ordering
        List<Integer> codeIds = new ArrayList<>(size);
        for (int i = 0; i < size; i++) codeIds.add(i);
        java.util.Collections.shuffle(codeIds, rng);

        for (int i = 0; i < size; i++) {
            // Unique code, but randomized insertion order
            String code = String.format("MED-%07d", codeIds.get(i));
            Medicine m = new Medicine(code, "MedName_" + i, "B_" + (i % 200), 50 + (i % 500), 12.5 + (i % 50), i);
            dataset.add(m);
        }

        // Prepare 100,000 lookup keys (mix of existing and some non-existing)
        String[] lookupKeys = new String[LOOKUP_COUNT];
        for (int i = 0; i < LOOKUP_COUNT; i++) {
            lookupKeys[i] = dataset.get(rng.nextInt(size)).getCode();
        }

        String[] mapTypes = {"HashMap", "TreeMap", "LinkedHashMap"};

        for (String type : mapTypes) {
            PharmacyInventory inv;
            switch (type) {
                case "HashMap":
                    inv = new PharmacyInventory(new HashMap<>(), "HashMap");
                    break;
                case "TreeMap":
                    inv = new PharmacyInventory(new TreeMap<>(), "TreeMap");
                    break;
                case "LinkedHashMap":
                    inv = new PharmacyInventory(new LinkedHashMap<>(), "LinkedHashMap");
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            // Populate
            for (Medicine m : dataset) {
                inv.addMedicine(m);
            }

            // Warmup lookups
            for (int w = 0; w < 10000; w++) {
                inv.lookup(lookupKeys[w]);
            }

            // 1. Measure 100,000 lookups
            long startLookup = System.nanoTime();
            for (int i = 0; i < LOOKUP_COUNT; i++) {
                Medicine m = inv.lookup(lookupKeys[i]);
            }
            long endLookup = System.nanoTime();
            double lookupTimeMs = (endLookup - startLookup) / 1_000_000.0;
            double avgLookupNs = (double) (endLookup - startLookup) / LOOKUP_COUNT;

            // 2. Measure Code Order Report
            long startCodeReport = System.nanoTime();
            List<Medicine> codeReport = inv.generateCodeOrderReport();
            long endCodeReport = System.nanoTime();
            double codeReportMs = (endCodeReport - startCodeReport) / 1_000_000.0;
            boolean codeValid = PharmacyInventory.verifyCodeOrder(codeReport);

            // 3. Measure Insertion Order Report
            long startInsReport = System.nanoTime();
            List<Medicine> insReport = inv.generateInsertionOrderReport();
            long endInsReport = System.nanoTime();
            double insReportMs = (endInsReport - startInsReport) / 1_000_000.0;
            boolean insValid = PharmacyInventory.verifyInsertionOrder(insReport);

            records.add(new BenchmarkRecord(type, size, lookupTimeMs, avgLookupNs,
                    codeReportMs, codeValid, insReportMs, insValid));

            System.out.printf("  %-14s : 100k Lookups = %8.2f ms (%6.1f ns/op) | Code Report = %7.3f ms (Valid: %5b) | Insertion Report = %7.3f ms (Valid: %5b)\n",
                    type, lookupTimeMs, avgLookupNs, codeReportMs, codeValid, insReportMs, insValid);
        }
    }

    private static void printSummaryTable(List<BenchmarkRecord> records) {
        System.out.println("\n" + "=".repeat(120));
        System.out.printf("%-14s | %8s | %18s | %14s | %16s | %14s | %16s | %14s\n",
                "Map Type", "Size", "100k Lookups (ms)", "Lookup (ns/op)", "Code Report (ms)", "Code Valid?", "Ins. Report (ms)", "Ins. Valid?");
        System.out.println("-".repeat(120));
        for (BenchmarkRecord r : records) {
            System.out.printf("%-14s | %8d | %18.3f | %14.1f | %16.3f | %14b | %16.3f | %14b\n",
                    r.mapType, r.size, r.lookupTimeMs, r.avgLookupNs, r.codeReportMs, r.codeOrderValid, r.insertionReportMs, r.insertionOrderValid);
        }
        System.out.println("=".repeat(120));
    }

    static class BenchmarkRecord {
        String mapType;
        int size;
        double lookupTimeMs;
        double avgLookupNs;
        double codeReportMs;
        boolean codeOrderValid;
        double insertionReportMs;
        boolean insertionOrderValid;

        BenchmarkRecord(String mapType, int size, double lookupTimeMs, double avgLookupNs,
                        double codeReportMs, boolean codeOrderValid, double insertionReportMs, boolean insertionOrderValid) {
            this.mapType = mapType;
            this.size = size;
            this.lookupTimeMs = lookupTimeMs;
            this.avgLookupNs = avgLookupNs;
            this.codeReportMs = codeReportMs;
            this.codeOrderValid = codeOrderValid;
            this.insertionReportMs = insertionReportMs;
            this.insertionOrderValid = insertionOrderValid;
        }
    }
}
