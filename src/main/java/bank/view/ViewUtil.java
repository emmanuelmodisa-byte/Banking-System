package bank.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;

import java.util.function.Function;

// Small helpers shared by the views. No banking logic in here
public class ViewUtil {

    // Makes a table column that shows one piece of text from each row object
    public static <T> TableColumn<T, String> column(String title, double width, Function<T, String> getter) {
        TableColumn<T, String> col = new TableColumn<>(title);
        col.setPrefWidth(width);
        col.setCellValueFactory(data -> new SimpleStringProperty(getter.apply(data.getValue())));
        return col;
    }

    // Shows a green message when ok is true, a red message when it is false
    public static void showMessage(Label label, String text, boolean ok) {
        label.setText(text);
        if (ok) {
            label.setStyle("-fx-text-fill: green;");
        } else {
            label.setStyle("-fx-text-fill: red;");
        }
    }

    public static String money(double amount) {
        return "BWP" + String.format("%.2f", amount);
    }
}
