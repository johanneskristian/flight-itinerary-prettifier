import java.util.*;
import java.util.regex.*;
import java.io.*;

public class Prettifier {
    public static void main(String[] args) {
        // Check command-line arguments or display help
        if (args.length < 3 || args[0].equals("-h")) {
            System.out.println("itinerary usage:\n$ java Prettifier.java ./input.txt ./output.txt ./airport-lookup.csv");
            return;
        }

        String inputPath = args[0];
        String outputPath = args[1];
        String csvPath = args[2];

        HashMap<String, String> airportMap = null;
        HashMap<String, String> cityMap = null;

        // Load airport and city lookup maps from CSV
        try {
            airportMap = CSVReader.loadcsvMap(csvPath, false);
            cityMap = CSVReader.loadcsvMap(csvPath, true);
        } catch (FileNotFoundException e) {
            System.out.println("Airport lookup not found");
            return;
        } catch (IllegalArgumentException e) {
            System.out.println("Airport lookup malformed"); // When the airport lookup is malformed, it displays "Airport lookup malformed"
            return;
        } catch (IOException e) {
            System.out.println("Error reading airport lookup");
            return;
        }

        List<String> processedLines = new ArrayList<>();
        Pattern codePattern = Pattern.compile("(\\*?)#(\\w{3,4})"); // Matches airport/city codes
            
        // Read and process each line from the input file
        try (BufferedReader reader = new BufferedReader(new FileReader(inputPath))) {
            String line;
            while ((line = reader.readLine()) != null) {

                line = normalizeVerticalWhitespace(line); // Clean up whitespace

                // Replace airport or city codes with full names
                Matcher matcher = codePattern.matcher(line);
                StringBuffer replacedLine = new StringBuffer();
                while (matcher.find()) {
                    boolean isCity = matcher.group(1).equals("*");
                    String code = matcher.group(2);
                    String name = isCity ? cityMap.getOrDefault(code, code) : airportMap.getOrDefault(code, code);
                    matcher.appendReplacement(replacedLine, Matcher.quoteReplacement(name));
                }
                matcher.appendTail(replacedLine);

                // Format date tokens
                String formattedLine = DateFormatter.formatDateTokens(replacedLine.toString());
                processedLines.add(formattedLine);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (processedLines.isEmpty()) {
            return;
        }

        // Write processed lines to output file and print with colors
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            boolean previousLineBlank = false;
            boolean startedWriting = false;

            for (String line : processedLines) {
                if (line.trim().isEmpty()) {
                    if (startedWriting && !previousLineBlank) {
                        writer.newLine();
                        System.out.println();
                        previousLineBlank = true;
                    }
                } else {
                    writer.write(line);
                    writer.newLine();
                    System.out.println(colorize(line)); // Print with ANSI color formatting
                    previousLineBlank = false;
                    startedWriting = true;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Converts vertical whitespace (\v, \f, \r) to newline characters (\n)
    private static String normalizeVerticalWhitespace(String text) {
        if (text == null) return null;
        return text.replace('\u000B', '\n')  // \v = vertical tab
                   .replace('\f', '\n')      // form feed
                   .replace('\r', '\n');     // carriage return
    }

    private static String colorize(String line) {
        return line
            // Date: day and month in cyan, year in blue
            .replaceAll("(\\d{2}) (\\w{3}) (20\\d{2})", "\u001B[36m$1 \u001B[36m$2 \u001B[34m$3\u001B[0m")

            // Offset: magenta
            .replaceAll("([+-]\\d{2}:\\d{2})", "\u001B[35m$1\u001B[0m")

            // 12-hour time: yellow
            .replaceAll("\\b(\\d{2}:\\d{2}[AP]M)\\b", "\u001B[33m$1\u001B[0m")

            // 24-hour time: yellow, exclude offsets
            .replaceAll("(?<![+-])(\\b\\d{2}:\\d{2}\\b)", "\u001B[33m$1\u001B[0m")

            // Names: green, excluding common words
            .replaceAll("\\b(?!(Flight|Departure|Arrival)\\b)([A-Z][a-z]+(?: [A-Z][a-z]+)*)", "\u001B[32m$2\u001B[0m");
    }

}

