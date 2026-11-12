# Task 1: Problem Analysis and Candidate Classes

## 1. System Context & Overview
The University Campus Smart-Parking System is an automated, multi-level facility management platform designed to regulate vehicular entry, slot allocation, ticket issuance, fee calculation, and departure operations. The system caters to diverse campus stakeholders including students, faculty/staff, and visitors commuting via two-wheelers, conventional cars, and electric vehicles (EVs).

---

## 2. System Inputs and Outputs

### System Inputs
1. **Vehicle Registration Number (`regNo`)**: String representing unique vehicle license plate (e.g., `"TN01AB1111"`).
2. **Vehicle Category**: Type of vehicle entering (`TwoWheeler`, `Car`, `ElectricCar`).
3. **User / Stakeholder Category**: `Student`, `Staff`, or `Visitor`.
4. **Pass Credentials**: Valid monthly parking pass ID (if held by student or staff).
5. **Entry Timestamp**: Date and time of barrier gate entry.
6. **Exit Timestamp**: Date and time of barrier gate exit.
7. **Auxiliary Service Data**: Electric vehicle charging consumption (in kWh) if an EV utilizes a campus charging station.

### System Outputs
1. **Allocated Slot Coordinates**: 2D coordinate `{floor, slot}` indicating assigned spot (or `null` / error message if full).
2. **Parking Ticket**: Issued ticket record containing unique ticket ID, vehicle registration, assigned slot, and entry timestamp.
3. **Slot Release Confirmation**: Boolean status indicating successful release of slot upon exit.
4. **Duration of Stay**: Total elapsed time in minutes and rounded billable hours.
5. **Itemized Fee Summary**:
   - Base parking tariff (Rs 0.0 for valid monthly pass holders).
   - EV charging fee (e.g., Rs 12 per kWh consumed).
   - Total payable amount in Indian Rupees (Rs).
6. **Facility Metrics**: Current floor-wise and overall occupancy statistics.

---

## 3. Noun-Verb Analysis

| Category | Extracted Terms | Role in Object-Oriented Design |
| :--- | :--- | :--- |
| **Nouns** | `ParkingFacility`, `Floor`, `Slot`, `Vehicle`, `TwoWheeler`, `Car`, `ElectricCar`, `ParkingTicket`, `User`, `Student`, `Staff`, `Visitor`, `Pass`, `Tariff` | **Candidate Classes & Interfaces** |
| **Verbs** | `allocate`, `release`, `issueTicket`, `recordEntry`, `recordExit`, `calculateFee`, `chargeBattery`, `validatePass`, `isAvailable` | **Class Methods / Behaviors** |

---

## 4. Candidate Classes and Responsibilities

### 1. `ParkingFacility`
- **Responsibility**: Manages the multi-level parking structure. Maintains jagged array of slots per floor. Dispatches vehicles to nearest available slots using first-fit strategy, validates slot releases, and tracks total facility occupancy.

### 2. `Vehicle` (Abstract Superclass)
- **Responsibility**: Encapsulates common vehicular attributes (unique registration number), defines the abstract contract `ratePerHour()`, and implements template logic for duration-based billing (`calculateFee(int minutes)`). Overrides `equals()` and `hashCode()` based on registration number.

### 3. `TwoWheeler`, `Car`, `ElectricCar` (Concrete Subclasses)
- **Responsibility**: Specialize vehicle behaviors and provide category-specific hourly rates:
  - `TwoWheeler`: Rs 10.0 / hour.
  - `Car`: Rs 30.0 / hour.
  - `ElectricCar`: Rs 30.0 / hour parking rate, plus implements `Chargeable` interface to compute electric charging fees (Rs 12.0 / kWh).

### 4. `Chargeable` (Interface)
- **Responsibility**: Defines the charging capability `double chargingFee(int kWh)` for electric vehicles or charging stations without coupling to vehicle mechanics.

### 5. `ParkingTicket`
- **Responsibility**: Encapsulates the transactional state of a parking session: unique sequential ticket ID, vehicle reference, assigned floor and slot, entry time, exit time, and calculated total fee.

### 6. `User` / `ParkingPass`
- **Responsibility**: Models campus patrons (`Student`, `Staff`, `Visitor`). Evaluates pass validity; students and staff holding active monthly passes enjoy 100% parking fee waivers, whereas visitors pay full tariff.

---

