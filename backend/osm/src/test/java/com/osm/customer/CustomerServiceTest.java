package com.osm.customer;

import com.osm.exception.DuplicateResourceException;
import com.osm.exception.RequestValidationException;
import com.osm.exception.ResourceNotFoundException;
import com.osm.s3.S3Buckets;
import com.osm.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDAO customerDAO;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private S3Service s3Service;
    @Mock
    private S3Buckets s3Buckets;
    private CustomerService underTest;
    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDAO,
                passwordEncoder,
                customerDTOMapper,
                s3Service,
                s3Buckets);
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
                "azerty.123..", 19,
                Gender.MALE
        );
        when(customerDAO.selectCustomersById(id))
                .thenReturn(Optional.of(customer));

        CustomerDTO expected = customerDTOMapper.apply(customer);

        // When
        CustomerDTO actual = underTest.getCustomer(id);

        // Then
        assertThat(actual).isEqualTo(expected);

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
                "azerty.123..",
                19,
                Gender.MALE
        );

        String passwordHash = "522@jhf5555;lkznfoS";

        when(passwordEncoder.encode(request.password())).thenReturn(passwordHash);

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
        assertThat(capturedCustomer.getPassword()).isEqualTo(passwordHash);
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
                "azerty.123..", 19,
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
                "azerty.123..", 19,
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
                "azerty.123..", 19,
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
                "azerty.123..", 19,
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
                "azerty.123..", 19,
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
                "azerty.123..", 19,
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
                "azerty.123..", 19,
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

    @Test
    void canUploadProfileImage() {
        // Given
        int customerId = 2;

        when(customerDAO.existsPersonWithId(customerId)).thenReturn(true);

        byte[] bytes = "Hello World".getBytes();
        MultipartFile multipartFile = new MockMultipartFile(
                "file", bytes);


        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);
        // When
        underTest.uploadCustomerProfileImage(
                customerId,
                multipartFile
        );

        // Then

        ArgumentCaptor<String> profileImageIdArgumentCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(customerDAO).updateCustomerProfileImageId(
                profileImageIdArgumentCaptor.capture(),
                eq(customerId)
        );

        verify(s3Service).putObject(
                bucket,
                "profile-images/%s/%s".formatted(customerId, profileImageIdArgumentCaptor.getValue()),
                bytes
        );
    }

    @Test
    void cannotUploadProfileImageWhenCustomerDoesNotExists() {
        // Given
        int customerId = 2;

        when(customerDAO.existsPersonWithId(customerId)).thenReturn(false);

        assertThatThrownBy(() -> underTest.uploadCustomerProfileImage(customerId, mock(MultipartFile.class)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id ["+customerId+"] not found");
        //when

        verify(customerDAO).existsPersonWithId(customerId);
        verifyNoMoreInteractions(customerDAO);
        verifyNoInteractions(s3Buckets);
        verifyNoInteractions(s3Service);

    }

    @Test
    void cannotUploadProfileImageWhenExceptionIsThrown() throws IOException {
        // Given
        int customerId = 2;

        when(customerDAO.existsPersonWithId(customerId)).thenReturn(true);

        byte[] bytes = "Hello World".getBytes();
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getBytes()).thenThrow(IOException.class);


        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);
        // When

        assertThatThrownBy(() -> {
            underTest.uploadCustomerProfileImage(customerId, multipartFile);

        }).isInstanceOf(RuntimeException.class)
                .hasMessage("failed to upload profile image")
                .hasRootCauseInstanceOf(IOException.class);
        // Then

        verify(customerDAO, never()).updateCustomerProfileImageId(any(), any());

    }

    @Test
    void canDownloadProfileImage() {
        // Given
        int customerId = 2;
        String profileImageId = "45454";
        Customer customer = new Customer(
                (long) customerId,
                "lzzy",
                "lzzy@mail.com",
                "azerty.123..",
                19,
                Gender.FEMALE,
                profileImageId
        );
        when(customerDAO.selectCustomersById(customerId)).thenReturn(Optional.of(customer));


        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);
        byte[] expectedImage = "image".getBytes();
        when(s3Service.getObject(
                bucket,
                "profile-images/%s/%s".formatted(customerId, profileImageId)
        )).thenReturn(expectedImage);
        // When
        byte[] actualImage = underTest.getCustomerProfileImage(customerId);

        // Then
        assertThat(actualImage).isEqualTo(expectedImage);
    }


    @Test
    void cannotDownloadWhenNoProfileImageId() {
        // Given
        int customerId = 2;
        Customer customer = new Customer(
                (long) customerId,
                "lzzy",
                "lzzy@mail.com",
                "azerty.123..",
                19,
                Gender.FEMALE

        );
        when(customerDAO.selectCustomersById(customerId)).thenReturn(Optional.of(customer));



        // When
        // Then

        assertThatThrownBy(() -> underTest.getCustomerProfileImage(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] profile image not found".formatted(customerId));


        verifyNoInteractions(s3Buckets);
        verifyNoInteractions(s3Service);
    }



    @Test
    void cannotDownloadProfileImageWhenCustomerDoesNotExists() {
        // Given
        int customerId = 2;

        when(customerDAO.selectCustomersById(customerId)).thenReturn(Optional.empty());


        // When
        // Then

        assertThatThrownBy(() -> underTest.getCustomerProfileImage(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(customerId));


        verifyNoInteractions(s3Buckets);
        verifyNoInteractions(s3Service);
    }
}