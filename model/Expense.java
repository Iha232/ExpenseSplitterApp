package model;

import interfaces.Exportable;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Expense model implementing Exportable interface.
 * 
 * This class demonstrates:
 * - Interface implementation (Exportable)
 * - Export functionality with PrintWriter
 * - Complex object export with nested objects
 * 
 * Syllabus Topics:
 * - Interface implementation
 * - PrintWriter for formatted output
 * - Complex object export
 * - Date formatting
 * 
 * @see interfaces.Exportable
 */
public class Expense implements Exportable {
    private int expenseId;
    private int payerId;
    private String description;
    private BigDecimal totalAmount;
    private LocalDate expenseDate;
    private List<ExpenseSplit> splits;
    private String payerName;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public Expense() {}

    public Expense(int expenseId, int payerId, String description, BigDecimal totalAmount, LocalDate expenseDate) {
        this.expenseId = expenseId;
        this.payerId = payerId;
        this.description = description;
        this.totalAmount = totalAmount;
        this.expenseDate = expenseDate;
    }

    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public int getPayerId() {
        return payerId;
    }

    public void setPayerId(int payerId) {
        this.payerId = payerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public List<ExpenseSplit> getSplits() {
        return splits;
    }

    public void setSplits(List<ExpenseSplit> splits) {
        this.splits = splits;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }
    
    @Override
    public void export(PrintWriter writer) throws IOException {
        writer.println("Expense ID: " + expenseId);
        writer.println("  Date: " + (expenseDate != null ? expenseDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "N/A"));
        writer.println("  Description: " + (description != null ? description : "N/A"));
        writer.println("  Payer: " + (payerName != null ? payerName : "ID: " + payerId));
        writer.println("  Amount: " + (totalAmount != null ? "₹" + totalAmount.setScale(2, java.math.RoundingMode.HALF_UP) : "N/A"));
        if (splits != null && !splits.isEmpty()) {
            writer.println("  Splits:");
            for (ExpenseSplit split : splits) {
                writer.println("    - " + split.getMemberName() + ": " + 
                    (split.getShareAmount() != null ? "₹" + split.getShareAmount().setScale(2, java.math.RoundingMode.HALF_UP) : "N/A") +
                    " (" + (split.isSettled() ? "SETTLED" : "PENDING") + ")");
            }
        }
        writer.println();
    }
    
    @Override
    public String toExportString() {
        return String.format("Expense{id=%d, description='%s', amount=%s, date=%s, payer='%s'}", 
            expenseId, description, totalAmount, expenseDate, payerName);
    }
}