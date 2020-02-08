package codingdojo;

public class CreateDuplicateCustomer implements Bla {
    Customer customer;

    @Override
    public void importExternalData(ExternalCustomer externalCustomer, CustomerDataAccess customerDataAccess) {
        customer = customerDataAccess.createCustomerRecord(Customer.fromExternalId(externalCustomer.getExternalId()));
        customer.setName(externalCustomer.getName());
    }

    @Override
    public void persist(CustomerDataAccess customerDataAccess) {
        customerDataAccess.updateCustomerRecord(customer);
    }
}
