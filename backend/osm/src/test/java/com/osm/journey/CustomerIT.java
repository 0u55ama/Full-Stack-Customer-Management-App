package com.osm.journey;


import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.osm.customer.*;
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
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIT {

    @Autowired 
    private WebTestClient webTestClient;
    private static final Random RANDOM = new Random();
    private static final String CUSTOMER_PATH = "/api/v1/customers";


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
                "azerty.123..", age,
                gender
        );
        // send a post request

        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);


        // get all customer
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s",jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        // make sure that a customer is a present



        var id = allCustomers.stream()
                .filter(customer -> customer.email().equals(email))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();

        CustomerDTO expectedCustomer = new CustomerDTO(
                id,
                name,
                email,
                gender,
                age,
                List.of("ROLE_USER"),
                email
        );

        assertThat(allCustomers).contains(expectedCustomer);

        //get customer by id
         webTestClient.get()
                 .uri(CUSTOMER_PATH +"/{id}", id)
                 .accept(MediaType.APPLICATION_JSON)
                 .header(AUTHORIZATION, String.format("Bearer %s",jwtToken))
                 .exchange()
                 .expectStatus()
                 .isOk()
                 .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
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
                "azerty.123..",
                age,
                gender
        );

        CustomerRegistrationRequest request2 = new CustomerRegistrationRequest(
                name,
                email + ".ma",
                "azerty.123..",
                age,
                gender
        );

        // send a post request to create customer 1
        webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // send a post request to create customer2
        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request2), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);

        // get all customer
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();



        var id = allCustomers.stream()
                .filter(customer -> customer.email().equals(email))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();

        // customer 2 deletes customer 1
        webTestClient.delete()
                .uri(CUSTOMER_PATH +"/{id}", id)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        //customer 2 gets customer 1 by id
        webTestClient.get()
                .uri(CUSTOMER_PATH +"/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
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
                "azerty.123..", age,
                gender
        );

        // send a post request
        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);

        // get all customer
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        var id = allCustomers.stream()
                .filter(customer -> customer.email().equals(email))
                .map(CustomerDTO::id)
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
                .uri(CUSTOMER_PATH +"/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //get customer by id
        CustomerDTO updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerDTO.class)
                .returnResult()
                .getResponseBody();

        CustomerDTO expected = new CustomerDTO(
                id,
                updatedName,
                email,
                gender,
                age,
                List.of("ROLE_USER"),
                email
        );
        assertThat(updatedCustomer).isEqualTo(expected);
    }
}
