package Q7_Design_Redesign;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Original Baseline Architecture for Q7:
 * Standard PriorityQueue with explicit aging sweeps.
 *
 * Bottleneck:
 * Because PriorityQueue cannot locate elements in O(1), aging all waiting patients
 * requires draining all N elements, updating their effective priority, and re-inserting them
 * into the min-heap. This degrades to O(N log N) per aging sweep, causing heavy latency spikes.
 */
public class OriginalTriageBaseline {

    public static class TriagePatient {
        public final String name;
        public int effectiveSeverity;
        public final long arrivalTime;
        public long lastAgedTime;

        public TriagePatient(String name, int severity, long arrivalTime) {
            this.name = name;
            this.effectiveSeverity = severity;
            this.arrivalTime = arrivalTime;
            this.lastAgedTime = arrivalTime;
        }

        @Override
        public String toString() {
            return name + "(S" + effectiveSeverity + ")";
        }
    }

    private final PriorityQueue<TriagePatient> pq;
    private long globalTime = 0;

    public OriginalTriageBaseline() {
        this.pq = new PriorityQueue<>(
                Comparator.comparingInt((TriagePatient p) -> p.effectiveSeverity)
                        .thenComparingLong(p -> p.arrivalTime)
        );
    }

    public void admit(String name, int severity) {
        pq.add(new TriagePatient(name, severity, globalTime++));
    }

    public TriagePatient next() {
        globalTime++;
        return pq.poll();
    }

    /**
     * Aging sweep on standard PriorityQueue:
     * Must drain all elements to modify priority, then re-heapify: O(N log N).
     */
    public void performAgingSweep(long agingThreshold) {
        if (pq.isEmpty()) return;

        List<TriagePatient> tempList = new ArrayList<>(pq.size());
        while (!pq.isEmpty()) {
            TriagePatient p = pq.poll();
            if (globalTime - p.lastAgedTime >= agingThreshold && p.effectiveSeverity > 1) {
                p.effectiveSeverity--; // Promote patient closer to critical
                p.lastAgedTime = globalTime;
            }
            tempList.add(p);
        }

        // Re-insert all patients into the heap: N * O(log N)
        for (TriagePatient p : tempList) {
            pq.add(p);
        }
    }

    public int size() { return pq.size(); }
}

