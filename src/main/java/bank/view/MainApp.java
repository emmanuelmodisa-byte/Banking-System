package bank.view;

import bank.controller.AccountController;
import bank.controller.CustomerController;
import bank.controller.LoginController;
import bank.model.User;
import bank.service.BankingService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

// Starts the program. It builds the service and controllers once and shows the screens
public class MainApp extends Application {
    private BankingService service;
    private LoginController loginController;
    private CustomerController customerController;
    private AccountController accountController;

    @Override
    public void start(Stage stage) {
        service = new BankingService();
        loginController = new LoginController(service);
        customerController = new CustomerController(service);
        accountController = new AccountController(service);

        stage.setTitle("Bank System");
        showLogin(stage);
        stage.show();
    }

    private void showLogin(Stage stage) {
        LoginView loginView = new LoginView(loginController, user -> showMain(stage, user));
        Scene scene = new Scene(loginView.getRoot(), 440, 360);
        applyTheme(scene);
        stage.setScene(scene);
    }

    private void showMain(Stage stage, User user) {
        CustomerView customerView = new CustomerView(customerController);
        AccountView accountView = new AccountView(accountController, customerController, user);
        TransactionView transactionView = new TransactionView(accountController, customerController);

        Tab customerTab = new Tab("Customers", customerView.getRoot());
        Tab accountTab = new Tab("Accounts", accountView.getRoot());
        Tab transactionTab = new Tab("Transactions", transactionView.getRoot());

        TabPane tabs = new TabPane(customerTab, accountTab, transactionTab);
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Reload the lists every time a tab is opened, so each screen shows the newest data
        tabs.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == customerTab) {
                customerView.refresh();
            } else if (newTab == accountTab) {
                accountView.refresh();
            } else if (newTab == transactionTab) {
                transactionView.refresh();
            }
        });

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> showLogin(stage));
        HBox topBar = new HBox(15, new Label("Logged in as " + user.getUsername() + " (" + user.getRole() + ")"),
                logoutButton);
        topBar.setPadding(new Insets(12));
        topBar.setStyle("-fx-background-color: #292929;");

        BorderPane layout = new BorderPane();
        layout.setTop(topBar);
        layout.setCenter(tabs);
        Scene scene = new Scene(layout, 1000, 680);
        applyTheme(scene);
        stage.setScene(scene);
    }


    // Applies the shared dark grey and crimson stylesheet to each screen
    private void applyTheme(Scene scene) {
        scene.getStylesheets().add(
                getClass().getResource("/bank/view/theme.css").toExternalForm()
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
