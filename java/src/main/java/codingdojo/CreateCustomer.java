package codingdojo;

public class CreateCustomer implements Bla {
    Customer customer;

    @Override
    public void importExternalData(ExternalCustomer externalCustomer, CustomerDataAccess customerDataAccess) {
        customer = customerDataAccess.createCustomerRecord(Customer.fromExternalId(externalCustomer.getExternalId()));
        customer.importExternalData(externalCustomer);
    }

    @Override
    public void persist(CustomerDataAccess customerDataAccess) {
        customerDataAccess.updateCustomerRecord(customer);
    }
}
