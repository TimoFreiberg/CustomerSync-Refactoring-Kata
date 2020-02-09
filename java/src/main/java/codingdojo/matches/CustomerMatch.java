package codingdojo.matches;

import codingdojo.CustomerDataAccess;
import codingdojo.ExternalCustomer;

public interface CustomerMatch {
    void importExternalData(ExternalCustomer externalCustomer);

    void persist(CustomerDataAccess customerDataAccess);

    boolean createsPrimaryCustomer();
}
