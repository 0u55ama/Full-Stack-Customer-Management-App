package com.osm.journey;


import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.osm.customer.Customer;
import com.osm.customer.CustomerRegistrationRequest;
import com.osm.customer.CustomerUpdateRequest;
import com.osm.customer.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIT {

    @Autowired 
    private WebTestClient webTestClient;
    private static final Random RANDOM = new Random();
    private static final String CUSTOMER_URI = "/api/v1/customers";


    @Test
    void canRegisterACustomer() {
        // create a registration request
        Faker faker = new Faker();

        Name fakeName = faker.name();
        String name = fakeName.lastName();
        String email = fakeName.lastName()+ "_" +UUID.randomUUID() + "@mail.com";
        int age = RANDOM.nextInt(1,100);
        Gender[] genders = Gender.values();
        Gender gender = genders[RANDOM.nextInt(genders.length)];

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name,
                email,
                age,
                gender
        );
        // send a post request

        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all customer
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // make sure that a customer is a present
        Customer expectedCustomer = new Customer(
                name,
                email,
                age,
                gender
        );
        assertThat(allCustomers).
                usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        expectedCustomer.setId(id);
        //get customer by id
         webTestClient.get()
                .uri(CUSTOMER_URI+"/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                 .isEqualTo(expectedCustomer);
    }

    @Test
    void canDeleteCustomer() {
        // create a registration request
        Faker faker = new Faker();

        Name fakeName = faker.name();
        String name = fakeName.lastName();
        String email = fakeName.lastName()+ "_" +UUID.randomUUID() + "@mail.com";
        int age = RANDOM.nextInt(1,100);

        Gender[] genders = Gender.values();
        Gender gender = genders[RANDOM.nextInt(genders.length)];

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name,
                email,
                age,
                gender
        );
        // send a post request

        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all customer
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();



        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //delete customer
        webTestClient.delete()
                .uri(CUSTOMER_URI+"/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        //get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI+"/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        // create a registration request
        Faker faker = new Faker();

        Name fakeName = faker.name();
        String name = fakeName.lastName();
        String email = fakeName.lastName()+ "_" +UUID.randomUUID() + "@mail.com";
        int age = RANDOM.nextInt(1,100);

        Gender[] genders = Gender.values();
        Gender gender = genders[RANDOM.nextInt(genders.length)];

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name,
                email,
                age,
                gender
        );

        // send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all customer
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String updatedName = "updatedName";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                updatedName,
                null,
                null
        ) ;

        //update customer
        webTestClient.put()
                .uri(CUSTOMER_URI+"/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //get customer by id
        Customer updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        Customer expected = new Customer(
                id,
                updatedName,
                email,
                age,
                gender
        );
        assertThat(updatedCustomer).isEqualTo(expected);
    }
}