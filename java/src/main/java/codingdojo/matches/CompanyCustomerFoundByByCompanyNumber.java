package codingdojo.matches;

import codingdojo.ConflictException;
import codingdojo.Customer;
import codingdojo.CustomerDataAccess;
import codingdojo.ExternalCustomer;

public class CompanyCustomerFoundByByCompanyNumber implements CustomerMatch {
    private Customer customer;

    public CompanyCustomerFoundByByCompanyNumber(Customer customer, String externalId) {
        String customerExternalId = customer.getExternalId();
        if (customerExternalId != null && !externalId.equals(customerExternalId)) {
            throw new ConflictException("Existing customer for externalCustomer "
                    + customer.getCompanyNumber()
                    + " doesn't match external id "
                    + externalId
                    + " instead found " + customerExternalId);
        }
        this.customer = customer;
    }

    @Override
    public void importExternalData(ExternalCustomer externalCustomer) {
        customer.setExternalId(externalCustomer.getExternalId());
        customer.setMasterExternalId(externalCustomer.getExternalId());
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
