package bank.view;

import bank.controller.AccountController;
import bank.controller.CustomerController;
import bank.model.Account;
import bank.model.Customer;
import bank.model.Transaction;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;

// Shows the transactions of one account
public class TransactionView {
    private AccountController accountController;
    private CustomerController customerController;
    private VBox root = new VBox(10);

    private ComboBox<Customer> customerBox = new ComboBox<>();
    private ComboBox<Account> accountBox = new ComboBox<>();
    private TableView<Transaction> table = new TableView<>();
    private DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public TransactionView(AccountController accountController, CustomerController customerController) {
        this.accountController = accountController;
        this.customerController = customerController;

        customerBox.setPromptText("Choose a customer");
        customerBox.setPrefWidth(250);
        customerBox.setOnAction(e -> loadAccounts());

        accountBox.setPromptText("Choose an account");
        accountBox.setPrefWidth(300);
        accountBox.setOnAction(e -> loadTransactions());

        table.getColumns().addAll(
                ViewUtil.<Transaction>column("Date", 150, t -> t.getDateTime().format(format)),
                ViewUtil.<Transaction>column("Type", 110, t -> t.getType().toString()),
                ViewUtil.<Transaction>column("Amount", 120, t -> ViewUtil.money(t.getAmount())),
                ViewUtil.<Transaction>column("Note", 200, t -> t.getNote()));
        table.setPlaceholder(new Label("Choose an account to see its transactions."));

        root.setPadding(new Insets(15));
        root.getChildren().addAll(new HBox(10, new Label("Customer:"), customerBox),
                new HBox(10, new Label("Account:"), accountBox), table);
        refresh();
    }

    private void loadAccounts() {
        Customer c = customerBox.getValue();
        if (c == null) {
            return;
        }
        accountBox.setItems(FXCollections.observableArrayList(accountController.getAccountsFor(c.getCustomerId())));
        table.getItems().clear();
    }

    private void loadTransactions() {
        Account a = accountBox.getValue();
        if (a == null) {
            return;
        }
        table.setItems(FXCollections.observableArrayList(accountController.getTransactions(a.getAccountNumber())));
    }

    // Reloads the customer list (call this when the tab is opened)
    public void refresh() {
        String keepId = "";
        if (customerBox.getValue() != null) {
            keepId = customerBox.getValue().getCustomerId();
        }
        customerBox.setItems(FXCollections.observableArrayList(customerController.getCustomers()));
        for (Customer c : customerBox.getItems()) {
            if (c.getCustomerId().equals(keepId)) {
                customerBox.setValue(c);
            }
        }
    }

    public VBox getRoot() { return root; }
}
