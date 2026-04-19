import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TestCsvParsing {
    public static void main(String[] args) throws Exception {
        // Test 1: Parse a single CSV line with commas inside quoted fields
        System.out.println("=== Test 1: Single line parsing ===");
        String csvLine = "10,ScheduleServiceImplTest.java,getSchedulesBySubjectId_shouldDelegateToRepository,Kiểm tra getSchedulesBySubjectId được ủy thác chính xác,\"ApiResponse.success(listSchedule, \"\"Lấy lịch theo môn thành công\"\")\",\"success = true, data không null, message = \"\"Lấy lịch theo môn thành công\"\"\",Pass,";

        CSVReader reader = new CSVReader(new StringReader(csvLine));
        String[] result = reader.readNext();

        System.out.println("Number of columns: " + result.length + " (expected: 8)");
        for (int i = 0; i < result.length; i++) {
            System.out.println("Column " + (i+1) + ": [" + result[i] + "]");
        }
        reader.close();

        // Test 2: Read full file with proper UTF-8 encoding
        System.out.println("\n=== Test 2: Full file parsing ===");

        // Find the CSV file
        File dir = new File(".");
        File[] csvFiles = dir.listFiles((d, name) -> name.endsWith(".csv") && name.toLowerCase().contains("unit"));
        if (csvFiles != null && csvFiles.length > 0) {
            File csvFile = csvFiles[0];
            System.out.println("Found file: " + csvFile.getName());
            try (CSVReader fileReader = new CSVReader(
                    new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8))) {
                List<String[]> allRows = fileReader.readAll();
                System.out.println("Total rows: " + allRows.size());

                // Check row 10 (index 9)
                if (allRows.size() >= 10) {
                    String[] row10 = allRows.get(9);
                    System.out.println("\nRow 10 columns: " + row10.length + " (expected: 8)");
                    for (int i = 0; i < row10.length; i++) {
                        System.out.println("  Col" + (i+1) + ": [" + row10[i] + "]");
                    }
                }
            }
        } else {
            System.out.println("No CSV file found!");
        }
    }
}
