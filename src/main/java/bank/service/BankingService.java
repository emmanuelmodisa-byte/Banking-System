package bank.service;

import bank.dao.AccountDAO;
import bank.dao.CustomerDAO;
import bank.dao.Database;
import bank.dao.TransactionDAO;
import bank.dao.UserDAO;
import bank.exception.BankException;
import bank.model.*;

import java.util.List;

//The "brain" of the system. Controllers call this class
//It applies the banking rules (using the model classes) and saves everything with the DAOs.
public class BankingService {
    private UserDAO userDAO = new UserDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private AccountDAO accountDAO = new AccountDAO();
    private TransactionDAO transactionDAO = new TransactionDAO();

    public BankingService() {
        Database.createTables();   // makes the tables and the starter logins if they are missing
    }

    //Login
    public User login(String username, String password) {
        User u = userDAO.findByUsername(username);
        if (u == null || !u.passwordMatches(password)) {
            throw new BankException("Wrong username or password.");
        }
        return u;
    }

    // Customers
    public Customer registerIndividual(String firstName, String surname, String nationalId,
                                       String address, String phone, String email) {
        if (firstName.isBlank() || surname.isBlank() || nationalId.isBlank()) {
            throw new BankException("First name, surname and national ID are required.");
        }
        checkNotRegistered(nationalId);
        Customer c = new IndividualCustomer(newCustomerId(), firstName, surname, nationalId,
                address, phone, email);
        customerDAO.insert(c);
        return c;
    }

    public Customer registerOrganisation(String organisationName, String registrationNumber, String contactPerson,
                                         String address, String phone, String email) {
        if (organisationName.isBlank() || registrationNumber.isBlank() || contactPerson.isBlank()) {
            throw new BankException("Organisation name, registration number and contact person are required.");
        }
        checkNotRegistered(registrationNumber);
        Customer c = new OrganisationCustomer(newCustomerId(), organisationName, registrationNumber,
                contactPerson, address, phone, email);
        customerDAO.insert(c);
        return c;
    }

    private void checkNotRegistered(String identityNumber) {
        if (customerDAO.identityExists(identityNumber)) {
            throw new BankException("This ID / registration number is already registered.");
        }
    }

    // Finds a customer and loads that customer's accounts
    public Customer findCustomer(String customerId) {
        Customer c = customerDAO.findById(customerId);
        if (c == null) {
            throw new BankException("Customer not found.");
        }
        loadAccounts(c);
        return c;
    }

    // Accounts
    public Account openSavings(String customerId, double openingDeposit) {
        Customer owner = findCustomer(customerId);
        return save(new SavingsAccount(newAccountNumber(), owner, "Main Branch", openingDeposit));
    }

    public Account openInvestment(String customerId, double openingDeposit) {
        Customer owner = findCustomer(customerId);
        return save(new InvestmentAccount(newAccountNumber(), owner, "Main Branch", openingDeposit));
    }

    public Account openCheque(String customerId, double openingDeposit,
                              String employerName, String employerAddress, String employerContact) {
        Customer owner = findCustomer(customerId);
        if (!(owner instanceof IndividualCustomer)) {
            throw new BankException("A Cheque account can only be opened for an employed individual.");
        }
        return save(new ChequeAccount(newAccountNumber(), owner, "Main Branch", openingDeposit,
                employerName, employerAddress, employerContact));
    }

    public Account findAccount(String accountNumber) {
        Account a = accountDAO.findByNumber(accountNumber);
        if (a == null) {
            throw new BankException("Account not found.");
        }
        return a;
    }

    // Transactions
    public void deposit(String accountNumber, double amount) {
        Account a = findAccount(accountNumber);
        a.deposit(amount);              // the model checks the rules
        saveLatestChange(a);            // then we save the new balance and the new transaction
    }

    public void withdraw(String accountNumber, double amount) {
        Account a = findAccount(accountNumber);
        a.withdraw(amount);             // polymorphism: each account type decides
        saveLatestChange(a);
    }

    public List<Transaction> getTransactions(String accountNumber) {
        findAccount(accountNumber);     // makes sure the account exists
        return transactionDAO.findByAccount(accountNumber);
    }

    // Interest (manager only)
    public double payMonthlyInterest(User user) {
        if (!user.isManager()) {
            throw new BankException("Only a manager can pay interest.");
        }
        double total = 0;
        for (Account a : accountDAO.findAll()) {
            if (a instanceof InterestBearing) {              // Cheque accounts are skipped
                InterestBearing ib = (InterestBearing) a;
                double interest = ib.payInterest();
                if (interest > 0) {
                    saveLatestChange(a);
                    total = total + interest;
                }
            }
        }
        return Math.round(total * 100) / 100.0;
    }

    // Lists for the screens
    public List<Customer> getCustomers() {
        List<Customer> customers = customerDAO.findAll();
        for (Customer c : customers) {
            loadAccounts(c);
        }
        return customers;
    }

    public List<Account> getAccounts() {
        return accountDAO.findAll();
    }

    // helpers
    private void loadAccounts(Customer c) {
        for (Account a : accountDAO.findByCustomer(c)) {
            c.addAccount(a);
        }
    }

    // Saves a brand new account and its opening transaction
    private Account save(Account account) {
        accountDAO.insert(account);
        for (Transaction t : account.getTransactions()) {
            transactionDAO.insert(account.getAccountNumber(), t);
        }
        account.getOwner().addAccount(account);
        return account;
    }

    // After a deposit, withdrawal or interest: save the new balance and the newest transaction
    private void saveLatestChange(Account account) {
        accountDAO.updateBalance(account);
        List<Transaction> all = account.getTransactions();
        transactionDAO.insert(account.getAccountNumber(), all.get(all.size() - 1));
    }

    private String newCustomerId() {
        return "C" + (customerDAO.count() + 1);
    }

    private String newAccountNumber() {
        return String.valueOf(1001 + accountDAO.count());
    }
}
