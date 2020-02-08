package codingdojo;

public class DuplicateCompanyCustomerWithMatchingCompanyId implements CustomerMatch {
    private Customer customer;

    public DuplicateCompanyCustomerWithMatchingCompanyId(Customer customer) {
        this.customer = customer;
    }

    @Override
    public void importExternalData(ExternalCustomer externalCustomer) {
        customer.setName(externalCustomer.getName());
        customer.setMasterExternalId(null);
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
