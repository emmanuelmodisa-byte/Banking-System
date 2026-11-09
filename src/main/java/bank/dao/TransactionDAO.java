package bank.dao;

import bank.exception.BankException;
import bank.model.Transaction;
import bank.model.TransactionType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

//All SQL for the transactions table.
//A bank transaction is a permanent record, so there is no update method on purpose
public class TransactionDAO {

    // CREATE
    public void insert(String accountNumber, Transaction t) {
        String sql = "INSERT INTO transactions(account_number, tx_type, amount, note, created_at) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Database.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ps.setString(2, t.getType().name());
            ps.setDouble(3, t.getAmount());
            ps.setString(4, t.getNote());
            ps.setTimestamp(5, Timestamp.valueOf(t.getDateTime()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    // READ. Oldest first.
    public List<Transaction> findByAccount(String accountNumber) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT tx_type, amount, note, created_at FROM transactions "
                + "WHERE account_number = ? ORDER BY transaction_id";
        try (Connection con = Database.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Transaction(
                            TransactionType.valueOf(rs.getString("tx_type")),
                            rs.getDouble("amount"),
                            rs.getString("note"),
                            rs.getTimestamp("created_at").toLocalDateTime()));
                }
            }
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
        return list;
    }

    // DELETE. Removes all the transactions of one account (needed before deleting the account).
    public void deleteByAccount(String accountNumber) {
        String sql = "DELETE FROM transactions WHERE account_number = ?";
        try (Connection con = Database.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }
}
