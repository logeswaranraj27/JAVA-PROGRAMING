package Q6_Priority_Queue_Triage;

import java.util.Comparator;
import java.util.PriorityQueue;

class Patient {
    final String name;
    final int severity;
    final long arrival;

    Patient(String name, int severity, long arrival) {
        this.name = name;
        this.severity = severity;
        this.arrival = arrival;
    }

    public int getSeverity() { return severity; }
    public long getArrival() { return arrival; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return name + "(S" + severity + ")";
    }
}

public class Triage {
    private long counter = 0;

    // TODO 1: Create a PriorityQueue that serves the lowest severity number first and,
    // for equal severity, the earliest arrival first
    private final PriorityQueue<Patient> queue = new PriorityQueue<>(
            Comparator.comparingInt(Patient::getSeverity)
                    .thenComparingLong(Patient::getArrival)
    );

    void admit(String name, int severity) {
        queue.add(new Patient(name, severity, counter++));
    }

    Patient next() {
        return queue.poll();
    }

    public static void main(String[] args) {
        Triage er = new Triage();
        er.admit("Ravi", 3);
        er.admit("Meena", 1);
        er.admit("John", 3);
        er.admit("Asha", 5);
        er.admit("Kumar", 1);
        er.admit("Lata", 2);

        StringBuilder order = new StringBuilder();
        Patient p;
        while ((p = er.next()) != null) {
            order.append(p).append(' ');
        }
        String actualOutput = order.toString().trim();

        System.out.println("Actual output:");
        System.out.println(actualOutput);

        System.out.println("\nExpected output:");
        System.out.println("Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)");

        System.out.println("\nExplanation of Arrival Counter FIFO Tie-Breaking:");
        System.out.println("1. Meena and Kumar both arrived with severity 1. Meena arrived at counter=1, Kumar at counter=4.");
        System.out.println("   The secondary comparator (thenComparingLong(Patient::getArrival)) evaluates 1 < 4,");
        System.out.println("   guaranteeing Meena is served before Kumar.");
        System.out.println("2. Ravi and John both arrived with severity 3. Ravi arrived at counter=0, John at counter=2.");
        System.out.println("   Evaluating 0 < 2 guarantees Ravi is served before John.");
        System.out.println("3. Monotonically increasing arrival counter converts unstable heap ordering into strict FIFO fairness.");
    }
}
