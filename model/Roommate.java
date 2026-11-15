package model;

import interfaces.Exportable;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Roommate model implementing Exportable interface.
 * 
 * This class demonstrates:
 * - Interface implementation (Exportable)
 * - Serialization support
 * - Export functionality with PrintWriter
 * 
 * Syllabus Topics:
 * - Interface implementation
 * - PrintWriter for formatted output
 * - Serialization
 * 
 * @see interfaces.Exportable
 */
public class Roommate implements Exportable {
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
    
    @Override
    public void export(PrintWriter writer) throws IOException {
        writer.println("Roommate: " + name);
        writer.println("  ID: " + memberId);
        writer.println("  Phone: " + (phone != null ? phone : "N/A"));
        writer.println("  UPI ID: " + (upiId != null ? upiId : "N/A"));
        writer.println();
    }
    
    @Override
    public String toExportString() {
        return String.format("Roommate{id=%d, name='%s', phone='%s', upiId='%s'}", 
            memberId, name, phone != null ? phone : "N/A", upiId != null ? upiId : "N/A");
    }
}