package codingdojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerMatches {
    private Collection<Customer> duplicates;
    private Customer customer;
    private Collection<Bla> blas;

    public CustomerMatches() {
        this.duplicates = new ArrayList<>();
    }

    public CustomerMatches(Customer customer, Customer... duplicates) {
        this.duplicates = Arrays.stream(duplicates).filter(x -> x != null).collect(Collectors.toList());
        this.customer = customer;
    }

    public Iterable<Bla> matches() {
        return List.of();
    }

    public CustomerMatches with(Bla... blas) {
        this.blas = new ArrayList(Arrays.asList(blas));
        return this;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void addDuplicate(Customer duplicate) {
        if (duplicate != null) {
            duplicates.add(duplicate);
        }
    }

    public Collection<Customer> getDuplicates() {
        return duplicates;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
