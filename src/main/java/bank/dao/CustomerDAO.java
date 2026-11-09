package bank.dao;

import bank.exception.BankException;
import bank.model.Customer;
import bank.model.IndividualCustomer;
import bank.model.OrganisationCustomer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//All SQL for the customers table:Create,Read,Update,Delete
public class CustomerDAO {

    // CREATE. One table holds both kinds of customer, so the columns that do not apply stay NULL.
    public void insert(Customer c) {
        String sql = "INSERT INTO customers(customer_id, customer_type, first_name, surname, national_id, "
                + "organisation_name, registration_number, contact_person, address, phone, email) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Database.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCustomerId());
            ps.setString(2, c.getCustomerType());
            if (c instanceof IndividualCustomer) {
                IndividualCustomer p = (IndividualCustomer) c;
                ps.setString(3, p.getFirstName());
                ps.setString(4, p.getSurname());
                ps.setString(5, p.getIdentityNumber());
                ps.setNull(6, java.sql.Types.VARCHAR);
                ps.setNull(7, java.sql.Types.VARCHAR);
                ps.setNull(8, java.sql.Types.VARCHAR);
            } else {
                OrganisationCustomer o = (OrganisationCustomer) c;
                ps.setNull(3, java.sql.Types.VARCHAR);
                ps.setNull(4, java.sql.Types.VARCHAR);
                ps.setNull(5, java.sql.Types.VARCHAR);
                ps.setString(6, o.getDisplayName());
                ps.setString(7, o.getIdentityNumber());
                ps.setString(8, o.getContactPerson());
            }
            ps.setString(9, c.getAddress());
            ps.setString(10, c.getPhone());
            ps.setString(11, c.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    // READ (one). Returns null when there is no such customer.
    public Customer findById(String customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (Connection con = Database.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return makeCustomer(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    // READ (all)
    public List<Customer> findAll() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY customer_id";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(makeCustomer(rs));
            }
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
        return list;
    }

    // READ (check). Is this Omang / registration number already used?
    public boolean identityExists(String identityNumber) {
        String sql = "SELECT COUNT(*) FROM customers WHERE national_id = ? OR registration_number = ?";
        try (Connection con = Database.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, identityNumber);
            ps.setString(2, identityNumber);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    // READ (count). Used to make the next customer ID.
    public int count() {
        String sql = "SELECT COUNT(*) FROM customers";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    // UPDATE. Only the contact details can change.
    public void update(Customer c) {
        String sql = "UPDATE customers SET address = ?, phone = ?, email = ? WHERE customer_id = ?";
        try (Connection con = Database.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getAddress());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getCustomerId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    // DELETE. Fails if the customer still has accounts (foreign key).
    public void delete(String customerId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (Connection con = Database.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BankException("Cannot delete this customer: " + e.getMessage());
        }
    }

    // Turns one database row into an IndividualCustomer or an OrganisationCustomer
    private Customer makeCustomer(ResultSet rs) throws SQLException {
        String id = rs.getString("customer_id");
        String address = rs.getString("address");
        String phone = rs.getString("phone");
        String email = rs.getString("email");
        if (rs.getString("customer_type").equals("Individual")) {
            return new IndividualCustomer(id, rs.getString("first_name"), rs.getString("surname"),
                    rs.getString("national_id"), address, phone, email);
        }
        return new OrganisationCustomer(id, rs.getString("organisation_name"), rs.getString("registration_number"),
                rs.getString("contact_person"), address, phone, email);
    }
}

