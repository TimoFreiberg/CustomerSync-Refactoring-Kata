package codingdojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Customer {
    private String externalId;
    private String masterExternalId;
    private Address address;
    private String preferredStore;
    private List<ShoppingList> shoppingLists = new ArrayList<>();
    private String internalId;
    private String name;
    private CustomerType customerType;
    private String companyNumber;

    public Customer() {
    }

    public static Customer fromExternalId(String externalId) {
        Customer customer = new Customer();
        customer.externalId = externalId;
        customer.masterExternalId = externalId;
        return customer;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public void setMasterExternalId(String masterExternalId) {
        this.masterExternalId = masterExternalId;
    }

    public String getMasterExternalId() {
        return masterExternalId;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    public String getInternalId() {
        return internalId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPreferredStore(String preferredStore) {
        this.preferredStore = preferredStore;
    }

    public String getPreferredStore() {
        return preferredStore;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public List<ShoppingList> getShoppingLists() {
        return shoppingLists;
    }

    public void setShoppingLists(List<ShoppingList> shoppingLists) {
        this.shoppingLists = shoppingLists;
    }

    public void importExternalData(ExternalCustomer externalCustomer) {
        this.setName(externalCustomer.getName());
        this.setCompanyNumber(externalCustomer.getCompanyNumber());
        this.setCustomerType(externalCustomer.getCustomerType());
        this.setAddress(externalCustomer.getPostalAddress());
        this.setPreferredStore(externalCustomer.getPreferredStore());
        this.addShoppingLists(externalCustomer.getShoppingLists());
    }

    public String getName() {
        return name;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public void addShoppingList(ShoppingList consumerShoppingList) {
        this.shoppingLists.add(consumerShoppingList);
    }

    public void addShoppingLists(List<ShoppingList> consumerShoppingLists) {
        this.shoppingLists.addAll(consumerShoppingLists);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return Objects.equals(externalId, customer.externalId) &&
                Objects.equals(masterExternalId, customer.masterExternalId) &&
                Objects.equals(companyNumber, customer.companyNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalId, masterExternalId, companyNumber);
    }
}
