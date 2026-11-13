package Q1_String_Performance;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TicketBenchmark {

    private static final int WARMUP_RUNS = 5;
    private static final int BENCHMARK_RUNS = 10;

    public static void main(String[] args) throws Exception {
        System.out.println("==========================================================================");
        System.out.println("Q1: Railway E-Ticket Generation Performance Benchmark");
        System.out.println("Comparing: String (+), StringBuilder, StringBuffer, StringJoiner, String.format");
        System.out.println("==========================================================================\n");

        List<BenchmarkResult> results = new ArrayList<>();

        // Workload 1: 1 Short Ticket with 5 fields
        System.out.println(">>> Running Workload 1: 1 Short Ticket (5 fields)...");
        runWorkload("Workload 1 (1 Ticket, 5 Fields)", 1, 5, results);

        // Workload 2: 10,000 Tickets with 50 fields each
        System.out.println("\n>>> Running Workload 2: 10,000 Tickets (50 fields each)...");
        runWorkload("Workload 2 (10000 Tickets 50 Fields)", 10000, 50, results);

        // Workload 3: Scaling fields on 1 Large Manifest Ticket (1000 and 5000 fields)
        System.out.println("\n>>> Running Workload 3A: 1 Manifest Ticket (1,000 fields)...");
        runWorkload("Workload 3A (1 Ticket 1000 Fields)", 1, 1000, results);

        System.out.println("\n>>> Running Workload 3B: 1 Manifest Ticket (5,000 fields)...");
        runWorkload("Workload 3B (1 Ticket 5000 Fields)", 1, 5000, results);

        // Export to CSV
        String csvPath = "Experimental_Data/Q1_string_performance.csv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(csvPath))) {
            pw.println("Workload,Approach,Tickets,FieldsPerTicket,AvgTimeMs,TimePerTicketNs,EstIntermediateObjects,MemoryUsedMB");
            for (BenchmarkResult res : results) {
                pw.printf("\"%s\",%s,%d,%d,%.4f,%.2f,%d,%.2f\n",
                        res.workload, res.approach, res.tickets, res.fields,
                        res.avgTimeMs, res.timePerTicketNs, res.estObjects, res.memoryUsedMB);
            }
        }
        System.out.println("\n[SUCCESS] Raw experimental data saved to " + csvPath);

        // Print final summary table
        printTable(results);
    }

    private static void runWorkload(String workloadName, int ticketCount, int fieldCount, List<BenchmarkResult> results) {
        // Pre-generate field data to exclude data preparation time
        List<List<String>> allTickets = new ArrayList<>(ticketCount);
        for (int i = 0; i < ticketCount; i++) {
            allTickets.add(TicketWorkload.generateFields(fieldCount, i));
        }

        // Test approaches
        String[] approaches = {"String (+)", "StringBuilder", "StringBuffer", "StringJoiner", "String.format"};
        
        for (String approach : approaches) {
            // Skip String.format on 10,000 tickets or 5,000 fields to prevent 10+ minute hang
            if (approach.equals("String.format") && (ticketCount >= 10000 || fieldCount >= 5000)) {
                System.out.printf("  %-15s : Skipped (exponential format string parsing)\n", approach);
                continue;
            }

            // Warmup
            for (int w = 0; w < WARMUP_RUNS; w++) {
                executeBatch(approach, allTickets, 0, Math.min(ticketCount, 50));
            }

            // Benchmark
            long totalDurationNs = 0;
            long memoryUsedBytes = 0;

            for (int r = 0; r < BENCHMARK_RUNS; r++) {
                System.gc();
                try { Thread.sleep(20); } catch (InterruptedException ignored) {}

                long memBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                long start = System.nanoTime();

                executeBatch(approach, allTickets, 0, ticketCount);

                long end = System.nanoTime();
                long memAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                totalDurationNs += (end - start);
                if (r == 0) {
                    memoryUsedBytes = Math.max(0, memAfter - memBefore);
                }
            }

            double avgTimeMs = (totalDurationNs / (double) BENCHMARK_RUNS) / 1_000_000.0;
            double timePerTicketNs = (totalDurationNs / (double) BENCHMARK_RUNS) / (double) ticketCount;
            long estObjects = estimateIntermediateObjects(approach, ticketCount, fieldCount);
            double memoryUsedMB = memoryUsedBytes / (1024.0 * 1024.0);

            results.add(new BenchmarkResult(workloadName, approach, ticketCount, fieldCount,
                    avgTimeMs, timePerTicketNs, estObjects, memoryUsedMB));

            System.out.printf("  %-15s : Avg Time = %10.3f ms | Per Ticket = %10.1f ns | Est. Objects = %,12d | Mem = %6.2f MB\n",
                    approach, avgTimeMs, timePerTicketNs, estObjects, memoryUsedMB);
        }
    }

    private static void executeBatch(String approach, List<List<String>> tickets, int startIdx, int count) {
        for (int i = startIdx; i < startIdx + count; i++) {
            List<String> fields = tickets.get(i);
            switch (approach) {
                case "String (+)":
                    TicketGenerator.buildTicketString(fields);
                    break;
                case "StringBuilder":
                    TicketGenerator.buildTicketStringBuilder(fields);
                    break;
                case "StringBuffer":
                    TicketGenerator.buildTicketStringBuffer(fields);
                    break;
                case "StringJoiner":
                    TicketGenerator.buildTicketStringJoiner(fields);
                    break;
                case "String.format":
                    TicketGenerator.buildTicketStringFormat(fields);
                    break;
            }
        }
    }

    private static long estimateIntermediateObjects(String approach, int ticketCount, int fieldCount) {
        switch (approach) {
            case "String (+)":
                // In loop: result = result + field + "\n"
                // Evaluated as: (result + field) -> 1 intermediate String, then + "\n" -> 1 intermediate String.
                // For 'fieldCount' fields, approximately 2 * fieldCount intermediate String objects per ticket.
                return (long) ticketCount * (2L * fieldCount);
            case "StringBuilder":
                // 1 StringBuilder object + 0 intermediate Strings, final 1 String created at .toString()
                return (long) ticketCount * 1L;
            case "StringBuffer":
                return (long) ticketCount * 1L;
            case "StringJoiner":
                return (long) ticketCount * 1L;
            case "String.format":
                // Format string parsing creates Regex Matchers, Object arrays, FormatSpecifiers per field
                return (long) ticketCount * (3L * fieldCount);
            default:
                return 0;
        }
    }

    private static void printTable(List<BenchmarkResult> results) {
        System.out.println("\n" + "=".repeat(110));
        System.out.printf("%-35s | %-15s | %12s | %14s | %18s | %10s\n",
                "Workload", "Approach", "Avg Time (ms)", "Time/Ticket(ns)", "Est. Interm. Objs", "Memory(MB)");
        System.out.println("-".repeat(110));
        for (BenchmarkResult r : results) {
            System.out.printf("%-35s | %-15s | %12.3f | %14.1f | %,18d | %10.2f\n",
                    r.workload, r.approach, r.avgTimeMs, r.timePerTicketNs, r.estObjects, r.memoryUsedMB);
        }
        System.out.println("=".repeat(110));
    }

    static class BenchmarkResult {
        String workload;
        String approach;
        int tickets;
        int fields;
        double avgTimeMs;
        double timePerTicketNs;
        long estObjects;
        double memoryUsedMB;

        BenchmarkResult(String workload, String approach, int tickets, int fieldCount,
                        double avgTimeMs, double timePerTicketNs, long estObjects, double memoryUsedMB) {
            this.workload = workload;
            this.approach = approach;
            this.tickets = tickets;
            this.fields = fieldCount;
            this.avgTimeMs = avgTimeMs;
            this.timePerTicketNs = timePerTicketNs;
            this.estObjects = estObjects;
            this.memoryUsedMB = memoryUsedMB;
        }
    }
}
