package bank.view;

import bank.controller.CustomerController;
import bank.model.Customer;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

//Register a new customer (Individual or Organisation) and see all customers
public class CustomerView {
    private CustomerController controller;
    private VBox root = new VBox(10);

    private ComboBox<String> typeBox = new ComboBox<>();
    private TextField field1 = new TextField();   // first name  / organisation name
    private TextField field2 = new TextField();   // surname     / registration number
    private TextField field3 = new TextField();   // national ID / contact person
    private TextField addressField = new TextField();
    private TextField phoneField = new TextField();
    private TextField emailField = new TextField();
    private Label messageLabel = new Label();
    private TableView<Customer> table = new TableView<>();

    public CustomerView(CustomerController controller) {
        this.controller = controller;

        typeBox.setItems(FXCollections.observableArrayList("Individual", "Organisation"));
        typeBox.setValue("Individual");
        typeBox.setOnAction(e -> updatePrompts());
        updatePrompts();

        addressField.setPromptText("Address");
        phoneField.setPromptText("Phone");
        emailField.setPromptText("Email");

        HBox row1 = new HBox(10, new Label("Type:"), typeBox, field1, field2, field3);
        HBox row2 = new HBox(10, addressField, phoneField, emailField);

        Button registerButton = new Button("Register customer");
        registerButton.setOnAction(e -> register());

        table.getColumns().addAll(
                ViewUtil.<Customer>column("ID", 60, c -> c.getCustomerId()),
                ViewUtil.<Customer>column("Type", 100, c -> c.getCustomerType()),
                ViewUtil.<Customer>column("Name", 200, c -> c.getDisplayName()),
                ViewUtil.<Customer>column("ID / Reg. no.", 130, c -> c.getIdentityNumber()),
                ViewUtil.<Customer>column("Phone", 110, c -> c.getPhone()),
                ViewUtil.<Customer>column("Email", 200, c -> c.getEmail()));
        table.setPlaceholder(new Label("No customers yet."));

        root.setPadding(new Insets(15));
        root.getChildren().addAll(new Label("Register a customer"), row1, row2, registerButton,
                messageLabel, new Label("All customers"), table);
        refresh();
    }

    // The three text fields mean different things for each customer type
    private void updatePrompts() {
        if (typeBox.getValue().equals("Individual")) {
            field1.setPromptText("First name");
            field2.setPromptText("Surname");
            field3.setPromptText("National ID (Omang)");
        } else {
            field1.setPromptText("Organisation name");
            field2.setPromptText("Registration number");
            field3.setPromptText("Contact person");
        }
    }

    private void register() {
        boolean ok;
        if (typeBox.getValue().equals("Individual")) {
            ok = controller.registerIndividual(field1.getText(), field2.getText(), field3.getText(),
                    addressField.getText(), phoneField.getText(), emailField.getText());
        } else {
            ok = controller.registerOrganisation(field1.getText(), field2.getText(), field3.getText(),
                    addressField.getText(), phoneField.getText(), emailField.getText());
        }
        ViewUtil.showMessage(messageLabel, controller.getMessage(), ok);
        if (ok) {
            field1.clear();
            field2.clear();
            field3.clear();
            addressField.clear();
            phoneField.clear();
            emailField.clear();
            refresh();
        }
    }

    public void refresh() {
        table.setItems(FXCollections.observableArrayList(controller.getCustomers()));
    }

    public VBox getRoot() { return root; }
}
