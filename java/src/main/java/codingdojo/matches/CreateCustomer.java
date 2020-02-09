package codingdojo.matches;

import codingdojo.Customer;
import codingdojo.CustomerDataAccess;
import codingdojo.ExternalCustomer;

public class CreateCustomer implements CustomerMatch {
    Customer customer;

    @Override
    public void importExternalData(ExternalCustomer externalCustomer) {
        customer = Customer.fromExternalId(externalCustomer.getExternalId());
        customer.importExternalData(externalCustomer);
    }

    @Override
    public void persist(CustomerDataAccess customerDataAccess) {
        customerDataAccess.createCustomerRecord(customer);
    }

    @Override
    public boolean createsPrimaryCustomer() {
        return true;
    }
}
