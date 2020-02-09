package codingdojo;

import codingdojo.matches.CustomerMatch;

import java.util.List;

public class CustomerSync {

    private final CustomerDataAccess customerDataAccess;

    public CustomerSync(CustomerDataLayer customerDataLayer) {
        this(new CustomerDataAccess(customerDataLayer));
    }

    public CustomerSync(CustomerDataAccess db) {
        this.customerDataAccess = db;
    }

    public boolean syncWithDataLayer(ExternalCustomer externalCustomer) {

        List<CustomerMatch> customerMatches;
        if (externalCustomer.isCompany()) {
            customerMatches = customerDataAccess.loadCompanyCustomer(
                    externalCustomer.getExternalId(),
                    externalCustomer.getCompanyNumber()
            );
        } else {
            customerMatches = customerDataAccess.loadPersonCustomer(externalCustomer.getExternalId());
        }

        boolean created = false;
        for (CustomerMatch match : customerMatches) {
            created |= match.createsPrimaryCustomer();
            match.importExternalData(externalCustomer);
            match.persist(this.customerDataAccess);
        }

        this.customerDataAccess.updateShoppingLists(externalCustomer.getShoppingLists());

        return created;
    }

}
