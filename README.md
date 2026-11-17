# Programming in Java – Assignment 2 (CO2): Strings and Collections

## Academic Information
- **Course Title:** Programming in Java
- **Course Code:** CSA0914
- **Course Outcome:** CO2 (Sessions 15–26: Strings, Collections, Generics, Ordering)
- **Student Name:** Logeshwaran R
- **Register Number:** 192511175
- **Faculty / Guide:** Dr. Mathivanan
- **Repository Name:** `CSA0914-192511175-ASSIGNMENT2`
- **Total Marks:** 50 Marks
- **Java Environment:** OpenJDK 21 LTS (Microsoft HotSpot 64-Bit Server VM, build 21.0.12.1+1-LTS)
- **Platform:** Windows x64

---

## 1. Project Directory Structure

```
CSA0914-192511175-ASSIGNMENT2/
│
├── Q1_String_Performance/
│   ├── TicketWorkload.java           # Realistic railway e-ticket data generator
│   ├── TicketGenerator.java          # String (+), StringBuilder, StringBuffer, StringJoiner, String.format
│   ├── TicketBenchmark.java          # High-precision System.nanoTime() benchmark across 4 workloads
│   └── MultithreadedLoggerDemo.java   # Concurrency stress test: thread-confined vs shared logging
│
├── Q2_Playlist_Collections/
│   ├── Song.java                     # Song entity class
│   ├── PlaylistBenchmark.java        # Benchmarks 7 core operations across 1k, 10k, 100k songs
│   ├── UpNextQueueEvaluation.java    # Changed condition: ArrayList vs LinkedList vs ArrayDeque
│   └── PlainArrayPlaylist.java       # Alternative concept: raw array with manual System.arraycopy
│
├── Q3_HashSet_Equals_HashCode/
│   ├── AttendanceInitial.java        # Initial uncompleted code (demonstrating the duplicate bug)
│   ├── Attendance.java               # Official code with TODO 1 completed (proper hashCode)
│   ├── ContractInvestigation.java     # 3 designed test scenarios (identity match, duplicate, mutation bug)
│   └── AlternativeDeDuplication.java # Alternative concept: TreeSet vs HashMap vs HashSet
│
├── Q4_Pharmacy_Inventory_Maps/
│   ├── Medicine.java                 # Medicine model (code, name, batch, stock, sequence)
│   ├── PharmacyInventory.java        # Inventory lookup and dual evening report generation
│   ├── MapBenchmark.java             # 100k lookups & report benchmarks for HashMap, TreeMap, LinkedHashMap
│   ├── HashtableVsConcurrentComparison.java # Scalability inquiry: why java.util.Hashtable is obsolete
│   └── BinarySearchInventory.java    # Alternative concept: sorted array searched via Arrays.binarySearch
│
├── Q5_Generic_Leaderboard/
│   ├── Team.java                     # Sports team entity
│   ├── LeaderboardDemo.java          # Official code with TODO 1 (defensive sort) & TODO 2 (compound comparator)
│   ├── LeaderboardVerification.java  # Multi-level ties, integer subtraction overflow, reusability with Player
│   └── StableMultiPassSort.java      # Alternative concept: 3-pass stable sorting vs single chained comparator
│
├── Q6_Priority_Queue_Triage/
│   ├── Patient.java                  # Patient record (name, severity, arrival)
│   ├── Triage.java                   # Official code with TODO 1 completed (compound PriorityQueue)
│   ├── TriageVerification.java       # 8-patient hand-traced proof & heap array vs poll ordering
│   ├── TriageBenchmark.java          # Scalability benchmark across 1k, 10k, 100k patients & 5k/day evaluation
│   └── AlternativeTriageComparison.java # Starvation demonstration, Sorted LinkedList vs 5 FIFO bucket queues
│
├── Q7_Design_Redesign/
│   ├── OriginalTriageBaseline.java   # Baseline PriorityQueue with periodic draining/re-heapification aging
│   ├── AgingMultiLevelBucketTriage.java # Redesigned Aging Fair Multi-Level Bucket Queue (AF-MLBQ)
│   └── ComparativeValidation.java    # Controlled side-by-side empirical benchmark
│
├── Experimental_Data/                # Raw empirical benchmark measurements in CSV format
│   ├── Q1_string_performance.csv
│   ├── Q2_playlist_operations.csv
│   ├── Q4_map_benchmarks.csv
│   ├── Q6_triage_scaling.csv
│   └── Q7_comparative_validation.csv
│
├── Graphs/                           # Publication-quality charts generated via Python Matplotlib
│   ├── generate_graphs.py
│   ├── Q1_execution_time_comparison.png
│   ├── Q1_memory_and_objects.png
│   ├── Q2_operation_latency_vs_size.png
│   ├── Q4_lookup_and_report_latency.png
│   ├── Q6_heap_throughput.png
│   └── Q7_original_vs_redesign_latency.png
│
├── Screenshots/                      # Full raw terminal execution outputs for all tasks
│   ├── Q1_execution_benchmark.txt
│   ├── Q2_execution_benchmark.txt
│   ├── Q3_execution_investigation.txt
│   ├── Q4_execution_benchmark.txt
│   ├── Q5_execution_leaderboard.txt
│   ├── Q6_execution_triage.txt
│   └── Q7_execution_validation.txt
│
├── Report/                           # Complete academic project reports
│   ├── CSA09-Assignment2-Report.docx # Comprehensive Microsoft Word Report with cover page & tables
│   ├── CSA09-Assignment2-Report.md   # Full Markdown Report
│   └── build_full_report.py          # Script generating the official report documents
│
├── .gitignore                        # Git exclusion configuration
└── README.md                         # Project documentation and guide
```

