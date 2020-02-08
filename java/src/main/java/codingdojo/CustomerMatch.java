package codingdojo;

public interface CustomerMatch {
    void importExternalData(ExternalCustomer externalCustomer);

    void persist(CustomerDataAccess customerDataAccess);

    boolean createsPrimaryCustomer();
}
