package bank.controller;

import bank.exception.BankException;
import bank.model.User;
import bank.service.BankingService;

//Sits in between LoginView and the service. It checks the input and catches errors
public class LoginController {
    private BankingService service;
    private User loggedInUser;
    private String message = "";

    public LoginController(BankingService service) {
        this.service = service;
    }

    // Returns true when the login worked. Otherwise getMessage() says why not
    public boolean login(String username, String password) {
        if (username.isBlank() || password.isBlank()) {
            message = "Enter your username and password.";
            return false;
        }
        try {
            loggedInUser = service.login(username.trim(), password);
            message = "Welcome, " + loggedInUser.getUsername() + ".";
            return true;
        } catch (BankException e) {
            message = e.getMessage();
            return false;
        }
    }

    public User getLoggedInUser() { return loggedInUser; }
    public String getMessage() { return message; }
}
