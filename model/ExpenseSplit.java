package model;

import java.math.BigDecimal;

public class ExpenseSplit {
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
}