---

## 2. Compilation and Execution Instructions

All programs can be compiled and executed using standard JDK 21 commands from the project root:

```powershell
cd CSA0914-192511175-ASSIGNMENT2
```

### **Q1 – Building Text Under Load: String, StringBuilder or StringBuffer**
```powershell
javac Q1_String_Performance/*.java
java Q1_String_Performance.TicketBenchmark
java Q1_String_Performance.MultithreadedLoggerDemo
```

### **Q2 – Music Playlist Editor: ArrayList, LinkedList & ArrayDeque**
```powershell
javac Q2_Playlist_Collections/*.java
java Q2_Playlist_Collections.PlaylistBenchmark
java Q2_Playlist_Collections.UpNextQueueEvaluation
```

### **Q3 – Engineer the hashCode–equals Contract**
```powershell
javac Q3_HashSet_Equals_HashCode/*.java
java Q3_HashSet_Equals_HashCode.AttendanceInitial
java Q3_HashSet_Equals_HashCode.Attendance
java Q3_HashSet_Equals_HashCode.ContractInvestigation
java Q3_HashSet_Equals_HashCode.AlternativeDeDuplication
```

### **Q4 – Choosing a Map for a Pharmacy Inventory**
```powershell
javac Q4_Pharmacy_Inventory_Maps/*.java
java Q4_Pharmacy_Inventory_Maps.MapBenchmark
java Q4_Pharmacy_Inventory_Maps.HashtableVsConcurrentComparison
```

### **Q5 – Generic Ranking Component from a Principle**
```powershell
javac Q5_Generic_Leaderboard/*.java
java Q5_Generic_Leaderboard.LeaderboardDemo
java Q5_Generic_Leaderboard.LeaderboardVerification
java Q5_Generic_Leaderboard.StableMultiPassSort
```

### **Q6 – Priority, Order and Fairness in a Queue**
```powershell
javac Q6_Priority_Queue_Triage/*.java
java Q6_Priority_Queue_Triage.Triage
java Q6_Priority_Queue_Triage.TriageVerification
java Q6_Priority_Queue_Triage.TriageBenchmark
java Q6_Priority_Queue_Triage.AlternativeTriageComparison
```

### **Q7 – Day-4 Design Challenge: Diagnose, Redesign and Validate**
```powershell
javac Q7_Design_Redesign/*.java
java Q7_Design_Redesign.ComparativeValidation
```

### **Regenerate Graphs and Word Report**
```powershell
python Graphs/generate_graphs.py
python Report/build_full_report.py
```

---

## 3. Summary of Results for Each Question

### **Q1: String, StringBuilder or StringBuffer (6 Marks)**
- **Workload 2 (10,000 Tickets, 50 Fields each):**
  - `String (+)`: 80.08 ms | 1,000,000 intermediate objects | 40.50 MB heap consumed.
  - `StringBuilder`: 24.24 ms | 10,000 objects | 3.01 MB heap consumed (**3.3x faster, 13x less memory**).
  - In scaling manifest test (5,000 fields per single ticket), `StringBuilder` was **>120x faster** than `String (+)`, empirically verifying the theoretical $O(N^2)$ quadratic copying penalty of String concatenation in loops.
