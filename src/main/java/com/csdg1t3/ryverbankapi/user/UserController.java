package com.csdg1t3.ryverbankapi.user;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.Valid;


/**
 * Controller that manages HTTP  requests
 */
@RestController
public class UserController {
    private UserRepository userRepository;
    private BCryptPasswordEncoder encoder;


    public UserController(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository= userRepository;
        this.encoder = encoder;
    }

    /**
     * 
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/customers")
    public List<User> getCustomers() {
        return userRepository.findAll();
    }

    /**
     * Search for customer with the given id. If there is no customer with the given "id", throw 
     * @param id
     * @return customer with the given id
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/customers/{id}")
    public User getCustomer(@PathVariable Long id) {
        Optional<User> result = userRepository.findById(id); // userService.getUser(id);
        if (!result.isPresent())
            throw new UserNotFoundException(id);

        User user = result.get();
        UserDetails uDetails = (UserDetails)SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
        
        if (!uDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER")) 
            && !user.getUsername().equals(uDetails.getUsername())) 
            throw new RoleNotAuthorisedException("You cannot view other customer accounts");
                
        return user;
    }

    /**
     * 
     * @param user
     * @return 
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/customers")
    public User createUser(@Valid @RequestBody User user) {
        Optional<User> result = userRepository.findByUsername(user.getUsername());
        if (result.isPresent())
            throw new UserNotValidException("Username already taken");

        if (!Validator.validateNRIC(user.getNric())) 
            throw new RoleNotAuthorisedException("NRIC is invalid");

        if (!Validator.validatePhoneno(user.getPhoneNo())) 
            throw new RoleNotAuthorisedException("Phone number is invalid");

        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Updates customer info using HTTP PUT request. Customers with ROLE_USER are authorised 
     * to update their phone no, address and password. Customers with ROLE_MANAGER are further
     * authorised to update customer status 
     * @param id
     * @param newUserInfo
     * @return
    */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/customers/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User newUserInfo) {
        Optional<User> result = userRepository.findById(id); // userService.getUser(id);
        if (!result.isPresent())
            throw new UserNotFoundException(id);

        User user = result.get();
        UserDetails uDetails = (UserDetails)SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();

        if (!uDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER")) 
            && !user.getUsername().equals(uDetails.getUsername()))
            throw new RoleNotAuthorisedException("You cannot update another customer's details");
        
        if (newUserInfo.getPhoneNo() != null)
            user.setPhoneNo(newUserInfo.getPhoneNo());
        
        if (newUserInfo.getAddress() != null)
            user.setAddress(newUserInfo.getAddress());
        
        if (newUserInfo.getPassword() != null)
            user.setPassword(newUserInfo.getPassword());
        
        if (uDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER")) 
            && newUserInfo.getStatus() != null)
            user.setStatus(newUserInfo.getStatus());

        return userRepository.save(user);
    }
//    // add methods 
//    /**
//     * General add user method, check if authorities is matching
//     * @param user
//     * @param authority
//     * @return
//     */
//    public User addUser(User user, String authority) {
//     // check if authority of user matches where it is supposed to be mapped to 
//     List<String> authorities = Arrays.asList(user.getStringAuthorities());
//     if(!authorities.contains(authority)){
//         throw new UserNotValidException("authorities of" + authority + " is wrong" );
//     }
    
//     // encrpyt customer's password 
//     user.setPassword(encoder.encode(user.getPassword()));
//     return userService.addUser(user);
// }

    /**
     * Add a new customer with POST request to "/customers"
     * Note the use of @RequestBody
     * @param user
     * @return list of all customers 
     */
    // @ResponseStatus(HttpStatus.CREATED)
    // @PostMapping("/customers")
    // public User addCustomer(@Valid @RequestBody User customer) {
    //     return addUser(customer,"ROLE_USER");
    // }

    // update methods 
    /**
     * General update method for manager/ analyst/ customer 
     * @param id
     * @param newUser
     * @param authority
     * @return
     */
    // public User updateUser(Long id, User newUser, String authority) {
    // return userService.updateUser(id,newUser, authority); 
    // }

    /**
     * If there is no customer with the given "id", UserNotFoundException
     * @param id
     * @param newCustomer
     * @return the updated, or newly added customer
     */
    
    // @PutMapping("/customers/{id}")
    // public User updateCustomerInfo(@PathVariable Long id, @Valid @RequestBody User newCustomer) {
    //     User customer = userService.getUser(id);
    //     if (customer == null) 
    //         throw new UserNotFoundException(id);

    //     // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    //     // String username = auth.getPrincipal().toString(); // substring(0, auth.getPrincipal().toString().indexOf(" "))
    //     // String userrole = auth.getPrincipal().toString().substring(auth.getPrincipal().toString().indexOf("["));
    //     // String password = auth.getPrincipal().toString().substring(auth.getPrincipal().toString().indexOf("$"), auth.getPrincipal().toString().lastIndexOf(" "));
    //     // System.out.println(auth.getPrincipal().toString());
    //     // System.out.println(password);
    //     // if(!customer.getUsername().equals(username) && !userrole.equals("[ROLE_MANAGER]")){
    //     //     throw new UserNotValidException("Customer cannot update other customers");
    //     // }

    //     // if(!customer.getPassword().equals(password) && !userrole.equals("[ROLE_MANAGER]")) {
    //     //     throw new UserNotValidException("Password is wrong");
    //     // }

    //     return updateUser(id,newCustomer,"ROLE_USER");
    // }

    // @PreAuthorize("hasRole('ROLE_MANAGER') ")
    // // @PutMapping("/customers/{id}")
    // @RequestMapping(value = "/customers/{id}" ,method = RequestMethod.PUT, headers = "updateCustomerStatus")
    // public User updateCustomerStatus(@PathVariable Long id, @Valid @RequestBody User newCustomer) {
    //     return updateUser(id,newCustomer,"ROLE_MANAGER");
    // }


        
    // public void deleteUser(Long id, String authority){
    //     User userAtId = userService.getUser(id);
    //     if (userAtId == null){
    //         throw new UserNotFoundException(id);
    //     }

    //     // if(userAtId.getAuthorities().size() == 1 ){
    //     //     try {
    //     //         userService.deleteUser(id);
    //     //     } catch (Exception e) {
    //     //         throw new UserNotFoundException(id);
    //     //     }
    //     //     return;
    //     // }

    //     // // user has 2 roles 
    //     // String newAuthorities = "";
    //     // String[] authorities = userAtId.getAuthorities().toArray(new String[0]);
    //     // // String[] authorities = userAtId.getAuthorities();
    //     // for(String role : authorities){
    //     //     if(!role.equals(authority)){
    //     //         newAuthorities += role;
    //     //     }
    //     // }
    //     // userAtId.setAuthorities(newAuthorities);
    //     userService.updateUser(id, userAtId,"ROLE_MANAGER");
    // }

}