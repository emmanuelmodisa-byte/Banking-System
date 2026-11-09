package bank;

import bank.dao.Database;
import bank.exception.BankException;
import bank.model.Account;
import bank.model.Customer;
import bank.model.Transaction;
import bank.model.User;
import bank.service.BankingService;

// Quick console test of the model, service and DAOs. It uses a temporary in-memory database
public class BankDemo {
    public static void main(String[] args) {
        Database.useMemory();   // nothing is saved to disk, so the demo can be run again and again
        BankingService bank = new BankingService();

        User teller = bank.login("teller", "teller123");
        User manager = bank.login("manager", "manager123");
        System.out.println("Logged in: " + teller.getUsername() + " (" + teller.getRole() + ")");

        Customer thato = bank.registerIndividual("Thato", "Molefe", "123456789",
                "Plot 123, Gaborone", "71234567", "thato@example.com");
        Customer acme = bank.registerOrganisation("Acme Traders", "BW0001234", "Neo Kgosi",
                "Plot 9, Francistown", "2412345", "info@acme.co.bw");

        String sav = bank.openSavings(thato.getCustomerId(), 1000).getAccountNumber();
        String inv = bank.openInvestment(thato.getCustomerId(), 500).getAccountNumber();
        String chq = bank.openCheque(thato.getCustomerId(), 0, "Acme Ltd", "Plot 9", "3951234").getAccountNumber();
        bank.openInvestment(acme.getCustomerId(), 2000);

        bank.deposit(chq, 8000);
        bank.withdraw(chq, 250.50);
        bank.withdraw(inv, 100);

        try { bank.withdraw(sav, 50); }                          catch (BankException e) { System.out.println("Rejected: " + e.getMessage()); }
        try { bank.withdraw(chq, 99999); }                       catch (BankException e) { System.out.println("Rejected: " + e.getMessage()); }
        try { bank.openInvestment(thato.getCustomerId(), 300); } catch (BankException e) { System.out.println("Rejected: " + e.getMessage()); }
        try { bank.openCheque(acme.getCustomerId(), 0, "x", "y", "z"); } catch (BankException e) { System.out.println("Rejected: " + e.getMessage()); }
        try { bank.login("teller", "wrong"); }                   catch (BankException e) { System.out.println("Rejected: " + e.getMessage()); }
        try { bank.payMonthlyInterest(teller); }                 catch (BankException e) { System.out.println("Rejected: " + e.getMessage()); }

        System.out.println("Interest paid by manager: BWP" + bank.payMonthlyInterest(manager));

        for (Customer c : bank.getCustomers()) {
            System.out.println("\n" + c.getCustomerType() + ": " + c);
            for (Account a : c.getAccounts()) {
                System.out.println("  " + a);
                for (Transaction t : a.getTransactions()) {
                    System.out.println("      " + t);
                }
            }
        }
    }
}
