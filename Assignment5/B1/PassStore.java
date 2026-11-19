import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class PassStore {
    private final Path file;

    public PassStore(String fileName) {
        file = Paths.get(fileName);
    }

    public synchronized void add(Pass pass) throws IOException {
        Files.createDirectories(file.toAbsolutePath().getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(file,
                StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(pass.toString());
            writer.newLine();
        }
    }

    public Pass find(String passId) throws IOException {
        if (!Files.exists(file)) return null;
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",", -1);
                if (fields.length != 7) continue;
                try {
                    double amount = Double.parseDouble(fields[6].trim());
                    if (fields[0].trim().equals(passId)) {
                        return new Pass(fields[0].trim(), fields[1].trim(), fields[2].trim(),
                                fields[3].trim(), fields[4].trim(), fields[5].trim(), amount);
                    }
                } catch (NumberFormatException ignored) {
                    // Damaged amount; continue searching later records.
                }
            }
        }
        return null;
    }

    public void listByRoute(String route) throws IOException {
        if (!Files.exists(file)) return;
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",", -1);
                if (fields.length == 7 && fields[3].trim().equals(route)) {
                    try {
                        double amount = Double.parseDouble(fields[6].trim());
                        System.out.println(new Pass(fields[0].trim(), fields[1].trim(),
                                fields[2].trim(), fields[3].trim(), fields[4].trim(),
                                fields[5].trim(), amount));
                    } catch (NumberFormatException ignored) {
                        // Skip the damaged record.
                    }
                }
            }
        }
    }
}
