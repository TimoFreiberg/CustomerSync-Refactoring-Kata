package codingdojo;

public class CreateDuplicateCustomer implements CustomerMatch {
    Customer customer;

    @Override
    public void importExternalData(ExternalCustomer externalCustomer) {
        customer = Customer.fromExternalId(externalCustomer.getExternalId());
        customer.setName(externalCustomer.getName());
    }

    @Override
    public void persist(CustomerDataAccess customerDataAccess) {
        customerDataAccess.createCustomerRecord(customer);
    }

    @Override
    public boolean createsPrimaryCustomer() {
        return false;
    }
}
