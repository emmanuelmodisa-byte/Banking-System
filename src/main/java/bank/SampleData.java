package bank;

import bank.model.Customer;
import bank.service.BankingService;

//Sample records in the database before the presentation.
public class SampleData {
    public static void main(String[] args) {
        BankingService bank = new BankingService();

        Customer thato = bank.registerIndividual("Thato", "Molefe", "123456789", "Plot 123, Gaborone", "71234567", "thato@example.com");
        Customer naledi = bank.registerIndividual("Naledi", "Sebego", "234567890", "Plot 45, Francistown", "72345678", "naledi@example.com");
        Customer kabo = bank.registerIndividual("Kabo", "Dube", "345678901", "Plot 7, Maun", "73456789", "kabo@example.com");
        Customer acme = bank.registerOrganisation("Acme Traders", "BW0001234", "Neo Kgosi", "Plot 9, Francistown", "2412345", "info@acme.co.bw");
        Customer baobab = bank.registerOrganisation("Baobab Farms", "BW0005678", "Lesedi Phiri", "Plot 3, Palapye", "4921234", "farm@baobab.co.bw");

        String thatoSavings = bank.openSavings(thato.getCustomerId(), 1000).getAccountNumber();
        String thatoCheque = bank.openCheque(thato.getCustomerId(), 5000, "Gaborone City Council", "Main Mall, Gaborone", "3951234").getAccountNumber();
        String nalediInvestment = bank.openInvestment(naledi.getCustomerId(), 2500).getAccountNumber();
        bank.openSavings(kabo.getCustomerId(), 300);
        String acmeInvestment = bank.openInvestment(acme.getCustomerId(), 10000).getAccountNumber();
        bank.openInvestment(baobab.getCustomerId(), 750);

        bank.deposit(thatoSavings, 500);
        bank.deposit(thatoCheque, 3200);
        bank.withdraw(thatoCheque, 450.75);
        bank.deposit(nalediInvestment, 1000);
        bank.withdraw(acmeInvestment, 2000);

        System.out.println("Sample data added: 5 customers, 6 accounts and the transactions.");
    }
}
