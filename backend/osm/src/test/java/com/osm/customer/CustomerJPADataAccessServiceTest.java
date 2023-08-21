package com.osm.customer;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;
    @Mock private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        Page<Customer> page = mock(Page.class);
        List<Customer> customers = List.of(new Customer());
        when(page.getContent()).thenReturn(customers);
        when(customerRepository.findAll(any(Pageable.class))).thenReturn(page);
        // When
        List<Customer> expected = underTest.selectAllCustomers();

        // Then
        assertThat(expected).isEqualTo(customers);
        ArgumentCaptor<Pageable> pageArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(customerRepository).findAll(pageArgumentCaptor.capture());
        assertThat(pageArgumentCaptor.getValue()).isEqualTo(Pageable.ofSize(100));
    }

    @Test
    void selectCustomersById() {
        // Given
        int id = 1;

        // When
        underTest.selectCustomersById(id);

        // Then
        verify(customerRepository)
                .findById(id);
    }

    @Test
    void insertCustomer() {
        // Given
        Customer customer = new Customer(
                "Ali",
                "ali_abdelaziz@mail.com",
                "azerty.123..", 44,
                Gender.MALE
        );

        // When
        underTest.insertCustomer(customer);

        // Then
        verify(customerRepository)
                .save(customer);
    }

    @Test
    void existsPersonWithEmail() {
        // Given
        String email = "foooo@mail.com";

        // When
        underTest.existsPersonWithEmail(email);

        // Then
        verify(customerRepository)
                .existsCustomerByEmail(email);
    }

    @Test
    void deleteCustomerById() {
        // Given
        int id = 11;

        // When
        underTest.deleteCustomerById(id);

        // Then
        verify(customerRepository)
                .deleteById(id);
    }

    @Test
    void existsPersonWithId() {
        // Given
        int id = 2;

        // When
        underTest.existsPersonWithId(id);

        // Then
        verify(customerRepository)
                .existsCustomerById(id);
    }

    @Test
    void updateCustomer() {
        // Given
        Customer customer = new Customer(
                1L,
                "Ali",
                "ali_abdelaziz2@mail.com",
                "azerty.123..",
                45,
                Gender.FEMALE
        );

        // When
        underTest.updateCustomer(customer);

        // Then
        verify(customerRepository)
                .save(customer);
    }

    @Test
    void canUpdateProfileImageId() {
        // Given
        String profileImageId = "45454";
        Integer customerId = 2;
        // When

        underTest.updateCustomerProfileImageId(profileImageId, customerId);

        // Then

        verify(customerRepository).updateProfileImageId(profileImageId, customerId);
    }
}