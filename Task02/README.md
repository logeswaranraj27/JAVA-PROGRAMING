# Task 2: Data Representation and Array Structure

## 1. Chosen Data Types and Justifications

| Field | Java Data Type | Technical Justification |
| :--- | :--- | :--- |
| **Floor Index** | `int` | Discrete non-negative numerical level ($0, 1, \dots, F-1$). Supports native array indexing without type conversion. |
| **Slot Number** | `int` | Sequential non-negative index per floor ($0, 1, \dots, S_f-1$). 32-bit signed integer offers up to $2 \times 10^9$ slots with minimal 4-byte memory footprint. |
| **Vehicle Registration Number** | `String` | Alphanumeric sequence representing official state registration codes (e.g., `"TN01AB1111"`). Immutable sequence provides thread-safe key semantics for hash tables and equals checks. |
| **Entry / Exit Time** | `java.time.LocalDateTime` or `int` (epoch minutes) | ISO-8601 standard date-time representations avoid manual leap-year and timezone arithmetic errors. For duration computations, conversion to elapsed minutes (`ChronoUnit.MINUTES.between(...)`) avoids floating-point inaccuracies. |
| **Duration** | `int` | Elapsed parking duration in minutes. Storing duration in discrete minutes eliminates floating-point rounding errors that occur when storing fractional hours. |
| **Parking Fee** | `double` | Currency value in Indian Rupees (e.g., Rs 30.0, Rs 90.0). In production financial systems, `BigDecimal` is preferred to eliminate IEEE 754 floating-point inaccuracies, but for standard academic models, `double` satisfies precision requirements when combined with integer ceiling rules. |

---

## 2. Array Structure: Jagged Arrays vs 1D / Regular 2D Arrays

### Representation:
```java
// slots[floor][slot] holds a vehicle number, or null if the slot is free
private final String[][] slots;

public ParkingFacility(int[] slotsPerFloor) {
    slots = new String[slotsPerFloor.length][];
    for (int f = 0; f < slotsPerFloor.length; f++) {
        slots[f] = new String[slotsPerFloor[f]];
    }
}
```

### Why Jagged Arrays (`String[][]`)?
Real-world multi-level structures feature asymmetrical architecture across levels:
- Ground floor ($f=0$) accommodates ramps, cashier booths, security barriers, and EV chargers, yielding fewer standard spots (e.g., 20 slots).
- Upper floors ($f=1, 2$) feature expansive uninterrupted layouts (e.g., 50 or 100 slots).

A rectangular matrix `new String[floors][maxSlots]` would force every floor array to allocate memory matching the largest floor. If Floor 0 has 20 slots and Floor 4 has 100 slots, 80 array pointers on Floor 0 would sit permanently unused as `null`, wasting heap memory.
A **jagged array** creates an array of arrays where each row `slots[f]` is sized independently via `new String[slotsPerFloor[f]]`, matching real facility topology and eliminating unused reference overhead.

---

## 3. Type Conversion, Casting Risks & Resolution

### The Integer Division Pitfall
The business rule mandates: **"Every started hour is charged in full"** (i.e., ceiling of $\frac{\text{minutes}}{60}$).

If a developer writes:
```java
int hours = minutes / 60; // FAILS: Truncates towards zero
double fee = hours * ratePerHour;
```
- A vehicle parked for 15 minutes: $15 / 60 = 0$ hours $\rightarrow$ Fee = Rs 0.0 (unauthorized free parking!).
- A vehicle parked for 65 minutes: $65 / 60 = 1$ hour $\rightarrow$ Billed Rs 30.0 instead of Rs 60.0 (loss of revenue).

### The Ineffective Float-Cast Pitfall
```java
int hours = (int) Math.ceil(minutes / 60); // FAILS: Integer division evaluates first!
```
Because `minutes` and `60` are both integer primitives, Java performs integer division before passing the quotient to `Math.ceil()`. For `minutes = 65`, `65 / 60` becomes integer `1`, which is widened to `1.0`, and `Math.ceil(1.0)` returns `1.0`.

### The Correct Integer Arithmetic Ceiling Formula
To avoid floating-point casting overhead and precision issues, the ceiling is computed purely through integer arithmetic:
```java
int hours = (minutes + 59) / 60;
```
For `minutes = 130`:
$$\text{hours} = \frac{130 + 59}{60} = \frac{189}{60} = 3 \text{ billable hours}$$
For `minutes = 60`:
$$\text{hours} = \frac{60 + 59}{60} = \frac{119}{60} = 1 \text{ billable hour}$$
For `minutes = 61`:
$$\text{hours} = \frac{61 + 59}{60} = \frac{120}{60} = 2 \text{ billable hours}$$
*(For $0$ minutes, a ternary check `(minutes == 0) ? 0 : (minutes + 59) / 60` can handle immediate turnaround).*

---

## 4. Input Reading Strategies: `Scanner` vs Command-Line Arguments

1. **`java.util.Scanner`**:
   - Best for interactive terminal prompts and automated pipeline tests (`System.in`).
   - Offers convenient parsing methods (`scanner.nextInt()`, `scanner.next()`).
   - Slower due to regex tokenization, but user-friendly during manual evaluation.
2. **Command-Line Arguments (`String[] args`)**:
   - Ideal for automated batch scripts, continuous integration (CI) test runners, and non-interactive execution.
   - Requires manual parsing (`Integer.parseInt(args[0])`) and bounds checking (`args.length`).

---

## 5. Alternative Concept and Complexity Analysis

### Comparison: Jagged Array (`String[][]`) vs 1D Flat Array vs `ArrayList<String[]>`

#### 1. Flat 1D Array with Mapping Formula
Suppose all slots were flattened into a single array `String[] slots`:
- If each floor had a fixed uniform capacity $S$:
  $$\text{index} = \text{floor} \times S + \text{slot}$$
  $$\text{floor} = \text{index} / S, \quad \text{slot} = \text{index} \% S$$
- **When floors have different capacities**, the simple multiplication formula fails. A separate prefix-sum array `floorOffsets[f]` is required:
  $$\text{index} = \text{floorOffsets}[\text{floor}] + \text{slot}$$
- To locate the floor given a flattened index requires a binary search over `floorOffsets`, increasing lookup time to $O(\log F)$.
- Deallocation and allocation logic become convoluted because bounds checking requires verifying both `floor` and `slot` against dynamic partition boundaries.

#### 2. `ArrayList<String[]>`
- Allows dynamic addition of new floors at runtime ($O(1)$ amortized expansion).
- Incurs object wrapper overhead (~24 bytes per ArrayList instance + internal array growth buffers).

#### 3. Complexity & Memory Comparison

| Architecture | Slot Location Time | Memory Overhead | Handling Variable Floor Capacities |
| :--- | :--- | :--- | :--- |
| **Jagged Array (`String[][]`)** | **$O(1)$ direct array indexing** | **$F$ row pointers + total slots $N$** | **Native and optimal (exact row lengths)** |
| Flat 1D Array (`String[]`) | $O(1)$ with offset table; $O(\log F)$ binary search | Flat array + prefix sum table | Brittle; offsets must be recomputed on changes |
| `ArrayList<String[]>` | $O(1)$ via `.get(f)[s]` | High (wrapper object + dynamic list array) | Highly flexible, slightly higher heap overhead |

**Conclusion:** The jagged array (`String[][]`) is the most space-efficient and time-optimal data representation for physical multi-level facilities with non-uniform floor layouts.
