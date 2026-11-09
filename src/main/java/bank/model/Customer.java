package bank.model;

import java.util.ArrayList;
import java.util.List;

//Abstract parent of the two kinds of customer. It cannot be created directly/
public abstract class Customer {
    private String customerId;
    private String address;
    private String phone;
    private String email;
    private List<Account> accounts = new ArrayList<>();   // one customer, many accounts

    public Customer(String customerId, String address, String phone, String email) {
        this.customerId = customerId;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    // Each customer type decides these (overriding + polymorphism)
    public abstract String getDisplayName();
    public abstract String getIdentityNumber();   // Omang for a person, registration number for an organisation
    public abstract String getCustomerType();

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public String getCustomerId() { return customerId; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public List<Account> getAccounts() { return accounts; }

    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return customerId + " - " + getDisplayName();
    }
}
