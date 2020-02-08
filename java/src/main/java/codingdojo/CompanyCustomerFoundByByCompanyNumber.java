package codingdojo;

public class CompanyCustomerFoundByByCompanyNumber implements Bla {
    private Customer customer;

    public CompanyCustomerFoundByByCompanyNumber(Customer customer, String externalId) {
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
}
