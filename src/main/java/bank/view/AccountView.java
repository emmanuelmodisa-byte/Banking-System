package bank.view;

import bank.controller.AccountController;
import bank.controller.CustomerController;
import bank.model.Account;
import bank.model.Customer;
import bank.model.User;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

//Open accounts, deposit, withdraw. A manager can also pay the monthly interest
public class AccountView {
    private AccountController accountController;
    private CustomerController customerController;
    private User user;
    private VBox root = new VBox(10);

    private ComboBox<Customer> customerBox = new ComboBox<>();
    private ComboBox<String> typeBox = new ComboBox<>();
    private TextField openingField = new TextField();
    private TextField employerNameField = new TextField();
    private TextField employerAddressField = new TextField();
    private TextField employerContactField = new TextField();
    private TextField amountField = new TextField();
    private TableView<Account> table = new TableView<>();
    private Label messageLabel = new Label();

    public AccountView(AccountController accountController, CustomerController customerController, User user) {
        this.accountController = accountController;
        this.customerController = customerController;
        this.user = user;

        customerBox.setPromptText("Choose a customer");
        customerBox.setPrefWidth(250);
        customerBox.setOnAction(e -> loadAccounts());

        typeBox.setItems(FXCollections.observableArrayList("Savings", "Investment", "Cheque"));
        typeBox.setValue("Savings");
        typeBox.setOnAction(e -> updateChequeFields());

        openingField.setPromptText("Opening deposit");
        employerNameField.setPromptText("Employer name");
        employerAddressField.setPromptText("Employer address");
        employerContactField.setPromptText("Employer contact");
        updateChequeFields();

        Button openButton = new Button("Open account");
        openButton.setOnAction(e -> openAccount());

        amountField.setPromptText("Amount");
        Button depositButton = new Button("Deposit");
        depositButton.setOnAction(e -> deposit());
        Button withdrawButton = new Button("Withdraw");
        withdrawButton.setOnAction(e -> withdraw());

        table.getColumns().addAll(
                ViewUtil.<Account>column("Account no.", 110, a -> a.getAccountNumber()),
                ViewUtil.<Account>column("Type", 110, a -> a.getAccountType()),
                ViewUtil.<Account>column("Balance", 130, a -> ViewUtil.money(a.getBalance())),
                ViewUtil.<Account>column("Branch", 130, a -> a.getBranch()));
        table.setPlaceholder(new Label("Choose a customer to see their accounts."));

        HBox openRow1 = new HBox(10, typeBox, openingField, openButton);
        HBox openRow2 = new HBox(10, employerNameField, employerAddressField, employerContactField);
        HBox moneyRow = new HBox(10, amountField, depositButton, withdrawButton);

        root.setPadding(new Insets(15));
        root.getChildren().addAll(new Label("Customer"), customerBox,
                new Label("Open a new account"), openRow1, openRow2,
                new Label("Accounts (select one to deposit or withdraw)"), table, moneyRow);

        // Only a manager sees the interest button
        if (user.isManager()) {
            Button interestButton = new Button("Pay monthly interest (manager only)");
            interestButton.setOnAction(e -> payInterest());
            root.getChildren().add(interestButton);
        }
        root.getChildren().add(messageLabel);
        refresh();
    }

    // Employer details are only needed for a Cheque account
    private void updateChequeFields() {
        boolean cheque = typeBox.getValue().equals("Cheque");
        employerNameField.setDisable(!cheque);
        employerAddressField.setDisable(!cheque);
        employerContactField.setDisable(!cheque);
    }

    private String selectedCustomerId() {
        Customer c = customerBox.getValue();
        if (c == null) {
            return "";
        }
        return c.getCustomerId();
    }

    private String selectedAccountNumber() {
        Account a = table.getSelectionModel().getSelectedItem();
        if (a == null) {
            return "";
        }
        return a.getAccountNumber();
    }

    private void openAccount() {
        String customerId = selectedCustomerId();
        if (customerId.isEmpty()) {
            ViewUtil.showMessage(messageLabel, "Choose a customer first.", false);
            return;
        }
        boolean ok;
        String type = typeBox.getValue();
        if (type.equals("Savings")) {
            ok = accountController.openSavings(customerId, openingField.getText());
        } else if (type.equals("Investment")) {
            ok = accountController.openInvestment(customerId, openingField.getText());
        } else {
            ok = accountController.openCheque(customerId, openingField.getText(),
                    employerNameField.getText(), employerAddressField.getText(), employerContactField.getText());
        }
        ViewUtil.showMessage(messageLabel, accountController.getMessage(), ok);
        if (ok) {
            openingField.clear();
            loadAccounts();
        }
    }

    private void deposit() {
        boolean ok = accountController.deposit(selectedAccountNumber(), amountField.getText());
        ViewUtil.showMessage(messageLabel, accountController.getMessage(), ok);
        if (ok) {
            amountField.clear();
            loadAccounts();
        }
    }

    private void withdraw() {
        boolean ok = accountController.withdraw(selectedAccountNumber(), amountField.getText());
        ViewUtil.showMessage(messageLabel, accountController.getMessage(), ok);
        if (ok) {
            amountField.clear();
            loadAccounts();
        }
    }

    private void payInterest() {
        boolean ok = accountController.payInterest(user);
        ViewUtil.showMessage(messageLabel, accountController.getMessage(), ok);
        loadAccounts();
    }

    private void loadAccounts() {
        String customerId = selectedCustomerId();
        if (customerId.isEmpty()) {
            table.getItems().clear();
            return;
        }
        table.setItems(FXCollections.observableArrayList(accountController.getAccountsFor(customerId)));
    }

    //Reloads the customer list (call this when the tab is opened)
    public void refresh() {
        String keepId = selectedCustomerId();
        customerBox.setItems(FXCollections.observableArrayList(customerController.getCustomers()));
        for (Customer c : customerBox.getItems()) {
            if (c.getCustomerId().equals(keepId)) {
                customerBox.setValue(c);
            }
        }
        loadAccounts();
    }

    public VBox getRoot() { return root; }
}
