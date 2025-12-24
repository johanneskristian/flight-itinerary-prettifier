# Prettifier

## Description

Prettifier is a Java command-line tool that formats raw flight itinerary text files into readable output.
It replaces airport and city codes with their names (from a CSV lookup), formats date/time tokens, and outputs both to a file and to the terminal with color.

---

## Usage
**While in itinerary_prettifier/src**

```bash
javac Prettifier.java CSVReader.java DateFormatter.java
```

**While in itinerary_prettifier**

```bash
java -cp src Prettifier data/input.txt data/output.txt data/airport-lookup.csv
```

---

## Overview

* Replaces airport codes (`#JFK`) and city codes (`*#NYC`) with names from a CSV lookup.
* Expands date/time tokens:

  * `D(2025-11-05T12:00:00Z)` → `05 Nov 2025`
  * `T12(2025-11-05T12:00:00-05:00)` → `12:00PM (-05:00)`
  * `T24(2025-11-05T12:00:00-05:00)` → `12:00 (-05:00)`
* Writes formatted text to the output file and prints colorized text to the console.

---

## Example Input

```
Flight #JFK to #LHR
Departure: D(2025-11-05T14:00:00-05:00) T12(2025-11-05T14:00:00-05:00)
Arrival: D(2025-11-06T06:00:00+00:00) T24(2025-11-06T06:00:00+00:00)
```

## Example Output

```
Flight John F. Kennedy International Airport to London Heathrow Airport
Departure: 05 Nov 2025 02:00PM (-05:00)
Arrival: 06 Nov 2025 06:00 (+00:00)
```

---

## Requirements

* Java 11 or newer
* Valid airport lookup CSV file with headers:

  ```csv
  name, iso_country, municipality, icao_code, iata_code
  John F. Kennedy International Airport,US,New York,KJFK,JFK
  London Heathrow Airport,UK,London,EGLL,LHR
  ```

---
