package bank.model;

//A company or organisation that banks with us
public class OrganisationCustomer extends Customer {
    private String organisationName;
    private String registrationNumber;
    private String contactPerson;

    public OrganisationCustomer(String customerId, String organisationName, String registrationNumber,
                                String contactPerson, String address, String phone, String email) {
        super(customerId, address, phone, email);
        this.organisationName = organisationName;
        this.registrationNumber = registrationNumber;
        this.contactPerson = contactPerson;
    }

    @Override
    public String getDisplayName() { return organisationName; }

    @Override
    public String getIdentityNumber() { return registrationNumber; }

    @Override
    public String getCustomerType() { return "Organisation"; }

    public String getContactPerson() { return contactPerson; }
}
