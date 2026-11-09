package bank.controller;

import bank.exception.BankException;
import bank.model.Customer;
import bank.service.BankingService;

import java.util.ArrayList;
import java.util.List;

// Placed in between CustomerView and the service
public class CustomerController {
    private BankingService service;
    private String message = "";

    public CustomerController(BankingService service) {
        this.service = service;
    }

    public boolean registerIndividual(String firstName, String surname, String nationalId,
                                      String address, String phone, String email) {
        try {
            Customer c = service.registerIndividual(firstName.trim(), surname.trim(), nationalId.trim(),
                    address.trim(), phone.trim(), email.trim());
            message = "Registered " + c + ".";
            return true;
        } catch (BankException e) {
            message = e.getMessage();
            return false;
        }
    }

    public boolean registerOrganisation(String name, String registrationNumber, String contactPerson,
                                        String address, String phone, String email) {
        try {
            Customer c = service.registerOrganisation(name.trim(), registrationNumber.trim(), contactPerson.trim(),
                    address.trim(), phone.trim(), email.trim());
            message = "Registered " + c + ".";
            return true;
        } catch (BankException e) {
            message = e.getMessage();
            return false;
        }
    }

    public List<Customer> getCustomers() {
        try {
            return service.getCustomers();
        } catch (BankException e) {
            message = e.getMessage();
            return new ArrayList<>();
        }
    }

    public String getMessage() { return message; }
}