- **Multithreaded Server Recommendation:**
  - *Per-ticket text:* `StringBuilder` (thread-confined stack allocation; zero lock overhead).
  - *Shared booking log:* `StringBuffer` (unsynchronized `StringBuilder` threw `ArrayIndexOutOfBoundsException` and dropped data under 10 threads; `StringBuffer` was 100% thread-safe).

### **Q2: ArrayList vs LinkedList & ArrayDeque (4 Marks)**
- **Random Access (`get(k)` at $N=100,000$):**
  - `ArrayList`: 172.4 ns (instant $O(1)$ pointer math).
  - `LinkedList`: 35,180.1 ns ($O(N)$ pointer traversal, **>200x slower** due to cache misses).
- **Middle Operations at $N=100,000$:**
  - `ArrayList`: 4,802.6 ns (single hardware-accelerated `System.arraycopy`).
  - `LinkedList`: 61,258.9 ns ($O(N)$ pointer chasing to locate node at $N/2$, **12x slower**).
- **Changed Condition ("Up Next" Queue):**
  - `ArrayList`: 183.24 ms (crippled by $O(N)$ front shifts).
  - `LinkedList`: 10.70 ms ($O(1)$ pointer updates, but heavy Node object allocations).
  - `ArrayDeque`: **10.28 ms (Optimal choice)**: Circular resizable ring buffer, zero Node allocations, optimal CPU cache locality.

### **Q3: Engineer the hashCode–equals Contract (6 Marks)**
- **Before TODO 1:** Initial `AttendanceInitial` printed `Unique students: 3` (De-duplication failed).
- **After TODO 1:** Completed `Attendance` printed `Unique students: 2` (De-duplication succeeded).
- **Root Cause:** Without overriding `hashCode()`, default `Object.hashCode()` used identity addresses. Distinct instances with identical `regNo` landed in different buckets `(n-1) & hash`, so `equals()` was never called.
- **The Disappearing Object Bug:** Mutating `regNo` after insertion shifted its expected bucket. `set.contains()` returned `false` while the object remained trapped in the set.
- **Rule Enforced:** All fields participating in `hashCode()` and `equals()` must be declared `final`.

### **Q4: Map for Pharmacy Inventory (6 Marks)**
- **100,000 Lookups at $N=100,000$ Medicines:**
  - `HashMap`: 110.0 ns/op (Meets clinical SLA $< 1,000$ ns).
  - `LinkedHashMap`: 109.2 ns/op (Meets clinical SLA $< 1,000$ ns).
  - `TreeMap`: 715.5 ns/op (6.5x slower due to $O(\log N)$ red-black tree traversal).
- **Evening Reports:**
  - `TreeMap` naturally yields Code-Order Report in 13.77 ms ($O(N)$).
  - `LinkedHashMap` naturally yields Insertion-Order Report in 1.08 ms ($O(N)$).
  - `LinkedHashMap` is the recommended single-map deployment because it provides fast $O(1)$ lookups and natural insertion-order reporting.
- **Hashtable Obsolete:** Monolithic method-level locking causes heavy thread contention; does not permit nulls. Modern code uses `ConcurrentHashMap`.

### **Q5: Generic Ranking Component (7 Marks)**
- **Principle Enforced:** Strict weak ordering and transitivity ($A > B \land B > C \implies A > C$).
- **Official Output:**
  ```
  [Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]
  ```
- **Tie-Breaker:** Bisons and Panthers tie on Points (9) and Goal Difference (+4). The tertiary tie-breaker resolves alphabetically: `"Bisons" < "Panthers"`, ranking Bisons 2nd.
- **Faulty Subtraction Bug:** `(t1, t2) -> t2.points - t1.points` overflows on extreme values, flipping comparison signs. Always use `Integer.compare()`.

### **Q6: Emergency Triage Priority Queue (8 Marks)**
- **Official Output:**
  ```
  Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)
  ```
- **Arrival Counter:** Monotonically increasing counter provides strict FIFO tie-breaking among equal severities.
- **Heap vs Polling Order:** Direct `System.out.println(queue)` outputs `[Meena(S1), Kumar(S1), Lata(S2), Asha(S5), Ravi(S3), John(S3)]`. Min-heap array only enforces $parent \le children$. True medical priority order requires successive `poll()` calls.
- **Hospital Demand:** 5,000 patients/day = ~0.058 arrivals/second. Measured system throughput is **>850,000 operations/second**, exceeding daily hospital demand by **>100,000x**.

