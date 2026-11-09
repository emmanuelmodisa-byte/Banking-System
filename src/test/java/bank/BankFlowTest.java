package bank;

import bank.dao.AccountDAO;
import bank.dao.CustomerDAO;
import bank.dao.Database;
import bank.exception.BankException;
import bank.model.Account;
import bank.model.Customer;
import bank.model.User;
import bank.service.BankingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Integration tests. Each test starts with an empty in-memory database
public class BankFlowTest {
    private BankingService bank;
    private Customer thato;

    @BeforeEach
    void setUp() {
        Database.useMemory();
        Database.reset();                       // empty tables + the 2 starter logins
        bank = new BankingService();
        thato = bank.registerIndividual("Thato", "Molefe", "123456789",
                "Plot 123, Gaborone", "71234567", "thato@example.com");
    }

    // The main test from the README: login -> open account -> deposit -> check the balance in the database
    @Test
    void loginOpenAccountDepositAndCheckBalanceInDatabase() {
        User teller = bank.login("teller", "teller123");
        assertEquals("teller", teller.getUsername());

        Account opened = bank.openSavings(thato.getCustomerId(), 1000);
        bank.deposit(opened.getAccountNumber(), 500);

        // Read straight from the database with a new DAO (not from the objects in memory)
        Account saved = new AccountDAO().findByNumber(opened.getAccountNumber());
        assertEquals(1500.0, saved.getBalance(), 0.001);
        assertEquals(2, saved.getTransactions().size());   // opening deposit + deposit
    }

    @Test
    void wrongPasswordIsRejected() {
        assertThrows(BankException.class, () -> bank.login("teller", "wrong"));
    }

    @Test
    void savingsWithdrawalIsRejectedAndBalanceStaysTheSame() {
        Account savings = bank.openSavings(thato.getCustomerId(), 1000);
        assertThrows(BankException.class, () -> bank.withdraw(savings.getAccountNumber(), 50));

        Account saved = new AccountDAO().findByNumber(savings.getAccountNumber());
        assertEquals(1000.0, saved.getBalance(), 0.001);
    }

    @Test
    void chequeWithdrawalIsSavedInTheDatabase() {
        Account cheque = bank.openCheque(thato.getCustomerId(), 0, "Acme Ltd", "Plot 9", "3951234");
        bank.deposit(cheque.getAccountNumber(), 8000);
        bank.withdraw(cheque.getAccountNumber(), 250.50);

        Account saved = new AccountDAO().findByNumber(cheque.getAccountNumber());
        assertEquals(7749.50, saved.getBalance(), 0.001);
        assertEquals(2, saved.getTransactions().size());   // deposit + withdrawal (no opening deposit)
    }

    @Test
    void investmentNeedsAtLeast500ToOpen() {
        assertThrows(BankException.class, () -> bank.openInvestment(thato.getCustomerId(), 300));
        assertEquals(0, new AccountDAO().count());          // nothing was saved
    }

    @Test
    void onlyAManagerCanPayInterestAndItIsSaved() {
        User teller = bank.login("teller", "teller123");
        User manager = bank.login("manager", "manager123");
        Account savings = bank.openSavings(thato.getCustomerId(), 1000);       // 0.05%  -> 0.50
        Account investment = bank.openInvestment(thato.getCustomerId(), 1000); // 5%     -> 50.00

        assertThrows(BankException.class, () -> bank.payMonthlyInterest(teller));

        double paid = bank.payMonthlyInterest(manager);
        assertEquals(50.50, paid, 0.001);
        assertEquals(1000.50, new AccountDAO().findByNumber(savings.getAccountNumber()).getBalance(), 0.001);
        assertEquals(1050.00, new AccountDAO().findByNumber(investment.getAccountNumber()).getBalance(), 0.001);
    }

    @Test
    void sameNationalIdCannotBeRegisteredTwice() {
        assertThrows(BankException.class, () -> bank.registerIndividual("Other", "Person", "123456789",
                "x", "y", "z"));
    }

    @Test
    void customerDaoUpdateAndDelete() {
        CustomerDAO dao = new CustomerDAO();

        thato.setPhone("70000000");
        dao.update(thato);
        assertEquals("70000000", dao.findById(thato.getCustomerId()).getPhone());

        dao.delete(thato.getCustomerId());
        assertNull(dao.findById(thato.getCustomerId()));
    }

    @Test
    void customerWithAccountsCannotBeDeleted() {
        Account savings = bank.openSavings(thato.getCustomerId(), 100);
        CustomerDAO customers = new CustomerDAO();

        assertThrows(BankException.class, () -> customers.delete(thato.getCustomerId()));   // foreign key

        new AccountDAO().delete(savings.getAccountNumber());                                  // remove the account first
        customers.delete(thato.getCustomerId());
        assertNull(customers.findById(thato.getCustomerId()));
    }

    @Test
    void customersAreLoadedWithTheirAccounts() {
        bank.openSavings(thato.getCustomerId(), 100);
        bank.openInvestment(thato.getCustomerId(), 600);

        Customer loaded = bank.findCustomer(thato.getCustomerId());
        assertNotNull(loaded);
        assertEquals(2, loaded.getAccounts().size());
    }
}
