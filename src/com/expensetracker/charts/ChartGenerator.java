package com.expensetracker.charts;

import com.expensetracker.logic.ExpenseManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Map;

/**
 * ChartGenerator creates JFreeChart-based charts for expense visualization.
 * Provides pie chart (category distribution) and bar chart (monthly expenses).
 */
public class ChartGenerator {

    // Color palette for charts
    private static final Color[] CHART_COLORS = {
        new Color(255, 99, 132),   // Pink-Red
        new Color(54, 162, 235),   // Blue
        new Color(255, 206, 86),   // Yellow
        new Color(75, 192, 192),   // Teal
        new Color(153, 102, 255),  // Purple
        new Color(255, 159, 64),   // Orange
        new Color(46, 204, 113),   // Green
        new Color(231, 76, 60),    // Red
        new Color(52, 152, 219),   // Light Blue
    };

    private static final Color BG_COLOR      = new Color(15, 17, 26);
    private static final Color PANEL_COLOR   = new Color(22, 25, 37);
    private static final Color TEXT_COLOR    = new Color(230, 232, 245);
    private static final Color ACCENT_COLOR  = new Color(99, 102, 241);

    private final ExpenseManager manager;

    public ChartGenerator(ExpenseManager manager) {
        this.manager = manager;
    }

    // ========================
    // PIE CHART
    // ========================

    /**
     * Creates a styled pie chart showing category-wise expense distribution.
     */
    public ChartPanel createPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Double> totals = manager.getCategoryTotals();

        if (totals.isEmpty()) {
            dataset.setValue("No Data", 1.0);
        } else {
            totals.forEach(dataset::setValue);
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Expense by Category",
                dataset,
                true,   // legend
                true,   // tooltips
                false   // URLs
        );

