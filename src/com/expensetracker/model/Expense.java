package com.expensetracker.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Expense model class representing a single expense entry.
 * Implements OOP principles with encapsulation.
 */
public class Expense {

    // Date formatter for consistent date handling
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Predefined expense categories
    public enum Category {
        FOOD("🍔 Food"),
        TRANSPORT("🚗 Transport"),
        SHOPPING("🛍 Shopping"),
        ENTERTAINMENT("🎬 Entertainment"),
        HEALTH("🏥 Health"),
        UTILITIES("💡 Utilities"),
        EDUCATION("📚 Education"),
        TRAVEL("✈ Travel"),
        OTHER("📦 Other");

        private final String displayName;

        Category(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }

        // Return plain name without emoji for storage
        public String getStorageName() {
            return this.name();
        }
    }

    private String id;
    private double amount;
    private Category category;
    private LocalDate date;
    private String note;

    /**
     * Constructor for creating a new expense with auto-generated ID.
     */
    public Expense(double amount, Category category, LocalDate date, String note) {
        this.id = UUID.randomUUID().toString();
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.note = note;
    }

    /**
     * Constructor for loading an existing expense from storage (with existing ID).
     */
    public Expense(String id, double amount, Category category, LocalDate date, String note) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.note = note;
    }

    // --- Getters ---
    public String getId() { return id; }
    public double getAmount() { return amount; }
    public Category getCategory() { return category; }
    public LocalDate getDate() { return date; }
    public String getNote() { return note; }

    // --- Setters ---
    public void setAmount(double amount) { this.amount = amount; }
    public void setCategory(Category category) { this.category = category; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setNote(String note) { this.note = note; }

    /**
     * Converts expense to CSV format for file storage.
     */
    public String toCsv() {
        return String.join(",",
            id,
            String.valueOf(amount),
            category.getStorageName(),
            date.format(DATE_FORMAT),
            note.replace(",", ";") // Escape commas in notes
        );
    }

    /**
     * Creates an Expense object from a CSV string.
     */
    public static Expense fromCsv(String csv) {
        String[] parts = csv.split(",", 5);
        if (parts.length < 5) throw new IllegalArgumentException("Invalid CSV: " + csv);
        String id = parts[0].trim();
        double amount = Double.parseDouble(parts[1].trim());
        Category category = Category.valueOf(parts[2].trim());
        LocalDate date = LocalDate.parse(parts[3].trim(), DATE_FORMAT);
        String note = parts[4].trim().replace(";", ",");
        return new Expense(id, amount, category, date, note);
    }

    @Override
    public String toString() {
        return String.format("Expense{id='%s', amount=%.2f, category=%s, date=%s, note='%s'}",
                id, amount, category, date, note);
    }
}