## 5. Business Rules
1. **Single Occupancy Rule**: A parking slot cannot hold more than one vehicle simultaneously. Attempting to allocate an occupied slot must be rejected.
2. **Ticket Uniqueness**: Every issued parking ticket must have a globally unique, non-reusable ticket number generated monotonically.
3. **Temporal Validity**: Entry timestamp must strictly precede or equal exit timestamp ($\text{entryTime} \le \text{exitTime}$). Negative parking durations are invalid.
4. **Billing Slab Rule**: Every started hour is charged in full (mathematical ceiling of $\frac{\text{minutes}}{60}$). A parking duration of 61 minutes is billed as 2 full hours.
5. **Pass Holder Exemption**: Students and staff members presenting a valid, non-expired monthly pass receive a 100% parking fee exemption ($\text{baseFee} = 0.0$).
6. **EV Charging Separation**: Charging fees are computed based on actual units consumed ($\text{kWh} \times \text{rate}$) and billed in addition to base parking fees.
7. **Single Vehicle In-Facility Constraint**: A vehicle cannot occupy multiple slots at once. Checking in an already parked vehicle without an intermediate checkout is prohibited.

---

## 6. Assumptions Made
1. Floor numbers are 0-indexed ($0, 1, \dots, F-1$), and slot numbers on floor $f$ are 0-indexed ($0, 1, \dots, S_f-1$).
2. The slot allocation policy is deterministic first-fit: lower floor numbers are preferred, followed by lowest slot index on that floor.
3. Campus facility operates continuous 24/7 hours without overnight lockout penalties for standard pass holders.

---

## 7. Alternative Concept and Complexity Analysis

### Comparison: Object-Oriented Design vs. Primitive Parallel Arrays
Instead of creating encapsulated `Vehicle` and `ParkingTicket` objects, an alternative procedural design could store vehicle data in flat, parallel arrays:
```java
String[] regNos = new String[MAX_VEHICLES];
String[] types = new String[MAX_VEHICLES];
long[] entryTimes = new long[MAX_VEHICLES];
int[] floorIndices = new int[MAX_VEHICLES];
int[] slotIndices = new int[MAX_VEHICLES];
```

### 1. Finding a Vehicle by Registration Number
- **Object-Oriented Approach**:
  Vehicles or active tickets can be queried via encapsulated methods or an index collection.
  - Search Time Complexity: $O(N)$ with a sequential scan over `ParkingTicket` objects, or $O(1)$ when indexed in a `Map<String, ParkingTicket>`.
  - Type Safety: Strong. Returned reference is guaranteed to hold coordinated fields (`vehicle.getRegNo()`, `ticket.getSlot()`).
- **Parallel Arrays Approach**:
  Iterates through `regNos[i]` looking for a match:
  - Search Time Complexity: $O(N)$ linear scan.
  - Vulnerability: High risk of synchronization skew. If an element in `regNos` is removed or shifted during departure without simultaneously updating `types[i]`, `entryTimes[i]`, `floorIndices[i]`, and `slotIndices[i]`, the state becomes irreversibly corrupted.

### 2. Adding a New Vehicle Attribute (e.g., `batteryCapacity` or `userCategory`)
- **Object-Oriented Approach**: Add a private field to the target class (e.g., `private double batteryCapacity` in `ElectricCar`). All existing methods and signatures remain intact. Adheres to the Open-Closed Principle (OCP).
- **Parallel Arrays Approach**: Requires declaring a brand new array `double[] batteryCapacities = new double[MAX_VEHICLES]`, modifying every method signature across the application to accept the new array, and updating all shift/deletion routines.

### 3. Space Complexity & Memory Layout Comparison (per Vehicle)
- **Parallel Arrays**:
  - Requires 5 separate array references ($5 \times 8 = 40$ bytes) plus element slots ($5 \times 8 = 40$ bytes) + raw values.
  - Memory: ~48 to 64 bytes per entry. However, array sizes are typically pre-allocated to maximum capacity, causing substantial memory waste when occupancy is low ($O(\text{MAX\_CAPACITY})$ space).
- **Object-Oriented Design**:
  - Each `Vehicle` object has a standard 64-bit JVM object header (12 bytes with compressed OOPs + 4 bytes padding = 16 bytes), plus instance field references (8 bytes for `regNo` reference).
  - Total per vehicle: ~24–32 bytes heap overhead.
  - Dynamically allocated on-demand ($O(N)$ space proportional to actual active occupants).
  - **Verdict**: Object-Oriented design provides superior maintainability, memory elasticity, runtime safety, and modularity.