        // Style chart background
        chart.setBackgroundPaint(PANEL_COLOR);
        chart.getTitle().setPaint(TEXT_COLOR);
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));

        // Style the legend
        chart.getLegend().setBackgroundPaint(PANEL_COLOR);
        chart.getLegend().setItemPaint(TEXT_COLOR);
        chart.getLegend().setItemFont(new Font("Segoe UI", Font.PLAIN, 11));
        chart.getLegend().setFrame(new org.jfree.chart.block.BlockBorder(PANEL_COLOR));

        // Style the plot
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(PANEL_COLOR);
        plot.setOutlinePaint(null);
        plot.setShadowPaint(null);
        plot.setLabelBackgroundPaint(new Color(30, 34, 50));
        plot.setLabelOutlinePaint(null);
        plot.setLabelShadowPaint(null);
        plot.setLabelPaint(TEXT_COLOR);
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 11));

        // Apply colors
        int i = 0;
        for (Object key : dataset.getKeys()) {
            plot.setSectionPaint((Comparable<?>) key, CHART_COLORS[i % CHART_COLORS.length]);
            i++;
        }

        // Format labels with percentage
        plot.setLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator(
                "{0}: ₹{1} ({2})", new DecimalFormat("#,##0.00"), new DecimalFormat("0.0%")));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(PANEL_COLOR);
        chartPanel.setPreferredSize(new Dimension(500, 400));
        return chartPanel;
    }

    // ========================
    // BAR CHART
    // ========================

    /**
     * Creates a styled bar chart showing monthly expense totals.
     */
    public ChartPanel createBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Double> monthly = manager.getMonthlyTotals();

        if (monthly.isEmpty()) {
            dataset.addValue(0, "Expenses", "No Data");
        } else {
            monthly.forEach((month, total) -> dataset.addValue(total, "Expenses", formatMonth(month)));
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Monthly Expense Trend",
                "Month",
                "Amount (₹)",
                dataset,
                PlotOrientation.VERTICAL,
                false,  // legend
                true,   // tooltips
                false   // URLs
        );

        // Style chart
        chart.setBackgroundPaint(PANEL_COLOR);
        chart.getTitle().setPaint(TEXT_COLOR);
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));

        // Style the plot
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(22, 25, 37));
        plot.setRangeGridlinePaint(new Color(45, 50, 70));
        plot.setDomainGridlinePaint(new Color(45, 50, 70));
        plot.setOutlinePaint(null);

        // Style X-axis
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelPaint(TEXT_COLOR);
        domainAxis.setLabelPaint(TEXT_COLOR);
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
        domainAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 12));
        domainAxis.setAxisLinePaint(new Color(45, 50, 70));

        // Style Y-axis
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelPaint(TEXT_COLOR);
        rangeAxis.setLabelPaint(TEXT_COLOR);
        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
        rangeAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 12));
        rangeAxis.setAxisLinePaint(new Color(45, 50, 70));
        rangeAxis.setNumberFormatOverride(new DecimalFormat("₹#,##0"));

        // Style bars
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setShadowVisible(false);
        renderer.setSeriesPaint(0, ACCENT_COLOR);
        renderer.setMaximumBarWidth(0.1);
        renderer.setItemMargin(0.05);

        // Add value labels on bars
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(new Font("Segoe UI", Font.BOLD, 10));
        renderer.setDefaultItemLabelPaint(TEXT_COLOR);
        renderer.setDefaultItemLabelGenerator(
                new org.jfree.chart.labels.StandardCategoryItemLabelGenerator(
                        "₹{2}", new DecimalFormat("#,##0")));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(PANEL_COLOR);
        chartPanel.setPreferredSize(new Dimension(500, 400));
        return chartPanel;
    }

    // ========================
    // Charts Window
    // ========================

    /**
     * Opens a full charts dashboard window with pie and bar charts side by side.
     */
    public void showChartsWindow(JFrame parent) {
        JDialog dialog = new JDialog(parent, "📊 Analytics Dashboard", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BG_COLOR);

        // Title
        JLabel title = new JLabel("Expense Analytics", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_COLOR);
        title.setBorder(new EmptyBorder(20, 0, 10, 0));
        dialog.add(title, BorderLayout.NORTH);

        // Summary stats row
        JPanel statsPanel = buildSummaryPanel();
        dialog.add(statsPanel, BorderLayout.SOUTH);

        // Charts panel
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsPanel.setBackground(BG_COLOR);
        chartsPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Pie chart card
        JPanel pieCard = wrapInCard(createPieChart(), "Category Breakdown");
        chartsPanel.add(pieCard);

        // Bar chart card
        JPanel barCard = wrapInCard(createBarChart(), "Monthly Trend");
        chartsPanel.add(barCard);

        dialog.add(chartsPanel, BorderLayout.CENTER);

        dialog.setSize(1100, 700);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    /**
     * Wraps a chart panel in a styled card.
     */
    private JPanel wrapInCard(ChartPanel chartPanel, String label) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PANEL_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 50, 70), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        card.add(chartPanel, BorderLayout.CENTER);
        return card;
    }

    /**
     * Builds a summary statistics panel at the bottom of the chart window.
     */
    private JPanel buildSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(10, 20, 20, 20));

        addStatCard(panel, "💰 Total Spent",
                String.format("₹%.2f", manager.getTotalExpense()), new Color(99, 102, 241));
        addStatCard(panel, "📅 This Month",
                String.format("₹%.2f", manager.getCurrentMonthTotal()), new Color(46, 204, 113));
        addStatCard(panel, "🏆 Top Category",
                manager.getTopCategory(), new Color(255, 159, 64));
        addStatCard(panel, "📊 Transactions",
                String.valueOf(manager.getAllExpenses().size()), new Color(54, 162, 235));

        return panel;
    }

    private void addStatCard(JPanel parent, String label, String value, Color accent) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBackground(PANEL_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, accent),
                new EmptyBorder(12, 15, 12, 15)
        ));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(150, 155, 180));

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 18));
        val.setForeground(TEXT_COLOR);

        card.add(lbl);
        card.add(val);
        parent.add(card);
    }

    /**
     * Formats "YYYY-MM" into "MMM YYYY" for display.
     */
    private String formatMonth(String yearMonth) {
        try {
            String[] parts = yearMonth.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                               "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            return months[month - 1] + " " + year;
        } catch (Exception e) {
            return yearMonth;
        }
    }
}
