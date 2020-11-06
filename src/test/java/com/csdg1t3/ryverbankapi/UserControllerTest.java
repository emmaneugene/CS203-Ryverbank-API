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

import com.csdg1t3.ryverbankapi.trade.*;
import com.csdg1t3.ryverbankapi.user.*;
import com.csdg1t3.ryverbankapi.security.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

    private final User customer = new User((long) 1,"cspotatoes","S1234567G", "93223235", "Potato island", "potato","$2a$10$1/dOPkY80t.wyXV3p1MR0OhEJOnkljtU2AGkalTv1E3MZtJUqmmLO","ROLE_USER", true );
    private final User newCustomer = new User((long) 3,"tony starks","S1234567G", "93223235", "starship", "iamironman","i<3carmen","ROLE_USER", true );
    private final User manager = new User((long) 2,"Tan Li Ling","S1234567G", "93223235", "Potato island", "manager_1","$2a$10$HQKNcTWJ5Teo4dCwUeDc7uzijJoMJWgKvljwzQ/3aAS6w5Gf.Bblu","ROLE_MANAGER", true );

    private final String NEWCUSTOMER_USERNAME = "iamironman";
    private final String NEWCUSTOMER_PASSWORD = "i<3carmen";
    private final String NEWCUSTOMER_PASSWORD_ENCODED = "$2a$10$57cVEHOoCgQ5oXRRw8OD6OgufxqaW84orLtw9moW.cHEgxIFvy/F.";
    
    private final long CUST_ID = (long) 1;
    private final String CUSTOMER_FULL_NAME = "cspotatoes";
    private final String  CUSTOMER_USERNAME = "potato";
    private final String CUSTOMER_PASSWORD = "iamgoodpotato123";
    private final String CUSTOMER_ROLE = "ROLE_USER";
    private final String NRIC = "S1234567G";
    private final String PHONE = "93223235";
    private final String CUSTOMER_PASSWORD_ENCODED = "$2a$10$1/dOPkY80t.wyXV3p1MR0OhEJOnkljtU2AGkalTv1E3MZtJUqmmLO";

    // Assert that method returns saved user
    @Test
    void addCustomer_NewUserName_ReturnSavedCustomer() {
        // mock 
        Optional<User> notFound =  Optional.empty();
        Portfolio newCustomerPortfolio = new Portfolio(null, (long) 1, newCustomer, null, 0, 0);
        when(userRepo.findByUsername(any(String.class))).thenReturn(notFound);
        when(Validator.validateNRIC(any(String.class))).thenReturn(true);
        when(Validator.validatePhoneno(any(String.class))).thenReturn(true);
        when(userRepo.save(any(User.class))).thenReturn(newCustomer);
        when(portfolioRepo.save(any(Portfolio.class))).thenReturn(newCustomerPortfolio);
        when(encoder.encode(any(String.class))).thenReturn(NEWCUSTOMER_PASSWORD_ENCODED);

        //act
        User savedUser = userController.createUser(newCustomer);

        //assert 
        assertEquals(savedUser,newCustomer);
        assertNotNull(savedUser.getPortfolio());
        assertEquals(savedUser.getPassword(),NEWCUSTOMER_PASSWORD_ENCODED);
        verify(userRepo).findByUsername(NEWCUSTOMER_USERNAME);
        verify(Validator).validateNRIC(NRIC);
        verify(Validator).validatePhoneno(PHONE);
        verify(userRepo).save(newCustomer);
        verify(portfolioRepo).save(savedUser.getPortfolio());
        verify(encoder).encode(NEWCUSTOMER_PASSWORD);
        
    }

    // Assert that method throws UserConflictException
    @Test
    void addCustomer_SameUserName_ThrowUserConflictException() {
        //mock
        User newUser = new User(null, CUSTOMER_FULL_NAME, NRIC, PHONE, "HOLLAND V ROAD", CUSTOMER_USERNAME, encoder.encode(CUSTOMER_PASSWORD),CUSTOMER_ROLE, true);
        Optional<User> Found = Optional.of(customer);
        
        when(userRepo.findByUsername(any(String.class))).thenReturn(Found);

        //assert
        assertThrows(UserConflictException.class, () -> userController.createUser(newUser), "Username already taken");
        verify(userRepo).findByUsername(customer.getUsername());
    }

    @Test
    void addCustomer_InvalidNric_ThrowUserNotValidException() {
        //mock
        Long id = (long) 1;
        String invalidNRIC = "S1111111D";
        User user = new User(id, CUSTOMER_FULL_NAME, invalidNRIC, PHONE, "HOLLAND V ROAD", CUSTOMER_USERNAME, CUSTOMER_PASSWORD,CUSTOMER_ROLE, true);

        Optional<User> notFound =  Optional.empty();
        when(userRepo.findByUsername(any(String.class))).thenReturn(notFound);
        when(Validator.validateNRIC(any(String.class))).thenReturn(false);

        //assert
        assertThrows(UserNotValidException.class, () -> userController.createUser(user), "NRIC is invalid");
        verify(userRepo).findByUsername(CUSTOMER_USERNAME);
        verify(Validator).validateNRIC(invalidNRIC);

    }

    @Test
    void addCustomer_InvalidPhoneNoLength_ThrowUserNotValidException() {
        Long id = (long) 1;
        String invalidPhoneNo = "923079";
        User user = new User(id, CUSTOMER_FULL_NAME, "S9926201Z", invalidPhoneNo, "HOLLAND V ROAD", "pineapple", CUSTOMER_PASSWORD,CUSTOMER_ROLE, true);

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
        User user = new User(id, CUSTOMER_FULL_NAME, NRIC, invalidPhoneNo, "HOLLAND V ROAD", CUSTOMER_USERNAME, CUSTOMER_PASSWORD,CUSTOMER_ROLE, true);

        Optional<User> notFound =  Optional.empty(); 
        when(userRepo.findByUsername(any(String.class))).thenReturn(notFound);
        when(Validator.validateNRIC(any(String.class))).thenReturn(true);
        when(Validator.validatePhoneno(any(String.class))).thenReturn(false);

        //assert
        assertThrows(UserNotValidException.class, () -> userController.createUser(user), "Phone number is invalid");
        verify(userRepo).findByUsername(CUSTOMER_USERNAME);
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
        List<User> users = new ArrayList<User>();
        users.add(customer);
        users.add(manager);
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
       
        Optional<User> found = Optional.of(manager);
        when(userRepo.findById(any(Long.class))).thenReturn(found);
        when(uAuth.getAuthenticatedUser()).thenReturn(customer);

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
        
        Optional<User> found = Optional.of(customer);
        when(userRepo.findById(any(Long.class))).thenReturn(found);
        when(uAuth.getAuthenticatedUser()).thenReturn(manager);

        //act
        User returned = userController.getCustomer(id1);

        //assert
        assertEquals(customer, returned);
        verify(userRepo).findById(id1);
        verify(uAuth).getAuthenticatedUser();
    }

    @Test
    void getCustomer_AuthenticatedAndFoundRoleUser_returnUser(){
        //mock 
        Long id1 = (long) 1;
    
        Optional<User> found = Optional.of(customer);
        when(userRepo.findById(any(Long.class))).thenReturn(found);
        when(uAuth.getAuthenticatedUser()).thenReturn(customer);
 
        //act
        User returned = userController.getCustomer(id1);

        //assert
        assertEquals(customer, returned);
        verify(userRepo).findById(id1);
        verify(uAuth).getAuthenticatedUser();
    }

     @Test
    void updateUser_UserNotFound_ThrowsUserNotFoundException(){
        //mock 
        Long id2 = (long) 2;
        Optional<User> notFound = Optional.empty();
        when(userRepo.findById(any(Long.class))).thenReturn(notFound);
 
        //assert
        assertThrows(UserNotFoundException.class, () -> userController.updateUser(id2,manager), "Could not find user " + id2);
        verify(userRepo).findById(id2);
    }

    @Test
    void updateUser_NotAuthenticated_ThrowsRoleNotAuthorisedException(){
        //mock 
        Long id1 = (long) 1;
        Long id2 = (long) 2;

        Optional<User> found = Optional.of(manager);
        when(userRepo.findById(any(Long.class))).thenReturn(found);
        when(uAuth.getAuthenticatedUser()).thenReturn(customer);
 
        //assert
        assertThrows(RoleNotAuthorisedException.class, () -> userController.updateUser(id2,manager), "You cannot view other customer details");
        verify(userRepo).findById(id2);
        verify(uAuth).getAuthenticatedUser();
    }

    // @Test
    // void updateUser_UserSetStatus_NothingChanged(){
    //     //mock 
    //     Long id1 = (long) 1;
    //     User user1 = new User(id1, CUSTOMER_FULL_NAME, NRIC, PHONE, "HOLLAND V ROAD", CUSTOMER_USERNAME, CUSTOMER_PASSWORD,CUSTOMER_ROLE, true);
    //     User newUser1 =  new User(id1, CUSTOMER_FULL_NAME, NRIC, PHONE, "HOLLAND V ROAD", CUSTOMER_USERNAME, CUSTOMER_PASSWORD,CUSTOMER_ROLE, false);
    //     List<User> users = new ArrayList<User>();
    //     users.add(user1);

    //     Optional<User> found = Optional.of(user1);
    //     when(userRepo.findById(any(Long.class))).thenReturn(found);
    //     when(uAuth.getAuthenticatedUser()).thenReturn(user1);
    //     when(Validator.validatePhoneno(PHONE)).thenReturn(true);
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
    //     User user1 = new User(id1, CUSTOMER_FULL_NAME, NRIC, PHONE, "HOLLAND V ROAD", CUSTOMER_USERNAME, CUSTOMER_PASSWORD,CUSTOMER_ROLE, true);
    //     List<User> users = new ArrayList<User>();
    //     users.add(user1);

    //     User newUser1 =  new User(id1, CUSTOMER_FULL_NAME, NRIC, PHONE, "HOLLAND V ROAD", CUSTOMER_USERNAME, CUSTOMER_PASSWORD,CUSTOMER_ROLE, false);

    //     Long id2 = (long) 2;
    //     User manager = new User(id2, u2_FULL_NAME, NRIC, PHONE, "MANAGER THREE STORY HOUSE", u2_USERNAME, u2_PASSWORD, u2_ROLE, true);
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
    //     User user1 = new User(id1, CUSTOMER_FULL_NAME, NRIC, PHONE, "HOLLAND V ROAD", CUSTOMER_USERNAME, CUSTOMER_PASSWORD,CUSTOMER_ROLE, true);
    //     List<User> users = new ArrayList<User>();
    //     users.add(user1);

    //     Optional<User> found = Optional.of(user1);
    //     when(userRepo.findById(any(Long.class))).thenReturn(found);
    //     when(uAuth.getAuthenticatedUser()).thenReturn(user1);

    //     //change full name
    //     User newUser1 =  new User(id1, "apple", NRIC, PHONE, "HOLLAND V ROAD", CUSTOMER_USERNAME, CUSTOMER_PASSWORD,CUSTOMER_ROLE, true);       
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
    //     User user1 = new User(id1, CUSTOMER_FULL_NAME, NRIC, PHONE, "HOLLAND V ROAD", CUSTOMER_USERNAME, CUSTOMER_PASSWORD,CUSTOMER_ROLE, true);
    //     List<User> users = new ArrayList<User>();
    //     users.add(user1);

    //     Optional<User> found = Optional.of(user1);
    //     when(userRepo.findById(any(Long.class))).thenReturn(found);
    //     when(uAuth.getAuthenticatedUser()).thenReturn(user1);

    //     //change nric
    //     User newUser1 =  new User(id1, CUSTOMER_FULL_NAME, "S1234567D", PHONE, "HOLLAND V ROAD", CUSTOMER_USERNAME, CUSTOMER_PASSWORD,CUSTOMER_ROLE, true);
 
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
    //     User user1 = new User(id1, CUSTOMER_FULL_NAME, NRIC, PHONE, "HOLLAND V ROAD", CUSTOMER_USERNAME, CUSTOMER_PASSWORD,CUSTOMER_ROLE, true);
    //     List<User> users = new ArrayList<User>();
    //     users.add(user1);

    //     Optional<User> found = Optional.of(user1);
    //     when(userRepo.findById(any(Long.class))).thenReturn(found);
    //     when(uAuth.getAuthenticatedUser()).thenReturn(user1);

    //     //change username
    //     User newUser1 =  new User(id1, CUSTOMER_FULL_NAME, NRIC, PHONE, "HOLLAND V ROAD", "aplhagvdf", CUSTOMER_PASSWORD,CUSTOMER_ROLE, true);
 
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
    //      User user1 = new User(id1, CUSTOMER_FULL_NAME, NRIC, PHONE, "HOLLAND V ROAD", CUSTOMER_USERNAME, CUSTOMER_PASSWORD,CUSTOMER_ROLE, true);
    //      List<User> users = new ArrayList<User>();
    //      users.add(user1);
 
    //      Optional<User> found = Optional.of(user1);
    //      when(userRepo.findById(any(Long.class))).thenReturn(found);
    //      when(uAuth.getAuthenticatedUser()).thenReturn(user1);
 
    //      //change username
    //      User newUser1 =  new User(id1, CUSTOMER_FULL_NAME, NRIC, "90908765", "HOLLAND V ROAD", CUSTOMER_USERNAME, CUSTOMER_PASSWORD,CUSTOMER_ROLE, true);
  
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
    //      User user1 = new User(id1, CUSTOMER_FULL_NAME, NRIC, PHONE, "HOLLAND V ROAD", CUSTOMER_USERNAME, CUSTOMER_PASSWORD,CUSTOMER_ROLE, true);
    //      List<User> users = new ArrayList<User>();
    //      users.add(user1);
 
    //      Optional<User> found = Optional.of(user1);
    //      when(userRepo.findById(any(Long.class))).thenReturn(found);
    //      when(uAuth.getAuthenticatedUser()).thenReturn(user1);
 
    //      //change username
    //      User newUser1 =  new User(id1, CUSTOMER_FULL_NAME, NRIC, PHONE, "HOLLAND V ROAD", CUSTOMER_USERNAME, "i<3carmen",CUSTOMER_ROLE, true);
  
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
    //      User user1 = new User(id1, CUSTOMER_FULL_NAME, NRIC, PHONE, "HOLLAND V ROAD", CUSTOMER_USERNAME, CUSTOMER_PASSWORD,CUSTOMER_ROLE, true);
    //      List<User> users = new ArrayList<User>();
    //      users.add(user1);
 
    //      Optional<User> found = Optional.of(user1);
    //      when(userRepo.findById(any(Long.class))).thenReturn(found);
    //      when(uAuth.getAuthenticatedUser()).thenReturn(user1);
 
    //      //change username
    //      User newUser1 =  new User(id1, CUSTOMER_FULL_NAME, NRIC, PHONE, "OLD TOWN ROAD", CUSTOMER_USERNAME, CUSTOMER_PASSWORD,CUSTOMER_ROLE, true);
  
    //      //act
    //      User returned = userController.updateUser(id1,newUser1);
 
    //      //assert
    //      assertEquals(newUser1, returned);
    //      verify(userRepo).findById(id1);
    //      verify(uAuth).getAuthenticatedUser();
    // }
}
