package codingdojo;

public class CompanyCustomer implements CustomerMatch {
    private Customer customer;

    public CompanyCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public void importExternalData(ExternalCustomer externalCustomer) {
        customer.importExternalData(externalCustomer);
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
