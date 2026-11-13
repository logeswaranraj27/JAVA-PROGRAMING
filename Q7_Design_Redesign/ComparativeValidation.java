package Q7_Design_Redesign;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ComparativeValidation {

    private static final int[] TEST_SIZES = {1000, 5000, 10000, 25000};
    private static final int AGING_INTERVAL = 500; // Trigger aging every 500 operations

    public static void main(String[] args) throws Exception {
        System.out.println("==========================================================================");
        System.out.println("Q7: Day-4 Design Challenge - Controlled Empirical Validation");
        System.out.println("Comparing: Original PriorityQueue Baseline vs Redesigned AF-MLBQ");
        System.out.println("Test Sizes: 1,000 | 5,000 | 10,000 | 25,000 operations");
        System.out.println("==========================================================================\n");

        List<ValidationRow> rows = new ArrayList<>();

        for (int size : TEST_SIZES) {
            System.out.printf(">>> Running Comparative Benchmark for N = %,d operations...\n", size);
            runComparison(size, rows);
            System.out.println();
        }

        // Export to CSV
        String csvPath = "Experimental_Data/Q7_comparative_validation.csv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(csvPath))) {
            pw.println("Architecture,WorkloadSize,TotalTimeMs,AvgAdmitNs,AvgDispatchNs,TotalAgingTimeMs,MaxWaitS5Units");
            for (ValidationRow r : rows) {
                pw.printf("%s,%d,%.4f,%.2f,%.2f,%.4f,%d\n",
                        r.architecture, r.workloadSize, r.totalTimeMs,
                        r.avgAdmitNs, r.avgDispatchNs, r.agingTimeMs, r.maxWaitS5);
            }
        }
        System.out.println("[SUCCESS] Raw experimental data saved to " + csvPath);

        printSummaryTable(rows);
    }

    private static void runComparison(int size, List<ValidationRow> rows) {
        // Pre-generate patient arrivals
        Random rng = new Random(55555);
        int[] severities = new int[size];
        for (int i = 0; i < size; i++) {
            // Heavily skewed towards high priority (severe surge) to stress starvation
            severities[i] = (rng.nextDouble() < 0.70) ? (1 + rng.nextInt(3)) : (4 + rng.nextInt(2));
        }

        // 1. Benchmark Original PriorityQueue Baseline
        OriginalTriageBaseline orig = new OriginalTriageBaseline();
        long origAgingTotalNs = 0;
        long origAdmitTotalNs = 0;
        long origDispatchTotalNs = 0;
        long maxWaitS5Orig = 0;

        long startOrig = System.nanoTime();
        for (int i = 0; i < size; i++) {
            long t1 = System.nanoTime();
            orig.admit("P_" + i, severities[i]);
            origAdmitTotalNs += (System.nanoTime() - t1);

            if (i > 0 && i % AGING_INTERVAL == 0) {
                long tA = System.nanoTime();
                orig.performAgingSweep(100);
                origAgingTotalNs += (System.nanoTime() - tA);
            }

            if (i % 2 == 0) {
                long t2 = System.nanoTime();
                OriginalTriageBaseline.TriagePatient served = orig.next();
                origDispatchTotalNs += (System.nanoTime() - t2);
                if (served != null && severities[Integer.parseInt(served.name.substring(2))] == 5) {
                    long wait = i - served.arrivalTime;
                    if (wait > maxWaitS5Orig) maxWaitS5Orig = wait;
                }
            }
        }
        long durationOrig = System.nanoTime() - startOrig;

        rows.add(new ValidationRow("Original PriorityQueue", size, durationOrig / 1e6,
                (double) origAdmitTotalNs / size, (double) origDispatchTotalNs / (size / 2.0),
                origAgingTotalNs / 1e6, maxWaitS5Orig));

        // 2. Benchmark Redesigned AF-MLBQ
        AgingMultiLevelBucketTriage redesign = new AgingMultiLevelBucketTriage();
        long redAgingTotalNs = 0;
        long redAdmitTotalNs = 0;
        long redDispatchTotalNs = 0;
        long maxWaitS5Red = 0;

        long startRed = System.nanoTime();
        for (int i = 0; i < size; i++) {
            long t1 = System.nanoTime();
            redesign.admit("P_" + i, severities[i]);
            redAdmitTotalNs += (System.nanoTime() - t1);

            if (i > 0 && i % AGING_INTERVAL == 0) {
                long tA = System.nanoTime();
                redesign.performAgingSweep();
                redAgingTotalNs += (System.nanoTime() - tA);
            }

            if (i % 2 == 0) {
                long t2 = System.nanoTime();
                AgingMultiLevelBucketTriage.TriagePatient served = redesign.next();
                redDispatchTotalNs += (System.nanoTime() - t2);
                if (served != null && severities[Integer.parseInt(served.name.substring(2))] == 5) {
                    long wait = i - served.arrivalTime;
                    if (wait > maxWaitS5Red) maxWaitS5Red = wait;
                }
            }
        }
        long durationRed = System.nanoTime() - startRed;

        rows.add(new ValidationRow("Redesigned AF-MLBQ", size, durationRed / 1e6,
                (double) redAdmitTotalNs / size, (double) redDispatchTotalNs / (size / 2.0),
                redAgingTotalNs / 1e6, maxWaitS5Red));

        System.out.printf("  Original PriorityQueue : Total = %8.2f ms | Admit = %6.1f ns | Dispatch = %6.1f ns | Aging = %7.2f ms | Max Wait S5 = %d\n",
                durationOrig / 1e6, (double) origAdmitTotalNs / size, (double) origDispatchTotalNs / (size / 2.0), origAgingTotalNs / 1e6, maxWaitS5Orig);
        System.out.printf("  Redesigned AF-MLBQ     : Total = %8.2f ms | Admit = %6.1f ns | Dispatch = %6.1f ns | Aging = %7.2f ms | Max Wait S5 = %d\n",
                durationRed / 1e6, (double) redAdmitTotalNs / size, (double) redDispatchTotalNs / (size / 2.0), redAgingTotalNs / 1e6, maxWaitS5Red);
    }

    private static void printSummaryTable(List<ValidationRow> rows) {
        System.out.println("\n" + "=".repeat(110));
        System.out.printf("%-25s | %8s | %15s | %14s | %14s | %14s | %10s\n",
                "Architecture", "Size", "Total Time (ms)", "Admit Lat (ns)", "Disp Lat (ns)", "Aging Time(ms)", "Max Wait S5");
        System.out.println("-".repeat(110));
        for (ValidationRow r : rows) {
            System.out.printf("%-25s | %8d | %15.3f | %14.1f | %14.1f | %14.3f | %10d\n",
                    r.architecture, r.workloadSize, r.totalTimeMs, r.avgAdmitNs, r.avgDispatchNs, r.agingTimeMs, r.maxWaitS5);
        }
        System.out.println("=".repeat(110));
    }

    static class ValidationRow {
        String architecture;
        int workloadSize;
        double totalTimeMs;
        double avgAdmitNs;
        double avgDispatchNs;
        double agingTimeMs;
        long maxWaitS5;

        ValidationRow(String architecture, int workloadSize, double totalTimeMs,
                      double avgAdmitNs, double avgDispatchNs, double agingTimeMs, long maxWaitS5) {
            this.architecture = architecture;
            this.workloadSize = workloadSize;
            this.totalTimeMs = totalTimeMs;
            this.avgAdmitNs = avgAdmitNs;
            this.avgDispatchNs = avgDispatchNs;
            this.agingTimeMs = agingTimeMs;
            this.maxWaitS5 = maxWaitS5;
        }
    }
}

