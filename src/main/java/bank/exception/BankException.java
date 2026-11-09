package bank.exception;

//Thrown whenever a banking rule is broken (e.g.withdrawing from a Savings account)
public class BankException extends RuntimeException {
    public BankException(String message) {
        super(message);
    }
}
