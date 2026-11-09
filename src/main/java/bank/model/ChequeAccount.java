package bank.model;

import bank.exception.BankException;

// For salaries,only for employed customers,earns no interest.
public class ChequeAccount extends Account {
    private String employerName;
    private String employerAddress;
    private String employerContact;

    // Used by the DAO when loading from the database (no opening deposit)
    public ChequeAccount(String accountNumber, Customer owner, String branch,
                         String employerName, String employerAddress, String employerContact) {
        super(accountNumber, owner, branch);
        if (employerName.isBlank() || employerAddress.isBlank() || employerContact.isBlank()) {
            throw new BankException("A Cheque account needs the employer's name, address and contact.");
        }
        this.employerName = employerName;
        this.employerAddress = employerAddress;
        this.employerContact = employerContact;
    }

    public ChequeAccount(String accountNumber, Customer owner, String branch, double openingDeposit,
                         String employerName, String employerAddress, String employerContact) {
        this(accountNumber, owner, branch, employerName, employerAddress, employerContact);
        if (openingDeposit > 0) {
            deposit(openingDeposit, "Opening deposit");
        }
    }

    @Override
    public void withdraw(double amount) {
        takeMoneyOut(amount);
    }

    @Override
    public String getAccountType() { return "Cheque"; }

    public String getEmployerName() { return employerName; }
    public String getEmployerAddress() { return employerAddress; }
    public String getEmployerContact() { return employerContact; }
}
