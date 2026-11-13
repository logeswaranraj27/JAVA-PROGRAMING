package Q4_Pharmacy_Inventory_Maps;

import java.util.Objects;

public class Medicine implements Comparable<Medicine> {
    private final String code;
    private final String name;
    private final String batchNo;
    private int stockQuantity;
    private final double unitPrice;
    private final long additionSequence;

    public Medicine(String code, String name, String batchNo, int stockQuantity, double unitPrice, long additionSequence) {
        this.code = code;
        this.name = name;
        this.batchNo = batchNo;
        this.stockQuantity = stockQuantity;
        this.unitPrice = unitPrice;
        this.additionSequence = additionSequence;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getBatchNo() { return batchNo; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int qty) { this.stockQuantity = qty; }
    public double getUnitPrice() { return unitPrice; }
    public long getAdditionSequence() { return additionSequence; }

    @Override
    public int compareTo(Medicine other) {
        return this.code.compareTo(other.code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medicine)) return false;
        Medicine medicine = (Medicine) o;
        return Objects.equals(code, medicine.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (Stock: %d, Rate: $%.2f, Seq: %d)",
                code, name, stockQuantity, unitPrice, additionSequence);
    }
}

