package parking.model;

public abstract class Vehicle {
    private final String regNo;

    protected Vehicle(String regNo) {
        if (regNo == null || regNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number cannot be null or empty.");
        }
        this.regNo = regNo.trim().toUpperCase();
    }

    public final String getRegNo() {
        return regNo;
    }

    public abstract double ratePerHour();

    public double calculateFee(int minutes) {
        if (minutes < 0) {
            throw new IllegalArgumentException("Parking duration cannot be negative: " + minutes);
        }
        int hours = (minutes == 0) ? 0 : (minutes + 59) / 60;
        return hours * ratePerHour();
    }
}
