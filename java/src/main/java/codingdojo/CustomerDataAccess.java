package codingdojo;

import java.util.List;

public class CustomerDataAccess {

    private final CustomerDataLayer customerDataLayer;

    public CustomerDataAccess(CustomerDataLayer customerDataLayer) {
        this.customerDataLayer = customerDataLayer;
    }

    public CustomerMatches loadCompanyCustomer(String externalId, String companyNumber) {
        CustomerMatches matches = new CustomerMatches();
        Customer matchByExternalId = this.customerDataLayer.findByExternalId(externalId);
        if (matchByExternalId != null) {
            matches.setCustomer(matchByExternalId);
            matches.setMatchTerm("ExternalId");
            Customer matchByMasterId = this.customerDataLayer.findByMasterExternalId(externalId);
            if (matchByMasterId != null) {
                matches.addDuplicate(matchByMasterId);
            }
        } else {
            Customer matchByCompanyNumber = this.customerDataLayer.findByCompanyNumber(companyNumber);
            if (matchByCompanyNumber != null) {
                matches.setCustomer(matchByCompanyNumber);
                matches.setMatchTerm("CompanyNumber");
            }
        }

        if (matches.getCustomer() != null && !CustomerType.COMPANY.equals(matches.getCustomer().getCustomerType())) {
            throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a company");
        }

        if ("ExternalId".equals(matches.getMatchTerm())) {
            String customerCompanyNumber = matches.getCustomer().getCompanyNumber();
            if (!companyNumber.equals(customerCompanyNumber)) {
                matches.getCustomer().setMasterExternalId(null);
                matches.addDuplicate(matches.getCustomer());
                matches.setCustomer(null);
                matches.setMatchTerm(null);
            }
        } else if ("CompanyNumber".equals(matches.getMatchTerm())) {
            String customerExternalId = matches.getCustomer().getExternalId();
            if (customerExternalId != null && !externalId.equals(customerExternalId)) {
                throw new ConflictException("Existing customer for externalCustomer " + companyNumber + " doesn't match external id " + externalId + " instead found " + customerExternalId);
            }
            Customer customer = matches.getCustomer();
            customer.setExternalId(externalId);
            customer.setMasterExternalId(externalId);
            matches.addDuplicate(null);
        }

        return matches;
    }

    public CustomerMatches loadPersonCustomer(String externalId) {
        CustomerMatches matches = new CustomerMatches();
        Customer matchByPersonalNumber = this.customerDataLayer.findByExternalId(externalId);
        matches.setCustomer(matchByPersonalNumber);
        if (matchByPersonalNumber != null) matches.setMatchTerm("ExternalId");
        return matches;
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
