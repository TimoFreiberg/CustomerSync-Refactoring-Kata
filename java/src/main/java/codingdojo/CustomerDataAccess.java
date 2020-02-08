package codingdojo;

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

        return new CustomerMatches();
    }

    private CustomerMatches loadCompanyCustomerByCompanyNumber(String externalId, String companyNumber) {
        Customer matchByCompanyNumber = this.customerDataLayer.findByCompanyNumber(companyNumber);
        if (matchByCompanyNumber == null) {
            return null;
        }

        if (!CustomerType.COMPANY.equals(matchByCompanyNumber.getCustomerType())) {
            throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a company");
        }

        String customerExternalId = matchByCompanyNumber.getExternalId();
        if (customerExternalId != null && !externalId.equals(customerExternalId)) {
            throw new ConflictException("Existing customer for externalCustomer " + companyNumber + " doesn't match external id " + externalId + " instead found " + customerExternalId);
        }
        matchByCompanyNumber.setExternalId(externalId);
        matchByCompanyNumber.setMasterExternalId(externalId);

        return new CustomerMatches(
                matchByCompanyNumber,
                customerDataLayer.createCustomerRecord(Customer.fromExternalId(externalId))
        );
    }

    private CustomerMatches loadCompanyCustomerByExternalId(String externalId, String companyNumber) {
        Customer matchByExternalId = this.customerDataLayer.findByExternalId(externalId);
        if (matchByExternalId == null) {
            return null;
        }

        if (!CustomerType.COMPANY.equals(matchByExternalId.getCustomerType())) {
            throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a company");
        }

        String customerCompanyNumber = matchByExternalId.getCompanyNumber();
        if (!companyNumber.equals(customerCompanyNumber)) {
            matchByExternalId.setMasterExternalId(null);
            return new CustomerMatches(null, matchByExternalId, this.customerDataLayer.findByMasterExternalId(externalId));
        } else {
            return new CustomerMatches(matchByExternalId, this.customerDataLayer.findByMasterExternalId(externalId));
        }
    }

    public CustomerMatches loadPersonCustomer(String externalId) {
        Customer matchByPersonalNumber = this.customerDataLayer.findByExternalId(externalId);
        if (matchByPersonalNumber != null) {
            if (!CustomerType.PERSON.equals(matchByPersonalNumber.getCustomerType())) {
                throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a person");
            }
        }
        return new CustomerMatches(matchByPersonalNumber);
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
