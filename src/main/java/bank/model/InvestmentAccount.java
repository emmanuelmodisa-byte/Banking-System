package bank.model;

import bank.exception.BankException;

//Pays 5% monthly interest. Deposits and withdrawals allowed. Opens with at least BWP500
public class InvestmentAccount extends Account implements InterestBearing {
    private static final double RATE = 0.05;
    private static final double MINIMUM_OPENING = 500;

    // Used by the DAO when loading from the database (no opening deposit)
    public InvestmentAccount(String accountNumber, Customer owner, String branch) {
        super(accountNumber, owner, branch);
    }

    public InvestmentAccount(String accountNumber, Customer owner, String branch, double openingDeposit) {
        this(accountNumber, owner, branch);
        if (openingDeposit < MINIMUM_OPENING) {
            throw new BankException("An Investment account needs at least BWP500 to open.");
        }
        deposit(openingDeposit, "Opening deposit");
    }

    @Override
    public void withdraw(double amount) {
        takeMoneyOut(amount);
    }

    @Override
    public double getInterestRate() { return RATE; }

    @Override
    public double payInterest() {
        double interest = Math.round(getBalance() * RATE * 100) / 100.0;
        if (interest > 0) {
            addInterest(interest);
        }
        return interest;
    }

    @Override
    public String getAccountType() { return "Investment"; }
}
