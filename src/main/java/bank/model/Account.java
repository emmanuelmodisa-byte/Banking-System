package bank.model;

import bank.exception.BankException;
import java.util.ArrayList;
import java.util.List;

//Abstract parent of all accounts. It cannot be created directly
public abstract class Account {
    private String accountNumber;
    private Customer owner;          // an account cannot exist without a customer
    private String branch;
    private double balance;
    private List<Transaction> transactions = new ArrayList<>();

    public Account(String accountNumber, Customer owner, String branch) {
        if (owner == null) {
            throw new BankException("An account must belong to a customer.");
        }
        this.accountNumber = accountNumber;
        this.owner = owner;
        this.branch = branch;
    }

    // Overloading: same name, different parameters
    public void deposit(double amount) {
        deposit(amount, "Deposit");
    }

    public void deposit(double amount, String note) {
        if (amount <= 0) {
            throw new BankException("Amount must be more than zero.");
        }
        balance = balance + amount;
        transactions.add(new Transaction(TransactionType.DEPOSIT, amount, note));
    }

    // Abstract: each account type writes its own withdraw rules (overriding)
    public abstract void withdraw(double amount);

    public abstract String getAccountType();

    // Shared by the account types that DO allow withdrawals
    protected void takeMoneyOut(double amount) {
        if (amount <= 0) {
            throw new BankException("Amount must be more than zero.");
        }
        if (amount > balance) {
            throw new BankException("Not enough money. Balance is BWP" + balance);
        }
        balance = balance - amount;
        transactions.add(new Transaction(TransactionType.WITHDRAWAL, amount, "Withdrawal"));
    }

    // Shared by the account types that earn interest
    protected void addInterest(double amount) {
        balance = balance + amount;
        transactions.add(new Transaction(TransactionType.INTEREST, amount, "Monthly interest"));
    }

    // Used by the DAO when it loads an account from the database
    public void restore(double savedBalance, List<Transaction> savedTransactions) {
        this.balance = savedBalance;
        this.transactions = savedTransactions;
    }

    public String getAccountNumber() { return accountNumber; }
    public Customer getOwner() { return owner; }
    public String getBranch() { return branch; }
    public double getBalance() { return balance; }
    public List<Transaction> getTransactions() { return transactions; }

    @Override
    public String toString() {
        return getAccountType() + " " + accountNumber + "  balance: BWP" + balance;
    }
}
