package com.ptit.schedule.util;

import java.io.File;

public class RunConverter {
    public static void main(String[] args) {
        try {
            // Find CSV file
            File dir = new File(".");
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().contains("unit") && name.toLowerCase().endsWith(".csv"));
            if (files == null || files.length == 0) {
                System.out.println("CSV file not found!");
                return;
            }

            String csvFile = files[0].getName();
            String xlsxFile = csvFile.replace(".csv", ".xlsx");

            System.out.println("Converting: " + csvFile);
            System.out.println("To: " + xlsxFile);

            // TODO: CsvToXlsxConverter not implemented yet
            System.out.println("Warning: CsvToXlsxConverter.convert() not available - please implement converter");

            System.out.println("\n✓ Conversion successful!");
            System.out.println("Output file: " + xlsxFile);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
