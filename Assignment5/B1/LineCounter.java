import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class LineCounter {
    public static long countBuffered(Path file) throws IOException {
        long lines = 0;
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            while (reader.readLine() != null) lines++;
        }
        return lines;
    }

    public static long countUnbuffered(Path file) throws IOException {
        long lines = 0;
        try (InputStream input = Files.newInputStream(file)) {
            int value;
            while ((value = input.read()) != -1) {
                if (value == '\n') lines++;
            }
        }
        return lines;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java LineCounter <file>");
            return;
        }
        Path file = Paths.get(args[0]);
        long start = System.nanoTime();
        long lines = countBuffered(file);
        long bufferedMs = (System.nanoTime() - start) / 1_000_000;

        start = System.nanoTime();
        long unbufferedLines = countUnbuffered(file);
        long unbufferedMs = (System.nanoTime() - start) / 1_000_000;

        System.out.println("Buffered: " + lines + " lines in " + bufferedMs + " ms");
        System.out.println("Unbuffered: " + unbufferedLines + " lines in " + unbufferedMs + " ms");
    }
}
