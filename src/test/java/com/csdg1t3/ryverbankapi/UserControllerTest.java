package com.csdg1t3.ryverbankapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

import com.csdg1t3.ryverbankapi.trade.*;
import com.csdg1t3.ryverbankapi.user.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.csdg1t3.ryverbankapi.security.*;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jdk.jfr.Timestamp;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserRepository userRepo;

    @Mock 
    private PortfolioRepository portfolioRepo;

    @Mock
    private UserAuthenticator uAuth;

    @Mock
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private UserController userController;

    // Assert that method returns saved user
    @Test
    void addCustomer_NewUserName_ReturnSavedCustomer() {
        // mock 
    }

    // Assert that method throws UserNotValidException
    @Test
    void addCustomer_SameUserName_ThrowUserNotValidException() {

    }

    @Test
    void getCustomers_NoCustomers_returnNullList(){
        // List<User>
        when(userRepo.findAll()).thenReturn(null);
    }

    @Test
    void getCustomers_notUnauthorised_returnForbidden(){

    }

    @Test
    void getCustomers_WithCustomers_returnListOfCustomers(){

    }

    @Test
    void getCustomer_notInRepository_throwUserNotFoundException(){

    }

    @Test
    void getCustomer_notAuthenticated_returnForbbidden(){

    }

    @Test
    void getCustomer_notAuthorised_returnForbbidden(){

    }

    @Test
    void getCustomer_AuthenticatedAndFound_returnUser(){

    }

    @Test
    void getCustomer_AuthenticatedAndNotFound_throwUserNotFoundException(){

    }
}
