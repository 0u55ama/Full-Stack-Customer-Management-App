package com.osm.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDAO {
    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomersById(int id);

    void insertCustomer(Customer customer);
    boolean existsPersonWithEmail(String email);

    void deleteCustomerById(Integer customerId);
    boolean existsPersonWithId(Integer id);

    void updateCustomer(Customer update);

}
