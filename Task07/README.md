# Task 7: Method Overriding and Object Methods

## 1. Overridden Methods and Subclass Specialization

### Methods Overridden in the Domain Model:
1. `protected abstract double ratePerHour()`:
   - Defined in abstract superclass `Vehicle`.
   - Overridden in `TwoWheeler` to return Rs 10.0 and in `Car` to return Rs 30.0.
   - **Why Subclass Version is Needed**: Physical properties (parking footprint, floor ramp clearance) dictate varying tariff slabs. Each concrete class knows its own billing rate without cluttering the superclass with conditional logic.
2. `public double calculateFee(int minutes)`:
   - Implemented as a template method in `Vehicle`, but can be overridden in specialized categories (e.g., flat-rate e-bikes or delivery vans with time-of-day multipliers).

---

## 2. The `@Override` Annotation and Overriding Rules

The `@Override` annotation instructs the Java compiler to verify that the annotated method actually overrides a method from a superclass or interface. If a developer typos the name (`rateperhour()`) or alters the parameter list (`calculateFee(double min)`), the compiler generates a compile-time error immediately rather than silently creating an overloaded method.

### Fundamental Rules of Method Overriding:
1. **Method Signature**: Method name, parameter count, and parameter types must be identical.
2. **Return Type**: Must be identical or a **covariant subtype** (e.g., returning a subclass type is permitted).
3. **Access Level**: The overriding method cannot reduce visibility (e.g., a `protected` superclass method can be overridden as `protected` or `public`, but never `private` or package-private).
4. **Exception Specification**: An overriding method cannot throw new or broader checked exceptions than the superclass method.
5. **Final and Static Prohibition**: Methods declared `final` or `static` cannot be overridden.

---

## 3. Appropriate Use of the `final` Keyword

1. **`final` Variable / Field**:
   - `private final String regNo`: A vehicle's government-issued registration number is immutable. Marking it `final` guarantees that once assigned in the constructor, its value can never be reassigned.
2. **`final` Method**:
   - `public final String getRegNo()`: Prevents subclasses from overriding the accessor and returning fraudulent or shadowed identifiers.
3. **`final` Class**:
   - If a class (such as a utility class or security credential validator) should never be subclassed, marking it `final` prevents subclassing and preserves encapsulation.

---

## 4. Proper Overrides of `toString()`, `equals()`, and `hashCode()`

### Equivalence Contract for Vehicles
In the physical world, two vehicles are identical if and only if they bear the exact same state registration number.
```java
@Override
public boolean equals(Object o) {
    if (this == o) return true; // 1. Reflexivity
    if (!(o instanceof Vehicle other)) return false; // 2. Null check & type validation
    return this.regNo.equalsIgnoreCase(other.regNo); // 3. Equivalence by regNo
}

@Override
public int hashCode() {
    return regNo.toUpperCase().hashCode();
}

@Override
public String toString() {
    return getClass().getSimpleName() + "[RegNo=" + regNo + ", Rate=Rs " + ratePerHour() + "/hr]";
}
```

### Why Both `equals` and `hashCode` Must Be Overridden Together:
The Java Collections Framework mandates: **"If two objects are equal according to `equals(Object)`, their `hashCode()` must produce the same integer result."**
If `equals()` is overridden based on `regNo` but `hashCode()` is left as `Object.hashCode()` (which computes hashes based on memory addresses), adding a vehicle to a `HashSet` or looking up a vehicle in a `HashMap` will fail. Two distinct instances representing the same vehicle number would hash to different buckets, resulting in duplicate check-ins and untracked entries!

---

## 5. Alternative Concept and Complexity Analysis

### Comparison: Dynamic Method Dispatch vs. Single `calculateFee()` with `switch` on Type

#### The Procedural `switch` Approach:
Instead of overriding `ratePerHour()` in subclasses, the superclass `Vehicle` contains:
```java
public double calculateFeeProcedural(int minutes) {
    int hours = (minutes + 59) / 60;
    switch (this.type) {
        case "TWO_WHEELER":
            return hours * 10.0;
        case "CAR":
            return hours * 30.0;
        case "ELECTRIC_CAR":
            return hours * 30.0;
        default:
            throw new IllegalArgumentException("Unknown type: " + this.type);
    }
}
```

#### Comparison Matrix:

| Metric | Dynamic Method Dispatch (Overriding) | Monolithic `switch` Statement |
| :--- | :--- | :--- |
| **Adding a New Vehicle Type** | **Zero modifications** to existing classes. Author a new subclass `Bus extends Vehicle`. Adheres to Open-Closed Principle (OCP). | **Modifies existing tested class**. Developer must open `Vehicle.java`, edit the `switch` block, and re-test all existing branches. |
| **Classes Modified on New Type** | **0 existing classes modified** (1 new class added). | **1 core class modified and recompiled** (violates OCP). |
| **Execution Time for $k$ Types** | **$O(1)$ constant time vtable lookup** regardless of how many types ($k$) exist. | **$O(1)$ to $O(k)$** branch comparisons depending on bytecode switch implementation (`lookupswitch` vs `tableswitch`). |
| **Maintainability & Coupling** | Highly decoupled and modular. Each subclass encapsulates its own business rules. | Tightly coupled. A single bug or missing `break` in the `switch` cascades to all vehicle calculations. |

**Verdict**: Method overriding provides strictly superior extensibility, $O(1)$ dispatch time, and adheres to the SOLID Open-Closed Principle.
