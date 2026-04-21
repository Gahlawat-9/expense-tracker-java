# 💸 Expense Tracker — Java Desktop App with Analytics Dashboard

A complete, modern desktop expense tracking application built with Java Swing and JFreeChart.  
Dark-themed, beginner-friendly, resume-worthy.

---

## 📁 Project Structure

```
ExpenseTracker/
├── pom.xml                          ← Maven build file
└── src/
    └── com/
        └── expensetracker/
            ├── model/
            │   └── Expense.java         ← Data model (OOP)
            ├── storage/
            │   └── FileHandler.java     ← CSV read/write
            ├── logic/
            │   └── ExpenseManager.java  ← Business logic
            ├── charts/
            │   └── ChartGenerator.java  ← JFreeChart pie & bar
            └── ui/
                └── MainUI.java          ← Main Swing GUI
```

---

## ✅ Features

| Feature                    | Status |
|----------------------------|--------|
| Add expense (amount, category, date, note) | ✅ |
| Edit existing expense       | ✅ |
| Delete expense              | ✅ |
| Filter by category          | ✅ |
| Total & monthly stats       | ✅ |
| Persistent CSV storage      | ✅ |
| Pie chart (category split)  | ✅ |
| Bar chart (monthly trend)   | ✅ |
| Export to CSV               | ✅ |
| Dark modern UI              | ✅ |

---

## 🛠 Requirements

| Tool    | Version  |
|---------|----------|
| Java JDK | 11 or higher |
| Apache Maven | 3.6+ |

---

## 🚀 How to Run (Step-by-Step)

### Option A — Maven (Recommended)

**Step 1: Install Java JDK 11+**
- Download from: https://adoptium.net/
- Verify: `java -version`

**Step 2: Install Apache Maven**
- Download from: https://maven.apache.org/download.cgi
- Verify: `mvn -version`

**Step 3: Clone / download this project**
```bash
cd ExpenseTracker
```

**Step 4: Build the project**
```bash
mvn clean package
```
This downloads JFreeChart automatically and creates `target/ExpenseTracker.jar`.

**Step 5: Run the app**
```bash
java -jar target/ExpenseTracker.jar
```

Or double-click `ExpenseTracker.jar` if your OS supports it.

---

### Option B — IntelliJ IDEA (No Maven needed)

1. Open IntelliJ → `File > New > Project from Existing Sources` → select `ExpenseTracker/`
2. Choose **Maven** as the build system
3. IntelliJ auto-imports dependencies
4. Right-click `MainUI.java` → **Run 'MainUI.main()'**

---

### Option C — Eclipse

1. `File > Import > Maven > Existing Maven Projects`
2. Select the `ExpenseTracker/` folder
3. Right-click `MainUI.java` → `Run As > Java Application`

---

## 📦 Dependencies (Auto-downloaded by Maven)

| Library    | Version | Purpose |
|------------|---------|---------|
| JFreeChart | 1.5.4   | Pie & Bar charts |
| JCommon    | (included with JFreeChart) | Chart utilities |

> Maven downloads these automatically. No manual JAR downloads needed!

---

## 💾 Data Storage

- Expenses are saved to: `~/expense_tracker_data.csv` (your home folder)
- Format: `id,amount,category,date,note`
- Data persists across app restarts automatically

---

## 📊 Charts

Click **"📊 Show Charts"** in the app to open the Analytics Dashboard:
- **Pie Chart** — Category-wise expense breakdown with percentages
- **Bar Chart** — Monthly expense trend over time
- **Summary Cards** — Total spent, this month, top category, transaction count

---

## 📤 Export

Click **"⬇ Export CSV"** to save your expenses to any CSV file.  
Open with Excel, Google Sheets, or any spreadsheet software.

---

## 🎨 UI Overview

```
┌─────────────────────────────────────────────┐
│ 💸 Expense Tracker    Total: ₹X  Month: ₹Y  │  ← Header + Stats
├─────────────────────────────────────────────┤
│ Add New Expense                              │
│ [Amount] [Category ▼] [Date] [Note........] │  ← Input Form
│ [＋ Add] [✕ Clear]  [📊 Charts] [⬇ Export] │
├─────────────────────────────────────────────┤
│ All Expenses              Filter: [All ▼]   │
│ ┌────┬────────┬──────────┬────────┬───────┐ │
│ │ #  │ Amount │ Category │  Date  │ Note  │ │  ← Table
│ ├────┼────────┼──────────┼────────┼───────┤ │
│ │ 1  │ ₹250   │ 🍔 Food  │2025-06 │Lunch  │ │
│ └────┴────────┴──────────┴────────┴───────┘ │
│ [✏ Edit Selected]  [🗑 Delete Selected]      │
├─────────────────────────────────────────────┤
│ Data saved to: ~/expense_tracker_data.csv    │  ← Status Bar
└─────────────────────────────────────────────┘
```

---

## 🧰 OOP Concepts Used

| Concept       | Where Used |
|---------------|-----------|
| Encapsulation | `Expense.java` — private fields, getters/setters |
| Enum          | `Expense.Category` — fixed category list |
| Abstraction   | `ExpenseManager` — hides storage details from UI |
| Separation of Concerns | Model / Logic / UI / Storage / Charts |
| Stream API    | `ExpenseManager` — filtering and aggregation |

---

## 🐛 Troubleshooting

**"java is not recognized"** → Install JDK and add to PATH  
**"mvn is not recognized"** → Install Maven and add to PATH  
**Charts don't show** → Ensure `mvn package` ran successfully (JFreeChart downloaded)  
**Double-click JAR doesn't work** → Run `java -jar target/ExpenseTracker.jar` from terminal

---

## 🚀 Future Improvements

- 🔐 User authentication system
- 🗄️ MySQL database integration
- 📱 Convert to JavaFX or web version
- 📈 Advanced analytics dashboard

---

## 🚀 Download Application
[Download Expense Tracker](https://github.com/Gahlawat-9/expense-tracker-java/releases/tag/v1.0)

---

*Built with ❤️ using Java 11, Swing, and JFreeChart*
