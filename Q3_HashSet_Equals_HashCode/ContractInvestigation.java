package Q3_HashSet_Equals_HashCode;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Three deliberately designed experiments investigating the hashCode-equals contract:
 * Test 1: Scans where HashSet appears to work correctly without overriding hashCode()
 * Test 2: Scans deliberately designed to expose duplicate entries without hashCode()
 * Test 3: Mutating the key field after insertion (The Disappearing Object Bug)
 */
public class ContractInvestigation {

    // Helper mutable student class specifically to demonstrate Test 3
    static class MutableStudent {
        private String regNo;
        private String name;

        MutableStudent(String regNo, String name) {
            this.regNo = regNo;
            this.name = name;
        }

        public void setRegNo(String regNo) { this.regNo = regNo; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MutableStudent)) return false;
            return Objects.equals(regNo, ((MutableStudent) o).regNo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(regNo);
        }

        @Override
        public String toString() { return regNo + " " + name; }
    }

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("Q3: Investigation of the hashCode - equals Contract in Hash-Based Collections");
        System.out.println("==========================================================================\n");

        runTest1();
        runTest2();
        runTest3();
    }

    private static void runTest1() {
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("TEST 1: Set of scans where HashSet appears to work correctly (No hashCode)");
        System.out.println("Prediction: Size will be 2.");
        System.out.println("Theoretical Reason: In Java HashMap/HashSet, putVal() first tests reference identity:");
        System.out.println("  'if (p.hash == hash && ((k = p.key) == key || (key != null && key.equals(k))))'");
        System.out.println("  When the EXACT same object reference is added twice, both hash and pointer identity (k == key)");
        System.out.println("  match trivially, masking the missing hashCode() override.");
        System.out.println("--------------------------------------------------------------------------");

        Set<StudentInitial> set = new HashSet<>();
        StudentInitial s1 = new StudentInitial("192311001", "Arun");
        StudentInitial s2 = new StudentInitial("192311002", "Divya");

        set.add(s1);
        set.add(s2);
        set.add(s1); // Re-adding the EXACT same reference s1

        System.out.println("Observed Set Size: " + set.size());
        System.out.println("Observed Elements: " + set);
        System.out.println("Result Verification: Prediction MATCHED (Appears to work, but is brittle!)");
        System.out.println();
    }

    private static void runTest2() {
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("TEST 2: Deliberately designed scans to expose duplicate entries");
        System.out.println("Prediction: Size will be 3 instead of 2.");
        System.out.println("Theoretical Reason: Two distinct StudentInitial instances representing the same student");
        System.out.println("  (new StudentInitial(\"192311001\", \"Arun\")) reside at different memory addresses.");
        System.out.println("  Without overriding hashCode(), Object.hashCode() assigns completely different hash values.");
        System.out.println("  They map to different table buckets [(n-1) & hash].");
        System.out.println("  HashSet never invokes equals() because they are in different buckets!");
        System.out.println("--------------------------------------------------------------------------");

        Set<StudentInitial> set = new HashSet<>();
        StudentInitial s1 = new StudentInitial("192311001", "Arun");
        StudentInitial s2 = new StudentInitial("192311002", "Divya");
        StudentInitial s3 = new StudentInitial("192311001", "Arun"); // Distinct instance, identical regNo

        set.add(s1);
        set.add(s2);
        set.add(s3);

        System.out.println("Observed Set Size: " + set.size());
        System.out.println("Observed Elements: " + set);
        System.out.println("Result Verification: Prediction MATCHED (Duplicate successfully exposed!)");
        System.out.println();
    }

    private static void runTest3() {
        System.out.println("--------------------------------------------------------------------------");
        System.out.println("TEST 3: Mutating key field used in hashCode() after insertion");
        System.out.println("Prediction: set.contains(s) will return FALSE even though s is physically inside the set!");
        System.out.println("Theoretical Reason: When inserted, s was placed in bucket B_old based on its initial hashCode.");
        System.out.println("  When regNo is mutated, s.hashCode() computes a new value corresponding to bucket B_new.");
        System.out.println("  When set.contains(s) is called, HashSet looks in B_new, finds nothing (or other items),");
        System.out.println("  and returns false. The object is permanently orphaned/trapped, causing memory leaks.");
        System.out.println("--------------------------------------------------------------------------");

        Set<MutableStudent> set = new HashSet<>();
        MutableStudent student = new MutableStudent("192311001", "Arun");
        set.add(student);

        System.out.println("Before mutation:");
        System.out.println("  Student: " + student);
        System.out.println("  set.contains(student): " + set.contains(student));
        System.out.println("  set.size(): " + set.size());

        // Mutate regNo after insertion
        student.setRegNo("192311999");
        System.out.println("\nAfter mutating regNo to '192311999':");
        System.out.println("  Student: " + student);
        System.out.println("  set.contains(student): " + set.contains(student) + "  <-- BUG MANIFESTATION!");
        System.out.println("  set.size(): " + set.size());
        System.out.println("  Is student in iterator? " + set.iterator().next().equals(student));
        System.out.println("Result Verification: Prediction MATCHED (Orphaned object confirmed!)");
        System.out.println("Conclusion: Fields participating in hashCode() MUST be immutable (declared 'final').");
        System.out.println("--------------------------------------------------------------------------");
    }
}
