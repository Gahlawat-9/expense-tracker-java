package com.expensetracker.logic;

import com.expensetracker.model.Expense;
import com.expensetracker.storage.FileHandler;

import java.time.Month;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ExpenseManager handles all business logic for expense operations.
 * Acts as the service layer between UI and storage.
 */
public class ExpenseManager {

    private final List<Expense> expenses;
    private final FileHandler fileHandler;

    public ExpenseManager() {
        this.fileHandler = new FileHandler();
        this.expenses = new ArrayList<>(fileHandler.loadExpenses());
    }

    // ========================
    // CRUD Operations
    // ========================

    /**
     * Adds a new expense and persists it.
     */
    public void addExpense(Expense expense) {
        expenses.add(expense);
        save();
    }

    /**
     * Removes an expense by ID and persists.
     */
    public boolean deleteExpense(String id) {
        boolean removed = expenses.removeIf(e -> e.getId().equals(id));
        if (removed) save();
        return removed;
    }

    /**
     * Updates an existing expense by ID.
     */
    public boolean updateExpense(String id, double amount, Expense.Category category,
                                  java.time.LocalDate date, String note) {
        for (Expense e : expenses) {
            if (e.getId().equals(id)) {
                e.setAmount(amount);
                e.setCategory(category);
                e.setDate(date);
                e.setNote(note);
                save();
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a copy of all expenses.
     */
    public List<Expense> getAllExpenses() {
        return Collections.unmodifiableList(expenses);
    }

    /**
     * Finds an expense by ID.
     */
    public Optional<Expense> findById(String id) {
        return expenses.stream().filter(e -> e.getId().equals(id)).findFirst();
    }

    // ========================
    // Filtering & Analytics
    // ========================

    /**
     * Filters expenses by category.
     */
    public List<Expense> getByCategory(Expense.Category category) {
        return expenses.stream()
                .filter(e -> e.getCategory() == category)
                .collect(Collectors.toList());
    }

    /**
     * Returns total expense amount across all entries.
     */
    public double getTotalExpense() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }

    /**
     * Returns total expense for a specific category.
     */
    public double getTotalByCategory(Expense.Category category) {
        return expenses.stream()
                .filter(e -> e.getCategory() == category)
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    /**
     * Returns a map of category -> total amount for pie chart.
     */
    public Map<String, Double> getCategoryTotals() {
        Map<String, Double> totals = new LinkedHashMap<>();
        for (Expense.Category cat : Expense.Category.values()) {
            double total = getTotalByCategory(cat);
            if (total > 0) totals.put(cat.getDisplayName(), total);
        }
        return totals;
    }

    /**
     * Returns monthly expense totals for bar chart.
     * Key: "YYYY-MM", Value: total
     */
    public Map<String, Double> getMonthlyTotals() {
        Map<String, Double> monthly = new TreeMap<>();
        for (Expense e : expenses) {
            String key = e.getDate().getYear() + "-" + String.format("%02d", e.getDate().getMonthValue());
            monthly.merge(key, e.getAmount(), Double::sum);
        }
        return monthly;
    }

    /**
     * Returns total expense for the current month.
     */
    public double getCurrentMonthTotal() {
        YearMonth current = YearMonth.now();
        return expenses.stream()
                .filter(e -> YearMonth.from(e.getDate()).equals(current))
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    /**
     * Returns the highest-spending category.
     */
    public String getTopCategory() {
        return getCategoryTotals().entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    /**
     * Returns the number of expenses this month.
     */
    public long getCurrentMonthCount() {
        YearMonth current = YearMonth.now();
        return expenses.stream()
                .filter(e -> YearMonth.from(e.getDate()).equals(current))
                .count();
    }

    // ========================
    // Export
    // ========================

    /**
     * Exports all expenses to a CSV file at the given path.
     */
    public boolean exportToCSV(String path) {
        return fileHandler.exportToCSV(expenses, path);
    }

    /**
     * Exports filtered expenses to CSV.
     */
    public boolean exportFilteredToCSV(List<Expense> filtered, String path) {
        return fileHandler.exportToCSV(filtered, path);
    }

    // ========================
    // Internal
    // ========================

    /**
     * Persists current expense list to file.
     */
    private void save() {
        fileHandler.saveExpenses(expenses);
    }

    public String getDataFilePath() {
        return fileHandler.getFilePath();
    }
}
