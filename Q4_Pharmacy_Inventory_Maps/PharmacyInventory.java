package Q4_Pharmacy_Inventory_Maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PharmacyInventory {
    private final Map<String, Medicine> map;
    private final String mapType;

    public PharmacyInventory(Map<String, Medicine> map, String mapType) {
        this.map = map;
        this.mapType = mapType;
    }

    public void addMedicine(Medicine med) {
        map.put(med.getCode(), med);
    }

    public Medicine lookup(String code) {
        return map.get(code);
    }

    public int size() {
        return map.size();
    }

    /**
     * Report 1: Medicines in Code Order (Alphabetical / Natural key order).
     * Naturally provided by TreeMap in O(N).
     * For HashMap and LinkedHashMap, requires extracting and sorting: O(N log N).
     */
    public List<Medicine> generateCodeOrderReport() {
        if ("TreeMap".equals(mapType)) {
            // Natural code order iteration: O(N)
            return new ArrayList<>(map.values());
        } else {
            // Requires manual extraction and sorting: O(N log N)
            List<Medicine> list = new ArrayList<>(map.values());
            Collections.sort(list, Comparator.comparing(Medicine::getCode));
            return list;
        }
    }

    /**
     * Report 2: Medicines in the order they were added (Insertion sequence).
     * Naturally provided by LinkedHashMap in O(N).
     * For HashMap and TreeMap, requires secondary sequence sorting: O(N log N).
     */
    public List<Medicine> generateInsertionOrderReport() {
        if ("LinkedHashMap".equals(mapType)) {
            // Natural insertion order iteration: O(N)
            return new ArrayList<>(map.values());
        } else {
            // Requires manual extraction and sorting by additionSequence: O(N log N)
            List<Medicine> list = new ArrayList<>(map.values());
            Collections.sort(list, Comparator.comparingLong(Medicine::getAdditionSequence));
            return list;
        }
    }

    public static boolean verifyCodeOrder(List<Medicine> report) {
        for (int i = 0; i < report.size() - 1; i++) {
            if (report.get(i).getCode().compareTo(report.get(i + 1).getCode()) > 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean verifyInsertionOrder(List<Medicine> report) {
        for (int i = 0; i < report.size() - 1; i++) {
            if (report.get(i).getAdditionSequence() > report.get(i + 1).getAdditionSequence()) {
                return false;
            }
        }
        return true;
    }
}

