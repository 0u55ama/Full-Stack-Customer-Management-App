package com.osm.customer;

import com.osm.exception.DuplicateResourceException;
import com.osm.exception.RequestValidationException;
import com.osm.exception.ResourceNotFoundException;
import com.osm.s3.S3Buckets;
import com.osm.s3.S3Service;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerDAO customerDAO;
    private final PasswordEncoder passwordEncoder;
    private final CustomerDTOMapper customerDTOMapper;
    private final S3Service s3Service;
    private final S3Buckets s3Buckets;

    public CustomerService(@Qualifier("jdbc") CustomerDAO customerDAO, PasswordEncoder passwordEncoder, CustomerDTOMapper customerDTOMapper, S3Service s3Service, S3Buckets s3Buckets) {
        this.customerDAO = customerDAO;
        this.passwordEncoder = passwordEncoder;
        this.customerDTOMapper = customerDTOMapper;
        this.s3Service = s3Service;
        this.s3Buckets = s3Buckets;
    }

    public List<CustomerDTO> getAllCustomers(){
        return customerDAO.selectAllCustomers()
                .stream()
                .map(customerDTOMapper ).collect(Collectors.toList());
    }

    public CustomerDTO getCustomer(Integer id){
        return customerDAO.selectCustomersById(id)
                .map(customerDTOMapper)
                .orElseThrow(
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
                passwordEncoder.encode(customerRegistrationRequest.password()),
                customerRegistrationRequest.age(),
                customerRegistrationRequest.gender()
        );
        customerDAO.insertCustomer(customer);

    }

    public void deleteCustomerById(Integer id){
        checkIfCustomerExistsOrThrow(id);

        customerDAO.deleteCustomerById(id);
    }

    private void checkIfCustomerExistsOrThrow(Integer id) {
        if (!customerDAO.existsPersonWithId(id)){
            throw new ResourceNotFoundException("Customer with id [%s] not found".formatted(id));
        }
    }

    public void updateCustomer(Integer customerId, CustomerUpdateRequest updateRequest){
        Customer customer = customerDAO.selectCustomersById(customerId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Customer with id [%s] not found".formatted(customerId))
                );
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

    public void uploadCustomerProfileImage(Integer customerId,
                                           MultipartFile file) {
        checkIfCustomerExistsOrThrow(customerId);
        String profileImageId = UUID.randomUUID().toString();
        try {
            s3Service.putObject(
                    s3Buckets.getCustomer(),
                    "profile-images/%s/%s".formatted(customerId, profileImageId),
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new RuntimeException("failed to upload profile image", e);
        }
        customerDAO.updateCustomerProfileImageId(profileImageId, customerId);
    }

    public byte[] getCustomerProfileImage(Integer customerId) {
        var customer = customerDAO.selectCustomersById(customerId)
                .map(customerDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "customer with id [%s] not found".formatted(customerId)
                ));

        if (StringUtils.isBlank(customer.profileImageId())) {
            throw new ResourceNotFoundException(
                    "customer with id [%s] profile image not found".formatted(customerId));
        }

        byte[] profileImage = s3Service.getObject(
                s3Buckets.getCustomer(),
                "profile-images/%s/%s".formatted(customerId, customer.profileImageId())
        );
        return profileImage;
    }
}