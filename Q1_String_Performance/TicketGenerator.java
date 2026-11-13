package Q1_String_Performance;

import java.util.List;
import java.util.StringJoiner;

/**
 * Implements the different text generation approaches for Railway E-Ticket generation.
 */
public class TicketGenerator {

    /**
     * Approach 1: Naive String concatenation with '+' inside a loop.
     * Demonstrates O(n^2) character copying overhead and heavy intermediate object churn.
     */
    public static String buildTicketString(List<String> fields) {
        String result = "";
        for (String field : fields) {
            result = result + field + "\n";
        }
        return result;
    }

    /**
     * Approach 2: StringBuilder append in a loop.
     * Unsynchronized, dynamic buffer resizing with amortized O(1) appends, total O(n).
     */
    public static String buildTicketStringBuilder(List<String> fields) {
        StringBuilder sb = new StringBuilder();
        for (String field : fields) {
            sb.append(field).append("\n");
        }
        return sb.toString();
    }

    /**
     * Approach 3: StringBuffer append in a loop.
     * Synchronized, thread-safe dynamic buffer resizing. Incurs thread locking overhead.
     */
    public static String buildTicketStringBuffer(List<String> fields) {
        StringBuffer sb = new StringBuffer();
        for (String field : fields) {
            sb.append(field).append("\n");
        }
        return sb.toString();
    }

    /**
     * Alternative 1: String.join()
     */
    public static String buildTicketStringJoin(List<String> fields) {
        return String.join("\n", fields) + "\n";
    }

    /**
     * Alternative 2: StringJoiner
     */
    public static String buildTicketStringJoiner(List<String> fields) {
        StringJoiner sj = new StringJoiner("\n", "", "\n");
        for (String field : fields) {
            sj.add(field);
        }
        return sj.toString();
    }

    /**
     * Alternative 3: String.format()
     * Incurs heavy reflection, format string parsing, and regex tokenization overhead.
     */
    public static String buildTicketStringFormat(List<String> fields) {
        StringBuilder formatBuilder = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            formatBuilder.append("%s\n");
        }
        return String.format(formatBuilder.toString(), fields.toArray());
    }
}

