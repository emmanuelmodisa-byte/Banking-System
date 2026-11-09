package bank.model;

//Only accounts that earn interest implement this (Savings and Investment)
public interface InterestBearing {
    double getInterestRate();   // e.g. 0.05 means 5%
    double payInterest();       // adds the interest to the balance and returns the amount
}
