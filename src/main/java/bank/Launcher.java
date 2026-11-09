package bank;

import bank.view.MainApp;

// A class that extends Application cannot be started directly when JavaFX comes from Maven

public class Launcher {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
