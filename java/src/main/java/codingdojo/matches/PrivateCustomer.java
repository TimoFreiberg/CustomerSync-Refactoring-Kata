package codingdojo.matches;

import codingdojo.*;

public class PrivateCustomer implements CustomerMatch {
    private Customer customer;

    public PrivateCustomer(Customer customer, String externalId) {
        if (customer != null && !CustomerType.PERSON.equals(customer.getCustomerType())) {
            throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a person");
        }
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
