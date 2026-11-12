# Task 9: Introduce Abstraction

## 1. Abstract Classes vs. Interfaces in the Parking Architecture

### 1. Abstract Class: `Vehicle`
- **Why Abstract Class?**
  - Represents the fundamental **core identity ("IS-A")** of all motor vehicles entering the facility.
  - **Maintains Common State**: Vehicles have concrete, immutable state (`private final String regNo`) and constructors that guarantee this state is initialized at birth. Interfaces cannot hold instance state or constructors.
  - **Shared Template Behavior**: Implements shared, non-trivial methods (`calculateFee(int minutes)`, `equals()`, `hashCode()`) while leaving `ratePerHour()` abstract to force subclass specialization.
- **Abstract Methods in `Vehicle`**:
  - `protected abstract double ratePerHour();`: Forces subclasses to declare their category-specific hourly rate.

### 2. Interface: `Chargeable`
- **Why Interface?**
  - Represents an **orthogonal capability ("CAN-DO")** rather than core identity.
  - Not all vehicles are chargeable; only EVs possess battery charging capability.
  - Unrelated entities (such as portable electric battery carts, electric lawnmowers, or external campus shuttles) could also implement `Chargeable` without being a subclass of `Vehicle`.
- **Other Potential Interfaces**:
  - `Payable`: For fee-paying users or automated smart-wallet payment gateways.
  - `Trackable`: For RFID transponder scanning at entry/exit gates.

---

## 2. Obtaining Multiple Behaviors Through Interfaces

Java strictly prohibits multiple class inheritance (a class cannot `extend` more than one class, avoiding the Deadly Diamond of Death). However, Java allows a class to implement **multiple interfaces**:
```java
public class ElectricCar extends Car implements Chargeable, Payable, Trackable {
    // Possesses vehicular state from Vehicle
    // Implements electric charging via Chargeable
    // Implements fee settlement via Payable
}
```
This enables flexible, decoupled polymorphism. A barrier gate can treat an `ElectricCar` as a `Vehicle`, a charging station controller treats it as a `Chargeable`, and the university finance office treats it as a `Payable`.

---

## 3. Default and Static Methods in Interfaces (Java 8+)

1. **Default Methods (`default double calculateTotalChargingCost(...)`)**:
   - Allows interface designers to add new capabilities to interfaces with concrete implementations without breaking existing implementors.
   - Example: Calculating charging tax or green subsidies directly inside `Chargeable`.
2. **Static Methods (`static double getCleanEnergyCessRate()`)**:
   - Serves as utility/helper methods closely bound to the interface's domain, avoiding external standalone utility classes.

---

## 4. Alternative Concept and Complexity Analysis

### Comparison: If `Vehicle` Were an Interface and `Chargeable` an Abstract Class

#### What Happens Under the Inverted Design?
1. **Loss of State and Constructors in `Vehicle`**:
   - In Java, interfaces **cannot have instance variables** (all interface fields are implicitly `public static final` constants).
   - Interfaces **cannot have constructors**.
   - As a consequence, every single subclass (`TwoWheeler`, `Car`, `Bus`, `Truck`) would be forced to duplicate the declaration of `private String regNo;`, its constructor, getter, `equals()`, and `hashCode()` boilerplate!
2. **Failure of Multiple Inheritance with Abstract Class `Chargeable`**:
   - If `Chargeable` were an abstract class (`abstract class Chargeable`), and `Vehicle` were an abstract class or interface:
     - An `ElectricCar` that extends `Car` **could never extend `Chargeable`**, because Java permits extending only **one** class!
     - `ElectricCar` would be locked out of inheriting shared charging station mechanics.
3. **Method Dispatch Time and Space Cost**:
   - **Interface Dispatch (`invokeinterface`) vs Virtual Dispatch (`invokevirtual`)**:
     - Modern JVM HotSpot JIT compilers optimize both using inline caching (monomorphic and bimorphic call sites).
     - However, uncached `invokeinterface` requires traversing the interface method table (itable), which incurs a slight hash lookup overhead compared to direct vtable index indexing in class inheritance.
   - **Heap Memory**:
     - Implementing multiple interfaces does not increase instance heap memory (no extra pointer fields per interface unless state is manually added).

**Conclusion**: The chosen architecture (`abstract class Vehicle` for core identity and state + `interface Chargeable` for behavioral capability) is technically sound, cleanly avoids diamond inheritance, and prevents redundant code duplication.
