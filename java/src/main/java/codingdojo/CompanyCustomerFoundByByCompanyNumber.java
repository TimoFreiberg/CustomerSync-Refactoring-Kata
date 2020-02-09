package codingdojo;

public class CompanyCustomerFoundByByCompanyNumber implements CustomerMatch {
    private Customer customer;

    private CompanyCustomerFoundByByCompanyNumber(Customer customer) {
        this.customer = customer;
    }

    public static CustomerMatch fromNullable(Customer customer, String externalId) {
        if (customer == null) {
            return new NoOpMatch();
        }
        if (customer.getCustomerType() != CustomerType.COMPANY) {
            throw new ConflictException("Existing customer for externalCustomer "
                    + externalId
                    + " already exists and is not a company");
        }
        String customerExternalId = customer.getExternalId();
        if (customerExternalId != null && !externalId.equals(customerExternalId)) {
            throw new ConflictException("Existing customer for externalCustomer "
                    + customer.getCompanyNumber()
                    + " doesn't match external id "
                    + externalId
                    + " instead found " + customerExternalId);
        }
        return new CompanyCustomerFoundByByCompanyNumber(customer);
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
