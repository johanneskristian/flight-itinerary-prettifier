import java.util.*;
import java.io.*;

public class CSVReader {

    private CSVReader() {}

    public static HashMap<String, String> loadcsvMap(String filePath, boolean useCity) throws IOException {
        HashMap<String, String> csvMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            // READ HEADER
            String headerLine = br.readLine();
            if (headerLine == null)
                throw new IllegalArgumentException("Empty CSV file");

            headerLine = headerLine.replace("\uFEFF", "");   // strip BOM

            String[] headers = headerLine.split(",", -1);

            Map<String, Integer> indexMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                indexMap.put(headers[i].trim().toLowerCase(), i);
            }

            // REQUIRED FIELDS
            Integer nameIdx = indexMap.get("name");
            Integer iataIdx = indexMap.get("iata_code");
            Integer icaoIdx = indexMap.get("icao_code");

            Integer cityIdx = null;
            if (useCity) {
                for (String key : indexMap.keySet()) {
                    if (key.equals("city") || key.equals("municipality")) {
                        cityIdx = indexMap.get(key);
                        break;
                    }
                }
            }

            // HEADER VALIDATION
            if (!useCity && nameIdx == null)
                throw new IllegalArgumentException("Missing column: name");

            if (useCity && cityIdx == null)
                throw new IllegalArgumentException("Missing column: city or municipality");

            if (iataIdx == null && icaoIdx == null)
                throw new IllegalArgumentException("Missing column: iata_code or icao_code");

            // ROW VALIDATION
            String line;
            int rowNum = 1;

            while ((line = br.readLine()) != null) {
                rowNum++;

                String[] values = line.split(",", -1);

                if (values.length < headers.length) {
                    throw new IllegalArgumentException(
                        "Malformed CSV: row " + rowNum + " has fewer columns than header"
                    );
                }

                String name = nameIdx != null ? values[nameIdx].trim() : "";
                String city = cityIdx != null ? values[cityIdx].trim() : "";
                String iata = iataIdx != null ? values[iataIdx].trim() : "";
                String icao = icaoIdx != null ? values[icaoIdx].trim() : "";

                if (!useCity && name.isEmpty())
                    throw new IllegalArgumentException("Missing airport name on row " + rowNum);

                if (useCity && city.isEmpty())
                    throw new IllegalArgumentException("Missing city on row " + rowNum);

                if (iata.isEmpty() && icao.isEmpty())
                    throw new IllegalArgumentException("Missing IATA and ICAO on row " + rowNum);

                String value = useCity ? city : name;

                if (!iata.isEmpty()) csvMap.put(iata, value);
                if (!icao.isEmpty()) csvMap.put(icao, value);
            }
        }

        return csvMap;
    }
}
