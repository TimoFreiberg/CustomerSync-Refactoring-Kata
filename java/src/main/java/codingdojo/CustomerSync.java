package codingdojo;

public class CustomerSync {

    private final CustomerDataAccess customerDataAccess;

    public CustomerSync(CustomerDataLayer customerDataLayer) {
        this(new CustomerDataAccess(customerDataLayer));
    }

    public CustomerSync(CustomerDataAccess db) {
        this.customerDataAccess = db;
    }

    public boolean syncWithDataLayer(ExternalCustomer externalCustomer) {

        CustomerMatches customerMatches;
        if (externalCustomer.isCompany()) {
            customerMatches = customerDataAccess.loadCompanyCustomer(
                    externalCustomer.getExternalId(),
                    externalCustomer.getCompanyNumber()
            );
        } else {
            customerMatches = customerDataAccess.loadPersonCustomer(externalCustomer.getExternalId());
        }
        Customer customer = customerMatches.getCustomer();

        boolean newCustomer = customer == null;
        if (newCustomer) {
            customer = this.customerDataAccess.createCustomerRecord(
                    Customer.fromExternalId(externalCustomer.getExternalId())
            );
        }

        customer.importExternalData(externalCustomer);

        for (Customer duplicate : customerMatches.getDuplicates()) {
            updateDuplicate(externalCustomer, duplicate);
        }

        this.customerDataAccess.updateShoppingLists(externalCustomer.getShoppingLists());
        this.customerDataAccess.updateCustomerRecord(customer);

        return newCustomer;
    }

    private void updateDuplicate(ExternalCustomer externalCustomer, Customer duplicate) {
        duplicate.setName(externalCustomer.getName());
        this.customerDataAccess.updateCustomerRecord(duplicate);
    }

}
