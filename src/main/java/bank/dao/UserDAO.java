package bank.dao;

import bank.exception.BankException;
import bank.model.Role;
import bank.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//All SQL for the users table: Create, Read, Update, Delete
public class UserDAO {

    // CREATE
    public void insert(String username, String password, Role role) {
        String sql = "INSERT INTO users(username, password, role) VALUES (?, ?, ?)";
        try (Connection con = Database.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role.name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    // READ (one). Returns null when the username does not exist.
    public User findByUsername(String username) {
        String sql = "SELECT username, password, role FROM users WHERE username = ?";
        try (Connection con = Database.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return makeUser(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    // READ (all)
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT username, password, role FROM users ORDER BY username";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(makeUser(rs));
            }
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
        return list;
    }

    // UPDATE
    public void updatePassword(String username, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection con = Database.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    // DELETE
    public void delete(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection con = Database.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    // Turns one database row into a User object
    private User makeUser(ResultSet rs) throws SQLException {
        return new User(rs.getString("username"), rs.getString("password"), Role.valueOf(rs.getString("role")));
    }
}
