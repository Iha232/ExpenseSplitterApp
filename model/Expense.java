package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Expense {
    private int expenseId;
    private int payerId;
    private String description;
    private BigDecimal totalAmount;
    private LocalDate expenseDate;
    private List<ExpenseSplit> splits;
    private String payerName;

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
}