package Q1_String_Performance;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility to generate realistic Railway E-Ticket fields for benchmarking.
 */
public class TicketWorkload {

    public static List<String> generateFields(int fieldCount, int ticketIndex) {
        List<String> fields = new ArrayList<>(fieldCount);
        fields.add("PNR: " + (2458901230L + ticketIndex));
        fields.add("TRAIN_NO: 12675");
        fields.add("TRAIN_NAME: KOVAI_SF_EXP");
        fields.add("FROM: CHENNAI_CENTRAL_MAS");
        fields.add("TO: COIMBATORE_JN_CBE");
        fields.add("DATE_OF_JOURNEY: 2026-11-20");
        fields.add("CLASS: 3A");
        fields.add("QUOTA: GENERAL");
        fields.add("BOOKING_STATUS: CONFIRMED");
        fields.add("COACH: B3");
        fields.add("BERTH: 24 (SIDE_LOWER)");
        fields.add("BASE_FARE: INR 850.00");
        fields.add("RESERVATION_FEE: INR 40.00");
        fields.add("SUPERFAST_SURCHARGE: INR 45.00");
        fields.add("GST: INR 46.75");
        fields.add("TOTAL_FARE: INR 981.75");
        fields.add("PAYMENT_MODE: UPI_IRCTC_GATEWAY");
        fields.add("TRANSACTION_ID: TXN89230192389");

        // Fill remaining fields if requested fieldCount > predefined fields
        int current = fields.size();
        for (int i = current; i < fieldCount; i++) {
            fields.add("METADATA_FIELD_" + i + ": VAL_" + (ticketIndex * 31 + i));
        }

        // If fieldCount is smaller than default, trim
        if (fieldCount < fields.size()) {
            return new ArrayList<>(fields.subList(0, fieldCount));
        }

        return fields;
    }
}

