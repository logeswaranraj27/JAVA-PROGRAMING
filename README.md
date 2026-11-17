# Assignment 1 | CO1: Campus Smart-Parking System
**Course:** Programming in Java (CO1)  
**Institution:** SIMATS Engineering  
**Student Name:** Logeshwaran R  
**Register Number:** 192511175  
**Faculty / Guide:** Dr. Mathivanan  
**Repository Name:** `CSA0914-192511175-ASSIGNMENT1`  
**Java Version:** OpenJDK 21.0.12.1 LTS (Microsoft HotSpot build 21.0.12.1+1-LTS)  
**Operating System:** Windows 11  

---

## 1. Project Overview
This repository contains the complete, validated solution for **Assignment 1 (CO1): Campus Smart-Parking System**. The project designs, implements, and evaluates an object-oriented software model for a multi-level campus parking facility catering to two-wheelers, conventional cars, and electric vehicles (EVs) with charging ports, used by students, faculty/staff holding monthly passes, and paying visitors.

The implementation strictly satisfies all requirements outlined in the SIMATS Engineering assignment brief, including progressive Git commits, completed code tasks with exact console outputs, comprehensive alternative concept complexity analyses ($O$-notation), unit/boundary test tables, and the Day-4 Design Challenge adaptation.

---

## 2. Progressive Git Commit History
In compliance with the assignment submission regulations ("Commit your work progressively – at least one commit for each task"), the repository features progressive atomic commits:

- `ef29de2` - `feat(task01): Problem analysis, candidate classes, responsibilities and alternative concept analysis`
- `5c0fbe2` - `feat(task02): Data representation, jagged arrays, casting pitfalls and complexity analysis`
- `ccc5573` - `feat(task03): Complete ParkingFacility code, SIMATS pseudocode, and free-stack complexity analysis`
- `d99a6d6` - `feat(task04): Constructors, constructor chaining, copy constructors and memory lifecycle analysis`
- `e3bd428` - `feat(task05): Static members, object lifecycle, multi-gate counter analysis and GC`
- `7dc1250` - `feat(task06): Inheritance hierarchy, UML class diagram, constructor trace and composition trade-offs`
- `e57ec32` - `feat(task07): Method overriding, equals/hashCode contract, final keyword and switch vs dispatch analysis`
- `60f0f8b` - `feat(task08): Complete FeeDemo implementation, dynamic dispatch trace, and instanceof analysis`
- `d430c1d` - `feat(task09): Abstraction design, abstract class vs interface, default methods and inverted design analysis`
- `2ebdcb9` - `feat(task10): Package structure, encapsulation, validated accessors, autoboxing pitfalls and caller validation analysis`
- `045241f` - `feat(task11): Integration test suite, boundary tests, hand-calculated verification and HashMap benchmark`
- `d5433fc` - `feat(task12): Day-4 design challenge impact analysis, evaluation, adaptation, and evidence-based defence`

---

