# Task 10: Encapsulation, Packages, and Access Modifiers

## 1. Package Architecture

To adhere to the Single Responsibility Principle and high cohesion, the project is organized into modular packages:

```
Task10/
├── src/
│   └── parking/
│       ├── model/
│       │   ├── Vehicle.java         (Abstract base vehicle, domain state)
│       │   ├── Car.java             (Concrete vehicle specialization)
│       │   └── ParkingTicket.java   (Immutable ticket attributes, mutator validation)
│       ├── facility/
│       │   └── ParkingFacility.java (Multi-level jagged slot matrix, allocation rules)
│       └── app/
│           └── EncapsulationApp.java(Main driver, integration and bounds testing)
└── bin/                             (Compiled bytecode classes)
```

---

## 2. Justification of Access Modifiers

| Access Modifier | Applied Elements | Justification & Protection Level |
| :--- | :--- | :--- |
| **`private`** | Fields: `regNo`, `slots`, `ticketId`, `entryTime`, `exitTime`, `fee` | **Maximum data hiding**. Prevents direct outside mutation or unauthorized bypass of business rules. |
| **`default` (package-private)** | Internal slot indexing helpers, package-internal state | Accessible only to classes within the same package, keeping internal mechanisms hidden from external callers. |
| **`protected`** | Constructor `Vehicle(String regNo)`, method `ratePerHour()` | Restricts access strictly to subclasses (even in other packages) and same-package classes, enabling controlled inheritance without public leakage. |
| **`public`** | API Entry Points: `allocate()`, `release()`, `calculateFee()`, validated getters | Exposes clean, contracted public interfaces to client applications. |

---

## 3. Data Validation via Getters and Setters

- **Blank / Null Registration Numbers**:
  `Vehicle` constructor and `allocate()` reject null or empty strings via `throw new IllegalArgumentException(...)`.
- **Negative Dimensions / Coordinates**:
  `ParkingFacility` verifies that `floor >= 0` and `slot >= 0`.
- **Chronological Validity**:
  In `ParkingTicket.recordExit(...)`, setting an `exitTime` chronologically prior to `entryTime` throws an `IllegalArgumentException`, preventing backward-time corruption and negative fee liabilities.
- **Negative Durations & Fees**:
  Guarded by range checks rejecting negative numbers.

---

## 4. Wrapper Classes and Autoboxing Risks

Java provides object wrappers (`Integer`, `Double`, `Boolean`) for primitive types (`int`, `double`, `boolean`). While convenient, autoboxing introduces subtle runtime risks:

### 1. The Integer Cache Comparison Trap (`==` vs `.equals()`)
- Java caches `Integer` objects between **$-128$ and $127$**.
- For values inside the cache:
  ```java
  Integer a = 100, b = 100;
  a == b; // Evaluates to TRUE (points to same cached instance)
  ```
- For values outside the cache:
  ```java
  Integer a = 200, b = 200;
  a == b; // Evaluates to FALSE! (points to distinct heap objects)
  ```
- **Consequence**: Comparing slot numbers or floor numbers stored as `Integer` using `==` causes intermittent, hard-to-detect bugs.
- **Resolution**: Always compare object wrappers with `.equals()` or compare raw primitives.

### 2. Autounboxing `NullPointerException`
- If an unallocated slot is represented by a nullable wrapper `Integer slot = null;`:
  ```java
  int s = slot; // Triggers java.lang.NullPointerException at runtime!
  ```
- The JVM attempts `slot.intValue()` on a `null` reference.

---

## 5. Alternative Concept and Complexity Analysis

### Comparison: Encapsulated Private Fields + Validating Setters vs. Public Fields + Caller-Side Validation

#### The Public Fields Anti-Pattern:
```java
class PublicTicket {
    public String regNo;
    public int floor;
    public int slot;
    public double fee;
}
```

#### What Could Go Wrong?
1. **Pervasive State Corruption**: Any caller anywhere in the codebase can directly write `ticket.fee = -500.0;` or `facility.slots[0][0] = null;` without releasing locks, printing receipts, or validating boundaries.
2. **Duplication of Validation Logic**: If 10 different gate controllers, web portals, and mobile apps interact with `PublicTicket`, each of the 10 call sites must duplicate the exact same `if (exitTime < entryTime)` and bounds checks.
3. **Fragility Under Rule Changes**: If the university updates validation logic (e.g., license plates must be 6 to 10 uppercase characters), all 10 call sites must be tracked down and rewritten. A single missed site creates a security vulnerability.

#### Complexity Analysis:

| Dimension | Encapsulated (Private + Mutator) | Public Fields + Caller Validation |
| :--- | :--- | :--- |
| **Validation Cost per Update** | **$O(1)$ centralized check** executed inside the mutator. | **$O(1)$ check**, but executed across $M$ different call sites. |
| **Places Validation Repeated** | **Exactly 1 place** (inside the class definition). | **$M$ places** throughout calling code ($M \times \text{maintenance overhead}$). |
| **Defensive Overhead** | Zero caller boilerplate. | High boilerplate, high cognitive load for developers. |
| **Class Invariant Guarantee** | **Guaranteed valid at all times**. | **Zero guarantee** (subject to human caller error). |

**Verdict**: Encapsulation is non-negotiable for system integrity, audit reliability, and maintainability.
