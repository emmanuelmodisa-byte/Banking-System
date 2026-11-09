package bank.model;

import java.time.LocalDateTime;

// One record of money moving in or out of an account
public class Transaction {
    private TransactionType type;
    private double amount;
    private String note;
    private LocalDateTime dateTime;

    // New transaction: the time is "now"
    public Transaction(TransactionType type, double amount, String note) {
        this(type, amount, note, LocalDateTime.now());
    }

    // Overloading: used by the DAO when it loads an old transaction from the database
    public Transaction(TransactionType type, double amount, String note, LocalDateTime dateTime) {
        this.type = type;
        this.amount = amount;
        this.note = note;
        this.dateTime = dateTime;
    }

    public TransactionType getType() { return type; }
    public double getAmount() { return amount; }
    public String getNote() { return note; }
    public LocalDateTime getDateTime() { return dateTime; }

    @Override
    public String toString() {
        return dateTime.toLocalDate() + "  " + type + "  BWP" + amount + "  " + note;
    }
}
