package com.expensetracker.storage;

import com.expensetracker.model.Expense;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FileHandler manages reading and writing expenses to a CSV file.
 * Provides persistent storage across application restarts.
 */
public class FileHandler {

    // Default storage file location (in user's home directory)
    private static final String DEFAULT_FILE = System.getProperty("user.home")
            + File.separator + "expense_tracker_data.csv";

    private final String filePath;

    public FileHandler() {
        this(DEFAULT_FILE);
    }

    public FileHandler(String filePath) {
        this.filePath = filePath;
        ensureFileExists();
    }

    /**
     * Creates the CSV file if it doesn't exist.
     */
    private void ensureFileExists() {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                // Write header line
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write("id,amount,category,date,note");
                    writer.newLine();
                }
            } catch (IOException e) {
                System.err.println("Error creating data file: " + e.getMessage());
            }
        }
    }

    /**
     * Loads all expenses from the CSV file.
     * @return List of Expense objects
     */
    public List<Expense> loadExpenses() {
        List<Expense> expenses = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // Skip header
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    expenses.add(Expense.fromCsv(line));
                } catch (Exception e) {
                    System.err.println("Skipping invalid line: " + line + " | Error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading data file: " + e.getMessage());
        }
        return expenses;
    }

    /**
     * Saves all expenses to the CSV file (overwrites existing data).
     * @param expenses List of expenses to save
     */
    public void saveExpenses(List<Expense> expenses) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("id,amount,category,date,note");
            writer.newLine();
            for (Expense expense : expenses) {
                writer.write(expense.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving data file: " + e.getMessage());
        }
    }

    /**
     * Exports expenses to a user-specified CSV file path.
     * @param expenses List of expenses
     * @param exportPath Path to export file
     * @return true if successful
     */
    public boolean exportToCSV(List<Expense> expenses, String exportPath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(exportPath))) {
            writer.write("ID,Amount (₹),Category,Date,Note");
            writer.newLine();
            for (Expense e : expenses) {
                writer.write(String.format("%s,%.2f,%s,%s,%s",
                        e.getId(),
                        e.getAmount(),
                        e.getCategory().getDisplayName(),
                        e.getDate(),
                        e.getNote().replace(",", ";")));
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Export failed: " + e.getMessage());
            return false;
        }
    }

    public String getFilePath() {
        return filePath;
    }
}
