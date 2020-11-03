package com.csdg1t3.ryverbankapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;
import java.util.Optional; 

// import javax.management.relation.RoleNotFoundException;

import com.csdg1t3.ryverbankapi.trade.*;
import com.csdg1t3.ryverbankapi.user.*;
import com.csdg1t3.ryverbankapi.user.UserNotFoundException;
import com.csdg1t3.ryverbankapi.user.RoleNotAuthorisedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jdk.jfr.Timestamp;

import com.csdg1t3.ryverbankapi.security.*;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Mock
    private Validator Validator;

    @InjectMocks
    private UserController userController;

    private final String u1_FULL_NAME = "cspotatoes";
    private final String  u1_USERNAME = "potato";
    private final String u1_PASSWORD = "iamgoodpotato123";
    private final String u1_ROLE = "ROLE_USER";
    private final String NRIC = "S1234567G";
    private final String PHONE_NO = "93223235";
    private final String u1_PASSWORD_ENCODED = "$2a$10$1/dOPkY80t.wyXV3p1MR0OhEJOnkljtU2AGkalTv1E3MZtJUqmmLO";

    private final String u2_FULL_NAME = "Tan Li Ling";
    private final String  u2_USERNAME = "manager_1";
    private final String u2_PASSWORD = "01_manager_01";
    private final String u2_ROLE = "ROLE_MANAGER";

    // Assert that method returns saved user
    @Test
    void addCustomer_NewUserName_ReturnSavedCustomer() {
        // mock 
        Long id = (long) 5;
        User newUser = new User(id, u1_FULL_NAME, NRIC, PHONE_NO, "HOLLAND V ROAD", u1_USERNAME, u1_PASSWORD,u1_ROLE, true);
        Optional<User> notFound =  Optional.empty();
        Portfolio newUserPortfolio = new Portfolio(null, id, newUser, null, 0, 0);
        when(userRepo.findByUsername(any(String.class))).thenReturn(notFound);
        when(Validator.validateNRIC(any(String.class))).thenReturn(true);
        when(Validator.validatePhoneno(any(String.class))).thenReturn(true);
        when(userRepo.save(any(User.class))).thenReturn(newUser);
        when(portfolioRepo.save(any(Portfolio.class))).thenReturn(newUserPortfolio);
        when(encoder.encode(any(String.class))).thenReturn(u1_PASSWORD_ENCODED);


        //act
        User savedUser = userController.createUser(newUser);

        //assert 
        assertEquals(savedUser,newUser);
        assertNotNull(savedUser.getPortfolio());
        assertEquals(savedUser.getPassword(),u1_PASSWORD_ENCODED);
        verify(userRepo).findByUsername(u1_USERNAME);
        verify(Validator).validateNRIC(NRIC);
        verify(Validator).validatePhoneno(PHONE_NO);
        verify(userRepo).save(newUser);
        verify(portfolioRepo).save(savedUser.getPortfolio());
        verify(encoder).encode(u1_PASSWORD);
        
    }

    // Assert that method throws UserNotValidException
    @Test
    void addCustomer_SameUserName_ThrowUserNotValidException() {
        //mock
        Long id = (long) 3;
        User existingUser = new User(id+1, u2_FULL_NAME, NRIC, PHONE_NO, "BLAH BLAH", u1_USERNAME, encoder.encode(u2_PASSWORD),u2_ROLE, true);
        User newUser = new User(id, u1_FULL_NAME, NRIC, PHONE_NO, "HOLLAND V ROAD", u1_USERNAME, encoder.encode(u1_PASSWORD),u1_ROLE, true);
        Optional<User> Found = Optional.of(existingUser);
        
        when(userRepo.findByUsername(any(String.class))).thenReturn(Found);

        //assert
        assertThrows(UserNotValidException.class, () -> userController.createUser(newUser), "Username already taken");
        verify(userRepo).findByUsername(existingUser.getUsername());
    }

    @Test
    void addCustomer_InvalidNric_ThrowUserNotValidException() {
        //mock
        Long id = (long) 1;
        String invalidNRIC = "S1111111D";
        User user = new User(id, u1_FULL_NAME, invalidNRIC, PHONE_NO, "HOLLAND V ROAD", u1_USERNAME, u1_PASSWORD,u1_ROLE, true);

        Optional<User> notFound =  Optional.empty();
        when(userRepo.findByUsername(any(String.class))).thenReturn(notFound);
        when(Validator.validateNRIC(any(String.class))).thenReturn(false);

        //assert
        assertThrows(UserNotValidException.class, () -> userController.createUser(user), "NRIC is invalid");
        verify(userRepo).findByUsername(u1_USERNAME);
        verify(Validator).validateNRIC(invalidNRIC);

    }

    @Test
    void addCustomer_InvalidPhoneNoLength_ThrowUserNotValidException() {
        Long id = (long) 1;
        String invalidPhoneNo = "923079";
        User user = new User(id, u1_FULL_NAME, "S9926201Z", invalidPhoneNo, "HOLLAND V ROAD", "pineapple", u1_PASSWORD,u1_ROLE, true);

        Optional<User> notFound =  Optional.empty(); 
        when(userRepo.findByUsername(any(String.class))).thenReturn(notFound);
        when(Validator.validateNRIC(any(String.class))).thenReturn(true);
        when(Validator.validatePhoneno(any(String.class))).thenReturn(false);

        //assert
        assertThrows(UserNotValidException.class, () -> userController.createUser(user), "Phone number is invalid");
        verify(userRepo).findByUsername("pineapple");
        verify(Validator).validateNRIC("S9926201Z");
        verify(Validator).validatePhoneno(invalidPhoneNo);
    }

    @Test
    void addCustomer_InvalidPhoneNotSGNo_ThrowUserNotValidException() {
        Long id = (long) 1;
        String invalidPhoneNo = "99319908";
        User user = new User(id, u1_FULL_NAME, NRIC, invalidPhoneNo, "HOLLAND V ROAD", u1_USERNAME, u1_PASSWORD,u1_ROLE, true);

        Optional<User> notFound =  Optional.empty(); 
        when(userRepo.findByUsername(any(String.class))).thenReturn(notFound);
        when(Validator.validateNRIC(any(String.class))).thenReturn(true);
        when(Validator.validatePhoneno(any(String.class))).thenReturn(false);

        //assert
        assertThrows(UserNotValidException.class, () -> userController.createUser(user), "Phone number is invalid");
        verify(userRepo).findByUsername(u1_USERNAME);
        verify(Validator).validateNRIC(NRIC);
        verify(Validator).validatePhoneno(invalidPhoneNo);
    }


    @Test
    void getCustomers_NoCustomers_returnEmptyList(){
        // mock
        List<User> users = new ArrayList<User>();
        when(userRepo.findAll()).thenReturn(users);

        //act 
        List<User> returnedList = userController.getCustomers();

        //assert
        assertEquals(users,returnedList);
        verify(userRepo).findAll();
    }

    // test ROLE_manager for getCustomers with sec config 

    @Test
    void getCustomers_WithCustomers_returnListOfCustomers(){
        //mock 
        User user1 = new User(null, u1_FULL_NAME, NRIC, PHONE_NO, "HOLLAND V ROAD", u1_USERNAME, u1_PASSWORD,u1_ROLE, true);
        List<User> users = new ArrayList<User>();
        users.add(user1);
        when(userRepo.findAll()).thenReturn(users);
        
        //act 
        List<User> returned = userController.getCustomers();
        
        //assert
        assertEquals(users,returned);

    }

    @Test
    void getCustomer_notInRepository_throwUserNotFoundException(){
        //mock 
        Long id = (long)2;
        Optional<User> notFound =  Optional.empty();
        when(userRepo.findById(any(Long.class))).thenReturn(notFound);

        //assert
        assertThrows(UserNotFoundException.class, () -> userController.getCustomer(id), "Could not find user " + id);
        verify(userRepo).findById(id);
    }

    @Test
    void getCustomer_notAuthenticated_throwRoleNotAuthorisedException(){
        //mock 
        Long id1 = (long) 1;
        Long id2 = (long) 2;
        User user1 = new User(id1, u1_FULL_NAME, NRIC, PHONE_NO, "HOLLAND V ROAD", u1_USERNAME, u1_PASSWORD,u1_ROLE, true);
        User user2 = new User(id2, u2_FULL_NAME, NRIC, PHONE_NO, "MANAGER THREE STORY HOUSE", u2_USERNAME, u2_PASSWORD, u2_ROLE, true);
        List<User> users = new ArrayList<User>();
        users.add(user2);
       
        Optional<User> found = Optional.of(user2);
        when(userRepo.findById(any(Long.class))).thenReturn(found);
        when(uAuth.getAuthenticatedUser()).thenReturn(user1);

        //assert
        assertThrows(RoleNotAuthorisedException.class, () -> userController.getCustomer(id2), "You cannot view other customer details");
        verify(userRepo).findById(id2);
        verify(uAuth).getAuthenticatedUser();
    }

    @Test
    void getCustomer_AuthenticatedAndFoundRoleManager_returnUser(){
        //mock 
        Long id1 = (long) 1;
        Long id2 = (long) 2;
        //user
        User user1 = new User(id1, u1_FULL_NAME, NRIC, PHONE_NO, "HOLLAND V ROAD", u1_USERNAME, u1_PASSWORD,u1_ROLE, true);
        //manager
        User user2 = new User(id2, u2_FULL_NAME, NRIC, PHONE_NO, "MANAGER THREE STORY HOUSE", u2_USERNAME, u2_PASSWORD, u2_ROLE, true);
        List<User> users = new ArrayList<User>();
        users.add(user1);
        users.add(user2);
        
        Optional<User> found = Optional.of(user1);
        when(userRepo.findById(any(Long.class))).thenReturn(found);
        when(uAuth.getAuthenticatedUser()).thenReturn(user2);

        //act
        User returned = userController.getCustomer(id1);

        //assert
        assertEquals(user1, returned);
        verify(userRepo).findById(id1);
        verify(uAuth).getAuthenticatedUser();
    }

    @Test
    void getCustomer_AuthenticatedAndFoundRoleUser_returnUser(){
        //mock 
        Long id1 = (long) 1;
        User user1 = new User(id1, u1_FULL_NAME, NRIC, PHONE_NO, "HOLLAND V ROAD", u1_USERNAME, u1_PASSWORD,u1_ROLE, true);
        List<User> users = new ArrayList<User>();
        users.add(user1);

        Optional<User> found = Optional.of(user1);
        when(userRepo.findById(any(Long.class))).thenReturn(found);
        when(uAuth.getAuthenticatedUser()).thenReturn(user1);
 
        //act
        User returned = userController.getCustomer(id1);

        //assert
        assertEquals(user1, returned);
        verify(userRepo).findById(id1);
        verify(uAuth).getAuthenticatedUser();
    }

     @Test
    void updateUser_UserNotFound_ThrowsUserNotFoundException(){
        //mock 
        Long id2 = (long) 2;
        User user2 = new User(id2, u2_FULL_NAME, NRIC, PHONE_NO, "MANAGER THREE STORY HOUSE", u2_USERNAME, u2_PASSWORD, u2_ROLE, true);
        Optional<User> notFound = Optional.empty();
        when(userRepo.findById(any(Long.class))).thenReturn(notFound);
 
        //assert
        assertThrows(UserNotFoundException.class, () -> userController.updateUser(id2,user2), "Could not find user " + id2);
        verify(userRepo).findById(id2);
    }

    @Test
    void updateUser_NotAuthenticated_ThrowsRoleNotAuthorisedException(){
        //mock 
        Long id1 = (long) 1;
        User user1 = new User(id1, u1_FULL_NAME, NRIC, PHONE_NO, "HOLLAND V ROAD", u1_USERNAME, u1_PASSWORD,u1_ROLE, true);
        List<User> users = new ArrayList<User>();
        users.add(user1);

        Long id2 = (long) 2;
        User user2 = new User(id2, u2_FULL_NAME, NRIC, PHONE_NO, "MANAGER THREE STORY HOUSE", u2_USERNAME, u2_PASSWORD, u2_ROLE, true);
        Optional<User> found = Optional.of(user2);
        when(userRepo.findById(any(Long.class))).thenReturn(found);
        when(uAuth.getAuthenticatedUser()).thenReturn(user1);
 
        //assert
        assertThrows(RoleNotAuthorisedException.class, () -> userController.updateUser(id2,user2), "You cannot view other customer details");
        verify(userRepo).findById(id2);
        verify(uAuth).getAuthenticatedUser();
    }

    // @Test
    // void updateUser_UserSetStatus_NothingChanged(){
    //     //mock 
    //     Long id1 = (long) 1;
    //     User user1 = new User(id1, u1_FULL_NAME, NRIC, PHONE_NO, "HOLLAND V ROAD", u1_USERNAME, u1_PASSWORD,u1_ROLE, true);
    //     User newUser1 =  new User(id1, u1_FULL_NAME, NRIC, PHONE_NO, "HOLLAND V ROAD", u1_USERNAME, u1_PASSWORD,u1_ROLE, false);
    //     List<User> users = new ArrayList<User>();
    //     users.add(user1);

    //     Optional<User> found = Optional.of(user1);
    //     when(userRepo.findById(any(Long.class))).thenReturn(found);
    //     when(uAuth.getAuthenticatedUser()).thenReturn(user1);
    //     when(Validator.validatePhoneno(PHONE_NO)).thenReturn(true);
    //     // when(Validator.validateNRIC(NRIC)).thenReturn(true);
    //    // when(userRepo.save(any(User.class))).thenReturn(user1);
    //     //act
    //     User returned = userController.updateUser(id1,newUser1);

    //     System.out.println("returned = " + returned);

    //     //assert
    //     assertEquals(user1, returned);
    //     verify(userRepo).findById(id1);
    //     verify(uAuth).getAuthenticatedUser();
    // }

    // @Test
    // void updateUser_ManagerSetStatus_StatusChanged(){
    //     //mock 
    //     Long id1 = (long) 1;
    //     User user1 = new User(id1, u1_FULL_NAME, NRIC, PHONE_NO, "HOLLAND V ROAD", u1_USERNAME, u1_PASSWORD,u1_ROLE, true);
    //     List<User> users = new ArrayList<User>();
    //     users.add(user1);

    //     User newUser1 =  new User(id1, u1_FULL_NAME, NRIC, PHONE_NO, "HOLLAND V ROAD", u1_USERNAME, u1_PASSWORD,u1_ROLE, false);

    //     Long id2 = (long) 2;
    //     User manager = new User(id2, u2_FULL_NAME, NRIC, PHONE_NO, "MANAGER THREE STORY HOUSE", u2_USERNAME, u2_PASSWORD, u2_ROLE, true);
    //     Optional<User> found = Optional.of(user1);
    //     when(userRepo.findById(any(Long.class))).thenReturn(found);
    //     when(uAuth.getAuthenticatedUser()).thenReturn(manager);
 
    //     //act
    //     User returned = userController.updateUser(id1,newUser1);

    //     //assert
    //     assertEquals(newUser1, returned);
    //     verify(userRepo).findById(id1);
    //     verify(uAuth).getAuthenticatedUser();
    // }

    // @Test
    // void updateUser_UpdateFullName_FieldsNotChanged(){
    //     //mock 
    //     Long id1 = (long) 1;
    //     User user1 = new User(id1, u1_FULL_NAME, NRIC, PHONE_NO, "HOLLAND V ROAD", u1_USERNAME, u1_PASSWORD,u1_ROLE, true);
    //     List<User> users = new ArrayList<User>();
    //     users.add(user1);

    //     Optional<User> found = Optional.of(user1);
    //     when(userRepo.findById(any(Long.class))).thenReturn(found);
    //     when(uAuth.getAuthenticatedUser()).thenReturn(user1);

    //     //change full name
    //     User newUser1 =  new User(id1, "apple", NRIC, PHONE_NO, "HOLLAND V ROAD", u1_USERNAME, u1_PASSWORD,u1_ROLE, true);       
    //     //act
    //     User returned = userController.updateUser(id1,newUser1);

    //     //assert
    //     assertEquals(user1, returned);
    //     verify(userRepo).findById(id1);
    //     verify(uAuth).getAuthenticatedUser();
    // }

    // @Test
    // void updateUser_UpdateNRIC_FieldsNotChanged(){
    //     //mock 
    //     Long id1 = (long) 1;
    //     User user1 = new User(id1, u1_FULL_NAME, NRIC, PHONE_NO, "HOLLAND V ROAD", u1_USERNAME, u1_PASSWORD,u1_ROLE, true);
    //     List<User> users = new ArrayList<User>();
    //     users.add(user1);

    //     Optional<User> found = Optional.of(user1);
    //     when(userRepo.findById(any(Long.class))).thenReturn(found);
    //     when(uAuth.getAuthenticatedUser()).thenReturn(user1);

    //     //change nric
    //     User newUser1 =  new User(id1, u1_FULL_NAME, "S1234567D", PHONE_NO, "HOLLAND V ROAD", u1_USERNAME, u1_PASSWORD,u1_ROLE, true);
 
    //     //act
    //     User returned = userController.updateUser(id1,newUser1);

    //     //assert
    //     assertEquals(user1, returned);
    //     verify(userRepo).findById(id1);
    //     verify(uAuth).getAuthenticatedUser();
    // }

    // @Test
    // void updateUser_UpdateUsername_FieldsNotChanged(){
    //     //mock 
    //     Long id1 = (long) 1;
    //     User user1 = new User(id1, u1_FULL_NAME, NRIC, PHONE_NO, "HOLLAND V ROAD", u1_USERNAME, u1_PASSWORD,u1_ROLE, true);
    //     List<User> users = new ArrayList<User>();
    //     users.add(user1);

    //     Optional<User> found = Optional.of(user1);
    //     when(userRepo.findById(any(Long.class))).thenReturn(found);
    //     when(uAuth.getAuthenticatedUser()).thenReturn(user1);

    //     //change username
    //     User newUser1 =  new User(id1, u1_FULL_NAME, NRIC, PHONE_NO, "HOLLAND V ROAD", "aplhagvdf", u1_PASSWORD,u1_ROLE, true);
 
    //     //act
    //     User returned = userController.updateUser(id1,newUser1);

    //     //assert
    //     assertEquals(user1, returned);
    //     verify(userRepo).findById(id1);
    //     verify(uAuth).getAuthenticatedUser();
    // }

    // @Test
    // void updateUser_UpdatePhoneNo_PhoneNoChanged(){
    //      //mock 
    //      Long id1 = (long) 1;
    //      User user1 = new User(id1, u1_FULL_NAME, NRIC, PHONE_NO, "HOLLAND V ROAD", u1_USERNAME, u1_PASSWORD,u1_ROLE, true);
    //      List<User> users = new ArrayList<User>();
    //      users.add(user1);
 
    //      Optional<User> found = Optional.of(user1);
    //      when(userRepo.findById(any(Long.class))).thenReturn(found);
    //      when(uAuth.getAuthenticatedUser()).thenReturn(user1);
 
    //      //change username
    //      User newUser1 =  new User(id1, u1_FULL_NAME, NRIC, "90908765", "HOLLAND V ROAD", u1_USERNAME, u1_PASSWORD,u1_ROLE, true);
  
    //      //act
    //      User returned = userController.updateUser(id1,newUser1);
 
    //      //assert
    //      assertEquals(newUser1, returned);
    //      verify(userRepo).findById(id1);
    //      verify(uAuth).getAuthenticatedUser();
    // }

    // @Test
    // void updateUser_UpdatePassword_PasswordChanged(){
    //      //mock 
    //      Long id1 = (long) 1;
    //      User user1 = new User(id1, u1_FULL_NAME, NRIC, PHONE_NO, "HOLLAND V ROAD", u1_USERNAME, u1_PASSWORD,u1_ROLE, true);
    //      List<User> users = new ArrayList<User>();
    //      users.add(user1);
 
    //      Optional<User> found = Optional.of(user1);
    //      when(userRepo.findById(any(Long.class))).thenReturn(found);
    //      when(uAuth.getAuthenticatedUser()).thenReturn(user1);
 
    //      //change username
    //      User newUser1 =  new User(id1, u1_FULL_NAME, NRIC, PHONE_NO, "HOLLAND V ROAD", u1_USERNAME, "i<3carmen",u1_ROLE, true);
  
    //      //act
    //      User returned = userController.updateUser(id1,newUser1);
 
    //      //assert
    //      assertEquals(newUser1, returned);
    //      verify(userRepo).findById(id1);
    //      verify(uAuth).getAuthenticatedUser();
    // }

    // @Test
    // void updateUser_UpdateAddress_AddressChanged(){
    //      //mock 
    //      Long id1 = (long) 1;
    //      User user1 = new User(id1, u1_FULL_NAME, NRIC, PHONE_NO, "HOLLAND V ROAD", u1_USERNAME, u1_PASSWORD,u1_ROLE, true);
    //      List<User> users = new ArrayList<User>();
    //      users.add(user1);
 
    //      Optional<User> found = Optional.of(user1);
    //      when(userRepo.findById(any(Long.class))).thenReturn(found);
    //      when(uAuth.getAuthenticatedUser()).thenReturn(user1);
 
    //      //change username
    //      User newUser1 =  new User(id1, u1_FULL_NAME, NRIC, PHONE_NO, "OLD TOWN ROAD", u1_USERNAME, u1_PASSWORD,u1_ROLE, true);
  
    //      //act
    //      User returned = userController.updateUser(id1,newUser1);
 
    //      //assert
    //      assertEquals(newUser1, returned);
    //      verify(userRepo).findById(id1);
    //      verify(uAuth).getAuthenticatedUser();
    // }
}
