import java.io.*;
import java.util.*;

public class CsvReader {

    private CsvReader() {}

    public static Map<String, String> readAirportLookup(String filePath) throws IOException {
        Map<String, String> airportLookup = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 5) {
                String code = parts[0].trim();
                String name = parts[4].trim();
                airportLookup.put(code, name);
            }
        }
        reader.close();
        return airportLookup;
    }






}
