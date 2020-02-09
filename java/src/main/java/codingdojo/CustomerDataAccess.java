package codingdojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CustomerDataAccess {

    private final CustomerDataLayer customerDataLayer;

    public CustomerDataAccess(CustomerDataLayer customerDataLayer) {
        this.customerDataLayer = customerDataLayer;
    }

    public List<CustomerMatch> loadCompanyCustomer(String externalId, String companyNumber) {

        List<CustomerMatch> matchesByExternalId = loadCompanyCustomerByExternalId(externalId, companyNumber);
        if (matchesByExternalId != null) {
            return matchesByExternalId;
        }

        List<CustomerMatch> matchesByCompanyNumber = loadCompanyCustomerByCompanyNumber(externalId, companyNumber);
        if (!matchesByCompanyNumber.isEmpty()) {
            return matchesByCompanyNumber;
        }

        return List.of(new CreateCustomer());
    }

    private List<CustomerMatch> loadCompanyCustomerByCompanyNumber(String externalId, String companyNumber) {
        Optional<Customer> matchByCompanyNumber = this.customerDataLayer.findByCompanyNumber(companyNumber);
        return matchByCompanyNumber
                .map(match -> new CompanyCustomerFoundByByCompanyNumber(match, externalId))
                .stream()
                .collect(Collectors.toList());
    }

    private List<CustomerMatch> loadCompanyCustomerByExternalId(String externalId, String companyNumber) {
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
        return matches;
    }

    public List<CustomerMatch> loadPersonCustomer(String externalId) {
        Optional<Customer> matchByPersonalNumber = this.customerDataLayer.findByExternalId(externalId);
        if (matchByPersonalNumber.isPresent()) {
            return List.of(new PrivateCustomer(matchByPersonalNumber.get(), externalId));
        } else {
            return List.of(new CreateCustomer());
        }
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
