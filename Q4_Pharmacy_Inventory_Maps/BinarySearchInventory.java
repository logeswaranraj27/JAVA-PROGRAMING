package Q4_Pharmacy_Inventory_Maps;

import java.util.Arrays;

/**
 * Alternative Concept for Q4:
 * Sorted Array searched via Arrays.binarySearch()
 */
public class BinarySearchInventory {
    private Medicine[] data;
    private int size;

    public BinarySearchInventory(int capacity) {
        this.data = new Medicine[capacity];
        this.size = 0;
    }

    /**
     * Insertion into sorted array: O(N) due to binary search + shifting.
     */
    public void insert(Medicine m) {
        if (size == data.length) {
            data = Arrays.copyOf(data, data.length * 2);
        }
        int idx = Arrays.binarySearch(data, 0, size, m);
        if (idx < 0) {
            idx = -(idx + 1); // Insertion point
        }
        // Shift right
        System.arraycopy(data, idx, data, idx + 1, size - idx);
        data[idx] = m;
        size++;
    }

    /**
     * Lookup via binary search: O(log N).
     */
    public Medicine lookup(String code) {
        Medicine dummy = new Medicine(code, "", "", 0, 0.0, 0);
        int idx = Arrays.binarySearch(data, 0, size, dummy);
        if (idx >= 0) return data[idx];
        return null;
    }

    /**
     * Code order report: already in sorted order! O(N) sequential copy.
     */
    public Medicine[] getCodeOrderReport() {
        return Arrays.copyOf(data, size);
    }

    public int size() { return size; }
}
