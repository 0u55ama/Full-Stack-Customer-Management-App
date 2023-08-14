package com.osm.customer;

import com.osm.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        // Given
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress()+ "_" +UUID.randomUUID(),
                "azerty.123..", 20,
                Gender.MALE
        );
        underTest.insertCustomer(customer);

        // When
        List<Customer> customers = underTest.selectAllCustomers();

        // Then
        assertThat(customers).isNotEmpty();
    }

    @Test
    void selectCustomersById() {
        // Given
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                email,
                "azerty.123..", 20,
                Gender.FEMALE
        );
        underTest.insertCustomer(customer);
        int id = Math.toIntExact(underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow());

        // When
        Optional<Customer> actual = underTest.selectCustomersById(id);

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getGender()).isEqualTo(customer.getGender());

        });
    }

    @Test
    void willReturnEmptyWhenSelectedCustomerByID() {
        // Given
        int id = -1;

        // When
        var actual = underTest.selectCustomersById(id);

        // Then
        assertThat(actual).isEmpty();
    }


    @Test
    void insertCustomer() {
        // Given

        // When

        // Then
    }

    @Test
    void existsPersonWithEmail() {
        // Given
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                email,
                "azerty.123..", 25,
                Gender.MALE
        );
        underTest.insertCustomer(customer);

        // When
        boolean actual = underTest.existsPersonWithEmail(email);

        // Then
        assertThat(actual).isTrue();

    }

    @Test
    void existsPersonWithEmailReturnsFalseWhenDoesNotExists() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();

        // When
        boolean actual = underTest.existsPersonWithEmail(email);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void deleteCustomerById() {
        // Given
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                email,
                "azerty.123..", 20,
                Gender.FEMALE
        );
        underTest.insertCustomer(customer);
        int id = Math.toIntExact(underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow());

        // When
        underTest.deleteCustomerById(id);

        // Then
        Optional<Customer> actual = underTest.selectCustomersById(id);
        assertThat(actual).isNotPresent();
    }

    @Test
    void existsPersonWithId() {
        // Given
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                email,
                "azerty.123..", 20,
                Gender.FEMALE
        );
        underTest.insertCustomer(customer);
        int id = Math.toIntExact(underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow());

        // When
        var actual = underTest.existsPersonWithId(id);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsPersonWithIdReturnsFalseWhenDoesNotExists() {
        // Given
        int id = 0;

        // When
        var actual = underTest.existsPersonWithId(id);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void updateCustomerName() {
        // Given
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                email,
                "azerty.123..", 20,
                Gender.FEMALE
        );
        underTest.insertCustomer(customer);
        int id = Math.toIntExact(underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow());

        var updatedName = "foo";

        // When
        Customer update = new Customer();
        update.setId((long) id);
        update.setName(updatedName);

        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual = underTest.selectCustomersById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(updatedName);
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getGender()).isEqualTo(customer.getGender());
 

        });
    }

    @Test
    void updateCustomerEmail() {
        // Given
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                email,
                "azerty.123..", 20,
                Gender.MALE
        );
        underTest.insertCustomer(customer);
        int id = Math.toIntExact(underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow());

        var updatedEmail = FAKER.internet().safeEmailAddress()+ "_" +UUID.randomUUID();

        // When
        Customer update = new Customer();
        update.setId((long) id);
        update.setEmail(updatedEmail);

        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual = underTest.selectCustomersById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getEmail()).isEqualTo(updatedEmail);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getGender()).isEqualTo(customer.getGender());
        });
    }

    @Test
    void updateCustomerAge() {
        // Given
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                email,
                "azerty.123..", 20,
                Gender.FEMALE
        );
        underTest.insertCustomer(customer);
        int id = Math.toIntExact(underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow());

        var updatedAge = 30;

        // When
        Customer update = new Customer();
        update.setId((long) id);
        update.setAge(updatedAge);

        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual = underTest.selectCustomersById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getAge()).isEqualTo(updatedAge);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getGender()).isEqualTo(customer.getGender());

        });
    }

    @Test
    void willUpdatedAllPropertiesCustomer() {
        // Given
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                email,
                "azerty.123..", 20,
                Gender.MALE
        );
        underTest.insertCustomer(customer);
        int id = Math.toIntExact(underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow());

        // When
        Customer update = new Customer();
        update.setId((long) id);
        update.setName("foo");
        String updatedEmail = UUID.randomUUID().toString();
        update.setEmail(updatedEmail);
        update.setAge(22);

        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual = underTest.selectCustomersById(id);
        assertThat(actual).isPresent().hasValueSatisfying(updated -> {
            assertThat(updated.getId()).isEqualTo(id);
            assertThat(updated.getGender()).isEqualTo(Gender.MALE);
            assertThat(updated.getName()).isEqualTo("foo");
            assertThat(updated.getEmail()).isEqualTo(updatedEmail);
            assertThat(updated.getAge()).isEqualTo(22);


        });
    }

    @Test
    void willNoUpdatedWhenNothingToUpdate() {
        // Given
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        Customer customer = new Customer(
                name,
                email,
                "azerty.123..", 20,
                Gender.FEMALE
        );
        underTest.insertCustomer(customer);
        int id = Math.toIntExact(underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow());

        //When no changes
        Customer update = new Customer();
        update.setId((long) id);

        underTest.updateCustomer(update);

        //Then

        Optional<Customer> actual = underTest.selectCustomersById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getGender()).isEqualTo(customer.getGender());
        });
    }
}