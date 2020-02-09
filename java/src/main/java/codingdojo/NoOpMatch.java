package codingdojo;

public class NoOpMatch implements CustomerMatch {
    @Override
    public void importExternalData(ExternalCustomer externalCustomer) {
    }

    @Override
    public void persist(CustomerDataAccess customerDataAccess) {
    }

    @Override
    public boolean createsPrimaryCustomer() {
        return false;
    }
}
