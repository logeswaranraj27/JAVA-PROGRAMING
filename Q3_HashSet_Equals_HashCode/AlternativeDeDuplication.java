package Q3_HashSet_Equals_HashCode;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Alternative Concept and Complexity Analysis for Q3:
 * Comparing:
 * 1. HashSet<Student> (with proper hashCode/equals): O(1) average lookup/insert
 * 2. TreeSet<Student> with Comparator on regNo: O(log n) worst/average lookup/insert
 * 3. HashMap<String, Student> keyed by regNo: O(1) average lookup/insert, decouples identity from Student class
 */
public class AlternativeDeDuplication {

    private static final int SCAN_COUNT = 100000;

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("Q3 Alternative Concept: TreeSet vs HashMap vs HashSet for De-Duplication");
        System.out.println("Simulating " + SCAN_COUNT + " scans with 50% duplicate rate...");
        System.out.println("==========================================================================\n");

        // 1. HashSet<Student>
        Set<Student> hashSet = new HashSet<>();
        long startHashSet = System.nanoTime();
        for (int i = 0; i < SCAN_COUNT; i++) {
            String regNo = "REG_" + (i % (SCAN_COUNT / 2)); // 50,000 unique students
            hashSet.add(new Student(regNo, "Student_" + i));
        }
        long timeHashSet = System.nanoTime() - startHashSet;

        // 2. TreeSet<Student> with Comparator on regNo
        Set<Student> treeSet = new TreeSet<>(Comparator.comparing(Student::getRegNo));
        long startTreeSet = System.nanoTime();
        for (int i = 0; i < SCAN_COUNT; i++) {
            String regNo = "REG_" + (i % (SCAN_COUNT / 2));
            treeSet.add(new Student(regNo, "Student_" + i));
        }
        long timeTreeSet = System.nanoTime() - startTreeSet;

        // 3. HashMap<String, Student> keyed by regNo
        Map<String, Student> hashMap = new HashMap<>();
        long startHashMap = System.nanoTime();
        for (int i = 0; i < SCAN_COUNT; i++) {
            String regNo = "REG_" + (i % (SCAN_COUNT / 2));
            hashMap.putIfAbsent(regNo, new Student(regNo, "Student_" + i));
        }
        long timeHashMap = System.nanoTime() - startHashMap;

        System.out.printf("  %-30s : Time = %8.2f ms | Unique Size = %,d | Time/Scan = %6.1f ns\n",
                "HashSet<Student> (O(1) Avg)", timeHashSet / 1e6, hashSet.size(), (double) timeHashSet / SCAN_COUNT);
        System.out.printf("  %-30s : Time = %8.2f ms | Unique Size = %,d | Time/Scan = %6.1f ns\n",
                "TreeSet<Student> (O(log n))", timeTreeSet / 1e6, treeSet.size(), (double) timeTreeSet / SCAN_COUNT);
        System.out.printf("  %-30s : Time = %8.2f ms | Unique Size = %,d | Time/Scan = %6.1f ns\n",
                "HashMap<String, Student> (O(1))", timeHashMap / 1e6, hashMap.size(), (double) timeHashMap / SCAN_COUNT);

        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("Theoretical & Space Analysis:");
        System.out.println("1. HashSet<Student>:");
        System.out.println("   - Time per scan: O(1) average. Backed by HashMap. Node overhead ~32 bytes.");
        System.out.println("   - Relies on Student.hashCode() and Student.equals().");
        System.out.println("2. TreeSet<Student>:");
        System.out.println("   - Time per scan: O(log n). Red-black tree navigation on every insertion.");
        System.out.println("   - Tree entry overhead ~40 bytes (left, right, parent pointers + color bit).");
        System.out.println("   - Does NOT rely on hashCode() or equals(), only on Comparator.compare().");
        System.out.println("3. HashMap<String, Student>:");
        System.out.println("   - Time per scan: O(1) average.");
        System.out.println("   - Keyed by String which has a cached hashCode and rock-solid immutability.");
        System.out.println("   - Safely insulates collection integrity even if Student object is mutable.");
        System.out.println("--------------------------------------------------------------------------");
    }
}
