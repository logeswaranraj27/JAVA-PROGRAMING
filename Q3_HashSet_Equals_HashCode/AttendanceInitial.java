package Q3_HashSet_Equals_HashCode;

import java.util.HashSet;
import java.util.Set;

/**
 * Initial Attendance program provided in the assignment briefing.
 * hashCode() is NOT overridden here to demonstrate the contract violation bug.
 */
class StudentInitial {
    private final String regNo;
    private final String name;

    StudentInitial(String regNo, String name) {
        this.regNo = regNo;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentInitial)) return false;
        return regNo.equals(((StudentInitial) o).regNo);
    }

    // hashCode() is NOT overridden here!
    // Inherits Object.hashCode() which generates identity hash codes.

    @Override
    public String toString() {
        return regNo + " " + name;
    }
}

public class AttendanceInitial {
    public static void main(String[] args) {
        System.out.println("=== INITIAL ATTENDANCE RUN (WITHOUT TODO 1) ===");
        Set<StudentInitial> present = new HashSet<>();
        present.add(new StudentInitial("192311001", "Arun"));  // scanned at Gate 1
        present.add(new StudentInitial("192311002", "Divya")); // scanned at Gate 2
        present.add(new StudentInitial("192311001", "Arun"));  // scanned again at Gate 3

        System.out.println("Actual Set Elements: " + present);
        System.out.println("Unique students: " + present.size());
        System.out.println("Observation: 3 entries stored instead of 2. De-duplication failed!");
    }
}

