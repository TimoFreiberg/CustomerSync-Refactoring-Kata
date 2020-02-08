package codingdojo;

public class CompanyCustomer implements Bla {
    private Customer customer;

    public CompanyCustomer(Customer customer, String externalId) {
        if (!CustomerType.COMPANY.equals(customer.getCustomerType())) {
            throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a company");
        }
        this.customer = customer;
    }

    @Override
    public void importExternalData(ExternalCustomer externalCustomer, CustomerDataAccess customerDataAccess) {

    }

    @Override
    public void persist(CustomerDataAccess customerDataAccess) {

    }
}
