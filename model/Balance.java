package model;

import interfaces.Exportable;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

/**
 * Balance model implementing Exportable interface.
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
public class Balance implements Exportable {
    private int memberId;
    private String memberName;
    private String upiId;
    private BigDecimal totalPaid;
    private BigDecimal totalOwed;
    private BigDecimal netBalance;

    public Balance() {}

    public Balance(int memberId, String memberName, String upiId, BigDecimal totalPaid, BigDecimal totalOwed) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.upiId = upiId;
        this.totalPaid = totalPaid;
        this.totalOwed = totalOwed;
        this.netBalance = totalPaid.subtract(totalOwed);
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public BigDecimal getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(BigDecimal totalPaid) {
        this.totalPaid = totalPaid;
    }

    public BigDecimal getTotalOwed() {
        return totalOwed;
    }

    public void setTotalOwed(BigDecimal totalOwed) {
        this.totalOwed = totalOwed;
    }

    public BigDecimal getNetBalance() {
        return netBalance;
    }

    public void setNetBalance(BigDecimal netBalance) {
        this.netBalance = netBalance;
    }

    public String getStatus() {
        if (netBalance.compareTo(BigDecimal.ZERO) > 0) {
            return "Gets back: " + netBalance;
        } else if (netBalance.compareTo(BigDecimal.ZERO) < 0) {
            return "Owes: " + netBalance.abs();
        } else {
            return "Settled";
        }
    }
    
    @Override
    public void export(PrintWriter writer) throws IOException {
        writer.println("Balance for: " + memberName + " (ID: " + memberId + ")");
        writer.println("  UPI ID: " + (upiId != null ? upiId : "N/A"));
        writer.println("  Total Paid: ₹" + (totalPaid != null ? totalPaid.setScale(2, java.math.RoundingMode.HALF_UP) : "0.00"));
        writer.println("  Total Owed: ₹" + (totalOwed != null ? totalOwed.setScale(2, java.math.RoundingMode.HALF_UP) : "0.00"));
        writer.println("  Net Balance: ₹" + (netBalance != null ? netBalance.setScale(2, java.math.RoundingMode.HALF_UP) : "0.00"));
        writer.println("  Status: " + getStatus());
        writer.println();
    }
    
    @Override
    public String toExportString() {
        return String.format("Balance{memberId=%d, memberName='%s', totalPaid=%s, totalOwed=%s, netBalance=%s, status='%s'}", 
            memberId, memberName, totalPaid, totalOwed, netBalance, getStatus());
    }
}