### **Q7: Day-4 Design Challenge – Redesign & Validation (13 Marks)**
- **Diagnosed Bottleneck:** Standard `PriorityQueue` starves low-priority patients under continuous high-severity arrivals. Implementing dynamic aging requires draining and re-inserting all $N$ patients, collapsing performance from $O(\log N)$ to $O(N^2)$ per aging sweep.
- **Redesigned Solution (AF-MLBQ):** Aging Fair Multi-Level Bucket Queue using an array of 5 circular deques (`ArrayDeque<Patient>[5]`).
  - Admission: $O(1)$ direct bucket append.
  - Dispatch: $O(1)$ pop from highest non-empty bucket.
  - Aging Promotion: $O(1)$ bulk cohort splicing (`buckets[s-1].addAll(buckets[s])`) without touching individual patient timestamps or rebalancing heaps!
- **Measured Empirical Validation at $N=25,000$ operations:**
  - Total time dropped from **66.31 ms to 7.97 ms** (**8.3x faster**).
  - Dynamic aging overhead dropped from **57.00 ms to 3.08 ms** (**18.5x faster**).
  - Dispatch latency dropped from 252.8 ns to 33.6 ns (**7.5x faster**).
  - Starvation was completely eliminated, guaranteeing bounded wait times for all patients.

---

## 4. Progressive Git Commit Log

```
* e79c21b - Complete Q1: String, StringBuilder and StringBuffer performance benchmark and multithreaded logging analysis
* 9ef0a1a - Configure .gitignore to exclude compiled class files
* bdb7f0a - Complete Q2: Music Playlist Editor benchmarks (ArrayList vs LinkedList) and ArrayDeque Up-Next queue evaluation
* 111255a - Complete Q3: Engineer the hashCode-equals Contract, TODO 1 completion, 3 designed test investigations, and TreeSet/HashMap comparisons
* 6bf837d - Complete Q4: Hospital Pharmacy Inventory Map benchmarks (HashMap, TreeMap, LinkedHashMap), Hashtable deprecation study, and binary search alternative
* 48f9dfc - Complete Q5: Generic Leaderboard ranking component, TODO 1 & 2 completion, multi-level tie verification, faulty comparator overflow demonstration, and stable sort comparison
* f68470e - Complete Q6: Hospital Emergency Triage PriorityQueue implementation, arrival tie-breaking, 8-patient verification, heap vs poll ordering, and 5-bucket alternative analysis
* fe630d9 - Complete Q7: Day-4 Design Challenge - Bottleneck diagnosis, AF-MLBQ architectural redesign, and controlled empirical validation
```

---

## 5. Viva Voce Defense Checklist

1. **Why does String concatenation inside a loop scale as $O(N^2)$?**
   - Strings are immutable. Each concatenation creates a new String and copies all characters from the previous string. For $N$ concatenations, total characters copied is $\sum_{i=1}^N i = O(N^2)$.
2. **Why is `ArrayDeque` superior to `LinkedList` for double-ended queues?**
   - `ArrayDeque` is backed by a circular array. Operations advance head/tail indices in $O(1)$ with contiguous memory locality and zero object allocation. `LinkedList` allocates a 24–32 byte `Node` object per element, causing cache misses and GC pressure.
3. **What happens if a class overrides `equals()` but forgets `hashCode()`?**
   - Distinct instances with identical field values get different identity hash codes, landing in different hash buckets. `HashSet` only invokes `equals()` on items within the same bucket, leading to duplicate entries.
4. **Why does `PriorityQueue.toString()` not display patients in priority order?**
   - `PriorityQueue` stores elements in an array-based binary min-heap. Direct iteration scans array indices $0, 1, 2, \dots$ which only guarantees the partial heap property ($A[i] \le A[2i+1], A[2i+2]$). True sorted order requires calling `poll()`.
5. **How does AF-MLBQ eliminate starvation and heap rebalancing in Q7?**
   - It partitions patients into 5 discrete severity circular deques. Promoting waiting patients during an aging epoch is accomplished by bulk queue splicing in $O(1)$ time, guaranteeing bounded maximum wait times without $O(N^2)$ heap re-sorting.
