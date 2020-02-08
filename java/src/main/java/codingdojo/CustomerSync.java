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

        for (CustomerMatch match : customerMatches.matches()) {
            match.importExternalData(externalCustomer);
            match.persist(this.customerDataAccess);
        }

        this.customerDataAccess.updateShoppingLists(externalCustomer.getShoppingLists());

        return customerMatches.isPrimaryCustomerCreated();
    }

}