## 3. Repository Directory Structure
```
d:/lokeswaran college/courses/JAVA/assignment 1/
├── Task01/
│   └── README.md                  # Problem analysis, candidate classes, business rules, parallel arrays analysis
├── Task02/
│   ├── CastingDemo.java           # Precision loss demonstration (integer division vs ceiling)
│   ├── output.txt                 # Console execution log
│   └── README.md                  # Justification of data types, jagged arrays, and 1D/ArrayList complexity
├── Task03/
│   ├── ParkingFacility.java       # Complete the Code implementation (allocate & release)
│   ├── output.txt                 # Console execution log matching exact test output
│   └── README.md                  # SIMATS pseudocode and Free-Slot Stack complexity analysis
├── Task04/
│   ├── ConstructorDemo.java       # Overloaded constructors, chaining this(), copy constructor
│   ├── output.txt                 # Console execution log
│   └── README.md                  # Object creation, memory layout (stack/heap), validity guarantees
├── Task05/
│   ├── StaticAndLifecycleDemo.java# Static counters vs instance counters, multi-facility simulation
│   ├── output.txt                 # Console execution log
│   └── README.md                  # Static members, object lifecycle, pass-by-value, GC analysis
├── Task06/
│   ├── InheritanceTraceDemo.java  # Constructor execution trace for ElectricCar
│   ├── output.txt                 # Console execution log
│   └── README.md                  # Inheritance hierarchy, UML Mermaid diagram, Composition trade-off
├── Task07/
│   ├── OverrideDemo.java          # equals(), hashCode(), toString(), final keyword, HashSet deduplication
│   ├── output.txt                 # Console execution log
│   └── README.md                  # Overriding rules, vtable dispatch vs switch statement analysis
├── Task08/
│   ├── FeeDemo.java               # Complete the Code implementation (FeeDemo, TwoWheeler, Car, ElectricCar)
│   ├── output.txt                 # Console execution log matching exact test output
│   └── README.md                  # Polymorphism, dynamic dispatch trace, regNo equals justification
├── Task09/
│   ├── AbstractionDemo.java       # Abstract class Vehicle, Interface Chargeable & Payable, default/static methods
│   ├── output.txt                 # Console execution log
│   └── README.md                  # Abstraction rationale, multiple interface behavior, inverted design analysis
├── Task10/
│   ├── src/                       # Multi-package architecture
│   │   └── parking/
│   │       ├── model/             # Vehicle, Car, ParkingTicket
│   │       ├── facility/          # ParkingFacility
│   │       └── app/               # EncapsulationApp
│   ├── bin/                       # Compiled class files
│   ├── output.txt                 # Console execution log
│   └── README.md                  # Access modifiers, defensive setters, autoboxing traps
├── Task11/
│   ├── CampusSmartParkingSystem.java # Full integration system test suite & benchmark
│   ├── output.txt                 # Test execution log with hand-calculated financial audit
│   └── README.md                  # Test table, hand-calculated verification, Array vs HashMap benchmark
├── Task12/
│   ├── Day4DesignChallenge.java   # Adapted architecture (EBike, DeliveryVan peak surcharge, DayPass)
│   ├── output.txt                 # Validation execution log
│   └── README.md                  # Impact analysis, evaluation, adaptation, and evidence-based defence
└── README.md                      # Comprehensive laboratory report and execution guide
```

---

## 4. How to Compile and Execute Each Task

All programs are built and verified using **Java 21**. From the root project directory:

### Task 02: Casting & Precision Demo
```bash
javac Task02/CastingDemo.java
java Task02.CastingDemo
```
*Result:* Verifies that integer division `/ 60` produces illegal Rs 0.0 fees for short stays, while `(minutes + 59) / 60` produces correct billable hours.

### Task 03: ParkingFacility (Complete the Code)
```bash
javac Task03/ParkingFacility.java
java -cp Task03 ParkingFacility
```
*Result:* Matches exact console output specified on Page 9 of the brief:
```
[0, 0]
[0, 1]
[1, 0]
null
true false false
[0, 1]
```

### Task 04: Constructors & Memory Lifecycle
```bash
javac Task04/ConstructorDemo.java
java Task04.ConstructorDemo
```
*Result:* Validates constructor chaining via `this()`, duplicate ticket generation via copy constructor, and defensive exception handling.

### Task 05: Static Members & Multi-Facility Gates
```bash
javac Task05/StaticAndLifecycleDemo.java
java Task05.StaticAndLifecycleDemo
```
*Result:* Demonstrates ticket collision when using instance counters across North/South gates vs. globally unique ticket serials using static sequence generators.

### Task 06: Inheritance & Constructor Tracing
```bash
javac Task06/InheritanceTraceDemo.java
java Task06.InheritanceTraceDemo
```
*Result:* Traces the 4-stage constructor invocation chain: `Object` $\rightarrow$ `Vehicle` $\rightarrow$ `Car` $\rightarrow$ `ElectricCar`.

### Task 07: Method Overriding & Object Methods
```bash
javac Task07/OverrideDemo.java
java Task07.OverrideDemo
```
*Result:* Verifies `equals()` and `hashCode()` contract, case-insensitive vehicle equivalence, and automated deduplication in `HashSet`.

