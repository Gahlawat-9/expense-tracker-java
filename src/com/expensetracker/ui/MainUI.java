package com.expensetracker.ui;

import com.expensetracker.charts.ChartGenerator;
import com.expensetracker.logic.ExpenseManager;
import com.expensetracker.model.Expense;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * MainUI - Primary GUI class for the Expense Tracker application.
 * Uses Java Swing with a modern dark-themed design.
 */
public class MainUI extends JFrame {

    // ========================
    // Colors & Fonts
    // ========================
    private static final Color BG_DARK        = new Color(15, 17, 26);
    private static final Color BG_PANEL       = new Color(22, 25, 37);
    private static final Color BG_INPUT       = new Color(30, 34, 50);
    private static final Color ACCENT         = new Color(99, 102, 241);
    private static final Color ACCENT_HOVER   = new Color(79, 82, 221);
    private static final Color SUCCESS        = new Color(46, 204, 113);
    private static final Color DANGER         = new Color(231, 76, 60);
    private static final Color WARNING        = new Color(241, 196, 15);
    private static final Color TEXT_PRIMARY   = new Color(230, 232, 245);
    private static final Color TEXT_SECONDARY = new Color(140, 145, 170);
    private static final Color BORDER_COLOR   = new Color(45, 50, 70);
    private static final Color TABLE_ROW_ALT  = new Color(25, 28, 42);
    private static final Color TABLE_SELECTED = new Color(50, 54, 80);

    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_LABEL  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_INPUT  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_TABLE  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 13);

    // ========================
    // Components
    // ========================
    private final ExpenseManager manager;
    private final ChartGenerator chartGenerator;

    // Input fields
    private JTextField amountField;
    private JComboBox<Expense.Category> categoryCombo;
    private JTextField dateField;
    private JTextField noteField;

    // Table
    private JTable expenseTable;
    private DefaultTableModel tableModel;

    // Filter
    private JComboBox<String> filterCombo;

    // Summary labels
    private JLabel totalLabel;
    private JLabel monthLabel;
    private JLabel countLabel;

    // Edit state
    private String editingId = null;
    private JButton addButton;

    public MainUI() {
        this.manager = new ExpenseManager();
        this.chartGenerator = new ChartGenerator(manager);

        initLookAndFeel();
        buildUI();
        refreshTable();
        refreshStats();
    }

    // ========================
    // Initialization
    // ========================

    private void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Global UI defaults for dark theme
        UIManager.put("OptionPane.background", BG_PANEL);
        UIManager.put("Panel.background", BG_PANEL);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        UIManager.put("Button.background", ACCENT);
        UIManager.put("Button.foreground", Color.WHITE);
    }

    private void buildUI() {
        setTitle("💸 Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 780);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildMainContent(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    // ========================
    // Header
    // ========================

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_PANEL);
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(0, 65));

        // Left: title
        JLabel title = new JLabel("  💸  Expense Tracker");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        // Right: stat chips
        JPanel statsRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        statsRight.setBackground(BG_PANEL);

        totalLabel = createStatChip("Total", "₹0.00", ACCENT);
        monthLabel = createStatChip("This Month", "₹0.00", SUCCESS);
        countLabel = createStatChip("Entries", "0", WARNING);

        statsRight.add(totalLabel);
        statsRight.add(monthLabel);
        statsRight.add(countLabel);
        header.add(statsRight, BorderLayout.EAST);

        return header;
    }

    private JLabel createStatChip(String label, String value, Color color) {
        JLabel chip = new JLabel(label + ": " + value);
        chip.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chip.setForeground(color);
        chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                new EmptyBorder(4, 10, 4, 10)
        ));
        chip.setOpaque(true);
        chip.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        return chip;
    }

    // ========================
    // Main Content
    // ========================

    private JPanel buildMainContent() {
        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBackground(BG_DARK);
        content.setBorder(new EmptyBorder(15, 15, 15, 15));

        content.add(buildInputPanel(), BorderLayout.NORTH);
        content.add(buildTablePanel(), BorderLayout.CENTER);

        return content;
    }

    // ========================
    // Input Panel
    // ========================

    private JPanel buildInputPanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Section label
        JLabel sectionLabel = new JLabel("Add New Expense");
        sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        sectionLabel.setForeground(TEXT_PRIMARY);
        sectionLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        card.add(sectionLabel, BorderLayout.NORTH);

        // Fields row
        JPanel fieldsRow = new JPanel(new GridBagLayout());
        fieldsRow.setBackground(BG_PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 12);
        gbc.weighty = 1.0;

        // Amount
        gbc.gridx = 0; gbc.weightx = 0.15;
        fieldsRow.add(buildFieldGroup("Amount (₹)", amountField = buildTextField("e.g. 250.00")), gbc);

        // Category
        gbc.gridx = 1; gbc.weightx = 0.2;
        categoryCombo = new JComboBox<>(Expense.Category.values());
        styleComboBox(categoryCombo);
        fieldsRow.add(buildFieldGroup("Category", categoryCombo), gbc);

        // Date
        gbc.gridx = 2; gbc.weightx = 0.15;
        dateField = buildTextField(LocalDate.now().toString());
        dateField.setText(LocalDate.now().toString());
        fieldsRow.add(buildFieldGroup("Date (YYYY-MM-DD)", dateField), gbc);

        // Note
        gbc.gridx = 3; gbc.weightx = 0.35; gbc.insets = new Insets(0, 0, 0, 0);
        fieldsRow.add(buildFieldGroup("Note", noteField = buildTextField("Optional description...")), gbc);

        card.add(fieldsRow, BorderLayout.CENTER);

        // Buttons row
        JPanel btnsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnsPanel.setBackground(BG_PANEL);
        btnsPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        addButton = buildButton("＋ Add Expense", ACCENT, ACCENT_HOVER);
        JButton clearBtn = buildButton("✕ Clear", BG_INPUT, BORDER_COLOR);
        JButton chartBtn = buildButton("📊 Show Charts", new Color(75, 192, 192), new Color(55, 162, 162));
        JButton exportBtn = buildButton("⬇ Export CSV", new Color(46, 204, 113), new Color(36, 174, 93));

        addButton.addActionListener(e -> handleAddOrUpdate());
        clearBtn.addActionListener(e -> clearForm());
        chartBtn.addActionListener(e -> chartGenerator.showChartsWindow(this));
        exportBtn.addActionListener(e -> handleExport());

        btnsPanel.add(addButton);
        btnsPanel.add(clearBtn);
        btnsPanel.add(Box.createHorizontalStrut(15));
        btnsPanel.add(chartBtn);
        btnsPanel.add(exportBtn);

        card.add(btnsPanel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildFieldGroup(String label, JComponent field) {
        JPanel group = new JPanel(new BorderLayout(0, 6));
        group.setBackground(BG_PANEL);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_SECONDARY);
        group.add(lbl, BorderLayout.NORTH);
        group.add(field, BorderLayout.CENTER);
        return group;
    }

    // ========================
    // Table Panel
    // ========================

    private JPanel buildTablePanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Table toolbar
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(BG_PANEL);
        toolbar.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel tableTitle = new JLabel("All Expenses");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tableTitle.setForeground(TEXT_PRIMARY);
        toolbar.add(tableTitle, BorderLayout.WEST);

        // Filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterPanel.setBackground(BG_PANEL);

        JLabel filterLbl = new JLabel("Filter by:");
        filterLbl.setFont(FONT_LABEL);
        filterLbl.setForeground(TEXT_SECONDARY);

        filterCombo = new JComboBox<>();
        filterCombo.addItem("All Categories");
        for (Expense.Category cat : Expense.Category.values()) {
            filterCombo.addItem(cat.getDisplayName());
        }
        filterCombo.setPreferredSize(new Dimension(200, 36));
        styleComboBox(filterCombo);
        filterCombo.addActionListener(e -> applyFilter());

        filterPanel.add(filterLbl);
        filterPanel.add(filterCombo);
        toolbar.add(filterPanel, BorderLayout.EAST);

        card.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] columns = {"#", "Amount (₹)", "Category", "Date", "Note", "ID"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        expenseTable = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(expenseTable);
        scrollPane.setBackground(BG_DARK);
        scrollPane.getViewport().setBackground(BG_DARK);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getVerticalScrollBar().setBackground(BG_PANEL);

        card.add(scrollPane, BorderLayout.CENTER);

        // Row action buttons
        JPanel rowActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rowActions.setBackground(BG_PANEL);
        rowActions.setBorder(new EmptyBorder(12, 0, 0, 0));

        JButton editBtn = buildButton("✏ Edit Selected", WARNING, WARNING.darker());
        JButton deleteBtn = buildButton("🗑 Delete Selected", DANGER, DANGER.darker());

        editBtn.addActionListener(e -> handleEdit());
        deleteBtn.addActionListener(e -> handleDelete());

        rowActions.add(editBtn);
        rowActions.add(deleteBtn);
        card.add(rowActions, BorderLayout.SOUTH);

        return card;
    }

    // ========================
    // Status Bar
    // ========================

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(12, 14, 22));
        bar.setBorder(new EmptyBorder(5, 15, 5, 15));

        JLabel status = new JLabel("Data saved to: " + manager.getDataFilePath());
        status.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        status.setForeground(TEXT_SECONDARY);
        bar.add(status, BorderLayout.WEST);

        JLabel version = new JLabel("Expense Tracker v1.0");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        version.setForeground(TEXT_SECONDARY);
        bar.add(version, BorderLayout.EAST);

        return bar;
    }

    // ========================
    // Styling Helpers
    // ========================

    private JTextField buildTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(TEXT_SECONDARY);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString(placeholder, 10, getHeight() / 2 + 5);
                }
            }
        };
        field.setFont(FONT_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(BG_INPUT);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        field.setPreferredSize(new Dimension(0, 38));
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT, 2),
                        new EmptyBorder(7, 9, 7, 9)));
            }
            @Override public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 1),
                        new EmptyBorder(8, 10, 8, 10)));
            }
        });
        return field;
    }

    private void styleComboBox(JComboBox<?> combo) {
        combo.setFont(FONT_INPUT);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBackground(BG_INPUT);
        combo.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        combo.setPreferredSize(new Dimension(0, 38));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? ACCENT : BG_INPUT);
                setForeground(TEXT_PRIMARY);
                setBorder(new EmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
    }

    private JButton buildButton(String text, Color bg, Color hover) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setBorder(new EmptyBorder(9, 18, 9, 18));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    private void styleTable() {
        expenseTable.setFont(FONT_TABLE);
        expenseTable.setForeground(TEXT_PRIMARY);
        expenseTable.setBackground(BG_DARK);
        expenseTable.setGridColor(BORDER_COLOR);
        expenseTable.setRowHeight(40);
        expenseTable.setSelectionBackground(TABLE_SELECTED);
        expenseTable.setSelectionForeground(TEXT_PRIMARY);
        expenseTable.setShowVerticalLines(false);
        expenseTable.setIntercellSpacing(new Dimension(0, 1));

        // Hide the ID column
        expenseTable.getColumnModel().getColumn(5).setMinWidth(0);
        expenseTable.getColumnModel().getColumn(5).setMaxWidth(0);
        expenseTable.getColumnModel().getColumn(5).setWidth(0);

        // Column widths
        expenseTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        expenseTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        expenseTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        expenseTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        expenseTable.getColumnModel().getColumn(4).setPreferredWidth(400);

        // Header styling
        JTableHeader header = expenseTable.getTableHeader();
        header.setFont(FONT_HEADER);
        header.setForeground(TEXT_SECONDARY);
        header.setBackground(BG_PANEL);
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setReorderingAllowed(false);

        // Alternating row colors
        expenseTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setFont(FONT_TABLE);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (isSelected) {
                    setBackground(TABLE_SELECTED);
                    setForeground(TEXT_PRIMARY);
                } else {
                    setBackground(row % 2 == 0 ? BG_DARK : TABLE_ROW_ALT);
                    setForeground(TEXT_PRIMARY);
                }
                // Colorize amount column
                if (col == 1) {
                    setForeground(new Color(46, 204, 113));
                    setFont(FONT_TABLE.deriveFont(Font.BOLD));
                }
                return this;
            }
        });
    }

    // ========================
    // Logic: Add / Edit
    // ========================

    private void handleAddOrUpdate() {
        // Validate amount
        String amountStr = amountField.getText().trim();
        if (amountStr.isEmpty()) {
            showError("Please enter an amount.");
            amountField.requestFocus();
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showError("Invalid amount. Please enter a positive number.");
            amountField.requestFocus();
            return;
        }

        // Validate date
        String dateStr = dateField.getText().trim();
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, Expense.DATE_FORMAT);
        } catch (DateTimeParseException ex) {
            showError("Invalid date format. Use YYYY-MM-DD (e.g., 2025-06-15).");
            dateField.requestFocus();
            return;
        }

        Expense.Category category = (Expense.Category) categoryCombo.getSelectedItem();
        String note = noteField.getText().trim();

        if (editingId != null) {
            // Update existing
            manager.updateExpense(editingId, amount, category, date, note);
            showSuccess("Expense updated successfully.");
            editingId = null;
            addButton.setText("＋ Add Expense");
        } else {
            // Add new
            manager.addExpense(new Expense(amount, category, date, note));
            showSuccess("Expense added successfully.");
        }

        clearForm();
        refreshTable();
        refreshStats();
    }

    private void handleEdit() {
        int row = expenseTable.getSelectedRow();
        if (row < 0) {
            showError("Please select an expense to edit.");
            return;
        }
        String id = (String) tableModel.getValueAt(row, 5);
        manager.findById(id).ifPresent(expense -> {
            amountField.setText(String.valueOf(expense.getAmount()));
            categoryCombo.setSelectedItem(expense.getCategory());
            dateField.setText(expense.getDate().format(Expense.DATE_FORMAT));
            noteField.setText(expense.getNote());
            editingId = id;
            addButton.setText("💾 Save Changes");
            amountField.requestFocus();
        });
    }

    private void handleDelete() {
        int row = expenseTable.getSelectedRow();
        if (row < 0) {
            showError("Please select an expense to delete.");
            return;
        }
        String id = (String) tableModel.getValueAt(row, 5);
        String amountStr = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete expense of " + amountStr + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            manager.deleteExpense(id);
            refreshTable();
            refreshStats();
            showSuccess("Expense deleted.");
        }
    }

    private void handleExport() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Expenses to CSV");
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        chooser.setSelectedFile(new java.io.File("expenses_export.csv"));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".csv")) path += ".csv";

            boolean success = manager.exportToCSV(path);
            if (success) {
                showSuccess("Exported successfully to: " + path);
            } else {
                showError("Export failed. Please try again.");
            }
        }
    }

    // ========================
    // Table Management
    // ========================

    private void refreshTable() {
        applyFilter();
    }

    private void applyFilter() {
        tableModel.setRowCount(0);
        List<Expense> expenses;

        String selected = (String) (filterCombo != null ? filterCombo.getSelectedItem() : "All Categories");
        if (selected == null || selected.equals("All Categories")) {
            expenses = manager.getAllExpenses();
        } else {
            // Match display name
            Expense.Category matched = null;
            for (Expense.Category cat : Expense.Category.values()) {
                if (cat.getDisplayName().equals(selected)) {
                    matched = cat;
                    break;
                }
            }
            expenses = (matched != null) ? manager.getByCategory(matched) : manager.getAllExpenses();
        }

        int i = 1;
        for (Expense e : expenses) {
            tableModel.addRow(new Object[]{
                    i++,
                    String.format("₹%.2f", e.getAmount()),
                    e.getCategory().getDisplayName(),
                    e.getDate().toString(),
                    e.getNote(),
                    e.getId()
            });
        }
    }

    private void refreshStats() {
        totalLabel.setText("Total: ₹" + String.format("%.2f", manager.getTotalExpense()));
        monthLabel.setText("This Month: ₹" + String.format("%.2f", manager.getCurrentMonthTotal()));
        countLabel.setText("Entries: " + manager.getAllExpenses().size());
    }

    private void clearForm() {
        amountField.setText("");
        categoryCombo.setSelectedIndex(0);
        dateField.setText(LocalDate.now().toString());
        noteField.setText("");
        editingId = null;
        addButton.setText("＋ Add Expense");
    }

    // ========================
    // Notifications
    // ========================

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "✅ Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "⚠ Error",
                JOptionPane.ERROR_MESSAGE);
    }

    // ========================
    // Main Entry Point
    // ========================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainUI ui = new MainUI();
            ui.setVisible(true);
        });
    }
}
