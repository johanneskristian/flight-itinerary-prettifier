import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateFormatter {
    public static String formatDateTokens(String line) {
        // Match D(...), T12(...), or T24(...) with any ISO-8601 timestamp inside
        Pattern pattern = Pattern.compile("(D|T12|T24)\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(line);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String type = matcher.group(1); // The token type: D, T12, or T24
            String rawDate = matcher.group(2).replace("−", "-"); // Normalize unicode minus

            try {
                // Parse the timestamp into an OffsetDateTime (handles time zones)
                OffsetDateTime dateTime = OffsetDateTime.parse(rawDate);
                String formatted;

                // Format based on token type
                switch (type) {
                    case "D":
                        // Format as date
                        formatted = dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
                        break;
                    case "T12":
                        // Format as 12-hour time
                        formatted = dateTime.format(DateTimeFormatter.ofPattern("hh:mma")) +
                                " (" + formatOffset(dateTime) + ")";
                        break;
                    case "T24":
                        // Format as 24-hour time
                        formatted = dateTime.format(DateTimeFormatter.ofPattern("HH:mm")) +
                                " (" + formatOffset(dateTime) + ")";
                        break;
                    default:
                        // Should never happen, but just in case
                        formatted = matcher.group(0);
                }
                // Replace the token with the formatted string
                matcher.appendReplacement(result, Matcher.quoteReplacement(formatted));

            } catch (DateTimeParseException e) {
                // If the timestamp is invalid, leave it unchanged
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(0)));
            }
        }

        // Append any remaining text after the last match
        matcher.appendTail(result);
        return result.toString();
    }

    // Formats a time zone offset for display
    // Converts 'Z' (UTC) to '+00:00' for consistency.
    private static String formatOffset(OffsetDateTime dateTime) {
        String offset = dateTime.getOffset().toString();
        return offset.equals("Z") ? "+00:00" : offset;
    }
}
