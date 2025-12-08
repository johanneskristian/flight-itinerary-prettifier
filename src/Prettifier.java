import java.io.*;
import java.util.*;

public class Prettifier {
    public static void main(String[] args) throws IOException {
        if (args.length < 3 || "-h".equals(args[0])) {
            System.out.println("itinerary usage:\njava Prettifier.java ./input.txt ./output.txt ./airport-lookup.csv");
            return;
        }

        Map<String, String> map = CsvReader.readAirportLookup("./airport-lookup.csv");
        System.out.println("Loaded: " + map.size());
        map.entrySet().stream().limit(10).forEach(System.out::println);
    }















}