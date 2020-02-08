package codingdojo;

public interface Bla {
    void importExternalData(ExternalCustomer externalCustomer, CustomerDataAccess customerDataAccess);

    void persist(CustomerDataAccess customerDataAccess);
}
