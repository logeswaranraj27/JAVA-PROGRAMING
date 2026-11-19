import java.io.IOException;

public class PassStoreDemo {
    public static void main(String[] args) throws IOException {
        PassStore store = new PassStore("passes.txt");
        store.add(new Pass("P1001", "192511175", "Logeshwaran R", "R12",
                "Main Gate", "2026-12-31", 2500.00));
        store.add(new Pass("P1003", "192324302", "Barath", "R12",
                "Library", "2026-12-31", 2500.00));

        System.out.println(store.find("P1003"));
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(
                new java.io.FileWriter("passes.txt", true))) {
            writer.write("P1002,only,wrong,line");
            writer.newLine();
        }
        System.out.println(store.find("P1002"));
        store.listByRoute("R12");
    }
}
