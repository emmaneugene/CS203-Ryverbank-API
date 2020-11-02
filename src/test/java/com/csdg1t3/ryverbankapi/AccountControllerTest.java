package com.csdg1t3.ryverbankapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

import com.csdg1t3.ryverbankapi.account.*;
import com.csdg1t3.ryverbankapi.user.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {
    @Mock 
    private AccountRepository accountRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private AccountController accountController;

    @InjectMocks
    private UserController userController;

    private User user = new User((long) 1, "Test User", "S9926201Z", "92307743", "23 Hume Rd", "testUser", "testing", "ROLE_USER", true);


    @Test
    void createAccount_ValidUserId_ReturnsSavedAccount(){ 
        //Arrange
        Account newAccount = new Account(Long.valueOf(1), user, user.getId(), 1000.0, 1000.0);
            
        //mock userRepo behaviour
        when(userRepo.findById(any(Long.class))).thenReturn(Optional.of(user));

        //mock accountRepo behaviour
        when(accountRepo.save(newAccount)).thenReturn(newAccount);

        //act
        Account savedAccount = accountController.addAccount(newAccount);

        //assert result
        assertNotNull(savedAccount);
        verify(userRepo).findById(newAccount.getCustomer_id());
        verify(accountRepo).save(newAccount);

    }
    
    @Test
    void createAccount_InvalidUserID_ThrowsAccountNotValidException(){
        // Arrange
        user.setId(Long.valueOf(100));
        Account newAccount = new Account(Long.valueOf(100), user, user.getId(), 1000.0, 1000.0);

        // mock userRepo behaviour
        when(userRepo.findById(any(Long.class))).thenReturn(Optional.empty());

        // Act and assert result
        assertThrows(AccountNotValidException.class, () -> accountController.addAccount(newAccount));
        verify(userRepo).findById(newAccount.getCustomer_id());
    }

    
    //assert that account balance is valid
    @Test
    void createAccount_ValidAccountBalance_ReturnsSavedAccount(){
        //Arrange
        Account newAccount = new Account(Long.valueOf(1), user, user.getId(), 1000.0, 1000.0);
            
        //mock userRepo behaviour
        when(userRepo.findById(any(Long.class))).thenReturn(Optional.of(user));

        //mock accountRepo behaviour
        when(accountRepo.save(newAccount)).thenReturn(newAccount);

        //act
        Account savedAccount = accountController.addAccount(newAccount);

        //assert result
        assertNotNull(savedAccount);
        verify(userRepo).findById(newAccount.getCustomer_id());
        verify(accountRepo).save(newAccount);
    }

    @Test
    void createAccount_InvalidAccountBalance_ThrowsAccountNotValidException() { 
        Account newAccount = new Account(Long.valueOf(100), user, user.getId(), -1000.0, -1000.0);

        // mock userRepo behaviour
        when(userRepo.findById(any(Long.class))).thenReturn(Optional.empty());

        // Act and assert result
        assertThrows(AccountNotValidException.class, () -> accountController.addAccount(newAccount));
        verify(userRepo).findById(newAccount.getCustomer_id());
    }

    // @Test
    // void getAccount_isOwnAccount_ReturnAccount() {
    //     Account newAccount = new Account(Long.valueOf(100), user, user.getId(), 1000.0, 1000.0);

    //     // mock userAuth ?
        
    //     // mock userRepo behaviour
    //     when(accountRepo.findById((any(Long.class)))).thenReturn(Optional.of(newAccount));

    //     // Act 
    //     Account getAccount = accountController.getAccount(newAccount.getId());

    //     // Assert result
    //     assertNotNull(getAccount);
    //     verify(accountRepo).findById(newAccount.getId());
    // }

    // @Test
    // void getAccount_isOtherUser_ThrowsRoleNotAuthorisedException() {
    //    //Arrange
    //    Account newAccount = new Account(Long.valueOf(1), user, user.getId(), 1000.0, 1000.0);
    //    User secondUser = new User((long) 2, "Test User", "S9926201Z", "92307743", "23 Hume Rd", "testUser", "testing", "ROLE_USER", true);
    //    Account secondAccount = new Account(Long.valueOf(1), user, user.getId(), 1000.0, 1000.0);

            
    //    //mock userRepo behaviour
    //    when(userRepo.findById(any(Long.class))).thenReturn(Optional.of(user));

    //    //act
    //    Account savedAccount = accountController.addAccount(newAccount);
    //    Account secondSavedAccount = accountController.addAccount(secondAccount);

    //    //assert result
    //    assertNotNull(savedAccount);
    //    assertNotNull(second);
    //    verify(userRepo).findById(newAccount.getCustomer_id());
    //    verify(accountRepo).save(newAccount);
    // }

    @Test
    void getAccount_isAnalyst_Success() {
        
    }

    @Test
    void getAccount_isManager_Success() {

    }
}
