package com.osm.customer;

import com.osm.exception.DuplicateResourceException;
import com.osm.exception.RequestValidationException;
import com.osm.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDAO customerDAO;

    public CustomerService(@Qualifier("jdbc") CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public List<Customer> getAllCustomers(){
        return customerDAO.selectAllCustomers();
    }

    public Customer getCustomer(Integer id){
        return customerDAO.selectCustomersById(id).
                orElseThrow(
                () -> new ResourceNotFoundException("Customer with id [%s] not found".formatted(id))
        );
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest){
        //check if email exists then add it
        String email = customerRegistrationRequest.email();
        if (customerDAO.existsPersonWithEmail(email)){
            throw new DuplicateResourceException("Email already taken");
        }
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age(),
                customerRegistrationRequest.gender()
        );
        customerDAO.insertCustomer(customer);

    }

    public void deleteCustomerById(Integer id){
        if (!customerDAO.existsPersonWithId(id)){
            throw new ResourceNotFoundException("Customer with id [%s] not found".formatted(id));
        }
        customerDAO.deleteCustomerById(id);
    }

    public void updateCustomer(Integer customerId, CustomerUpdateRequest updateRequest){
        Customer customer = getCustomer(customerId);
        boolean changes = false;

        if (updateRequest.name() != null && !updateRequest.name().equals(customer.getName())){
            customer.setName(updateRequest.name());
            changes = true;
        }

        if (updateRequest.age() != null && !updateRequest.age().equals(customer.getAge())){
            customer.setAge(updateRequest.age());
             changes = true;
        }

        if (updateRequest.email() != null && !updateRequest.email().equals(customer.getEmail())){
            if (customerDAO.existsPersonWithEmail(updateRequest.email())){
                throw new DuplicateResourceException("Email is already taken");
            }
            customer.setEmail(updateRequest.email());
            changes = true;
        }
/*
        if (updateRequest.gender() != null && !updateRequest.gender().equals(customer.getGender())){
            customer.setGender(updateRequest.gender());
            changes = true;

        }


 */
        if (!changes){
            throw new RequestValidationException("No data changes found !!");
        }

        customerDAO.updateCustomer(customer);






    }

}
