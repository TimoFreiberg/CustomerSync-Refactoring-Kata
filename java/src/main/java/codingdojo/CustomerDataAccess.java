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

        // remove this validation step as soon as the setters are gone
        String customerExternalId = matchByCompanyNumber.getExternalId();
        if (customerExternalId != null && !externalId.equals(customerExternalId)) {
            throw new ConflictException("Existing customer for externalCustomer " + companyNumber + " doesn't match external id " + externalId + " instead found " + customerExternalId);
        }
        matchByCompanyNumber.setExternalId(externalId);
        matchByCompanyNumber.setMasterExternalId(externalId);

        return new CustomerMatches(
                matchByCompanyNumber,
                customerDataLayer.createCustomerRecord(Customer.fromExternalId(externalId))
        ).with(new CompanyCustomerFoundByByCompanyNumber(matchByCompanyNumber, externalId));
    }

    private CustomerMatches loadCompanyCustomerByExternalId(String externalId, String companyNumber) {
        Customer matchByExternalId = this.customerDataLayer.findByExternalId(externalId);
        if (matchByExternalId == null) {
            return null;
        }

        if (!CustomerType.COMPANY.equals(matchByExternalId.getCustomerType())) {
            throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a company");
        }

        List<CustomerMatch> matches = new ArrayList<>();


        if (!companyNumber.equals(matchByExternalId.getCompanyNumber())) {
            matches.add(new CreateCustomer());
            matches.add(new DuplicateCompanyCustomerWithMatchingCompanyId(matchByExternalId));
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
        if (matchByPersonalNumber != null && !CustomerType.PERSON.equals(matchByPersonalNumber.getCustomerType())) {
            throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a person");
        }
        CustomerMatch customer;
        if (matchByPersonalNumber == null) {
            customer = new CreateCustomer();
        } else {
            customer = new PrivateCustomer(matchByPersonalNumber, externalId);
        }
        return new CustomerMatches(matchByPersonalNumber).with(customer);
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
