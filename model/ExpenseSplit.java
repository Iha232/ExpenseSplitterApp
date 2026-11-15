package model;

import interfaces.Exportable;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

/**
 * ExpenseSplit model implementing Exportable interface.
 * 
 * This class demonstrates:
 * - Interface implementation (Exportable)
 * - Export functionality with PrintWriter
 * 
 * Syllabus Topics:
 * - Interface implementation
 * - PrintWriter for formatted output
 * 
 * @see interfaces.Exportable
 */
public class ExpenseSplit implements Exportable {
    private int splitId;
    private int expenseId;
    private int memberId;
    private BigDecimal shareAmount;
    private boolean isSettled;
    private String memberName;

    public ExpenseSplit() {}

    public ExpenseSplit(int expenseId, int memberId, BigDecimal shareAmount) {
        this.expenseId = expenseId;
        this.memberId = memberId;
        this.shareAmount = shareAmount;
        this.isSettled = false;
    }

    public int getSplitId() {
        return splitId;
    }

    public void setSplitId(int splitId) {
        this.splitId = splitId;
    }

    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public BigDecimal getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(BigDecimal shareAmount) {
        this.shareAmount = shareAmount;
    }

    public boolean isSettled() {
        return isSettled;
    }

    public void setSettled(boolean settled) {
        isSettled = settled;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
    
    @Override
    public void export(PrintWriter writer) throws IOException {
        writer.println("  Split for: " + (memberName != null ? memberName : "Member ID: " + memberId));
        writer.println("    Amount: " + (shareAmount != null ? "₹" + shareAmount.setScale(2, java.math.RoundingMode.HALF_UP) : "N/A"));
        writer.println("    Status: " + (isSettled ? "SETTLED" : "PENDING"));
        writer.println();
    }
    
    @Override
    public String toExportString() {
        return String.format("ExpenseSplit{id=%d, expenseId=%d, memberId=%d, amount=%s, settled=%s}", 
            splitId, expenseId, memberId, shareAmount, isSettled);
    }
}