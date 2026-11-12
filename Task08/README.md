# Task 8: Polymorphism (FeeDemo Analysis)

## 1. Compile-Time vs. Runtime Polymorphism

### 1. Compile-Time Polymorphism (Method Overloading)
- **Concept**: Occurs when multiple methods within the same class share the exact same method name but possess distinct parameter signatures (different number, types, or order of arguments).
- **Resolution**: Resolved statically at compile-time by the Java compiler based on the static declared type of arguments passed.
- **Example in Parking System**:
  ```java
  public double calculateFee(int minutes) { ... }
  public double calculateFee(int minutes, boolean isHolidaySurge) { ... }
  public double calculateFee(LocalTime entryTime, LocalTime exitTime) { ... }
  ```
  When `calculateFee(130)` is called, the compiler inspects the single integer literal `130` and binds the call directly to `calculateFee(int)`. No runtime method lookup overhead is incurred.

### 2. Runtime Polymorphism (Dynamic Method Dispatch)
- **Concept**: Occurs when a method call on a superclass reference variable is dynamically resolved at runtime to the overriding method of the actual instantiated subclass object on the heap.
- **Resolution**: Resolved at runtime via the JVM virtual method table (vtable).
- **Example in `FeeDemo`**:
  ```java
  Vehicle[] parked = { new TwoWheeler("TN01A1"), new Car("TN02B2"), new ElectricCar("TN03C3") };
  for (Vehicle v : parked) {
      double fee = v.calculateFee(130);
  }
  ```
  The array declared type is `Vehicle[]` (upcasting). At runtime, when `v.calculateFee(130)` executes, the JVM checks the actual runtime object `v` points to:
  - For `TwoWheeler`: invokes `ratePerHour()` returning Rs 10.0 $\rightarrow 3 \times 10 = \text{Rs } 30.0$.
  - For `Car`: invokes `ratePerHour()` returning Rs 30.0 $\rightarrow 3 \times 30 = \text{Rs } 90.0$.
  - For `ElectricCar`: inherits `ratePerHour()` from `Car` (Rs 30.0) $\rightarrow \text{Rs } 90.0$, then pattern matching `v instanceof Chargeable c` executes `c.chargingFee(10)` $\rightarrow 10 \times 12 = \text{Rs } 120.0$, totaling **Rs 210.0**.

---

## 2. Dynamic Method Dispatch Trace

Let us trace the call:
```java
Vehicle v = new ElectricCar("TN03C3");
double fee = v.calculateFee(130);
```

1. **Declared (Static) Type**: `Vehicle`
2. **Actual Runtime (Dynamic) Type**: `ElectricCar`
3. **Dispatch Trace**:
   - The bytecode generated is `invokevirtual Vehicle.calculateFee:(I)D`.
   - The JVM retrieves the reference from local variable `v` and inspects the object header's klass word pointing to `ElectricCar.class` in Metaspace.
   - `calculateFee(int minutes)` is defined in `Vehicle`. It executes `hours = (130 + 59) / 60 = 3`.
   - Next, `hours * ratePerHour()` is evaluated. Inside `calculateFee`, `this.ratePerHour()` is an `invokevirtual` call.
   - The JVM inspects the vtable of `ElectricCar`.
   - `ElectricCar` does not override `ratePerHour()`, so resolution walks up to its immediate superclass `Car`.
   - `Car.ratePerHour()` is found, returning `30.0`.
   - Calculation: $3 \times 30.0 = 90.0$.
4. **Interface Dispatch**:
   - `v instanceof Chargeable c` evaluates to `true`.
   - `c.chargingFee(10)` is invoked via `invokeinterface`.
   - The JVM looks up `ElectricCar.chargingFee(int)`, returning $10 \times 12.0 = 120.0$.
   - Total fee: $90.0 + 120.0 = \text{Rs } 210.0$.

---

## 3. Explanation of Why `equals` Prints `true`

At the conclusion of `FeeDemo`:
```java
System.out.println(new Car("TN02B2").equals(new ElectricCar("TN02B2")));
```
**Output**: `true`

### Why Does It Print `true`?
The `equals` method implementation in `Vehicle` states:
```java
@Override
public boolean equals(Object o) {
    return o instanceof Vehicle && regNo.equals(((Vehicle) o).regNo);
}
```
1. `o` is an instance of `ElectricCar`. Since `ElectricCar` extends `Car`, which extends `Vehicle`, `o instanceof Vehicle` evaluates to `true`.
2. Both objects have the registration number `"TN02B2"`.
3. `regNo.equals(((Vehicle) o).regNo)` compares `"TN02B2"` with `"TN02B2"`, which evaluates to `true`.
4. Therefore, the method returns `true`.

### Is This the Correct Behaviour for the System?
**Yes, from a domain modelling perspective**:
In a real-world motor vehicle registry and parking management system, a state registration number is a globally unique legal identifier for a physical vehicle chassis. No two vehicles in the physical world can legitimately hold the exact same license plate number.
If a vehicle enters the facility as a `Car` and another check-in is attempted for an `ElectricCar` bearing the exact same registration number `"TN02B2"`, treating them as equal is essential to prevent fraudulent duplicate check-ins or system desynchronization.

*(Note: If strict identical-class type equivalence were desired, the check would use `if (o == null || getClass() != o.getClass()) return false;`. However, for domain-level vehicular identity, equality by registration number across the `Vehicle` hierarchy is accurate).*

---

## 4. Alternative Concept and Complexity Analysis

### Comparison: Dynamic Method Dispatch vs. `instanceof` Cascades and Downcasting

#### The `instanceof` Anti-Pattern:
```java
public double calculateFeeWithInstanceOf(Vehicle v, int minutes) {
    int hours = (minutes + 59) / 60;
    if (v instanceof TwoWheeler) {
        return hours * 10.0;
    } else if (v instanceof ElectricCar) {
        return (hours * 30.0) + ((ElectricCar) v).chargingFee(10);
    } else if (v instanceof Car) {
        return hours * 30.0;
    } else {
        throw new IllegalArgumentException("Unknown vehicle subtype");
    }
}
```

#### Comparison Matrix:

| Metric | Dynamic Method Dispatch | `instanceof` Cascades & Casting |
| :--- | :--- | :--- |
| **Execution Time for $k$ Types** | **Guaranteed $O(1)$ constant time** (direct vtable array index indexing). | **$O(k)$ linear time** in the worst case, as every `instanceof` test must be evaluated sequentially. |
| **Code Fragility** | **Zero fragility**. Adding `Bus` or `Truck` requires zero changes to existing dispatch loops. | **High fragility**. Order of checks matters (`ElectricCar` must precede `Car` or it gets shadowed!). |
| **Code Size & Maintenance** | Minimal boilerplate. Business rules live inside their respective classes. | Massive duplication. Every billing, receipt, and display routine replicates the same $k$-branch `if-else` cascade. |
| **Extensibility (OCP)** | Fully adheres to Open-Closed Principle. | Blatantly violates Open-Closed Principle. |

**Verdict**: Dynamic method dispatch eliminates brittle type checking, guarantees $O(1)$ dispatch time, and ensures that subclass additions never impact existing billing pipelines.