### Task 08: FeeDemo Polymorphism (Complete the Code)
```bash
javac Task08/FeeDemo.java
java -cp Task08 FeeDemo
```
*Result:* Matches exact console output specified on Page 15 of the brief:
```
TwoWheeler TN01A1 -> Rs 30.0
Car TN02B2 -> Rs 90.0
ElectricCar TN03C3 -> Rs 210.0
true
```

### Task 09: Abstraction & Multiple Interfaces
```bash
javac Task09/AbstractionDemo.java
java Task09.AbstractionDemo
```
*Result:* Demonstrates `ElectricCar` implementing both `Chargeable` and `Payable`, with interface default methods computing clean energy cess taxes.

### Task 10: Packages, Encapsulation & Autoboxing
```bash
javac -d Task10/bin Task10/src/parking/model/*.java Task10/src/parking/facility/*.java Task10/src/parking/app/*.java
java -cp Task10/bin parking.app.EncapsulationApp
```
*Result:* Enforces package boundary protection, validates departure timestamps, and exposes the Integer caching trap (`100 == 100` is true, but `200 == 200` is false).

### Task 11: System Integration & Performance Benchmark
```bash
javac Task11/CampusSmartParkingSystem.java
java Task11.CampusSmartParkingSystem
```
*Result:* Executes 11 test cases covering normal, boundary, and invalid operations. Verifies hand-calculated daily collection of **Rs 340.00**. Demonstrates that `HashMap` exit lookups ($O(1)$) achieve a **99x speedup** over `ArrayList` linear scans ($O(N)$).

### Task 12: Day-4 Design Challenge
```bash
javac Task12/Day4DesignChallenge.java
java Task12.Day4DesignChallenge
```
*Result:* Validates 5-floor non-uniform layout (300 slots), `EBike` flat fee (Rs 20.00), `DeliveryVan` peak surge tariff (Rs 120.00 at 10:30 AM vs Rs 60.00 off-peak), and `OneDayVisitorPass` waiver.

---

## 5. Summary of Results Across All Tasks

| Task | Primary Concept | Core Outcome | Brief Compliance |
| :---: | :--- | :--- | :---: |
| **01** | Object Identification | Formal candidate classes, responsibilities, and parallel array trade-offs | Verified |
| **02** | Data Types & Arrays | Jagged array `String[][]` chosen; integer ceiling formula solved | Verified |
| **03** | Control Logic & Pseudocode | `ParkingFacility` completed; SIMATS pseudocode; expected output matched | **100% Match** |
| **04** | Constructors & Memory | Master constructor chaining `this()`; copy constructor implemented | Verified |
| **05** | Static Members & Life Cycle | Multi-gate counter collision resolved; GC unreachability analyzed | Verified |
| **06** | Inheritance Hierarchy | Multilevel & hierarchical classes; constructor trace verified | Verified |
| **07** | Method Overriding | `equals` & `hashCode` on `regNo`; vtable dispatch proven superior to switch | Verified |
| **08** | Runtime Polymorphism | `FeeDemo` completed; dynamic dispatch verified; expected output matched | **100% Match** |
| **09** | Abstraction & Interfaces | Abstract base `Vehicle` + `Chargeable` interface; inverted design analyzed | Verified |
| **10** | Encapsulation & Packages | `parking.model/facility/app` packages; defensive mutators; autoboxing | Verified |
| **11** | Integration & Testing | Full test suite; hand-calculated Rs 340 audit match; HashMap 99x faster | Verified |
| **12** | Day-4 Design Challenge | Retain & Extend strategy; EBikes, Vans, 1-Day Pass, 5 floors validated | Verified |

---

## 6. GitHub Repository Setup and Push Guide
To push this local repository to your remote GitHub account:
```bash
# 1. Add your remote repository URL
git remote add origin https://github.com/logeswaranraj27/JAVA-PROGRAMING.git

# 2. Checkout the assignment branch
git checkout -b CSA0914-192511175-ASSIGNMENT1

# 3. Push progressive commits to GitHub
git push -u origin CSA0914-192511175-ASSIGNMENT1
```
The commit history will demonstrate progressive task-by-task engineering development ready for faculty evaluation and viva verification.
