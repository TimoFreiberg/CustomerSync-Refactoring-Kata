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
            customerMatches = loadCompany(externalCustomer);
        } else {
            customerMatches = loadPerson(externalCustomer);
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
        if (duplicate == null) {
            duplicate = this.customerDataAccess.createCustomerRecord(
                    Customer.fromExternalId(externalCustomer.getExternalId())
            );
        }
        duplicate.setName(externalCustomer.getName());
        this.customerDataAccess.updateCustomerRecord(duplicate);
    }

    public CustomerMatches loadCompany(ExternalCustomer externalCustomer) {
        return customerDataAccess.loadCompanyCustomer(externalCustomer.getExternalId(), externalCustomer.getCompanyNumber());
    }

    public CustomerMatches loadPerson(ExternalCustomer externalCustomer) {
        final String externalId = externalCustomer.getExternalId();

        CustomerMatches customerMatches = customerDataAccess.loadPersonCustomer(externalId);

        if (customerMatches.getCustomer() != null) {
            if (!CustomerType.PERSON.equals(customerMatches.getCustomer().getCustomerType())) {
                throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a person");
            }

            if (!"ExternalId".equals(customerMatches.getMatchTerm())) {
                Customer customer = customerMatches.getCustomer();
                customer.setExternalId(externalId);
                customer.setMasterExternalId(externalId);
            }
        }

        return customerMatches;
    }
}
