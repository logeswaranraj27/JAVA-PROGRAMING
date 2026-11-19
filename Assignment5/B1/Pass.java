public class Pass {
    public final String passId;
    public final String registerNumber;
    public final String name;
    public final String route;
    public final String boardingStop;
    public final String validityDate;
    public final double amount;

    public Pass(String passId, String registerNumber, String name, String route,
                String boardingStop, String validityDate, double amount) {
        this.passId = passId;
        this.registerNumber = registerNumber;
        this.name = name;
        this.route = route;
        this.boardingStop = boardingStop;
        this.validityDate = validityDate;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%.2f",
                passId, registerNumber, name, route, boardingStop, validityDate, amount);
    }
}
