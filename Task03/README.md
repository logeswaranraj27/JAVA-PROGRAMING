# Task 3: Control Logic & Pseudocode (ParkingFacility)

## 1. Algorithmic Pseudocode (SIMATS Engineering Standard)

### Algorithm 1: Slot Allocation (`allocate`)
```text
ALGORITHM AllocateSlot(slots, vehicleNo)
    INPUT: 
        slots       : Jagged 2D array of String representing floors and slot assignments
        vehicleNo   : String containing vehicle license plate number
    OUTPUT: 
        Array [floor, slot] of allocated coordinates, or NULL if facility is full

BEGIN
    FOR floorIndex ← 0 TO LENGTH(slots) - 1 DO
        FOR slotIndex ← 0 TO LENGTH(slots[floorIndex]) - 1 DO
            IF slots[floorIndex][slotIndex] IS NULL THEN
                slots[floorIndex][slotIndex] ← vehicleNo
                RETURN [floorIndex, slotIndex]
            END IF
        END FOR
    END FOR

    RETURN NULL
END
```

---

### Algorithm 2: Slot Deallocation (`release`)
```text
ALGORITHM ReleaseSlot(slots, floor, slot)
    INPUT: 
        slots : Jagged 2D array of String representing facility slots
        floor : Integer representing target floor level
        slot  : Integer representing target slot index
    OUTPUT: 
        Boolean: TRUE if successfully released, FALSE if invalid coordinate or already free

BEGIN
    // Validate floor boundary
    IF floor < 0 OR floor >= LENGTH(slots) THEN
        RETURN FALSE
    END IF

    // Validate slot boundary on given floor
    IF slot < 0 OR slot >= LENGTH(slots[floor]) THEN
        RETURN FALSE
    END IF

    // Check if slot is already empty
    IF slots[floor][slot] IS NULL THEN
        RETURN FALSE
    END IF

    // Release the slot
    slots[floor][slot] ← NULL
    RETURN TRUE
END
```

---

### Algorithm 3: Parking Fee Computation (`calculateFee`)
```text
ALGORITHM ComputeParkingFee(minutes, hourlyRate, hasMonthlyPass, isEV, kwhConsumed, chargingUnitRate)
    INPUT:
        minutes          : Total parked duration in minutes (Integer >= 0)
        hourlyRate       : Hourly tariff for vehicle category (Double)
        hasMonthlyPass   : Boolean flag indicating if user holds active monthly pass
        isEV             : Boolean flag indicating if vehicle is an Electric Vehicle
        kwhConsumed      : Integer units of electricity consumed (kWh >= 0)
        chargingUnitRate : Tariff per kWh (Double, e.g., Rs 12.0)
    OUTPUT:
        Double totalFee payable

BEGIN
    // Rule 1: Monthly pass holders park free of charge
    IF hasMonthlyPass IS TRUE THEN
        baseParkingFee ← 0.0
    ELSE
        // Rule 2: Every started hour is charged in full (Integer Ceiling)
        IF minutes = 0 THEN
            billableHours ← 0
        ELSE
            billableHours ← (minutes + 59) / 60
        END IF
        baseParkingFee ← billableHours * hourlyRate
    END IF

    // Rule 3: Separate EV charging calculation
    chargingFee ← 0.0
    IF isEV IS TRUE AND kwhConsumed > 0 THEN
        chargingFee ← kwhConsumed * chargingUnitRate
    END IF

    totalFee ← baseParkingFee + chargingFee
    RETURN totalFee
END
```

---

## 2. Control Flow Analysis

### 1. Slot Search and Termination
- The nested loops in `allocate()` iterate sequentially: outer loop over floors ($f = 0 \dots F-1$), inner loop over slots ($s = 0 \dots S_f-1$).
- **Early Termination**: As soon as a slot satisfies `slots[f][s] == null`, the vehicle registration is written to that memory location, and the function terminates **immediately** via `return new int[]{f, s}`.
- This short-circuits the search, preventing unnecessary loop iterations through remaining slots or upper floors.

### 2. Expressing Fee Slabs
Fee slabs can be structured using chained decision trees:
```java
public double computeSlabFee(int hours) {
    if (hours <= 0) return 0.0;
    if (hours == 1) return 30.0;                 // First hour slab
    if (hours <= 5) return 30.0 + (hours - 1) * 20.0; // Hours 2 to 5 discount slab
    return 110.0 + (hours - 5) * 15.0;           // Extended stay slab
}
```

### 3. Loop Control Statements: `break`, `continue`, `return`
- `return`: Exits both loops instantly upon locating the first free slot, saving CPU cycles.
- `break`: Can terminate the inner loop if an entire floor is flagged as closed/reserved.
- `continue`: Skips reserved handicap or EV slots if the entering vehicle does not match criteria.

### 4. Facility Full Handling
- If the nested loops exhaust all rows and columns without encountering an empty slot, execution drops out of the loop construct and executes `return null`.
- The caller receives `null`, which alerts the entrance barrier to deny entry and illuminate the digital "FACILITY FULL" display board.

---

## 3. Alternative Concept and Complexity Analysis

### Comparison: Linear Scan vs. Free-Slot Stack & Counter per Floor

#### Current Linear Scan Architecture:
- Scans `slots[f][s]` sequentially on each arrival.
- **Time Complexity**:
  - `allocate()`: Best-case $O(1)$ (Ground floor slot 0 is empty); Worst-case $O(F \times S)$ (facility 99% full or completely full).
  - `release()`: $O(1)$ direct index validation.
- **Space Overhead**: $0$ extra auxiliary data structures beyond the base `slots[][]` array.

#### Alternative Architecture: Free-Slot Stack and Free Count per Floor
Suppose the facility maintains:
1. `int[] freeCount`: Free slot tally per floor.
2. `Stack<Integer>[] freeSlots`: A stack of available slot indices for each floor.

```java
// Allocation logic:
public int[] allocateOptimized(String vehicleNo) {
    for (int f = 0; f < slots.length; f++) {
        if (!freeSlots[f].isEmpty()) {
            int slot = freeSlots[f].pop();
            slots[f][slot] = vehicleNo;
            freeCount[f]--;
            return new int[]{f, slot};
        }
    }
    return null; // All stacks empty -> facility full
}

// Release logic:
public boolean releaseOptimized(int floor, int slot) {
    if (!isValid(floor, slot) || slots[floor][slot] == null) return false;
    slots[floor][slot] = null;
    freeSlots[floor].push(slot);
    freeCount[floor]++;
    return true;
}
```

#### Complexity Comparison Matrix:

| Metric | Linear Scan (Current) | Free-Slot Stack & Counter (Alternative) |
| :--- | :--- | :--- |
| **`allocate()` Time** | Worst-case: $O(F \times S)$, Average: $O(F \times S / 2)$ | **Guaranteed $O(F)$ worst-case, $O(1)$ amortized** |
| **`release()` Time** | $O(1)$ | $O(1)$ |
| **Auxiliary Space** | **$O(1)$ extra space** | **$O(F \times S)$ pointers/nodes for $F$ Stack objects** |
| **Memory per slot** | 0 bytes additional | ~24 to 32 bytes per integer node in Java heap |
| **Suitability** | Ideal for small/medium facilities ($\le 500$ slots) | Essential for mega-structures (5000+ slots, airports) |
