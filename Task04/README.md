# Task 4: Classes, Objects, and Constructors

## 1. Fields and Methods Overview

### Class: `ParkingTicket`
- **Fields**:
  - `private final String ticketId`: Unique identifier (e.g., `"TCK-1001"`).
  - `private final String regNo`: License plate string.
  - `private final int floor`: Floor index.
  - `private final int slot`: Slot index.
  - `private final LocalDateTime entryTime`: Entry timestamp.
- **Methods**:
  - Validated getters: `getTicketId()`, `getRegNo()`, `getFloor()`, `getSlot()`, `getEntryTime()`.
  - Display formatting: `toString()`.

---

## 2. Constructor Types & Constructor Chaining (`this()`)

### Default vs Parameterized vs Overloaded Constructors
1. **Default Constructor (`ParkingTicket()` without parameters)**:
   - Sets fields to defaults (`null`, `0`).
   - Risk: Leaves the ticket in an invalid state (missing vehicle number or coordinates). For entities with mandatory identifiers, a public no-arg constructor breaks domain invariants.
2. **Parameterized Constructor**:
   - Takes mandatory parameters (`regNo`, `floor`, `slot`, `entryTime`) and validates them prior to assigning to final fields.
3. **Overloaded Constructors & Chaining with `this(...)`**:
   - The primary constructor acts as the single source of truth for validation.
   - Secondary constructors chain to the primary constructor using `this(...)`:
     ```java
     public ParkingTicket(String regNo, int floor, int slot) {
         this(null, regNo, floor, slot, LocalDateTime.now());
     }
     ```
   - **Advantage**: Prevents duplicating argument validation logic across multiple constructor bodies. Ensures consistent object state.

---

## 3. Copy Constructor
- **Definition**: A constructor that accepts an existing object of the same class to create an independent copy:
  ```java
  public ParkingTicket(ParkingTicket original) {
      this.ticketId = original.ticketId + "-DUP";
      this.regNo = original.regNo;
      this.floor = original.floor;
      this.slot = original.slot;
      this.entryTime = original.entryTime;
  }
  ```
- **Domain Need**:
  If a customer misplaces their printed parking receipt or requests an official duplicate copy at the exit booth, the system issues a verified duplicate ticket object without creating an independent parking transaction or altering the active record in the database.

---

## 4. JVM Memory Storage: Stack vs Heap

When executing:
```java
ParkingTicket t1 = new ParkingTicket("TN01AA1111", 0, 1);
```
1. **Stack Memory**:
   - Holds the reference variable `t1` (a 64-bit pointer or 32-bit compressed reference on the thread stack frame).
   - Holds primitive method parameters (`int floor`, `int slot`) during constructor execution.
2. **Heap Memory**:
   - Holds the instantiated `ParkingTicket` object allocated by the `new` operator.
   - Contains:
     - 12-byte Object Header (Mark Word + Klass Word under Compressed OOPs) + 4-byte padding.
     - 4-byte reference to `ticketId` String.
     - 4-byte reference to `regNo` String.
     - 4-byte primitive `int floor`.
     - 4-byte primitive `int slot`.
     - 4-byte reference to `LocalDateTime entryTime`.
   - The String literal `"TN01AA1111"` and `LocalDateTime` instances reside in their respective heap locations, referenced by pointers inside `ParkingTicket`.
3. **Reference Dereferencing**: `t1.getRegNo()` dereferences the stack pointer `t1` to locate the object on the heap, then follows its internal pointer to read the String value.

---

## 5. Alternative Concept and Complexity Analysis

### Comparison: Overloaded Constructors/Chaining vs. Default Constructor + Setters (JavaBeans) vs. Static Factory Methods

#### 1. Default Constructor + Setter Calls:
```java
ParkingTicket ticket = new ParkingTicket();
ticket.setRegNo("TN01AA1111");
// If ticket.setFloor(0) or ticket.setSlot(1) is omitted accidentally:
// Object remains in an incomplete, partially initialized state!
```
- **Object Invariant Guarantee**: **None**. At line 1, the object exists in an invalid state. In a multithreaded environment or in the event of an unhandled runtime exception during intermediate setter execution, a corrupted object is leaked into memory.
- **Mutability**: Fields cannot be `final`, destroying thread safety and data integrity.

#### 2. Overloaded Constructors with Chaining (`this()`):
- **Object Invariant Guarantee**: **Absolute**. Fields can be marked `final`. The constructor either completes successfully with all invariants satisfied or throws an exception and no reference is escaped.

#### 3. Static Factory Methods (`ParkingTicket.createTicket(...)`):
- Provides descriptive naming (`createDuplicate()`, `createWithAutoTime()`).
- Can return cached objects or subclasses without client modification.

#### 4. Time and Space Cost for Creating $n$ Objects

| Approach | Time Complexity to Create $n$ Objects | Heap Space Cost per Object | Invariant Safety |
| :--- | :--- | :--- | :--- |
| **Overloaded Constructors (`this()`)** | $O(n)$ ($n$ allocations + $n$ constructor calls) | ~32 bytes (compact `final` fields, no overhead) | **Guaranteed valid upon exit** |
| **Default Constructor + Setters** | $O(k \cdot n)$ ($n$ allocations + $k \cdot n$ setter calls) | ~32 bytes (fields mutable; cannot be `final`) | **High risk of partial initialization** |
| **Static Factory Methods** | $O(n)$ ($n$ allocations + method overhead inlineable) | ~32 bytes (can leverage instance caching) | **Guaranteed valid upon exit** |
