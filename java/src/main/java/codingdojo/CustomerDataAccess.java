package codingdojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerDataAccess {

    private final CustomerDataLayer customerDataLayer;

    public CustomerDataAccess(CustomerDataLayer customerDataLayer) {
        this.customerDataLayer = customerDataLayer;
    }

    public CustomerMatches loadCompanyCustomer(String externalId, String companyNumber) {

        CustomerMatches matchesByExternalId = loadCompanyCustomerByExternalId(externalId, companyNumber);
        if (matchesByExternalId != null) {
            return matchesByExternalId;
        }

        CustomerMatches matchesByCompanyNumber = loadCompanyCustomerByCompanyNumber(externalId, companyNumber);
        if (matchesByCompanyNumber != null) {
            return matchesByCompanyNumber;
        }

        return new CustomerMatches().with(new CreateCustomer());
    }

    private CustomerMatches loadCompanyCustomerByCompanyNumber(String externalId, String companyNumber) {
        Optional<Customer> matchByCompanyNumber = this.customerDataLayer.findByCompanyNumber(companyNumber);
        if (!matchByCompanyNumber.isPresent()) {
            return null;
        }

        return new CustomerMatches().with(CompanyCustomerFoundByByCompanyNumber.fromNullable(matchByCompanyNumber.get(), externalId));
    }

    private CustomerMatches loadCompanyCustomerByExternalId(String externalId, String companyNumber) {
        Optional<Customer> found = this.customerDataLayer.findByExternalId(externalId);
        if (!found.isPresent()) {
            return null;
        }

        List<CustomerMatch> matches = new ArrayList<>();

        Customer matchByExternalId = found.get();

        if (!companyNumber.equals(matchByExternalId.getCompanyNumber())) {
            matches.add(new CreateCustomer());
            matches.add(new DuplicateCompanyCustomerWithMatchingCompanyId(matchByExternalId, externalId));
        } else {
            matches.add(new CompanyCustomer(matchByExternalId, externalId));
        }
        Optional<Customer> byMasterExternalId = this.customerDataLayer.findByMasterExternalId(externalId);
        if (byMasterExternalId.isPresent()) {
            matches.add(new DuplicateCompanyCustomer(byMasterExternalId.get()));
        }
        return new CustomerMatches().with(matches.toArray(new CustomerMatch[matches.size()]));
    }

    public CustomerMatches loadPersonCustomer(String externalId) {
        Optional<Customer> matchByPersonalNumber = this.customerDataLayer.findByExternalId(externalId);
        CustomerMatch customer;
        if (matchByPersonalNumber.isPresent()) {
            customer = new PrivateCustomer(matchByPersonalNumber.get(), externalId);
        } else {
            customer = new CreateCustomer();
        }
        return new CustomerMatches().with(customer);
    }

    public Customer updateCustomerRecord(Customer customer) {
        return customerDataLayer.updateCustomerRecord(customer);
    }

    public Customer createCustomerRecord(Customer customer) {
        return customerDataLayer.createCustomerRecord(customer);
    }

    public void updateShoppingLists(List<ShoppingList> shoppingLists) {
        for (ShoppingList consumerShoppingList : shoppingLists) {
            customerDataLayer.updateShoppingList(consumerShoppingList);
        }
    }
}
