package dao;

import interfaces.RoommateRepository;
import model.Roommate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Roommate Data Access Object implementing RoommateRepository interface.
 * 
 * This class demonstrates:
 * - Interface implementation (RoommateRepository)
 * - Polymorphism through interface implementation
 * - JDBC operations with proper exception handling
 * - try-with-resources for resource management
 * 
 * Syllabus Topics:
 * - Interface implementation
 * - Polymorphism
 * - JDBC
 * - PreparedStatement
 * - Exception handling
 * - try-with-resources
 * 
 * @see interfaces.RoommateRepository
 */
public class RoommateDAO implements RoommateRepository {

    @Override
    public List<Roommate> getAllRoommates() throws SQLException {
        List<Roommate> roommates = new ArrayList<>();
        String sql = "SELECT member_id, name, phone, upi_id FROM Roommates ORDER BY name";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Roommate r = new Roommate();
                r.setMemberId(rs.getInt("member_id"));
                r.setName(rs.getString("name"));
                r.setPhone(rs.getString("phone"));
                r.setUpiId(rs.getString("upi_id"));
                roommates.add(r);
            }
        }
        return roommates;
    }

    @Override
    public model.Roommate getRoommateById(int memberId) throws SQLException {
        String sql = "SELECT member_id, name, phone, upi_id FROM Roommates WHERE member_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Roommate r = new Roommate();
                    r.setMemberId(rs.getInt("member_id"));
                    r.setName(rs.getString("name"));
                    r.setPhone(rs.getString("phone"));
                    r.setUpiId(rs.getString("upi_id"));
                    return r;
                }
            }
        }
        return null;
    }

    @Override
    public int addRoommate(Roommate roommate) throws SQLException {
        String sql = "INSERT INTO Roommates (name, phone, upi_id) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, roommate.getName());
            stmt.setString(2, roommate.getPhone());
            stmt.setString(3, roommate.getUpiId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating roommate failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating roommate failed, no ID obtained.");
                }
            }
        }
    }

    @Override
    public boolean updateRoommate(Roommate roommate) throws SQLException {
        String sql = "UPDATE Roommates SET name = ?, phone = ?, upi_id = ? WHERE member_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roommate.getName());
            stmt.setString(2, roommate.getPhone());
            stmt.setString(3, roommate.getUpiId());
            stmt.setInt(4, roommate.getMemberId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteRoommate(int memberId) throws SQLException {
        String sql = "DELETE FROM Roommates WHERE member_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            return stmt.executeUpdate() > 0;
        }
    }
}