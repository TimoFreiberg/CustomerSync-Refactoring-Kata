package codingdojo;

import java.util.ArrayList;
import java.util.List;

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
        Customer matchByCompanyNumber = this.customerDataLayer.findByCompanyNumber(companyNumber);
        if (matchByCompanyNumber == null) {
            return null;
        }

        return new CustomerMatches().with(CompanyCustomerFoundByByCompanyNumber.fromNullable(matchByCompanyNumber, externalId));
    }

    private CustomerMatches loadCompanyCustomerByExternalId(String externalId, String companyNumber) {
        Customer matchByExternalId = this.customerDataLayer.findByExternalId(externalId);
        if (matchByExternalId == null) {
            return null;
        }

        List<CustomerMatch> matches = new ArrayList<>();

        if (!companyNumber.equals(matchByExternalId.getCompanyNumber())) {
            matches.add(new CreateCustomer());
            matches.add(new DuplicateCompanyCustomerWithMatchingCompanyId(matchByExternalId, externalId));
        } else {
            matches.add(new CompanyCustomer(matchByExternalId, externalId));
        }
        Customer byMasterExternalId = this.customerDataLayer.findByMasterExternalId(externalId);
        if (byMasterExternalId != null) {
            matches.add(new DuplicateCompanyCustomer(byMasterExternalId));
        }
        return new CustomerMatches().with(matches.toArray(new CustomerMatch[matches.size()]));
    }

    public CustomerMatches loadPersonCustomer(String externalId) {
        Customer matchByPersonalNumber = this.customerDataLayer.findByExternalId(externalId);
        CustomerMatch customer;
        if (matchByPersonalNumber == null) {
            customer = new CreateCustomer();
        } else {
            customer = new PrivateCustomer(matchByPersonalNumber, externalId);
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
