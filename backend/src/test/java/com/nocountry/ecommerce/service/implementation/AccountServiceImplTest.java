package com.nocountry.ecommerce.service.implementation;

import com.nocountry.ecommerce.dto.CustomerRegistration;
import com.nocountry.ecommerce.model.Customers;
import com.nocountry.ecommerce.repository.AccountRepository;
import com.nocountry.ecommerce.repository.CustomerRepository;
import com.nocountry.ecommerce.security.jwt.JwtProvider;
import com.nocountry.ecommerce.service.AccountService;
import com.nocountry.ecommerce.util.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.internal.bytebuddy.utility.RandomString;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountService accountService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtProvider jwtProvider;
    @DisplayName("Given a user account")
    @Nested
    class AccountTest{
        CustomerRegistration customerRegistration = new CustomerRegistration();
        int anoActual;
        String numeracion;
        @BeforeEach
        void setUp() {
            anoActual = LocalDate.now().getYear();
            numeracion = "1";
        }

        @Nested
        @DisplayName("Given a customer account")
        class CustomerAccount{
            @DisplayName("Then you can create a customer account")
            @Test
            public void createCustomer() {
                // Arrange
                String email = "alfonsoalmonte@gmail.com";
                customerRegistration.setEmail(email);
                String password = passwordEncoder.encode("1234");
                customerRegistration.setPassword(password);
                String name = "Alfonso";
                String lastName = "Almonte";
                String fullName = name + " " + lastName;
                customerRegistration.setFullName(fullName);
                String verificationCode = RandomString.make(64);
                customerRegistration.setVerificationCode(verificationCode);

                Customers customer = new Customers(email, password);
                customer.setEmail(email);
                customer.setPassword(password);
                customer.setName(name);
                customer.setLastName(lastName);
                String customerNumber = anoActual + "-" + numeracion;
                customer.setRol(Role.USER);
                customer.setNumber(customerNumber);
                customer.setVerificationCode(verificationCode);
                String jwt = jwtProvider.generateToken(customer);
                customerRegistration.setToken(jwt);

                // Act
                CustomerRegistration saveCustomer = accountService.createCustomer(customer);
                // Assert
                verify(customerRepository, times(1)).save(customer);
                assertEquals(saveCustomer,customer);
            }
        }
    }
}