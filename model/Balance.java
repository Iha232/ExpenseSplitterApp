package model;

import java.math.BigDecimal;

public class Balance {
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
}