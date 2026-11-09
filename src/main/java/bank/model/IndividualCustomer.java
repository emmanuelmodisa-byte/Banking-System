package bank.model;

//A person who banks with us
public class IndividualCustomer extends Customer {
    private String firstName;
    private String surname;
    private String nationalId;

    public IndividualCustomer(String customerId, String firstName, String surname, String nationalId,
                              String address, String phone, String email) {
        super(customerId, address, phone, email);
        this.firstName = firstName;
        this.surname = surname;
        this.nationalId = nationalId;
    }

    @Override
    public String getDisplayName() { return firstName + " " + surname; }

    @Override
    public String getIdentityNumber() { return nationalId; }

    @Override
    public String getCustomerType() { return "Individual"; }

    public String getFirstName() { return firstName; }
    public String getSurname() { return surname; }
}
