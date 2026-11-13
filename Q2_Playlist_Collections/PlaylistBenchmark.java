package Q2_Playlist_Collections;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PlaylistBenchmark {

    private static final int[] SIZES = {1000, 10000, 100000};
    private static final int OP_BATCH = 1000; // Number of probes/mutations to average per test
    private static final int WARMUP_ROUNDS = 3;

    public static void main(String[] args) throws Exception {
        System.out.println("==========================================================================");
        System.out.println("Q2: Music Playlist Editor - ArrayList vs LinkedList Performance Benchmark");
        System.out.println("Sizes tested: 1,000 | 10,000 | 100,000 songs");
        System.out.println("==========================================================================\n");

        List<BenchmarkRow> records = new ArrayList<>();

        for (int size : SIZES) {
            System.out.println(">>> Testing Playlist Size N = " + size + " songs...");
            benchmarkForSize(size, records);
            System.out.println();
        }

        // Export to CSV
        String csvPath = "Experimental_Data/Q2_playlist_operations.csv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(csvPath))) {
            pw.println("Collection,Size,Operation,TotalTimeMs,AvgTimePerOpNs");
            for (BenchmarkRow row : records) {
                pw.printf("%s,%d,%s,%.4f,%.2f\n",
                        row.collection, row.size, row.operation, row.totalTimeMs, row.avgTimePerOpNs);
            }
        }
        System.out.println("[SUCCESS] Raw experimental data saved to " + csvPath);

        printSummaryTable(records);
    }

    private static void benchmarkForSize(int size, List<BenchmarkRow> records) {
        // 1. Add at End (Populating list of size N)
        long arrayListAddEnd = measureAddAtEnd(new ArrayList<>(), size);
        long linkedListAddEnd = measureAddAtEnd(new LinkedList<>(), size);
        records.add(new BenchmarkRow("ArrayList", size, "Add at End", arrayListAddEnd / 1e6, (double) arrayListAddEnd / size));
        records.add(new BenchmarkRow("LinkedList", size, "Add at End", linkedListAddEnd / 1e6, (double) linkedListAddEnd / size));

        // Prepare populated lists
        List<Song> arrayList = new ArrayList<>(size);
        List<Song> linkedList = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            Song s = new Song("S_" + i, "Song Title " + i, "Artist " + (i % 100), 180 + (i % 120));
            arrayList.add(s);
            linkedList.add(s);
        }

        // 2. Insert at Front (add(0, song))
        int insertOps = Math.min(OP_BATCH, size);
        long arrayListInsertFront = measureInsertFront(new ArrayList<>(arrayList), insertOps);
        long linkedListInsertFront = measureInsertFront(new LinkedList<>(linkedList), insertOps);
        records.add(new BenchmarkRow("ArrayList", size, "Insert at Front", arrayListInsertFront / 1e6, (double) arrayListInsertFront / insertOps));
        records.add(new BenchmarkRow("LinkedList", size, "Insert at Front", linkedListInsertFront / 1e6, (double) linkedListInsertFront / insertOps));

        // 3. Insert in Middle (add(size/2, song))
        long arrayListInsertMid = measureInsertMiddle(new ArrayList<>(arrayList), insertOps);
        long linkedListInsertMid = measureInsertMiddle(new LinkedList<>(linkedList), insertOps);
        records.add(new BenchmarkRow("ArrayList", size, "Insert in Middle", arrayListInsertMid / 1e6, (double) arrayListInsertMid / insertOps));
        records.add(new BenchmarkRow("LinkedList", size, "Insert in Middle", linkedListInsertMid / 1e6, (double) linkedListInsertMid / insertOps));

        // 4. Remove at Front (remove(0))
        int removeOps = Math.min(OP_BATCH, size);
        long arrayListRemoveFront = measureRemoveFront(new ArrayList<>(arrayList), removeOps);
        long linkedListRemoveFront = measureRemoveFront(new LinkedList<>(linkedList), removeOps);
        records.add(new BenchmarkRow("ArrayList", size, "Remove at Front", arrayListRemoveFront / 1e6, (double) arrayListRemoveFront / removeOps));
        records.add(new BenchmarkRow("LinkedList", size, "Remove at Front", linkedListRemoveFront / 1e6, (double) linkedListRemoveFront / removeOps));

        // 5. Remove in Middle (remove(size/2))
        long arrayListRemoveMid = measureRemoveMiddle(new ArrayList<>(arrayList), removeOps);
        long linkedListRemoveMid = measureRemoveMiddle(new LinkedList<>(linkedList), removeOps);
        records.add(new BenchmarkRow("ArrayList", size, "Remove in Middle", arrayListRemoveMid / 1e6, (double) arrayListRemoveMid / removeOps));
        records.add(new BenchmarkRow("LinkedList", size, "Remove in Middle", linkedListRemoveMid / 1e6, (double) linkedListRemoveMid / removeOps));

        // 6. Access k-th element (get(k))
        int getOps = Math.min(OP_BATCH, size);
        Random rng = new Random(42);
        int[] indices = rng.ints(getOps, 0, size).toArray();
        long arrayListGet = measureGet(arrayList, indices);
        long linkedListGet = measureGet(linkedList, indices);
        records.add(new BenchmarkRow("ArrayList", size, "Access k-th Element", arrayListGet / 1e6, (double) arrayListGet / getOps));
        records.add(new BenchmarkRow("LinkedList", size, "Access k-th Element", linkedListGet / 1e6, (double) linkedListGet / getOps));

        // 7. Iterating through the whole list
        long arrayListIter = measureIterate(arrayList);
        long linkedListIter = measureIterate(linkedList);
        records.add(new BenchmarkRow("ArrayList", size, "Iterate Whole List", arrayListIter / 1e6, (double) arrayListIter / size));
        records.add(new BenchmarkRow("LinkedList", size, "Iterate Whole List", linkedListIter / 1e6, (double) linkedListIter / size));
    }

    private static long measureAddAtEnd(List<Song> list, int count) {
        long start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            list.add(new Song("S_" + i, "Title", "Artist", 200));
        }
        return System.nanoTime() - start;
    }

    private static long measureInsertFront(List<Song> list, int ops) {
        Song dummy = new Song("NEW_FRONT", "Head Song", "Artist", 210);
        long start = System.nanoTime();
        for (int i = 0; i < ops; i++) {
            list.add(0, dummy);
        }
        return System.nanoTime() - start;
    }

    private static long measureInsertMiddle(List<Song> list, int ops) {
        Song dummy = new Song("NEW_MID", "Mid Song", "Artist", 210);
        long start = System.nanoTime();
        for (int i = 0; i < ops; i++) {
            list.add(list.size() / 2, dummy);
        }
        return System.nanoTime() - start;
    }

    private static long measureRemoveFront(List<Song> list, int ops) {
        long start = System.nanoTime();
        for (int i = 0; i < ops; i++) {
            list.remove(0);
        }
        return System.nanoTime() - start;
    }

    private static long measureRemoveMiddle(List<Song> list, int ops) {
        long start = System.nanoTime();
        for (int i = 0; i < ops; i++) {
            list.remove(list.size() / 2);
        }
        return System.nanoTime() - start;
    }

    private static long measureGet(List<Song> list, int[] indices) {
        long start = System.nanoTime();
        for (int idx : indices) {
            Song s = list.get(idx);
        }
        return System.nanoTime() - start;
    }

    private static long measureIterate(List<Song> list) {
        long start = System.nanoTime();
        long count = 0;
        for (Song s : list) {
            if (s != null) count++;
        }
        return System.nanoTime() - start;
    }

    private static void printSummaryTable(List<BenchmarkRow> records) {
        System.out.println("\n" + "=".repeat(95));
        System.out.printf("%-12s | %8s | %-24s | %15s | %18s\n",
                "Collection", "Size", "Operation", "Total Time (ms)", "Avg Time/Op (ns)");
        System.out.println("-".repeat(95));
        for (BenchmarkRow r : records) {
            System.out.printf("%-12s | %8d | %-24s | %15.3f | %18.1f\n",
                    r.collection, r.size, r.operation, r.totalTimeMs, r.avgTimePerOpNs);
        }
        System.out.println("=".repeat(95));
    }

    static class BenchmarkRow {
        String collection;
        int size;
        String operation;
        double totalTimeMs;
        double avgTimePerOpNs;

        BenchmarkRow(String collection, int size, String operation, double totalTimeMs, double avgTimePerOpNs) {
            this.collection = collection;
            this.size = size;
            this.operation = operation;
            this.totalTimeMs = totalTimeMs;
            this.avgTimePerOpNs = avgTimePerOpNs;
        }
    }
}

