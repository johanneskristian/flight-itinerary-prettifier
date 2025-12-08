import java.io.*;
import java.util.*;

public class CsvReader {

    private CsvReader() {}

    List<List<String>> csvList = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader("airport-lookup.csv"))) {
        String line;
        while ((line = br.readLine()) != null) {

            }
        }
    }






}
