package com.osm.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDAO {

    private static List<Customer> customers;
    static {
        customers = new ArrayList<>();
        Customer Oussama = new Customer(
                "Oussama",
                "oussama@mail.com",
                "azerty.123..",
                22,
                Gender.MALE
        );
        customers.add(Oussama);
    }
    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomersById(int id) {
        return  customers.stream().
                filter(c -> c.getId().equals(id)).
                findFirst();

    }

    @Override
    public void insertCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        return customers.stream()
                .anyMatch(c -> c.getEmail().equals(email));
    }

    @Override
    public void deleteCustomerById(Integer customerId) {
        customers.stream()
                .filter(c -> c.getId().equals(customerId))
                .findFirst()
                .ifPresent(customers::remove);
    }

    @Override
    public boolean existsPersonWithId(Integer id) {
        return customers.stream()
                .anyMatch(c -> c.getId().equals(id));
    }

    @Override
    public void updateCustomer(Customer update) {
        customers.add(update);

    }

    @Override
    public Optional<Customer> selectUserByEmail(String email) {
        return  customers.stream().
                filter(c -> c.getUsername().equals(email)).
                findFirst();

    }

    @Override
    public void updateCustomerProfileImageId(String profileImageId, Integer customerId) {
        //TODO
    }
}
