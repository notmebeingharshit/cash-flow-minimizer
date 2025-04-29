# 💸 Cash Flow Minimizer

A Java program to minimize the number of transactions between multiple banks using graph-based debt settlement logic. It supports different payment modes and handles cases where an intermediary (World Bank) is required for compatibility.

## 🚀 Features

- Minimizes total cash flow transactions between banks
- Supports multiple payment modes (like UPI, NEFT, RTGS, etc.)
- Automatically uses an intermediary bank (World Bank) when needed
- Uses simple greedy algorithm for net settlement
- CLI-based input/output for banks, transactions, and results

## 🛠️ Technologies Used

- Java
- Data Structures (Graphs, HashMaps, Lists)
- Greedy Algorithm

## 🧪 How to Run

Compile and run the `CashFlowMinimizer.java` file using:

```bash
javac CashFlowMinimizer.java
java CashFlowMinimizer
