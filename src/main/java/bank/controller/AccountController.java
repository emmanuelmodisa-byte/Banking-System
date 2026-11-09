package bank.controller;

import bank.exception.BankException;
import bank.model.Account;
import bank.model.Transaction;
import bank.model.User;
import bank.service.BankingService;

import java.util.ArrayList;
import java.util.List;

//Placed in between TransactionView / AccountView and the service
public class AccountController {
    private BankingService service;
    private String message = "";

    public AccountController(BankingService service) {
        this.service = service;
    }

    // Open account
    public boolean openSavings(String customerId, String openingText) {
        try {
            Account a = service.openSavings(customerId, toNumber(openingText, true));
            message = "Opened " + a + ".";
            return true;
        } catch (BankException e) {
            message = e.getMessage();
            return false;
        }
    }

    public boolean openInvestment(String customerId, String openingText) {
        try {
            Account a = service.openInvestment(customerId, toNumber(openingText, true));
            message = "Opened " + a + ".";
            return true;
        } catch (BankException e) {
            message = e.getMessage();
            return false;
        }
    }

    public boolean openCheque(String customerId, String openingText,
                              String employerName, String employerAddress, String employerContact) {
        try {
            Account a = service.openCheque(customerId, toNumber(openingText, true),
                    employerName.trim(), employerAddress.trim(), employerContact.trim());
            message = "Opened " + a + ".";
            return true;
        } catch (BankException e) {
            message = e.getMessage();
            return false;
        }
    }

    //Deposit and withdraw
    public boolean deposit(String accountNumber, String amountText) {
        try {
            checkAccountChosen(accountNumber);
            service.deposit(accountNumber, toNumber(amountText, false));
            message = "Deposit successful.";
            return true;
        } catch (BankException e) {
            message = e.getMessage();
            return false;
        }
    }

    public boolean withdraw(String accountNumber, String amountText) {
        try {
            checkAccountChosen(accountNumber);
            service.withdraw(accountNumber, toNumber(amountText, false));
            message = "Withdrawal successful.";
            return true;
        } catch (BankException e) {
            message = e.getMessage();
            return false;
        }
    }

    // Interest (manager only)
    public boolean payInterest(User user) {
        try {
            double total = service.payMonthlyInterest(user);
            message = "Interest paid: BWP" + String.format("%.2f", total);
            return true;
        } catch (BankException e) {
            message = e.getMessage();
            return false;
        }
    }

    //Lists for the screens
    public List<Account> getAccountsFor(String customerId) {
        try {
            return service.findCustomer(customerId).getAccounts();
        } catch (BankException e) {
            message = e.getMessage();
            return new ArrayList<>();
        }
    }

    public List<Transaction> getTransactions(String accountNumber) {
        try {
            return service.getTransactions(accountNumber);
        } catch (BankException e) {
            message = e.getMessage();
            return new ArrayList<>();
        }
    }

    public String getMessage() { return message; }

    //input checks
    // Turns the text from a text field into a number. Empty opening deposit means zero.
    private double toNumber(String text, boolean emptyMeansZero) {
        if (text == null || text.isBlank()) {
            if (emptyMeansZero) {
                return 0;
            }
            throw new BankException("Enter an amount.");
        }
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            throw new BankException("The amount must be a number, for example 250.50");
        }
    }

    private void checkAccountChosen(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new BankException("Choose an account first.");
        }
    }
}
