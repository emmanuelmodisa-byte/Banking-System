package bank.dao;

import bank.exception.BankException;
import bank.model.Account;
import bank.model.ChequeAccount;
import bank.model.Customer;
import bank.model.InvestmentAccount;
import bank.model.SavingsAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

// All SQL for the accounts table: Create, Read, Update, Delete
public class AccountDAO {
    private CustomerDAO customerDAO = new CustomerDAO();
    private TransactionDAO transactionDAO = new TransactionDAO();

    // CREATE. Saves the account row only. The service saves its transactions with TransactionDAO.
    public void insert(Account a) {
        String sql = "INSERT INTO accounts(account_number, account_type, customer_id, branch, balance, "
                + "employer_name, employer_address, employer_contact) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Database.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getAccountNumber());
            ps.setString(2, a.getAccountType());
            ps.setString(3, a.getOwner().getCustomerId());
            ps.setString(4, a.getBranch());
            ps.setDouble(5, a.getBalance());
            if (a instanceof ChequeAccount) {
                ChequeAccount c = (ChequeAccount) a;
                ps.setString(6, c.getEmployerName());
                ps.setString(7, c.getEmployerAddress());
                ps.setString(8, c.getEmployerContact());
            } else {
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.VARCHAR);
                ps.setNull(8, Types.VARCHAR);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    // READ (one). Returns null when there is no such account.
    public Account findByNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection con = Database.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer owner = customerDAO.findById(rs.getString("customer_id"));
                    return makeAccount(rs, owner);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    // READ (all the accounts of one customer)
    public List<Account> findByCustomer(Customer owner) {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ? ORDER BY account_number";
        try (Connection con = Database.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, owner.getCustomerId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(makeAccount(rs, owner));
                }
            }
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
        return list;
    }

    // READ (every account in the bank)
    public List<Account> findAll() {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts ORDER BY account_number";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Customer owner = customerDAO.findById(rs.getString("customer_id"));
                list.add(makeAccount(rs, owner));
            }
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
        return list;
    }

    // READ (count). Used to make the next account number.
    public int count() {
        String sql = "SELECT COUNT(*) FROM accounts";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    // UPDATE. Only the balance changes after an account is opened.
    public void updateBalance(Account a) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (Connection con = Database.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, a.getBalance());
            ps.setString(2, a.getAccountNumber());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    // DELETE. The transactions go first because they point to the account (foreign key).
    public void delete(String accountNumber) {
        transactionDAO.deleteByAccount(accountNumber);
        String sql = "DELETE FROM accounts WHERE account_number = ?";
        try (Connection con = Database.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    // Turns one database row into a Savings, Investment or Cheque account, with its saved transactions
    private Account makeAccount(ResultSet rs, Customer owner) throws SQLException {
        String number = rs.getString("account_number");
        String type = rs.getString("account_type");
        String branch = rs.getString("branch");
        Account account;
        if (type.equals("Savings")) {
            account = new SavingsAccount(number, owner, branch);
        } else if (type.equals("Investment")) {
            account = new InvestmentAccount(number, owner, branch);
        } else {
            account = new ChequeAccount(number, owner, branch, rs.getString("employer_name"),
                    rs.getString("employer_address"), rs.getString("employer_contact"));
        }
        account.restore(rs.getDouble("balance"), transactionDAO.findByAccount(number));
        return account;
    }
}

