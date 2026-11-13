package Q6_Priority_Queue_Triage;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

public class TriageBenchmark {

    private static final int[] QUEUE_SIZES = {1000, 10000, 100000};
    private static final int BATCH_OPS = 10000;

    public static void main(String[] args) throws Exception {
        System.out.println("==========================================================================");
        System.out.println("Q6: PriorityQueue Growth Investigation & Scalability Benchmark");
        System.out.println("Sizes tested: 1,000 | 10,000 | 100,000 patients");
        System.out.println("==========================================================================\n");

        List<BenchmarkRow> records = new ArrayList<>();

        for (int size : QUEUE_SIZES) {
            System.out.printf(">>> Testing Queue Size N = %,d patients...\n", size);
            runBenchmarkForSize(size, records);
            System.out.println();
        }

        // Export to CSV
        String csvPath = "Experimental_Data/Q6_triage_scaling.csv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(csvPath))) {
            pw.println("QueueSize,BatchAdmissionsMs,AvgAdmissionNs,BatchDispatchesMs,AvgDispatchNs");
            for (BenchmarkRow r : records) {
                pw.printf("%d,%.4f,%.2f,%.4f,%.2f\n",
                        r.size, r.admitBatchMs, r.avgAdmitNs, r.dispatchBatchMs, r.avgDispatchNs);
            }
        }
        System.out.println("[SUCCESS] Raw experimental data saved to " + csvPath);

        printSummaryTable(records);
        evaluateHospitalDeployment(records);
    }

    private static void runBenchmarkForSize(int size, List<BenchmarkRow> records) {
        PriorityQueue<Patient> pq = new PriorityQueue<>(
                Comparator.comparingInt(Patient::getSeverity).thenComparingLong(Patient::getArrival)
        );

        Random rng = new Random(42);
        // Pre-populate queue to target size
        for (int i = 0; i < size; i++) {
            pq.add(new Patient("Pat_" + i, 1 + rng.nextInt(5), i));
        }

        // Measure BATCH_OPS admissions
        long startAdmit = System.nanoTime();
        for (int i = 0; i < BATCH_OPS; i++) {
            pq.add(new Patient("NewPat_" + i, 1 + rng.nextInt(5), size + i));
        }
        long durationAdmit = System.nanoTime() - startAdmit;
        double admitMs = durationAdmit / 1_000_000.0;
        double avgAdmitNs = (double) durationAdmit / BATCH_OPS;

        // Measure BATCH_OPS dispatches (poll)
        long startDispatch = System.nanoTime();
        for (int i = 0; i < BATCH_OPS; i++) {
            Patient p = pq.poll();
        }
        long durationDispatch = System.nanoTime() - startDispatch;
        double dispatchMs = durationDispatch / 1_000_000.0;
        double avgDispatchNs = (double) durationDispatch / BATCH_OPS;

        records.add(new BenchmarkRow(size, admitMs, avgAdmitNs, dispatchMs, avgDispatchNs));

        System.out.printf("  Admission (%d ops) : Total = %6.2f ms | Avg = %6.1f ns/op (O(log N) sift-up)\n",
                BATCH_OPS, admitMs, avgAdmitNs);
        System.out.printf("  Dispatch  (%d ops) : Total = %6.2f ms | Avg = %6.1f ns/op (O(log N) sift-down)\n",
                BATCH_OPS, dispatchMs, avgDispatchNs);
    }

    private static void printSummaryTable(List<BenchmarkRow> records) {
        System.out.println("\n" + "=".repeat(95));
        System.out.printf("%10s | %20s | %18s | %20s | %18s\n",
                "Queue Size", "Batch Admit (ms)", "Admit Latency (ns)", "Batch Poll (ms)", "Poll Latency (ns)");
        System.out.println("-".repeat(95));
        for (BenchmarkRow r : records) {
            System.out.printf("%10d | %20.3f | %18.1f | %20.3f | %18.1f\n",
                    r.size, r.admitBatchMs, r.avgAdmitNs, r.dispatchBatchMs, r.avgDispatchNs);
        }
        System.out.println("=".repeat(95));
    }

    private static void evaluateHospitalDeployment(List<BenchmarkRow> records) {
        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("Hospital Deployment Evaluation (City Hospital: 5,000 patients / day)");
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("Mathematical Demand Analysis:");
        System.out.println("  - Daily Load: 5,000 patients over 24 hours = 86,400 seconds.");
        System.out.println("  - Average Arrival Rate = 5,000 / 86,400 = ~0.058 patients per second.");
        System.out.println("  - Peak Surge Load (e.g., 20x surge during a major accident) = ~1.16 patients per second.");
        System.out.println("\nSystem Capacity Based on Experimental Evidence:");
        BenchmarkRow r = records.get(1); // 10,000 size
        double totalOpNs = r.avgAdmitNs + r.avgDispatchNs;
        double maxThroughputPerSec = 1_000_000_000.0 / totalOpNs;
        System.out.printf("  - Measured admission + dispatch latency per patient: %.1f ns + %.1f ns = %.1f ns\n",
                r.avgAdmitNs, r.avgDispatchNs, totalOpNs);
        System.out.printf("  - Theoretical Maximum System Throughput = %,d operations / second.\n", (long) maxThroughputPerSec);
        System.out.println("Verdict:");
        System.out.println("  The PriorityQueue easily handles the 5,000 patient/day volume by a factor of >100,000x.");
        System.out.println("  The primary computational bottleneck in triage is human clinical assessment,");
        System.out.println("  NOT the priority queue's data structure throughput.");
        System.out.println("--------------------------------------------------------------------------");
    }

    static class BenchmarkRow {
        int size;
        double admitBatchMs;
        double avgAdmitNs;
        double dispatchBatchMs;
        double avgDispatchNs;

        BenchmarkRow(int size, double admitBatchMs, double avgAdmitNs, double dispatchBatchMs, double avgDispatchNs) {
            this.size = size;
            this.admitBatchMs = admitBatchMs;
            this.avgAdmitNs = avgAdmitNs;
            this.dispatchBatchMs = dispatchBatchMs;
            this.avgDispatchNs = avgDispatchNs;
        }
    }
}

