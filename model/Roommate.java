package model;

public class Roommate {
    private int memberId;
    private String name;
    private String phone;
    private String upiId;

    public Roommate() {}

    public Roommate(int memberId, String name, String phone, String upiId) {
        this.memberId = memberId;
        this.name = name;
        this.phone = phone;
        this.upiId = upiId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    @Override
    public String toString() {
        return name + (upiId != null && !upiId.isEmpty() ? " (" + upiId + ")" : "");
    }
}