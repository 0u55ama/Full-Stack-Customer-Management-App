package com.osm.customer;

import com.osm.exception.DuplicateResourceException;
import com.osm.exception.RequestValidationException;
import com.osm.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDAO customerDAO;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDAO);
    }

    @Test
    void getAllCustomers() {
        // When
        underTest.getAllCustomers();

        // Then
        verify(customerDAO)
                .selectAllCustomers();
    }

    @Test
    void canGetCustomers() {
        // Given
        int id = 2;
        Customer customer = new Customer(
                (long) id,
                "lzzy",
                "lzzy@mail.com",
                19,
                Gender.MALE
        );
        when(customerDAO.selectCustomersById(id))
                .thenReturn(Optional.of(customer));

        // When
        Customer actual = underTest.getCustomer(id);

        // Then
        assertThat(actual).isEqualTo(customer);

    }

    @Test
    void willThrowWhenGetCustomersReturnsEmptyOptional() {
        // Given
        int id = 2;

        when(customerDAO.selectCustomersById(id))
                .thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(id));
    }

    @Test
    void addCustomer() {
        // Given
        String email = "lzzy@mail.com";
        when(customerDAO.existsPersonWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "lzzy",
                email,
                19,
                Gender.MALE
        );

        // When
        underTest.addCustomer(request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );

        verify(customerDAO).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
        assertThat(capturedCustomer.getGender()).isEqualTo(request.gender());

    }


    @Test
    void willThrowWhenEmailExistsWhileAddCustomer() {
        // Given
        String email = "lzzy@mail.com";
        when(customerDAO.existsPersonWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "lzzy",
                email,
                19,
                Gender.MALE
        );

        // When
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already taken");

        // Then
        verify(customerDAO,never()).insertCustomer(any());


    }

    @Test
    void deleteCustomerById() {
        // Given
        int id = 2;

        when(customerDAO.existsPersonWithId(id)).thenReturn(true);

        // When
        underTest.deleteCustomerById(id);

        // Then
        verify(customerDAO).deleteCustomerById(id);
    }

    @Test
    void willThrowWhenDeleteCustomerByIdNotExists() {
        // Given
        int id = 2;

        when(customerDAO.existsPersonWithId(id)).thenReturn(false);

        // When
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(id));

        // Then
        verify(customerDAO, never()).deleteCustomerById(id);
    }

    @Test
    void canUpdateAllCustomerProperties() {
        // Given
        int id = 2;
        Customer customer = new Customer(
                (long) id,
                "lzzy",
                "lzzy@mail.com",
                19,
                Gender.MALE

        );
        when(customerDAO.selectCustomersById(id)).thenReturn(Optional.of(customer));

        String updatedEmail = "zzz@mail.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "updatedZZZ",
                updatedEmail,
                23
        );

        when(customerDAO.existsPersonWithEmail(updatedEmail)).thenReturn(false);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void canUpdateCustomerName() {
        // Given
        int id = 2;
        Customer customer = new Customer(
                (long) id,
                "lzzy",
                "lzzy@mail.com",
                19,
                Gender.FEMALE
        );
        when(customerDAO.selectCustomersById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "updatedZZZ",
                null,
                null

        );

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void canUpdateCustomerEmail () {
        // Given
        int id = 2;
        Customer customer = new Customer(
                (long) id,
                "lzzy",
                "lzzy@mail.com",
                19,
                Gender.FEMALE
        );
        when(customerDAO.selectCustomersById(id)).thenReturn(Optional.of(customer));

        String updatedEmail = "zzz@mail.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null,
                updatedEmail,
                null
        );

        when(customerDAO.existsPersonWithEmail(updatedEmail)).thenReturn(false);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updatedEmail);
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void canUpdateCustomerAge() {
        // Given
        int id = 100;
        Customer customer = new Customer(
                (long) id,
                "lzzy",
                "lzzy@mail.com",
                19,
                Gender.FEMALE
        );
        when(customerDAO.selectCustomersById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null,
                null,
                20

        );

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
        assertThat(capturedCustomer.getGender()).isEqualTo(customer.getGender());
    }



    @Test
    void WillThrowWhenTryingToUpdateCustomerEmailWhenAlreadyTake() {
        // Given
        int id = 2;
        Customer customer = new Customer(
                (long) id,
                "lzzy",
                "lzzy@mail.com",
                19,
                Gender.MALE
        );
        when(customerDAO.selectCustomersById(id)).thenReturn(Optional.of(customer));

        String updatedEmail = "zzz@mail.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "updatedZZZ",
                updatedEmail,
                23
        );

        when(customerDAO.existsPersonWithEmail(updatedEmail)).thenReturn(true);

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email is already taken");

        // Then
        verify(customerDAO, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenCustomerUpdateHasNoChanges() {
        // Given
        int id = 2;
        Customer customer = new Customer(
                (long) id,
                "lzzy",
                "lzzy@mail.com",
                19,
                Gender.FEMALE
        );
        when(customerDAO.selectCustomersById(id)).thenReturn(Optional.of(customer));


        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                customer.getName(),
                customer.getEmail(),
                customer.getAge()
        );

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No data changes found !!");
        // Then


        verify(customerDAO, never()).updateCustomer(any());


    }


}