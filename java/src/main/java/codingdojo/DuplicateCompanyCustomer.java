package codingdojo;

public class DuplicateCompanyCustomer implements CustomerMatch {
    private Customer customer;

    public DuplicateCompanyCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public void importExternalData(ExternalCustomer externalCustomer) {
        customer.setName(externalCustomer.getName());
    }

    @Override
    public void persist(CustomerDataAccess customerDataAccess) {
        customerDataAccess.updateCustomerRecord(customer);
    }

    @Override
    public boolean createsPrimaryCustomer() {
        return false;
    }
}
