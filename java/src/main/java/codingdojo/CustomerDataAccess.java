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
            if (!CustomerType.COMPANY.equals(matchByExternalId.getCustomerType())) {
                throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a company");
            }

            String customerCompanyNumber = matchByExternalId.getCompanyNumber();
            if (!companyNumber.equals(customerCompanyNumber)) {
                matchByExternalId.setMasterExternalId(null);
                matches.addDuplicate(matchByExternalId);
            } else {
                matches.setCustomer(matchByExternalId);
            }

            matches.addDuplicate(this.customerDataLayer.findByMasterExternalId(externalId));
        } else {
            Customer matchByCompanyNumber = this.customerDataLayer.findByCompanyNumber(companyNumber);
            if (matchByCompanyNumber != null) {
                if (!CustomerType.COMPANY.equals(matchByCompanyNumber.getCustomerType())) {
                    throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a company");
                }

                String customerExternalId = matchByCompanyNumber.getExternalId();
                if (customerExternalId != null && !externalId.equals(customerExternalId)) {
                    throw new ConflictException("Existing customer for externalCustomer " + companyNumber + " doesn't match external id " + externalId + " instead found " + customerExternalId);
                }
                matchByCompanyNumber.setExternalId(externalId);
                matchByCompanyNumber.setMasterExternalId(externalId);

                matches.setCustomer(matchByCompanyNumber);
                matches.addDuplicate(createCustomerRecord(Customer.fromExternalId(externalId)));
            }
        }

        return matches;
    }

    public CustomerMatches loadPersonCustomer(String externalId) {
        CustomerMatches matches = new CustomerMatches();
        Customer matchByPersonalNumber = this.customerDataLayer.findByExternalId(externalId);
        if (matchByPersonalNumber != null) {
            matches.setCustomer(matchByPersonalNumber);
            if (!CustomerType.PERSON.equals(matches.getCustomer().getCustomerType())) {
                throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a person");
            }
        }
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
