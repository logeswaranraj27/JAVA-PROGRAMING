# Task 5: Static Members and Object Life Cycle

## 1. Static vs Instance Members

### 1. Where Static Variables are Needed
- **Ticket Serial Counter (`private static int ticketCounter`)**:
  - A ticket counter must generate monotonically increasing, globally unique identifiers across the entire application lifecycle.
  - **Why an instance variable fails**: If the counter were an instance variable inside `ParkingTicket`, each new ticket object instantiated would initialize its own counter to $0$ or $1$. Every ticket issued would get identical ticket number `TCK-1001`, violating the uniqueness business rule!
- **Total Facility Occupancy (`private static int totalOccupancy`)**:
  - Aggregates live occupancy across all floors and gates. It belongs to the parking subsystem as a whole, rather than any individual car, ticket, or slot.

### 2. Static Methods & Static Initialization Blocks
- **Static Methods**: E.g., `ParkingFacility.getSystemOccupancy()`, `MathUtils.roundFee(double fee)`. These methods perform class-level operations without requiring an instance reference (`this`).
- **Static Blocks (`static { ... }`)**:
  - Executes exactly once when the class is loaded into memory by the JVM ClassLoader, prior to any constructor invocation.
  - Used for one-time initialization, such as reading configuration files, initializing hardware barrier controllers, or pre-computing tariff matrix tables.

---

## 2. Object Lifecycle & Garbage Collection (GC)

### What Happens to a `ParkingTicket` Object Upon Departure?
1. **Creation**: When a vehicle enters, `new ParkingTicket(...)` is instantiated on the heap. A reference is stored in the facility's active tracking structure (or jagged array).
2. **Active Use**: While parked, the object is referenced by active roots (facility array, entry ledger).
3. **Release**: When the vehicle exits, `slots[floor][slot] = null;` removes the array reference.
4. **Unreachability**: Once the printed checkout summary is displayed and the local method reference leaves scope, no live reference from any GC root (thread stack frame, static field) points to that `ParkingTicket` object.
5. **Garbage Collection Eligibility**:
   - The JVM Garbage Collector (e.g., G1 GC or ZGC) traverses live references using the Mark phase.
   - The unreferenced `ParkingTicket` object is identified as unreachable garbage.
   - During the Sweep phase, the memory allocated on the heap is reclaimed and made available for future allocations.

---

## 3. Passing and Returning Objects in Java

- **Java is strictly Pass-by-Value**:
  - When an object reference (`ParkingTicket ticket`) is passed into a method, the *value of the reference pointer* (the memory address of the heap object) is copied onto the method's stack frame.
  - **Mutations Persist**: Invoking mutator methods on the object (e.g., `ticket.setExitTime(...)`) mutates the actual heap object, and those changes are visible to the caller.
  - **Reassignments are Local**: Reassigning the parameter itself (`ticket = new ParkingTicket(...)`) merely modifies the local stack variable; the caller's reference continues pointing to the original heap object.
- **Returning Objects**:
  - A method returns the 64-bit reference address pointing to the heap object. The object remains alive on the heap as long as the receiving caller stores the returned reference.

---

## 4. Alternative Concept and Complexity Analysis

### Comparison: Static Ticket Counter vs. Facility Instance Counter in a Multi-Facility Setup

#### Scenario: The university runs two facilities (North Gate and South Gate).

#### Case A: Counter is an Instance Variable of `ParkingFacility`
- Each `ParkingFacility` maintains its own `private int localCounter = 0;`.
- **Behavior**:
  - North Gate issues: `TICKET-1`, `TICKET-2`, `TICKET-3`.
  - South Gate issues: `TICKET-1`, `TICKET-2`, `TICKET-3`.
- **The Problem**: Duplicate ticket numbers exist simultaneously across campus. If a patron enters at North Gate and exits at South Gate's central payment kiosk, scanning `TICKET-1` creates ambiguity (ticket collision).
- **Workaround Solution**: Must prefix ticket IDs with facility identifier: `"NG-" + (++localCounter)` vs `"SG-" + (++localCounter)`.

#### Case B: Counter is a Static Variable (Global Sequence)
- `ParkingFacility` or `ParkingTicket` maintains `private static int globalCounter = 0;`.
- **Behavior**:
  - North Gate issues: `TCK-1001`.
  - South Gate issues: `TCK-1002`.
  - North Gate issues: `TCK-1003`.
- **Advantage**: Monotonically unique campus-wide serial sequence. Simplifies billing, audit trails, and multi-gate cross-exit operations. In multithreaded systems, implemented via `java.util.concurrent.atomic.AtomicInteger`.

#### Space Complexity Analysis for $k$ Facilities and $n$ Tickets:
- **Static Counter**:
  - Stored in the Class Metadata / Metaspace area: Exactly 4 bytes (for `int`) for the entire JVM.
  - Total Space: $O(1)$ constant memory, regardless of $k$ facilities or $n$ tickets.
- **Instance Counter per Facility**:
  - Stored inside each facility object: $k \times 4$ bytes.
  - Total Space: $O(k)$ memory.
- **Verdict**: For multi-gate campus parking with unified auditing, a centralized static counter or atomic sequence generator ensures ticket uniqueness and global consistency.
