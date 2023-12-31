package com.osm.customer;

import com.osm.AbstractTestcontainers;
import com.osm.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestConfig.class})
class CustomerRepositoryTest  extends AbstractTestcontainers {

    @Autowired
    private  CustomerRepository underTest;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
        //System.out.println(applicationContext.getBeanDefinitionCount());
    }

    @Test
    void existsCustomerByEmail() {
        // Given
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                email,
                "azerty.123..", 20,
                Gender.FEMALE
        );
        underTest.save(customer);

        // When
        var actual = underTest.existsCustomerByEmail(email);

        // Then
        assertThat(actual).isTrue();
    }


    @Test
    void existsCustomerByEmailFailsWhenEmailIsNotPresent() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();


        // When
        var actual = underTest.existsCustomerByEmail(email);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerById() {
        // Given
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                email,
                "azerty.123..", 20,
                Gender.FEMALE
        );
        underTest.save(customer);
        int id = Math.toIntExact(underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow());

        // When
        var actual = underTest.existsCustomerById(id);

        // Then
        assertThat(actual).isTrue();

    }

    @Test
    void existsCustomerByIdFailsWhenIdIsNotPresent() {
        // Given
        int id = -1;

        // When
        var actual = underTest.existsCustomerById(id);

        // Then
        assertThat(actual).isFalse();

    }

    @Test
    void canUpdateProfileImageId() {
        // Given
        String name = FAKER.name().fullName();
        String email = "email";
        Customer customer = new Customer(
                name,
                email,
                "azerty.123..",
                20,
                Gender.FEMALE
        );
        underTest.save(customer);

        int id = Math.toIntExact(underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow());
        // When
        underTest.updateProfileImageId("45454", id);

        // Then
        Optional<Customer> customerOptional = underTest.findById(id);
        assertThat(customerOptional)
                .isPresent()
                .hasValueSatisfying(c -> assertThat(c.getProfileImageId()).isEqualTo("45454"));
    }
}