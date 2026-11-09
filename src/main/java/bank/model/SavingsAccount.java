package bank.model;

import bank.exception.BankException;

//Pays 0.05% monthly interest. No withdrawals
public class SavingsAccount extends Account implements InterestBearing {
    private static final double RATE = 0.0005;

    // Used by the DAO when loading from the database (no opening deposit)
    public SavingsAccount(String accountNumber, Customer owner, String branch) {
        super(accountNumber, owner, branch);
    }

    public SavingsAccount(String accountNumber, Customer owner, String branch, double openingDeposit) {
        this(accountNumber, owner, branch);
        if (openingDeposit > 0) {
            deposit(openingDeposit, "Opening deposit");
        }
    }

    @Override
    public void withdraw(double amount) {
        throw new BankException("A Savings account does not allow withdrawals.");
    }

    @Override
    public double getInterestRate() { return RATE; }

    @Override
    public double payInterest() {
        double interest = Math.round(getBalance() * RATE * 100) / 100.0;   // 2 decimal places
        if (interest > 0) {
            addInterest(interest);
        }
        return interest;
    }

    @Override
    public String getAccountType() { return "Savings"; }
}
