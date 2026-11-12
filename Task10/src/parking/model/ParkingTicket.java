package parking.model;

import java.time.LocalDateTime;

public class ParkingTicket {
    private final String ticketId;
    private final String regNo;
    private final int floor;
    private final int slot;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double fee;

    public ParkingTicket(String ticketId, String regNo, int floor, int slot, LocalDateTime entryTime) {
        if (ticketId == null || ticketId.isBlank()) {
            throw new IllegalArgumentException("Ticket ID cannot be null or blank.");
        }
        if (regNo == null || regNo.isBlank()) {
            throw new IllegalArgumentException("Vehicle registration cannot be null or blank.");
        }
        if (floor < 0 || slot < 0) {
            throw new IllegalArgumentException("Floor and slot coordinates must be non-negative.");
        }
        this.ticketId = ticketId;
        this.regNo = regNo;
        this.floor = floor;
        this.slot = slot;
        this.entryTime = (entryTime != null) ? entryTime : LocalDateTime.now();
    }

    public String getTicketId() { return ticketId; }
    public String getRegNo() { return regNo; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public double getFee() { return fee; }

    // Validating setter
    public void recordExit(LocalDateTime exitTime, double fee) {
        if (exitTime == null) {
            throw new IllegalArgumentException("Exit time cannot be null.");
        }
        if (exitTime.isBefore(this.entryTime)) {
            throw new IllegalArgumentException("Exit time cannot be prior to entry time.");
        }
        if (fee < 0.0) {
            throw new IllegalArgumentException("Calculated fee cannot be negative.");
        }
        this.exitTime = exitTime;
        this.fee = fee;
    }
}
